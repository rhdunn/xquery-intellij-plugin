xquery version "3.0";
(:~
 : eXist-db XQDoc operation function
 :
 : @see http://exist-db.org/exist/apps/fundocs/view.html?uri=http://exist-db.org/xquery/xqdoc&location=java:org.exist.xqdoc.xquery.XQDocModule&details=true
 :
 :)
module namespace xqdm = "http://exist-db.org/xquery/xqdoc";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare %a:since("exist", "4.4") function xqdm:scan($uri as xs:anyURI) as node()* external;
declare %a:since("exist", "4.4") function xqdm:scan($data as xs:base64Binary, $name as xs:string) as node()* external;