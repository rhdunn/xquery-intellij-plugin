xquery version "3.0";
(:~
 : eXist-db counter module functions
 :
 : @see http://exist-db.org/exist/apps/fundocs/view.html?uri=http://exist-db.org/xquery/counter&location=java:org.exist.xquery.modules.counter.CounterModule&details=true
 :
 :)
module namespace counter ="http://exist-db.org/xquery/counter";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare %a:since("exist", "4.4") function counter:create($counter-name as item()) as xs:long? external;
declare %a:since("exist", "4.4") function counter:create($counter-name as item(), $init-value as xs:long) as xs:long? external;
declare %a:since("exist", "4.4") function counter:destroy($counter-name as item()) as xs:boolean? external;
declare %a:since("exist", "4.4") function counter:next-value($counter-name as item()) as xs:long? external;