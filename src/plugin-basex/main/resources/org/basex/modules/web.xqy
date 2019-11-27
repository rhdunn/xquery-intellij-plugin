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
declare %a:restrict-until("$parameters", "basex", "8.2", "map(*)")
        %a:restrict-until("$parameters", "basex", "9.0", "map(item(), item()*)")
        %a:restrict-since("$parameters", "basex", "8.5", "map(*)")
        %a:since("basex", "8.1") function web:create-url($url as xs:string, $parameters as map(*)) as xs:string external;
declare %a:since("basex", "8.2") function web:encode-url($string as xs:string) as xs:string external;
declare %a:since("basex", "8.2") function web:decode-url($string as xs:string) as xs:string external;
declare %a:since("basex", "8.1") function web:redirect($location as xs:string) as element(rest:response) external;
declare %a:since("basex", "8.1") function web:redirect($location as xs:string, $parameters as map(*)) as element(rest:response) external;
declare %a:since("basex", "8.1") function web:response-header() as element(rest:response) external;
declare %a:restrict-until("$output", "basex", "9.0", "map(*)")
        %a:since("basex", "8.1") function web:response-header($output as map(*)?) as element(rest:response) external;
declare %a:restrict-until("$output", "basex", "9.0", "map(*)")
        %a:restrict-until("$headers", "basex", "8.2", "map(*)")
        %a:restrict-until("$headers", "basex", "9.0", "map(xs:string, xs:string)")
        %a:since("basex", "8.1") function web:response-header($output as map(*)?, $headers as map(*)?) as element(rest:response) external;
declare %a:since("basex", "9.0") function web:response-header($output as map(*)?, $headers as map(*)?, $atts as map(*)?) as element(rest:response) external;