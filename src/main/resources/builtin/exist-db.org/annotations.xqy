xquery version "3.0";
(:~
 : eXist-db annotations functions
 :
 : @see http://exist-db.org/exist/apps/fundocs/view.html?uri=http://exist-db.org/annotations&location=/db/apps/wiki/modules/annotations.xql&details=true
 :
 :)
module namespace anno ="http://exist-db.org/annotations";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare %a:since("exist", "4.4") function anno:create-collection() as item()* external;
declare %a:since("exist", "4.4") function anno:list($target as xs:string, $top as xs:integer?, $left as xs:integer?, $bottom as xs:integer?, $right as xs:integer?) as item()* external;
declare %a:since("exist", "4.4") function anno:parse-body($body as xs:string) as element() external;
declare %a:since("exist", "4.4") function anno:retrieve($id as xs:string) as item()* external;
declare %a:since("exist", "4.4") function anno:store($target as xs:string?, $body as xs:string, $start as xs:string?, $startOffset as xs:integer?, $end as xs:string?, $endOffset as xs:integer?, $top as xs:integer?, $left as xs:integer?, $bottom as xs:integer?, $right as xs:integer?) as item()* external;
declare %a:since("exist", "4.4") function anno:update($id as xs:string, $body as xs:string) as item()* external;