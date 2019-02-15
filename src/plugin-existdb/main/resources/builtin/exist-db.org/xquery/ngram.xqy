xquery version "3.0";
(:~
 : eXist-db NGram module functions
 :
 : @see http://exist-db.org/exist/apps/fundocs/view.html?uri=http://exist-db.org/xquery/ngram&location=java:org.exist.xquery.modules.ngram.NGramModule&details=true
 :)
module namespace ngram = "http://exist-db.org/xquery/ngram";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare %a:since("exist", "4.4") function ngram:add-match($node-set as node()?) as node()* external;
declare %a:since("exist", "4.4") function ngram:contains($nodes as node()*, $queryString as xs:string?) as node()* external;
declare %a:since("exist", "4.4") function ngram:ends-with($nodes as node()*, $queryString as xs:string?) as node()* external;
declare %a:since("exist", "4.4") function ngram:filter-matches($nodes as node()*, $function-reference as function(*)) as node()* external;
declare %a:since("exist", "4.4") function ngram:starts-with($nodes as node()*, $queryString as xs:string?) as node()* external;
declare %a:since("exist", "4.4") function ngram:wildcard-contains($nodes as node()*, $queryString as xs:string?) as node()* external;