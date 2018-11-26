xquery version "3.0";
(:~
 : eXist-db atomic theme functions
 :
 : @see http://exist-db.org/exist/apps/fundocs/view.html?uri=http://atomic.exist-db.org/xquery/atomic/theme&location=/db/apps/wiki/modules/themes.xql
 :
 :)
module namespace theme ="http://atomic.exist-db.org/xquery/atomic/theme";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare %a:since("exist", "4.4") function theme:create-path($collection as xs:string) as item()* external;
declare %a:since("exist", "4.4") function theme:css($node as node(), $model as map(*)) as item()* external;
declare %a:since("exist", "4.4") function theme:locate($feed as xs:string) as xs:string external;
declare %a:since("exist", "4.4") function theme:locate($feed as xs:string, $resource as xs:string) as xs:string? external;
declare %a:since("exist", "4.4") function theme:parent-collection($collection as xs:string) as xs:string? external;
declare %a:since("exist", "4.4") function theme:resolve($collectionAbs as xs:string, $resource as xs:string, $root as xs:string, $controller as xs:string) as item()* external;
declare %a:since("exist", "4.4") function theme:resolve-relative($collectionRel as xs:string, $resource as xs:string, $root as xs:string, $controller as xs:string) as item()* external;
declare %a:since("exist", "4.4") function theme:theme-for-feed($feed as xs:string?) as xs:string? external;
declare %a:since("exist", "4.4") function theme:title($node as node(), $model as map(*)) as item()* external;