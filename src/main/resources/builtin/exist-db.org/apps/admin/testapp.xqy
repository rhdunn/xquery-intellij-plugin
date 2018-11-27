xquery version "3.0";
(:~
 : eXist-db admin test app functions
 :
 : @see http://exist-db.org/exist/apps/fundocs/view.html?uri=http://exist-db.org/apps/admin/testapp&location=/db/apps/monex/modules/test-app.xql&details=true
 :
 :)
module namespace testapp ="http://exist-db.org/apps/admin/testapp";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare %a:since("exist", "4.4") function testapp:millis-to-time($millis as xs:decimal) as xs:dateTime external;
declare %a:since("exist", "4.4") function testapp:time-to-millis($dateTime as xs:dateTime) as xs:long external;