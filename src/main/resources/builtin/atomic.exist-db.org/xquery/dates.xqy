xquery version "3.0";
(:~
 : eXist-db dates function
 :
 : @see http://exist-db.org/exist/apps/fundocs/view.html?uri=http://atomic.exist-db.org/xquery/dates&location=/db/apps/wiki/modules/dates.xql
 :
 :)
module namespace dates ="http://atomic.exist-db.org/xquery/dates";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare %a:since("exist", "4.4") function dates:formatDate($dateTime as xs:dateTime) as item()* external;