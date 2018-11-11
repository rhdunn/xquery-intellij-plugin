xquery version "3.0";
(:~
 : RESTXQ 1.0: RESTful Annotations for XQuery (EXQuery Unofficial Draft 21 March 2016)
 :
 : @see http://exquery.github.io/exquery/exquery-restxq-specification/restxq-1.0-specification.html
 : @see http://docs.basex.org/wiki/RESTXQ_Module
 :)
module namespace rest = "http://exquery.org/ns/restxq";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare namespace wadl = "http://wadl.dev.java.net/2009/02";

declare option o:requires-import "basex/7.7; location-uri=(none)";

declare %a:since("exquery-restxq", "1.0-20160321") %a:annotation function rest:DELETE() external;
declare %a:since("exquery-restxq", "1.0-20160321") %a:annotation function rest:GET() external;
declare %a:since("exquery-restxq", "1.0-20160321") %a:annotation function rest:HEAD() external;
declare %a:since("exquery-restxq", "1.0-20160321") %a:annotation function rest:OPTIONS() external;
declare %a:since("exquery-restxq", "1.0-20160321") %a:annotation function rest:POST() external;
declare %a:since("exquery-restxq", "1.0-20160321") %a:annotation function rest:POST($body-param as xs:string) external;
declare %a:since("exquery-restxq", "1.0-20160321") %a:annotation function rest:PUT() external;
declare %a:since("exquery-restxq", "1.0-20160321") %a:annotation function rest:PUT($body-param as xs:string) external;

declare %a:since("exquery-restxq", "1.0-20160321") %a:annotation function rest:consumes($mimetype1 as xs:string, $mimetypes as xs:string ...) external;
declare %a:since("exquery-restxq", "1.0-20160321") %a:annotation function rest:cookie-param($name as xs:string, $param-reference as xs:string, $default-value as xs:string ...) external;
declare %a:since("exquery-restxq", "1.0-20160321") %a:annotation function rest:form-param($name as xs:string, $param-reference as xs:string, $default-value as xs:string ...) external;
declare %a:since("exquery-restxq", "1.0-20160321") %a:annotation function rest:header-param($name as xs:string, $param-reference as xs:string, $default-value as xs:string ...) external;
declare %a:since("exquery-restxq", "1.0-20160321") %a:annotation function rest:path($path as xs:string) external;
declare %a:since("exquery-restxq", "1.0-20160321") %a:annotation function rest:produces($mimetype1 as xs:string, $mimetypes as xs:string ...) external;
declare %a:since("exquery-restxq", "1.0-20160321") %a:annotation function rest:query-param($name as xs:string, $param-reference as xs:string, $default-value as xs:string ...) external;

declare %a:since("exquery-restxq", "1.0-20160321") function rest:resource-functions() as document-node(element(rest:resource-functions)) external;
declare %a:since("exquery-restxq", "1.0-20160321") %a:since("basex", "7.7") function rest:base-uri() as xs:anyURI external;
declare %a:since("exquery-restxq", "1.0-20160321") %a:since("basex", "7.7") function rest:uri() as xs:anyURI external;

declare %a:since("basex", "7.7") function rest:wadl() as element(wadl:application) external;
declare %a:since("basex", "8.6") function rest:init() as empty-sequence() external;