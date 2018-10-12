xquery version "3.0";
(:~
: BaseX HTTP Functions Module
:
: @see http://docs.basex.org/wiki/HTTP_Module
:)
module namespace http = "http://expath.org/ns/http-client";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";

declare %a:since("basex", "7.1") function http:send-request($request as element(http:request)) as item()+ external;
declare %a:since("basex", "7.1") function http:send-request($request as element(http:request)?, $href as xs:string?) as item()+ external;
declare %a:since("basex", "7.1") function http:send-request($request as element(http:request)?, $href as xs:string?, $bodies as item()*) as item()+ external;