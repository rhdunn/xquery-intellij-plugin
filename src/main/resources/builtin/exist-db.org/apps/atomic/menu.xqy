xquery version "3.0";
(:~
 : eXist-db atomic menu functions
 :
 : @see http://exist-db.org/exist/apps/fundocs/view.html?uri=http://exist-db.org/apps/atomic/menu&location=/db/apps/wiki/modules/menu.xql&details=true
 :
 :)
module namespace menu ="http://exist-db.org/apps/atomic/menu";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare %a:since("exist", "4.4") function menu:find-nav($collection as xs:string?, $recursive as xs:boolean) as item()* external;
declare %a:since("exist", "4.4") function menu:relativize-links($node as node(), $path as xs:string) as item()* external;
declare %a:since("exist", "4.4") function menu:site-menu($node as node(), $model as map(*)) as item()* external;
declare %a:since("exist", "4.4") function menu:site-menu-for-feed($feed as node()?, $recursive as xs:boolean) as item()* external;