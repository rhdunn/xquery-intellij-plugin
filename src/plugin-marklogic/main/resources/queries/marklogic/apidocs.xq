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

declare variable $apidoc as element(apidoc:apidoc) :=
    typeswitch (.)
    case xs:string return fn:parse-xml(.)/element()
    case document-node() return ./element()
    default return .;

declare variable $language as xs:string := "xquery"; (: xquery | rest :)

(: functions --------------------------------------------------------------- :)

(: Several functions in the apidocs have duplicate parameter names in some versions, so fix these. :)
declare variable $param-fixes := map {
    "fn:subtract-dateTimes-yielding-dayTimeDuration": ("srcval1", "srcval2"),
    "fn:subtract-dateTimes-yielding-yearMonthDuration": ("srcval1", "srcval2"),
    "math:fmod": ("x", "y"),
    "math:rank": ("arg1", "arg2", "options")
};

declare variable $functions as element(apidoc:function)* := $apidoc//apidoc:function[
    local:function-language(.) = $language
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
    local:function-parameter($function, $name)/@type
};

declare function local:function-return-type($function as element(apidoc:function)) as xs:string {
    $function/apidoc:return
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

for $function in $functions
order by $function/@lib, $function/@name
return local:function-xquery-signatures($function)
