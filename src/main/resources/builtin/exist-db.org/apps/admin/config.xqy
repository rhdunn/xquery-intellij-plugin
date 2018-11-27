xquery version "3.0";
(:~
 : eXist-db admin configuration functions
 :
 : @see http://exist-db.org/exist/apps/fundocs/view.html?uri=http://exist-db.org/apps/admin/config&location=/db/apps/monex/modules/config.xqm&details=true
 :
 :)
module namespace config ="http://exist-db.org/apps/admin/config";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare %a:since("exist", "4.4") function config:app-info($node as node(), $model as map(*)) as item()* external;
declare %a:since("exist", "4.4") function config:app-meta($node as node(), $model as map(*)) as element()* external;
declare %a:since("exist", "4.4") function config:app-title($node as node(), $model as map(*)) as text() external;
declare %a:since("exist", "4.4") function config:expath-descriptor() as element() external;
declare %a:since("exist", "4.4") function config:repo-descriptor() as element() external;
declare %a:since("exist", "4.4") function config:resolve($relPath as xs:string) as item()* external;
