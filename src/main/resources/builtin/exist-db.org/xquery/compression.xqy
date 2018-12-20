xquery version "3.0";
(:~
 : eXist-db compression module functions
 :
 : @see http://exist-db.org/exist/apps/fundocs/view.html?uri=http://exist-db.org/xquery/compression&location=java:org.exist.xquery.modules.compression.CompressionModule&details=true
 :
 :)
module namespace compression ="http://exist-db.org/xquery/compression";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare %a:since("exist", "4.4") function compression:gzip($data as xs:base64Binary) as xs:base64Binary? external;
declare %a:since("exist", "4.4") function compression:tar($sources as xs:anyType+, $use-collection-hierarchy as xs:boolean) as xs:base64Binary* external;
declare %a:since("exist", "4.4") function compression:tar($sources as xs:anyType+, $use-collection-hierarchy as xs:boolean, $strip-prefix as xs:string) as xs:base64Binary* external;
declare %a:since("exist", "4.4") function compression:tar($sources as xs:anyType+, $use-collection-hierarchy as xs:boolean, $strip-prefix as xs:string, $encoding as xs:string) as xs:base64Binary* external;
declare %a:since("exist", "4.4") function compression:ungzip($gzip-data as xs:base64Binary) as xs:base64Binary? external;
declare %a:since("exist", "4.4") function compression:untar($tar-data as xs:base64Binary, $entry-filter as function(*), $entry-filter-param as xs:anyType*, $entry-data as function(*), $entry-data-param as xs:anyType*) as item()* external;
declare %a:since("exist", "4.4") function compression:untar($tar-data as xs:base64Binary, $entry-filter as function(*), $entry-filter-param as xs:anyType*, $entry-data as function(*), $entry-data-param as xs:anyType*, $encoding as xs:string) as item()* external;
declare %a:since("exist", "4.4") function compression:unzip($zip-data as xs:base64Binary, $entry-filter as function(*), $entry-filter-param as xs:anyType*, $entry-data as function(*), $entry-data-param as xs:anyType*) as item()* external;
declare %a:since("exist", "4.4") function compression:unzip($zip-data as xs:base64Binary, $entry-filter as function(*), $entry-filter-param as xs:anyType*, $entry-data as function(*), $entry-data-param as xs:anyType*, $encoding as xs:string) as item()* external;
declare %a:since("exist", "4.4") function compression:zip($sources as xs:anyType+, $use-collection-hierarchy as xs:boolean) as xs:base64Binary* external;
declare %a:since("exist", "4.4") function compression:zip($sources as xs:anyType+, $use-collection-hierarchy as xs:boolean, $strip-prefix as xs:string) as xs:base64Binary* external;
declare %a:since("exist", "4.4") function compression:zip($sources as xs:anyType+, $use-collection-hierarchy as xs:boolean, $strip-prefix as xs:string, $encoding as xs:string) as xs:base64Binary* external;