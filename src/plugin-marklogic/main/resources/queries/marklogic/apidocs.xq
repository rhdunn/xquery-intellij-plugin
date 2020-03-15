(:
 : Copyright (C) 2020 Reece H. Dunn
 :
 : Licensed under the Apache License, Version 2.0 (the "License");
 : you may not use this file except in compliance with the License.
 : You may obtain a copy of the License at
 :
 : http://www.apache.org/licenses/LICENSE-2.0
 :
 : Unless required by applicable law or agreed to in writing, software
 : distributed under the License is distributed on an "AS IS" BASIS,
 : WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 : See the License for the specific language governing permissions and
 : limitations under the License.
 :)
xquery version "3.1";

declare namespace apidoc = "http://marklogic.com/xdmp/apidoc";
declare namespace map = "http://www.w3.org/2005/xpath-functions/map";
declare namespace xhtml = "http://www.w3.org/1999/xhtml";

declare variable $apidoc as element(apidoc:apidoc) :=
    typeswitch (.)
    case xs:string return fn:parse-xml(.)/element()
    case document-node() return ./element()
    default return .;

declare variable $language as xs:string := "xquery"; (: xquery | rest :)

(: namespaces -------------------------------------------------------------- :)

declare variable $module-namespaces := map:merge(
    for $import in $apidoc//apidoc:module/apidoc:summary/(*:p|*:pre)
    let $match := fn:analyze-string(($import/*:code, $import)[1], "import module (namespace\s*([a-z0-9]+)\s*=\s*)?""([^""]+)""")/fn:match[1]
    let $prefix := ($match//fn:group[@nr = "2"]/string(), $import/ancestor::apidoc:module/@lib)[1]
    let $namespace := $match/fn:group[@nr = "3"]/string()
    where exists($namespace)
    return map:entry($prefix, $namespace)
);

declare variable $xquery-namespaces := map {
    "local": "http://www.w3.org/2005/xquery-local-functions",
    "fn": "http://www.w3.org/2005/xpath-functions",
    "xml": "http://www.w3.org/XML/1998/namespace",
    "xs": "http://www.w3.org/2001/XMLSchema",
    "xsi": "http://www.w3.org/2001/XMLSchema-instance"
};

declare variable $marklogic-5-builtin := map {
    "cts": "http://marklogic.com/cts",
    "dbg": "http://marklogic.com/xdmp/dbg",
    "dir": "http://marklogic.com/xdmp/directory",
    "error": "http://marklogic.com/xdmp/error",
    "map": "http://marklogic.com/xdmp/map",
    "math": "http://marklogic.com/xdmp/math",
    "prof": "http://marklogic.com/xdmp/profile",
    "sec": "http://marklogic.com/security",
    "spell": "http://marklogic.com/xdmp/spell",
    "xdmp": "http://marklogic.com/xdmp"
};

declare variable $marklogic-additional := map {
    "as": "http://marklogic.com/xdmp/assignments",
    "db": "http://marklogic.com/xdmp/database",
    "dg": "xdmp:document-get",
    "cl": "http://marklogic.com/xdmp/clusters",
    "exsl": "http://exslt.org/common",
    "gr": "http://marklogic.com/xdmp/group",
    "xdt": "http://www.w3.org/2004/10/xpath-datatypes",
    "xi": "http://www.w3.org/2001/XInclude",
    "mt": "http://marklogic.com/xdmp/mimetypes",
    "pkg": "http://marklogic.com/manage/package",
    "s": "http://www.w3.org/2009/xpath-functions/analyze-string",
    "x509": "http://marklogic.com/xdmp/x509"
};

declare variable $namespaces := map:merge((
    $module-namespaces,
    $xquery-namespaces,
    $marklogic-5-builtin,
    $marklogic-additional,
    ()
));

(: functions --------------------------------------------------------------- :)

(: Several functions in the apidocs have duplicate parameter names in some versions, so fix these. :)
declare variable $param-fixes := map {
    "fn:subtract-dateTimes-yielding-dayTimeDuration": ("srcval1", "srcval2"),
    "fn:subtract-dateTimes-yielding-yearMonthDuration": ("srcval1", "srcval2"),
    "math:fmod": ("x", "y"),
    "math:rank": ("arg1", "arg2", "options")
};

declare variable $functions as element(apidoc:function)* := $apidoc//apidoc:function[
    local:function-language(.) = $language and not(@name = "")
];

declare function local:function-language($function as element(apidoc:function)) as xs:string {
    let $bucket := $function/@bucket
    return switch ($bucket)
    case "REST Resources API" return "rest"
    default return "xquery"
};

declare function local:function-name($function as element(apidoc:function)) as xs:string {
    ``[`{$function/@lib}`:`{$function/@name}`]``
};

declare function local:function-parameters($function as element(apidoc:function)) as xs:string* {
    let $names as xs:string* :=
        let $names := map:get($param-fixes, local:function-name($function))
        return if (exists($names)) then
            $names
        else
            $function/apidoc:params/apidoc:param/@name/string()
    return if (count($names) != count(distinct-values($names))) then
        fn:error(xs:QName("err:XQST0039"),``[Duplicate parameter name in `{local:function-name($function)}`]``)
    else
        $names
};

declare function local:function-parameter($function as element(apidoc:function), $name as xs:string) as element(apidoc:param) {
    let $names := map:get($param-fixes, local:function-name($function))
    return if (exists($names)) then
        let $i := fn:index-of($names, $name)
        return $function/apidoc:params/apidoc:param[$i]
    else
        $function/apidoc:params/apidoc:param[@name = $name]
};

declare function local:function-parameter-type($function as element(apidoc:function), $name as xs:string) as xs:string {
    if ($function/@lib = "exsl" and $function/@name = "object-type") then
        "item()*" (: MarkLogic 7 and earlier docs have this as 'atomic type'. :)
    else
        let $type := local:function-parameter($function, $name)/@type/string()
        return replace($type, ",\.\.\.", "...")
};

declare function local:function-return-type($function as element(apidoc:function)) as xs:string {
    let $type := $function/apidoc:return/string()
    return switch ($type)
    case "An XML representation of the certificate." return "element()?"
    case "element())" return "element()"
    case "xs:unsignedLong)" return "xs:unsignedLong"
    default return $type
};

declare function local:function-xquery-signatures($function as element(apidoc:function)) as xs:string* {
    let $name := local:function-name($function)
    let $params :=
        for $name in local:function-parameters($function)
        let $type := local:function-parameter-type($function, $name)
        return ``[$`{$name}` as `{$type}`]``
    let $type := local:function-return-type($function)
    return ``[declare function `{$name}`(`{string-join($params, ", ")}`) as `{$type}` external;]``
};

(: query ------------------------------------------------------------------- :)

for $prefix in map:keys($namespaces)
let $namespace := map:get($namespaces, $prefix)
order by $prefix
return ``[declare namespace `{$prefix}` = "`{$namespace}`";]``
,
for $function in $functions
order by $function/@lib, $function/@name
return local:function-xquery-signatures($function)
