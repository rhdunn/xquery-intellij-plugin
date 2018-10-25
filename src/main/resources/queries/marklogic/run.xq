(:~ Run a query on a MarkLogic server.
 :
 : @param $query The query script to run.
 : @param $vars A map of the variable values by the variable name.
 : @param $types A map of the variable types by the variable name.
 :
 : This script has additional logic to map the semantics of the REST/XCC API to
 : the semantics of the API implemented in the xquery-intellij-plugin to support
 : the Run/Debug Configurations of the IntelliJ IDEs. Specifically:
 :
 : 1.  If there is an error, the REST API returns a "500 Internal Error" HTTP
 :     status. This catches the error and returns an element(error:error) object.
 :
 : 2.  The variables are passed via the REST API as xs:untypedAtomic. This
 :     converts the variables to the type they were specified as in the
 :     xquery-intellij-plugin API.
 :
 : 3.  The REST API only returns the primitive type of each item.
 :)
xquery version "1.0-ml";
declare namespace o = "http://reecedunn.co.uk/xquery/options";
declare option o:implementation "marklogic/6.0";

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

declare function local:parse-vars($values as map:map, $types as map:map) {
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

try {
    let $variables := local:parse-vars(xdmp:unquote($vars), xdmp:unquote($types))
    let $retvals := xdmp:eval($query, $variables, ())
    for $retval at $i in $retvals
    let $_ := local:derived-type-name($retval) ! xdmp:add-response-header("X-Derived-" || $i, .)
    return $retval
} catch ($e) {
    $e
}
