xquery version "3.0";
(:~
 : eXist-db image module functions
 :
 : @see http://exist-db.org/exist/apps/fundocs/view.html?uri=http://exist-db.org/xquery/image&location=java:org.exist.xquery.modules.image.ImageModule&details=true
 :
 :)
module namespace image = "http://exist-db.org/xquery/image";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare %a:since("exist", "4.4") function image:crop($image as xs:base64Binary, $dimension as xs:integer*, $mimeType as xs:string) as xs:base64Binary? external;
declare %a:since("exist", "4.4") function image:get-height($image as xs:base64Binary) as xs:integer? external;
declare %a:since("exist", "4.4") function image:get-metadata($image as xs:base64Binary, $native-format as xs:boolean) as node()? external;
declare %a:since("exist", "4.4") function image:get-width($image as xs:base64Binary) as xs:integer? external;
declare %a:since("exist", "4.4") function image:scale($image as xs:base64Binary, $dimension as xs:integer*, $mimeType as xs:string) as xs:base64Binary? external;
declare %a:since("exist", "4.4") function image:thumbnail($collection as xs:anyURI, $thumbnail-location as xs:anyURI?, $dimension as xs:integer*, $prefix as xs:string?) as xs:string* external;