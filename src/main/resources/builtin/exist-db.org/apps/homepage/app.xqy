xquery version "3.0";
(:~
 : eXist-db homepage app functions
 :
 : @see http://exist-db.org/exist/apps/fundocs/view.html?uri=http://exist-db.org/apps/homepage/app&location=/db/apps/homepage/modules/app.xql&details=true
 :
 :)
module namespace app ="http://exist-db.org/apps/homepage/app";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare %a:since("exist", "4.4") function app:github-account-verified($node as node(), $model as map(*)) as item()* external;