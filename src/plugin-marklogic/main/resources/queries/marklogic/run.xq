(:
 : Copyright (C) 2018-2021, 2023 Reece H. Dunn
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
xquery version "1.0-ml";

import module namespace sem = "http://marklogic.com/semantics" at "/MarkLogic/semantics.xqy";

declare namespace o = "http://reecedunn.co.uk/xquery/options";
declare namespace eval = "xdmp:eval";

declare option o:implementation "marklogic/6.0";

(:~ Run a query on a MarkLogic server.
 :
 : @param $mode Whether to "run", "profile", or "debug" the query.
 : @param $mimetype The mimetype of the query.
 : @param $module-path The path of the module to invoke, or "" for evaluated scripts.
 : @param $query The query script to evaluate, or "" for invoked modules.
 : @param $vars A map of the variable values by the variable name.
 : @param $types A map of the variable types by the variable name.
 : @param $context-value The context item for XSLT queries as an XML object.
 : @param $context-path The context item for XSLT queries as a module file.
 : @param $rdf-output-format The mimetype to format sem:triple results as, or "" to leave them as is.
 : @param $updating An updating query if "true", or a non-updating query if "false".
 : @param $server The name of the server to use, or "" to not use a server.
 : @param $database The name of the database to use, or "" to not use a database.
 : @param $module-root The path to the server modules, or "" to use the default server value.
 :
 : This script has additional logic to map the semantics of the REST/XCC API to
 : the semantics of the API implemented in the xquery-intellij-plugin to support
 : the Run/Debug Configurations of the IntelliJ IDEs. Specifically:
 :
 : 1.  If there is an error, the REST API returns a "500 Internal Error" HTTP
 :     status. This catches the error and returns an element(err:error) object.
 :
 : 2.  The variables are passed via the REST API as xs:untypedAtomic. This
 :     converts the variables to the type they were specified as in the
 :     xquery-intellij-plugin API.
 :
 : 3.  The REST API only returns the primitive type of each item.
 :)

declare variable $mode as xs:string external := "run";
declare variable $mimetype as xs:string external := "application/xquery";
declare variable $module-path as xs:string external := "";
declare variable $query as xs:string external := "()";
declare variable $vars as xs:string external := "{}";
declare variable $types as xs:string external := "{}";
declare variable $context-value as xs:string external := "";
declare variable $context-path as xs:string external := "";
declare variable $rdf-output-format as xs:string external := "";
declare variable $updating as xs:string external := "false";
declare variable $server as xs:string external := "";
declare variable $database as xs:string external := "";
declare variable $module-root as xs:string external := "";

declare variable $content-type-header-prefix as xs:string external := "X-Content-Type-";
declare variable $derived-type-header-prefix as xs:string external := "X-Derived-";

declare function local:nullize($value) {
    if (string-length($value) eq 0) then () else $value
};

declare function local:version() {
    (: MarkLogic 10 and earlier use the MAJOR.0-MINOR.PATCH version scheme.
     : MarkLogic 11 and later use the MAJOR.MINOR.PATCH version scheme.
     :)
    let $version := tokenize(xdmp:version(), "[-.]")[1 to 2]
    return fn:string-join($version, ".") cast as xs:double
};

declare function local:version-minor() {
    (: MarkLogic 10 and earlier use the MAJOR.0-MINOR.PATCH version scheme.
     : MarkLogic 11 and later use the MAJOR.MINOR.PATCH version scheme.
     :)
    let $version := tokenize(xdmp:version(), "[-.]")[3 to 4]
    return fn:string-join($version, ".") cast as xs:double
};

declare function local:function($ref as xs:string) {
    try { xdmp:eval($ref) } catch * { () }
};

declare function local:server-coordinate-system($server as xs:unsignedLong?) {
    let $server-coordinate-system := local:function("xdmp:server-coordinate-system#1")
    return if (exists($server) and exists($server-coordinate-system)) then
        $server-coordinate-system($server)
    else
        ()
};

declare function local:item($path as xs:string, $value as xs:string, $type as xs:string) {
    if (string-length($value) ne 0) then
        xdmp:unquote($value)
    else if (string-length($path) ne 0) then
        doc($path)
    else
        ()
};

declare function local:cast-as($value, $type) {
    switch ($type)
    case "xs:anyURI" return $value cast as xs:anyURI
    case "xs:base64Binary" return $value cast as xs:base64Binary
    case "xs:boolean" return $value cast as xs:boolean
    case "xs:byte" return $value cast as xs:byte
    case "xs:date" return $value cast as xs:date
    case "xs:dateTime" return $value cast as xs:dateTime
    case "xs:dateTimeStamp" return $value cast as xs:dateTime (: MarkLogic does not support XML Schema 1.1 Part 2 :)
    case "xs:dayTimeDuration" return $value cast as xs:dayTimeDuration
    case "xs:decimal" return $value cast as xs:decimal
    case "xs:double" return $value cast as xs:double
    case "xs:duration" return $value cast as xs:duration
    case "xs:float" return $value cast as xs:float
    case "xs:gDay" return $value cast as xs:gDay
    case "xs:gMonth" return $value cast as xs:gMonth
    case "xs:gMonthDay" return $value cast as xs:gMonthDay
    case "xs:gYear" return $value cast as xs:gYear
    case "xs:gYearMonth" return $value cast as xs:gYearMonth
    case "xs:hexBinary" return $value cast as xs:hexBinary
    case "xs:int" return $value cast as xs:int
    case "xs:integer" return $value cast as xs:integer
    case "xs:language" return $value cast as xs:language
    case "xs:long" return $value cast as xs:long
    case "xs:negativeInteger" return $value cast as xs:negativeInteger
    case "xs:nonNegativeInteger" return $value cast as xs:nonNegativeInteger
    case "xs:nonPositiveInteger" return $value cast as xs:nonPositiveInteger
    case "xs:normalizedString" return $value cast as xs:normalizedString
    case "xs:numeric" return $value cast as xs:numeric
    case "xs:positiveInteger" return $value cast as xs:positiveInteger
    case "xs:short" return $value cast as xs:short
    case "xs:string" return $value cast as xs:string
    case "xs:time" return $value cast as xs:time
    case "xs:token" return $value cast as xs:token
    case "xs:unsignedByte" return $value cast as xs:unsignedByte
    case "xs:unsignedInt" return $value cast as xs:unsignedInt
    case "xs:unsignedLong" return $value cast as xs:unsignedLong
    case "xs:unsignedShort" return $value cast as xs:unsignedShort
    case "xs:yearMonthDuration" return $value cast as xs:yearMonthDuration
    case "xs:ENTITY" return $value cast as xs:ENTITY
    case "xs:ID" return $value cast as xs:ID
    case "xs:IDREF" return $value cast as xs:IDREF
    case "xs:Name" return $value cast as xs:Name
    case "xs:NCName" return $value cast as xs:NCName
    case "xs:NMTOKEN" return $value cast as xs:NMTOKEN
    case "xs:QName" return $value cast as xs:QName
    default return $value
};

declare function local:parse-vars($values as map:map, $types as map:map) as item()* {
    for $key in map:keys($values)
    let $value := local:cast-as(map:get($values, $key), map:get($types, $key))
    return (xs:QName($key), $value)
};

declare function local:parse-vars-map($values as map:map, $types as map:map) as map:map {
    let $ret := map:map()
    let $_ :=
        for $key in map:keys($values)
        let $value := local:cast-as(map:get($values, $key), map:get($types, $key))
        return map:put($ret, $key, $value)
    return $ret
};

declare function local:derived-type-name($value) {
    (: NOTE: Ordering is important here -- reverse type hierarchy :)
    typeswitch ($value)
    case function(*) return "function(*)"
    (: xs:integer derived types :)
    case xs:positiveInteger return "xs:positiveInteger"
    case xs:unsignedByte return "xs:unsignedByte"
    case xs:unsignedShort return "xs:unsignedShort"
    case xs:unsignedInt return "xs:unsignedInt"
    case xs:unsignedLong return "xs:unsignedLong"
    case xs:nonNegativeInteger return "xs:nonNegativeInteger"
    case xs:byte return "xs:byte"
    case xs:short return "xs:short"
    case xs:int return "xs:int"
    case xs:long return "xs:long"
    case xs:negativeInteger return "xs:negativeInteger"
    case xs:nonPositiveInteger return "xs:nonPositiveInteger"
    (: xs:string derived types :)
    case xs:ENTITY return "xs:ENTITY"
    case xs:IDREF return "xs:IDREF"
    case xs:ID return "xs:ID"
    case xs:NCName return "xs:NCName"
    case xs:Name return "xs:Name"
    case xs:NMTOKEN return "xs:NMTOKEN"
    case xs:language return "xs:language"
    case xs:token return "xs:token"
    case xs:normalizedString return "xs:normalizedString"
    default return () (: primitive type, so does not need the type name to be specified. :)
};

declare function local:rdf-format($mimetype) {
    switch ($mimetype)
    case "application/n-quads" return "nquad"
    case "application/n-triples" return "ntriple"
    case "application/rdf+json" return "rdfjson"
    case "application/rdf+xml" return "rdfxml"
    case "application/trig" return "trig"
    case "application/vnd.marklogic.triples+xml" return "triplexml"
    case "text/n3" return "n3"
    case "text/turtle" return "turtle"
    default return fn:error("UNSUPPORTED-RDF-FORMAT", "Unsupported RDF format: " || $mimetype)
};

declare function local:use-modules-root($server-root) {
    exists($server-root) and string-length($module-root) ne 0 and not($module-root eq $server-root)
};

declare function local:eval-static-check($static-check as xs:boolean) {
    if ($static-check eq true()) then
        <eval:static-check>true</eval:static-check>
    else
        ()
};

declare function local:eval-database($database as xs:unsignedLong?) {
    if (exists($database)) then
        <eval:database>{$database}</eval:database>
    else
        ()
};

declare function local:eval-default-collation($collation as xs:string?) {
    if (exists($collation)) then
        <eval:default-collation>{$collation}</eval:default-collation>
    else
        ()
};

declare function local:eval-default-coordinate-system($coordinate-system as xs:string?) {
    if (exists($coordinate-system)) then
        <eval:default-coordinate-system>{$coordinate-system}</eval:default-coordinate-system>
    else
        ()
};

declare function local:eval-default-xquery-version($xquery-version as xs:string?) {
    if (exists($xquery-version) and $mimetype eq "application/xquery") then
        <eval:default-xquery-version>{$xquery-version}</eval:default-xquery-version>
    else
        ()
};

declare function local:eval-modules($database as xs:unsignedLong?) {
    if (exists($database)) then
        <eval:modules>{$database}</eval:modules>
    else
        ()
};

declare function local:eval-root($root as xs:string?) {
    if (exists($root)) then
        <eval:root>{$root}</eval:root>
    else
        ()
};

declare function local:eval-update($update as xs:boolean) {
    let $major := local:version()
    let $minor := local:version-minor()
    return if ($mode = "debug") then
        if ($update) then
            fn:error(xs:QName("err:FOER0000"), "Debugging an update query is not supported by MarkLogic.")
        else
            ()
    else if ($major lt 7.0 or ($major eq 8.0 and $minor lt 7.0) or ($major eq 9.0 and $minor lt 2.0)) then
        if ($update) then
            <eval:transaction-mode>update</eval:transaction-mode>
        else
            <eval:transaction-mode>query</eval:transaction-mode>
    else
        (: MarkLogic 8.0-7 and 9.0-2 support <update> :)
        <eval:update>{$update}</eval:update>
};

declare function local:eval-options() {
    let $server := local:nullize($server) ! xdmp:server(.)
    let $database :=
        let $id := local:nullize($database) ! xdmp:database(.)
        return if (exists($id)) then
            $id
        else if (exists($server)) then
            xdmp:server-database($server)
        else
            ()
    let $server-root := $server ! xdmp:server-root(.)
    let $modules-database :=
        if (local:use-modules-root($server-root)) then
            0 (: file system :)
        else
            xdmp:server-modules-database($server)
    let $modules-root :=
        if ($modules-database eq 0) then
            if (exists($server-root) and (string-length($module-root) eq 0 or $module-root eq "/")) then
                $server-root
            else
                $module-root
        else
            $server-root
    return <eval:options>{
        local:eval-static-check($mode eq "validate"),
        local:eval-database($database),
        local:eval-default-collation($server ! xdmp:server-collation(.)),
        local:eval-default-coordinate-system($server ! local:server-coordinate-system(.)),
        local:eval-default-xquery-version($server ! xdmp:server-default-xquery-version($server)),
        local:eval-modules($modules-database),
        local:eval-root($modules-root),
        local:eval-update($updating eq "true")
    }</eval:options>
};

declare function local:javascript() as item()* {
    let $variables := local:parse-vars(xdmp:unquote($vars), xdmp:unquote($types))
    let $options := local:eval-options()
    return if (local:version() < 8.0) then
        fn:error(
            xs:QName("UNSUPPORTED-VERSION"),
            "MarkLogic " || xdmp:version() || " does not support server-side JavaScript"
        )
    else if (string-length($query) ne 0) then
        switch ($mode)
        case "run" return
            let $f := local:function("xdmp:javascript-eval#3")
            let $start := xdmp:elapsed-time()
            let $ret := $f($query, $variables, $options)
            return (xdmp:elapsed-time() - $start, $ret)
        default return ()
    else
        switch ($mode)
        case "run" return
            let $start := xdmp:elapsed-time()
            let $ret := xdmp:invoke($module-path, $variables, $options)
            return (xdmp:elapsed-time() - $start, $ret)
        default return ()
};

declare function local:sparql-query() as item()* {
    let $variables := local:parse-vars-map(xdmp:unquote($vars), xdmp:unquote($types))
    let $query :=
        if (string-length($query) ne 0) then
            $query
        else
            () (: TODO: Read the contents of the query file from the modules database. :)
    return switch ($mode)
    case "run" return
        let $start := xdmp:elapsed-time()
        let $ret := sem:sparql($query, $variables)
        return (xdmp:elapsed-time() - $start, $ret)
    default return ()
};

declare function local:sparql-update() as item()* {
    let $variables := local:parse-vars-map(xdmp:unquote($vars), xdmp:unquote($types))
    let $query :=
        if (string-length($query) ne 0) then
            $query
        else
            () (: TODO: Read the contents of the query file from the modules database. :)
    return if (local:version() < 8.0) then
        fn:error(
            xs:QName("UNSUPPORTED-VERSION"),
            "MarkLogic " || xdmp:version() || " does not support SPARQL update queries"
        )
    else switch ($mode)
    case "run" return
        let $start := xdmp:elapsed-time()
        let $ret := sem:sparql-update($query, $variables)
        return (xdmp:elapsed-time() - $start, $ret)
    default return ()
};

declare function local:sql() as item()* {
    let $variables := local:parse-vars(xdmp:unquote($vars), xdmp:unquote($types))
    let $query :=
        if (string-length($query) ne 0) then
            $query
        else
            () (: TODO: Read the contents of the query file from the modules database. :)
    return switch ($mode)
    case "run" return
        if (local:version() < 9.0) then
            let $f := local:function("xdmp:sql#1")
            let $start := xdmp:elapsed-time()
            let $ret := $f($query)
            return (xdmp:elapsed-time() - $start, $ret)
        else
            let $f := local:function("xdmp:sql#3")
            let $start := xdmp:elapsed-time()
            let $ret := $f($query, (), $variables)
            return (xdmp:elapsed-time() - $start, $ret)
    default return ()
};

declare function local:xquery() as item()* {
    let $variables := local:parse-vars(xdmp:unquote($vars), xdmp:unquote($types))
    let $options := local:eval-options()
    return if (string-length($query) ne 0) then
        switch ($mode)
        case "debug" return dbg:eval($query, $variables, $options)
        case "profile" return prof:eval($query, $variables, $options)
        case "run" return
            let $start := xdmp:elapsed-time()
            let $ret := xdmp:eval($query, $variables, $options)
            return (xdmp:elapsed-time() - $start, $ret)
        case "validate" return xdmp:eval($query, $variables, $options)
        default return ()
    else
        switch ($mode)
        case "debug" return dbg:invoke($module-path, $variables, $options)
        case "profile" return prof:invoke($module-path, $variables, $options)
        case "run" return
            let $start := xdmp:elapsed-time()
            let $ret := xdmp:invoke($module-path, $variables, $options)
            return (xdmp:elapsed-time() - $start, $ret)
        case "validate" return xdmp:invoke($module-path, $variables, $options)
        default return ()
};

declare function local:xslt() as item()* {
    let $variables := local:parse-vars(xdmp:unquote($vars), xdmp:unquote($types))
    let $input := local:item($context-path, $context-value, "application/xml")
    let $options := local:eval-options()
    return if (string-length($query) ne 0) then
        switch ($mode)
        case "profile" return prof:xslt-eval(xdmp:unquote($query), $input, $variables, $options)
        case "run" return
            let $query := xdmp:unquote($query)
            let $start := xdmp:elapsed-time()
            let $ret := xdmp:xslt-eval($query, $input, $variables, $options)
            return (xdmp:elapsed-time() - $start, $ret)
        case "validate" return xdmp:xslt-eval(xdmp:unquote($query), $input, $variables, $options)
        default return ()
    else
        switch ($mode)
        case "profile" return prof:xslt-invoke($module-path, $input, $variables, $options)
        case "run" return
            let $start := xdmp:elapsed-time()
            let $ret := xdmp:xslt-invoke($module-path, $input, $variables, $options)
            return (xdmp:elapsed-time() - $start, $ret)
        case "validate" return xdmp:xslt-invoke($module-path, $input, $variables, $options)
        default return ()
};

try {
    let $retvals :=
        switch ($mimetype)
        case "application/sparql-query" return local:sparql-query()
        case "application/sparql-update" return local:sparql-update()
        case "application/sql" return local:sql()
        case "application/vnd.marklogic-javascript" return local:javascript()
        case "application/xquery" return local:xquery()
        case "application/xslt+xml" return local:xslt()
        default return ()
    let $triples := for $item in $retvals where $item instance of sem:triple return $item
    let $retvals := for $item in $retvals where not($item instance of sem:triple) return $item
    return if (exists($triples) and $rdf-output-format ne "") then
        let $rdf-output := sem:rdf-serialize($triples, local:rdf-format($rdf-output-format))
        let $_ := xdmp:add-response-header($content-type-header-prefix || (count($retvals) + 1), $rdf-output-format)
        return (
            for $retval at $i in $retvals
            let $_ := local:derived-type-name($retval) ! xdmp:add-response-header($derived-type-header-prefix || $i, .)
            return $retval,
            $rdf-output
        )
    else
        for $retval at $i in ($retvals, $triples)
        let $_ := local:derived-type-name($retval) ! xdmp:add-response-header($derived-type-header-prefix || $i, .)
        return $retval
} catch * {
    let $_ := xdmp:add-response-header($derived-type-header-prefix || 1, "err:error")
    return $err:additional
}
