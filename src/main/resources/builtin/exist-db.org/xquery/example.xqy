xquery version "3.0";
(:~
 : eXist-db example module functions
 :
 : @see http://exist-db.org/exist/apps/fundocs/view.html?uri=http://exist-db.org/xquery/examples&location=java:org.exist.xquery.modules.example.ExampleModule&details=true
 :)
module namespace example ="http://exist-db.org/xquery/example";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare %a:since("exist", "4.4") function example:echo($text as xs:string*) as xs:string* external;