xquery version "3.0";
(:~
 : eXist-db atomic search functions
 :
 : @see http://exist-db.org/exist/apps/fundocs/view.html?uri=http://atomic.exist-db.org/xquery/atomic/search&location=/db/apps/wiki/modules/search.xql&details=true
 :
 :)
module namespace search ="http://atomic.exist-db.org/xquery/atomic/search";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare %a:since("exist", "4.4") function search:do-query($context as node()*, $query as xs:string, $field as xs:string) as item()* external;
declare %a:since("exist", "4.4") function search:entry-id($node as node()) as item()* external;
declare %a:since("exist", "4.4") function search:get-entry($node as node(), $model as map(*)) as item()* external;
declare %a:since("exist", "4.4") function search:kwic($node as node(), $model as map(*)) as item()* external;
declare %a:since("exist", "4.4") function search:query($node as node()*, $model as map(*), $q as xs:string?, $field as xs:string, $view as xs:string) as item()* external;
declare %a:since("exist", "4.4") function search:result-count($node as node(), $model as map(*)) as item()* external;
declare %a:since("exist", "4.4") function search:results($node as node(), $model as map(*)) as item()* external;
declare %a:since("exist", "4.4") function search:title($node as node(), $model as map(*)) as item()* external;