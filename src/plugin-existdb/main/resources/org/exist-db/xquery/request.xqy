xquery version "3.0";
(:~
 : eXist-db HTTP request module functions
 :
 : @see http://exist-db.org/exist/apps/fundocs/view.html?uri=http://exist-db.org/xquery/request&location=java:org.exist.xquery.functions.request.RequestModule&details=true
 :)
module namespace request = "http://exist-db.org/xquery/request";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare %a:since("exist", "4.4") function request:attribute-names() as xs:string* external;
declare %a:since("exist", "4.4") function request:exists() as xs:boolean external;
declare %a:since("exist", "4.4") function request:get-attribute($attribute-name as xs:string) as item()* external;
declare %a:since("exist", "4.4") function request:get-context-path() as xs:string external;
declare %a:since("exist", "4.4") function request:get-cookie-names() as xs:string* external;
declare %a:since("exist", "4.4") function request:get-cookie-value($cookie-name as xs:string) as xs:string? external;
declare %a:since("exist", "4.4") function request:get-data() as item()? external;
declare %a:since("exist", "4.4") function request:get-effective-uri() as xs:anyURI external;
declare %a:since("exist", "4.4") function request:get-header($header-name as xs:string) as xs:string? external;
declare %a:since("exist", "4.4") function request:get-header-names() as xs:string* external;
declare %a:since("exist", "4.4") function request:get-hostname() as xs:string external;
declare %a:since("exist", "4.4") function request:get-method() as xs:string external;
declare %a:since("exist", "4.4") function request:get-parameter($name as xs:string, $default-value as item()*) as xs:string* external;
declare %a:since("exist", "4.4") function request:get-parameter($name as xs:string, $default-value as item()*, $failonerror as xs:boolean*) as xs:string* external;
declare %a:since("exist", "4.4") function request:get-parameter-names() as xs:string* external;
declare %a:since("exist", "4.4") function request:get-path-info() as xs:string external;
declare %a:since("exist", "4.4") function request:get-query-string() as xs:string? external;
declare %a:since("exist", "4.4") function request:get-remote-addr() as xs:string external;
declare %a:since("exist", "4.4") function request:get-remote-host() as xs:string external;
declare %a:since("exist", "4.4") function request:get-remote-port() as xs:integer external;
declare %a:since("exist", "4.4") function request:get-scheme() as xs:string external;
declare %a:since("exist", "4.4") function request:get-server-name() as xs:string external;
declare %a:since("exist", "4.4") function request:get-server-port() as xs:integer external;
declare %a:since("exist", "4.4") function request:get-servlet-path() as xs:string external;
declare %a:since("exist", "4.4") function request:get-uploaded-file-data($upload-param-name as xs:string) as xs:base64Binary* external;
declare %a:since("exist", "4.4") function request:get-uploaded-file-name($upload-param-name as xs:string) as xs:string* external;
declare %a:since("exist", "4.4") function request:get-uploaded-file-size($upload-param-name as xs:string) as xs:double* external;
declare %a:since("exist", "4.4") function request:get-uri() as xs:anyURI external;
declare %a:since("exist", "4.4") function request:get-url() as xs:string external;
declare %a:since("exist", "4.4") function request:is-multipart-content() as xs:boolean external;
declare %a:since("exist", "4.4") function request:set-attribute($name as xs:string, $value as item()*) as item() external;