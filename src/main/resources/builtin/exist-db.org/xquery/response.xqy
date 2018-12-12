xquery version "3.0";
(:~
 : eXist-db HTTP response module functions
 :
 : @see http://exist-db.org/exist/apps/fundocs/view.html?uri=http://exist-db.org/xquery/response&location=java:org.exist.xquery.functions.response.ResponseModule&details=true
 :
 :)
module namespace response = "http://exist-db.org/xquery/response";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare %a:since("exist", "4.4") function response:exists() as xs:boolean external;
declare %a:since("exist", "4.4") function response:redirect-to($uri as xs:anyURI) as item() external;
declare %a:since("exist", "4.4") function response:set-cookie($name as xs:string, $value as xs:string) as item() external;
declare %a:since("exist", "4.4") function response:set-cookie($name as xs:string, $value as xs:string, $max-age as xs:duration?, $secure-flag as xs:boolean?) as item() external;
declare %a:since("exist", "4.4") function response:set-cookie($name as xs:string, $value as xs:string, $max-age as xs:duration?, $secure-flag as xs:boolean?, $domain as xs:string?, $path as xs:string?) as item() external;
declare %a:since("exist", "4.4") function response:set-date-header($name as xs:string, $value as xs:string) as item() external;
declare %a:since("exist", "4.4") function response:set-header($name as xs:string, $value as xs:string) as item() external;
declare %a:since("exist", "4.4") function response:set-status-code($code as xs:integer) as item() external;
declare %a:since("exist", "4.4") function response:stream($content as item()*, $serialization-options as xs:string) as item() external;
declare %a:since("exist", "4.4") function response:stream-binary($binary-data as xs:base64Binary, $content-type as xs:string, $filename as xs:string?) as empty-sequence() external;