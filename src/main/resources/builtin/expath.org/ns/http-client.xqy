xquery version "3.0";
(:~
 : HTTP Client Module (EXPath Candidate Module 9 January 2010)
 :
 : @see http://expath.org/spec/http-client
 : @see http://docs.basex.org/wiki/HTTP_Module
 :)
module namespace http = "http://expath.org/ns/http-client";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare option o:requires "expath-http/1.0-20100109";

declare option o:implements-module "basex/7.1 as expath-http/1.0-20100109";

declare %a:since("expath-http", "1.0-20100109") function http:send-request($request as element(http:request)) as item()+ external;
declare %a:since("expath-http", "1.0-20100109") function http:send-request($request as element(http:request)?, $href as xs:string?) as item()+ external;
declare %a:since("expath-http", "1.0-20100109") function http:send-request($request as element(http:request)?, $href as xs:string?, $bodies as item()*) as item()+ external;
