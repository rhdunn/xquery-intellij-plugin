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

declare namespace exist = "http://exist.sourceforge.net/NS/exist";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare option o:implementation "exist-db/4.0";

(:~ Run a query on an eXist-db server.
 :
 : @param $username The database user to run the query as.
 : @param $password The password of the database user.
 : @param $query The query script to evaluate.
 : @param $vars A map of the variable values by the variable name.
 : @param $types A map of the variable types by the variable name.
 :
 : This script has additional logic to map the semantics of the REST API to
 : the semantics of the API implemented in the xquery-intellij-plugin to support
 : the Run/Debug Configurations of the IntelliJ IDEs. Specifically:
 :
 : 1. The user passed to the HTTP authentication is not logged in when the
 :    given query is run, due to one or more issues in the calling code.
 :
 : 2. When returning node types (element, text, etc.), those are included in
 :    the XML results as is.
 :
 : 3. Text and attribute nodes are included as their text content in the XML
 :    output, with adjacent text from the different nodes concatenated.
 :)

declare variable $username as xs:string external;
declare variable $password as xs:string external;
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
    case "xs:dateTimeStamp" return $value cast as xs:dateTime (: eXist-db does not support XML Schema 1.1 Part 2 :)
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

declare function local:parse-vars($values as map(*), $types as map(*)) as item()* {
    for $key in map:keys($values)
    let $value := local:cast-as(map:get($values, $key), map:get($types, $key))
    return (fn:QName("", $key), $value)
};

declare function local:serialize-type($value) {
    typeswitch ($value)
    case array(*) return "array(*)"
    case map(*) return "map(*)"
    case function(*) return "function(*)"
    (: nodes :)
    case attribute() return "attribute()"
    case comment() return "comment()"
    case document-node() return "document-node()"
    case element() return "element()"
    case namespace-node() return "namespace-node()"
    case processing-instruction() return "processing-instruction()"
    case text() return "text()"
    default return ()
};

declare function local:serialize-value($value) {
    typeswitch ($value)
    case attribute() | namespace-node() return $value/string()
    case array(*) | map(*) return fn:serialize($value, map { "method": "json" })
    case function(*) return fn:function-name($value) || "#" || fn:function-arity($value)
    default return fn:serialize($value)
};

declare function local:serialize($value) {
    let $type := local:serialize-type($value)
    return if (exists($type)) then
        <exist:value exist:type="{$type}">{local:serialize-value($value)}</exist:value>
    else
        $value
};

if (xmldb:login("", $username, $password, false())) then
    let $variables := local:parse-vars(fn:parse-json($vars), fn:parse-json($types))
    for $result in util:eval($query, false(), $variables)
    return local:serialize($result)
else
    response:set-status-code(403)
