xquery version "3.0";
(:~
 : eXist-db external process module functions
 :
 : @see http://exist-db.org/exist/apps/fundocs/view.html?uri=http://exist-db.org/xquery/process&location=java:org.exist.xquery.modules.process.ProcessModule&details=true
 :)
module namespace process = "http://exist-db.org/xquery/process";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare %a:since("exist", "4.4") function process:execute($args as xs:string+, $options as element()?) as element() external;