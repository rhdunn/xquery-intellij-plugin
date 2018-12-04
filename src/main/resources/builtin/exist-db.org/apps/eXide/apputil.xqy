xquery version "3.0";
(:~
 : eXist-db eXide functions
 :
 : @see http://exist-db.org/exist/apps/fundocs/view.html?uri=http://exist-db.org/apps/eXide/apputil&location=/db/apps/eXide/modules/util.xql&details=true
 :
 :)
module namespace apputil ="http://exist-db.org/apps/eXide/apputil";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare %a:since("exist", "4.4") function apputil:get-app-root($collection as xs:string) as item()* external;
declare %a:since("exist", "4.4") function apputil:get-info($collection as xs:string) as item()* external;
declare %a:since("exist", "4.4") function apputil:get-info-from-descriptor($collection as xs:string) as item()* external;