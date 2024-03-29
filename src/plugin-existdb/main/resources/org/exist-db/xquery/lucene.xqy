xquery version "3.0";
(:~
 : eXist-db Lucene module functions
 :
 : @see http://exist-db.org/exist/apps/fundocs/view.html?uri=http://exist-db.org/xquery/lucene&location=java:org.exist.xquery.modules.lucene.LuceneModule&details=true
 :)
module namespace ft = "http://exist-db.org/xquery/lucene";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare %a:since("exist", "4.4") function ft:close() as empty-sequence() external;
declare %a:since("exist", "5.1") function ft:facets($nodes as node()*, $dimension as xs:string) as map(*) external;
declare %a:since("exist", "5.1") function ft:facets($nodes as node()*, $dimension as xs:string, $count as xs:integer?) as map(*) external;
declare %a:since("exist", "5.1") function ft:facets($nodes as node()*, $dimension as xs:string, $count as xs:integer?, $paths as xs:string+) as map(*) external;
declare %a:since("exist", "5.1") function ft:field($node as node(), $field as xs:string) as xs:string* external;
declare %a:since("exist", "5.1") function ft:field($node as node(), $field as xs:string, $type as xs:string) as item()* external;
declare %a:since("exist", "4.4") function ft:get-field($path as xs:string*, $field as xs:string) as xs:string* external;
declare %a:since("exist", "4.4") function ft:has-index($path as xs:string) as xs:boolean* external;
declare %a:since("exist", "5.3") function ft:highlight-field-matches($node as node(), $field as xs:string) as element()? external;
declare %a:since("exist", "4.4") function ft:index($documentPath as xs:string, $solrExression as node()) as empty-sequence() external;
declare %a:since("exist", "4.4") function ft:index($documentPath as xs:string, $solrExression as node(), $close as xs:boolean) as empty-sequence() external;
declare %a:since("exist", "5.1") function ft:index-keys-for-field($field as xs:string, $start-value as xs:string?, $function-reference as function(*), $max-number-returned as xs:int?) as item()* external;
declare %a:since("exist", "4.4") function ft:optimize() as empty-sequence() external;
declare %a:since("exist", "4.4") function ft:query($nodes as node()*, $query as item()) as node()* external;
declare %a:since("exist", "4.4") function ft:query($nodes as node()*, $query as item(), $options as node()?) as node()* external;
declare %a:since("exist", "4.4") function ft:query-field($field as xs:string*, $query as item()) as node()* external;
declare %a:since("exist", "4.4") function ft:query-field($field as xs:string*, $query as item(), $options as node()?) as node()* external;
declare %a:since("exist", "4.4") function ft:remove-index($documentPath as xs:string) as empty-sequence() external;
declare %a:since("exist", "4.4") function ft:score($node as node()) as xs:float* external;
declare %a:since("exist", "4.4") function ft:search($path as xs:string*, $query as xs:string, $fields as xs:string*) as node() external;
declare %a:since("exist", "4.4") function ft:search($path as xs:string*, $query as xs:string) as node() external;
declare %a:since("exist", "4.4") function ft:search($query as xs:string) as node() external;
