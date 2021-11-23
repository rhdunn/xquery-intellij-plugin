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

declare variable $marklogic-version as xs:string external;

declare variable $language as xs:string external := "xquery"; (: xquery | javascript | rest :)
declare variable $namespace as xs:string external;
declare variable $local-name as xs:string external;

declare variable $apidoc as element(apidoc:apidoc) :=
    typeswitch (.)
    case xs:string return fn:parse-xml(.)/element()
    case document-node() return ./element()
    default return .;

(: documentation helpers --------------------------------------------------- :)

declare function local:apidoc-language($doc as element()) as xs:string {
    ($doc/@class, "xquery")[1]
};

declare function local:prefix($namespaces as map(*), $namespace as xs:string) as xs:string? {
    for $prefix in map:keys($namespaces)
    where map:get($namespaces, $prefix) = $namespace
    return $prefix
};

declare function local:documentation-html($doc as node()) as node()* {
    if ($doc/namespace-uri() = "http://marklogic.com/xdmp/apidoc") then
        let $first :=
            let $node := $doc/node()[position() = 1]
            return if ($node instance of text() and string-length($node/normalize-space()) != 0) then
                ()
            else
                $node
        return if (exists($first) and (some $child in $doc/node() satisfies $child instance of element())) then
            if (local:apidoc-language($doc) = $language) then
                for $child in $doc/node()
                return local:documentation-html($child)
            else ()
        else
            <p>{$doc/node()}</p>
    else if ($doc instance of text()) then
        let $text := $doc/normalize-space()
        return if ($text != "") then
            $doc
        else ()
    else if ($doc instance of element() and local:apidoc-language($doc) = $language) then
        element { $doc/local-name() } {
            for $child in $doc/node()
            return local:documentation-html($child)
        }
    else ()
};

(: namespaces -------------------------------------------------------------- :)

declare variable $module-namespaces := map:merge(
    for $import in $apidoc//apidoc:module/apidoc:summary//(*:p|*:pre)
    let $match := fn:analyze-string(($import/*:code, $import)[1], "import module (namespace\s*([a-z0-9]+)\s*=\s*)?""([^""]+)""")/fn:match[1]
    let $prefix := $import/ancestor::apidoc:module/@lib
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

declare variable $marklogic-6-builtin := map {
    "err": "http://www.w3.org/2005/xqt-error"
};

declare variable $marklogic-7-builtin := map {
    "rdf": "http://www.w3.org/1999/02/22-rdf-syntax-ns#",
    "sc": "http://marklogic.com/xdmp/schema-components",
    "sql": "http://marklogic.com/xdmp/sql"
};

declare variable $marklogic-10-builtin := map {
    "cntk": "http://marklogic.com/cntk",
    "ort": "http://marklogic.com/onnxruntime"
};

declare variable $marklogic-additional := map {
    "as": "http://marklogic.com/xdmp/assignments",
    "db": "http://marklogic.com/xdmp/database",
    "dg": "xdmp:document-get",
    "cl": "http://marklogic.com/xdmp/clusters",
    "exsl": "http://exslt.org/common",
    "extusr": "http://marklogic.com/xdmp/external/user",
    "gr": "http://marklogic.com/xdmp/group",
    "lang": "http://marklogic.com/xdmp/language",
    "list": "http://marklogic.com/manage/package/list",
    "md": "urn:oasis:names:tc:SAML:2.0:metadata",
    "mt": "http://marklogic.com/xdmp/mimetypes",
    "pkg": "http://marklogic.com/manage/package",
    "s": "http://www.w3.org/2009/xpath-functions/analyze-string",
    "sch": "http://purl.oclc.org/dsdl/schematron",
    "x509": "http://marklogic.com/xdmp/x509",
    "xdt": "http://www.w3.org/2004/10/xpath-datatypes",
    "xi": "http://www.w3.org/2001/XInclude"
};

declare variable $namespaces := map:merge((
    $module-namespaces,
    $xquery-namespaces,
    $marklogic-5-builtin,
    $marklogic-6-builtin,
    $marklogic-7-builtin,
    $marklogic-10-builtin,
    $marklogic-additional,
    ()
));

(: functions --------------------------------------------------------------- :)

(: Several functions in the apidocs have duplicate parameter names in some versions, so fix these. :)
declare variable $param-fixes := map {
    "fn:subtract-dateTimes-yielding-dayTimeDuration": ("srcval1", "srcval2"),
    "fn:subtract-dateTimes-yielding-yearMonthDuration": ("srcval1", "srcval2"),
    "geo:region-contains": ("target", "region", "options"),
    "math:fmod": ("x", "y"),
    "math:rank": ("arg1", "arg2", "options")
};

declare variable $functions as element(apidoc:function)* := $apidoc//apidoc:function[
    local:function-language(.) = $language and not(@name = "")
];

declare function local:function-uri($function as element(apidoc:function)) as xs:string {
    ``[http://docs.marklogic.com/`{$marklogic-version}`/`{local:function-name($function)}`]``
};

declare function local:function-language($function as element(apidoc:function)) as xs:string {
    let $bucket := $function/@bucket
    return switch ($bucket)
    case "REST Resources API" return "rest"
    default return local:apidoc-language($function)
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
            $function/apidoc:params/apidoc:param[local:apidoc-language(.) = $language]/@name/string()
    return if (count($names) != count(distinct-values($names))) then
        fn:error(xs:QName("err:XQST0039"),``[Duplicate parameter name in `{local:function-name($function)}`]``)
    else
        $names
};

declare function local:function-arity($function as element(apidoc:function)) as map(*) {
    let $params := $function/apidoc:params/apidoc:param[local:apidoc-language(.) = $language]
    return map {
        "min": count($params[not(@optional = "true")]),
        "max": count($params)
    }
};

declare function local:function-parameter($function as element(apidoc:function), $name as xs:string) as element(apidoc:param) {
    let $names := map:get($param-fixes, local:function-name($function))
    return if (exists($names)) then
        let $i := fn:index-of($names, $name)
        return $function/apidoc:params/apidoc:param[$i]
    else
        $function/apidoc:params/apidoc:param[@name = $name and local:apidoc-language(.) = $language]
};

declare function local:function-parameter-type($function as element(apidoc:function), $name as xs:string) as xs:string {
    let $type := local:function-parameter($function, $name)/@type/string()
    return switch ($type)
    case "atomic type" return "item()*"
    case "xs:string)" return "xs:string"
    case "xs:dateTime? | xs:date? | xs:string?" return "(xs:dateTime?|xs:date?|xs:string?)"
    case "binary())" return "binary()"
    case "element(sec:query-rolesets)*)" return "element(sec:query-rolesets)*"
    case "function()" return "function(*)"
    case "item()* | map:map" return "(item()*|map:map)"
    case "(binary()|node())*" return "(binary()*|node()*)"
    case "(binary()|node())?" return "(binary()?|node()?)"
    case "(element()|map:map)?" case "element()?|map:map?" return "(element()?|map:map?)"
    case "(node()? | map:map)?" return "(node()?|map:map?)"
    case "(cts:order|xs:string)*" return "(cts:order*|xs:string*)"
    case "[xs:string?]" return "xs:string?"
    default return replace($type, ",\.\.\.", "...")
};

declare function local:function-return-type($function as element(apidoc:function)) as xs:string {
    let $type := normalize-space($function/apidoc:return[local:apidoc-language(.) = $language])
    return switch ($type)
    case "An XML representation of the certificate." return "element()?"
    case "element())" case "Element()" return "element()"
    case "element()|map:map" return "(element()|map:map)"
    case "element(xsd:schema)*" return "element(xs:schema)*"
    case "empty-sequence" case "empty sequence" return "empty-sequence()"
    case "xs:string)" return "xs:string"
    case "xs:unsignedLong)" return "xs:unsignedLong"
    case "pkgins:install($pkgname)" return "element(pkg:install-status)"
    case "pkgins:revert($ticket)" return "element(pkg:revert-status)"
    case "query-results-serialize($results)" return "item()*"
    case "The return data type is the data type of the date argument" return "item()"
    case "" return
        switch (local:function-name($function))
        case "admin:appserver-set-output-indent-tabs" return "element(configuration)"
        default return ""
    default return $type
};

declare function local:function-xquery-signatures($function as element(apidoc:function)) as xs:string* {
    let $name := local:function-name($function)
    let $params :=
        for $name in local:function-parameters($function)
        let $type := local:function-parameter-type($function, $name)
        return ``[$`{$name}` as `{$type}`]``
    let $type := local:function-return-type($function)
    let $arity-range := local:function-arity($function)
    for $arity in $arity-range?min to $arity-range?max
    let $params := $params[position() = 1 to $arity]
    return if ($type = "") then
        ``[declare function `{$name}`(`{string-join($params, ", ")}`) external;]``
    else
        ``[declare function `{$name}`(`{string-join($params, ", ")}`) as `{$type}` external;]``
};

declare function local:function-html-signatures($function as element(apidoc:function)) as element(div)+ {
    let $name := local:function-name($function)
    let $params :=
        for $name in local:function-parameters($function)
        let $type := local:function-parameter-type($function, $name)
        return <div>
            <code class="arg">${$name}</code>,
            <code class="as">&#xA0;as&#xA0;</code>,
            <code class="type">{$type}</code>
        </div>
    let $type := local:function-return-type($function)
    let $arity-range := local:function-arity($function)
    for $arity in $arity-range?min to $arity-range?max
    let $params := $params[position() = 1 to $arity]
    return if (count($params) <= 2) then
        <div class="proto">
            <code class="function">{$name}</code>({
                for $param at $i in $params
                return (
                    if ($i > 1) then ", " else (),
                    $param/*
                )
            }){
                if ($type != "") then (
                    <code class="as">&#xA0;as&#xA0;</code>,
                    <code class="return-type">{$type}</code>
                ) else ()
            }
        </div>
    else
        <div class="proto">
            <table class="proto">{
                (: JEditorPanel does not support vertical-align on tr/td elements, so use valign instead. :)
                <tr>
                    <td valign="top" rowspan="{count($params)}"><code class="function">{$name}</code>(</td>
                    <td valign="top">{$params[1]/code[@class="arg"]}</td>
                    <td valign="top">{$params[1]/code[not(@class="arg")]},</td>
                </tr>,
                for $param at $i in $params[position() > 1]
                return
                    <tr>
                        <td valign="top">{$param/code[@class="arg"]}</td>
                        <td valign="top">{
                            $param/code[not(@class="arg")],
                            if ($i = count($params) - 1) then ")" else ","
                        }</td>
                    </tr>
            }</table>
        </div>
};

declare function local:function-html-parameters($function as element(apidoc:function)) as element(table)? {
    let $params := local:function-parameters($function)
    return if (exists($params)) then
        <table class="parameters">{
            for $name in local:function-parameters($function)
            let $type := local:function-parameter-type($function, $name)
            let $summary := local:function-parameter($function, $name)
            return
                <tr valign="top">
                    <td width="20%">${$name}</td>
                    <td width="80%">{
                        for $child in $summary/node()
                        return local:documentation-html($child)
                    }</td>
                </tr>
        }</table>
    else ()
};

(: query ------------------------------------------------------------------- :)

let $prefix := local:prefix($namespaces, $namespace)
for $function in $functions
where $function/@lib = $prefix and $function/@name = $local-name
return (
    local:function-uri($function),
    fn:serialize(local:documentation-html($function/apidoc:summary[local:apidoc-language(.) = $language])),
    string-join(local:function-html-signatures($function) ! fn:serialize(.), ""),
    fn:serialize(local:function-html-parameters($function)),
    fn:serialize($function/apidoc:privilege ! local:documentation-html(.)),
    fn:serialize($function/apidoc:usage ! local:documentation-html(.)),
    fn:serialize($function/apidoc:example ! local:documentation-html(.)),
    ()
)
