xquery version "3.0";
(:~
 : eXist-db sort index module functions
 :
 : @see http://exist-db.org/exist/apps/fundocs/view.html?uri=http://exist-db.org/xquery/sort&location=java:org.exist.xquery.modules.sort.SortModule&details=true
 :
 :)
module namespace sort = "http://exist-db.org/xquery/sort";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare %a:since("exist", "4.4") function sort:create-index($id as xs:string, $nodes as node()*, $values as xs:anyAtomicType*, $options as element()?) as item()* external;
declare %a:since("exist", "4.4") function sort:create-index-callback($id as xs:string, $nodes as node()*, $callback as function(*), $options as element()?) as item()* external;
declare %a:since("exist", "4.4") function sort:has-index($id as xs:string) as xs:boolean external;
declare %a:since("exist", "4.4") function sort:index($id as xs:string, $node as node()?) as xs:long? external;
declare %a:since("exist", "4.4") function sort:remove-index($id as xs:string) as item()* external;
declare %a:since("exist", "4.4") function sort:remove-index($id as xs:string, $document-node as node()) as item()* external;