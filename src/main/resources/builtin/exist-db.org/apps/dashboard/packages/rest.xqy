xquery version "3.0";
(:~
 : eXist-db dashboard rest/package functions
 :
 : @see http://exist-db.org/exist/apps/fundocs/view.html?uri=http://exist-db.org/apps/dashboard/packages/rest&location=/db/apps/dashboard/plugins/packageManager/packages.xql&details=true
 :
 :)
module namespace packages ="http://exist-db.org/apps/dashboard/packages/rest";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare %a:since("exist", "4.4") function packages:get($type as xs:string?, $format as xs:string?, $plugins as xs:string?) as item()* external;