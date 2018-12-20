xquery version "3.0";
(:~
 : eXist-db content extraction module functions
 :
 : @see http://exist-db.org/exist/apps/fundocs/view.html?uri=http://exist-db.org/xquery/contentextraction&location=java:org.exist.contentextraction.xquery.ContentExtractionModule&details=true
 :)
module namespace contentextraction ="http://exist-db.org/xquery/contentextraction";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare %a:since("exist", "4.4") function contentextraction:get-metadata($binary as xs:base64Binary) as document-node() external;
declare %a:since("exist", "4.4") function contentextraction:get-metadata-and-content($binary as xs:base64Binary) as document-node() external;
declare %a:since("exist", "4.4") function contentextraction:stream-content($binary as xs:base64Binary, $paths as xs:string*, $callback as function(*), $namespaces as element()?, $userData as item()*) as empty-sequence() external;