xquery version "3.0";
(:~
 : eXist-db extension functions
 :
 : @see http://exist-db.org/exist/apps/fundocs/view.html?uri=http://atomic.exist-db.org/xquery/extensions&location=/db/apps/wiki/modules/extensions.xql&details=true
 :
 :)
module namespace ext ="http://atomic.exist-db.org/xquery/extensions";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare %a:since("exist", "4.4") function ext:code($node as node(), $model as map(*), $lang as xs:string?, $edit as xs:string, $action as xs:string?) as item()* external;
declare %a:since("exist", "4.4") function ext:macro($node as node(), $model as map(*)) as item()* external;
declare %a:since("exist", "4.4") function ext:script($node as node(), $model as map(*)) as item()* external;