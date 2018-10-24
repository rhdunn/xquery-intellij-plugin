(: Run a query on a MarkLogic server.
 :)
xquery version "1.0-ml";
declare namespace o = "http://reecedunn.co.uk/xquery/options";
declare option o:implementation "marklogic/6.0";

declare variable $query as xs:string external;

try {
    xdmp:eval($query, (), ())
} catch ($e) {
    (: Return the element(error:error) object, instead of a "500 Internal Error"
     : HTTP status message. :)
    $e
}
