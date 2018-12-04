xquery version "3.0";
(:~
 : eXist-db preconference app functions
 :
 : @see http://exist-db.org/exist/apps/fundocs/view.html?uri=http://exist-db.org/apps/preconferece/templates&location=/db/apps/preconference-2017/modules/app.xql&details=true
 :
 :)
module namespace app = "http://exist-db.org/apps/preconference/templates";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare %a:since("exist", "4.4") function app:presentations($node as node(), $model as map(*)) as item()* external;
declare %a:since("exist", "4.4") function app:test($node as node(), $model as map(*)) as item()* external;