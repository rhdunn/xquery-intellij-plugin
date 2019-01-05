(:
 : Copyright (C) 2018-2019 Reece H. Dunn
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
declare namespace o = "http://reecedunn.co.uk/xquery/options";
declare option o:implementation "marklogic/6.0";

(:~ Run a query on a MarkLogic server.
 :
 : @param $mode Whether to "run", "profile", or "debug" the query.
 : @param $mimetype The mimetype of the query.
 : @param $module-path The path of the module to invoke, or "" for evaluated scripts.
 : @param $query The query script to evaluate, or "" for invoked modules.
 : @param $vars A map of the variable values by the variable name.
 : @param $types A map of the variable types by the variable name.
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

declare variable $mode as xs:string external;
declare variable $mimetype as xs:string external;
declare variable $module-path as xs:string external;
declare variable $query as xs:string external;
declare variable $vars as xs:string external;
declare variable $types as xs:string external;

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

declare function local:error(
    $err:code as xs:QName,
    $err:description as xs:string?,
    $err:value as item()*,
    $err:module as xs:string?,
    $err:line-number as xs:integer?,
    $err:column-number as xs:integer,
    $err:additional as element(error:error)
) {
    <err:error xmlns:dbg="http://reecedunn.co.uk/xquery/debug">
        <err:code>{$err:code}</err:code>
        <err:vendor-code>{$err:additional/error:code/text()}</err:vendor-code>
        <err:description>{$err:additional/error:message/text()}</err:description>
        <err:value count="{count($err:value)}"></err:value>
        <err:module line="{$err:line-number}" column="{$err:column-number + 1}">{$err:module}</err:module>
        <dbg:stack>{
            for $frame in $err:additional/error:stack/error:frame[position() != last()]
            let $module := $frame/error:uri/text()
            let $line := $frame/error:line/text() cast as xs:integer?
            let $column := $frame/error:column/text() cast as xs:integer?
            return <dbg:frame><dbg:module line="{$line}" column="{$column + 1}">{$module}</dbg:module></dbg:frame>
        }</dbg:stack>
    </err:error>
};

declare function local:version() {
    substring-before(xdmp:version(), "-") cast as xs:double
};

declare function local:function($ref as xs:string) {
    try { xdmp:eval($ref) } catch * { () }
};

declare function local:javascript($variables, $options, $mode) as item()* {
    if (local:version() < 8.0) then
        fn:error(
            xs:QName("UNSUPPORTED-VERSION"),
            "MarkLogic " || xdmp:version() || " does not support server-side JavaScript"
        )
    else if (string-length($query) ne 0) then
        switch ($mode)
        case "run" return
            let $f := local:function("xdmp:javascript-eval#3")
            return $f($query, $variables, $options)
        default return ()
    else
        switch ($mode)
        case "run" return xdmp:invoke($module-path, $variables, $options)
        default return ()
};

declare function local:sql($variables, $options, $mode) as item()* {
    let $query :=
        if (string-length($query) ne 0) then
            $query
        else
            () (: TODO: Read the contents of the query file from the modules database. :)
    return switch ($mode)
    case "run" return
        if (local:version() < 9.0) then
            let $f := local:function("xdmp:sql#1")
            return $f($query)
        else
            let $f := local:function("xdmp:sql#3")
            return $f($query, (), $variables)
    default return ()
};

declare function local:xquery($variables, $options, $mode) as item()* {
    if (string-length($query) ne 0) then
        switch ($mode)
        case "run" return xdmp:eval($query, $variables, $options)
        case "profile" return prof:eval($query, $variables, $options)
        default return ()
    else
        switch ($mode)
        case "run" return xdmp:invoke($module-path, $variables, $options)
        case "profile" return prof:invoke($module-path, $variables, $options)
        default return ()
};

try {
    let $variables := local:parse-vars(xdmp:unquote($vars), xdmp:unquote($types))
    let $options := ()

    let $retvals :=
        switch ($mimetype)
        case "application/javascript" return local:javascript($variables, $options, $mode)
        case "application/sql" return local:sql($variables, $options, $mode)
        case "application/xquery" return local:xquery($variables, $options, $mode)
        default return ()

    for $retval at $i in $retvals
    let $_ := local:derived-type-name($retval) ! xdmp:add-response-header("X-Derived-" || $i, .)
    return $retval
} catch * {
    let $_ := xdmp:add-response-header("X-Derived-1", "err:error")
    return local:error($err:code, $err:description, $err:value, $err:module, $err:line-number, $err:column-number, $err:additional)
}
