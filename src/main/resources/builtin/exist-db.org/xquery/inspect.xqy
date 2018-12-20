xquery version "3.0";
(:~
 : eXist-db inspection module functions
 :
 : @see http://exist-db.org/exist/apps/fundocs/view.html?uri=http://exist-db.org/xquery/inspection&location=java:org.exist.xquery.functions.inspect.InspectionModule&details=true
 :
 :)
module namespace inspect = "http://exist-db.org/xquery/inspect";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare %a:since("exist", "4.4") function inspect:inspect-function($function as function(*)) as node() external;
declare %a:since("exist", "4.4") function inspect:inspect-module($location as xs:anyURI) as element()? external;
declare %a:since("exist", "4.4") function inspect:inspect-module-uri($uri as xs:anyURI) as element()? external;
declare %a:since("exist", "4.4") function inspect:module-functions() as function(*)* external;
declare %a:since("exist", "4.4") function inspect:module-functions($location as xs:anyURI) as function(*)* external;
declare %a:since("exist", "4.4") function inspect:module-functions-by-uri($uri as xs:anyURI) as function(*)* external;