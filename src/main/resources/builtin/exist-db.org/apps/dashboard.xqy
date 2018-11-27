xquery version "3.0";
(:~
 : eXist-db dashboard functions
 :
 : @see http://exist-db.org/exist/apps/fundocs/view.html?uri=http://exist-db.org/apps/dashboard&location=/db/apps/dashboard/modules/dashboard.xql&details=true
 :
 :)
module namespace dash ="http://exist-db.org/apps/dashboard";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare %a:since("exist", "4.4") function dash:list-apps($node as node(), $model as map(*)) as item()* external;
declare %a:since("exist", "4.4") function dash:user($node as node(), $model as map(*)) as item()* external;