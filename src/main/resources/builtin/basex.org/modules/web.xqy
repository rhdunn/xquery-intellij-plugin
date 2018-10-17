xquery version "3.1";
(:~
 : BaseX Web Module functions
 :
 : @see http://docs.basex.org/wiki/Web_Module
 :)
module namespace web = "http://basex.org/modules/web";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare namespace rest = "http://exquery.org/ns/restxq";

declare option o:requires "basex/8.1";

declare %a:since("basex", "8.1") function web:content-type($path as xs:string) as xs:string external;
declare %a:since("basex", "8.1") function web:create-url($url as xs:string, $parameters as map(*) (: [8.1] as map(*) [8.2] as map(item(), item()*) [9.0] as map(*) :)) as xs:string external;
declare %a:since("basex", "8.2") function web:encode-url($string as xs:string) as xs:string external;
declare %a:since("basex", "8.2") function web:decode-url($string as xs:string) as xs:string external;
declare %a:since("basex", "8.1") function web:redirect($location as xs:string) as element(rest:response) external;
declare %a:since("basex", "8.1") function web:redirect($location as xs:string, $parameters as map(*)) as element(rest:response) external;
declare %a:since("basex", "8.1") function web:response-header() as element(rest:response) external;
declare %a:since("basex", "8.1") function web:response-header($output as map(*)? (: [8.1] as map(*) [9.0] as map(*)? :)) as element(rest:response) external;
declare %a:since("basex", "8.1") function web:response-header($output as map(*)? (: [8.1] as map(*) [9.0] as map(*)? :), $headers as map(*)? (: [8.1] as map(*) [8.2] as map(xs:string, xs:string) [9.0] as map(*)? :)) as element(rest:response) external;
declare %a:since("basex", "9.0") function web:response-header($output as map(*)?, $headers as map(*)?, $atts as map(*)?) as element(rest:response) external;