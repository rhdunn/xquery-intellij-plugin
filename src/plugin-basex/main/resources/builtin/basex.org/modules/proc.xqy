xquery version "3.1";
(:~
 : BaseX Process Module functions
 :
 : @see http://docs.basex.org/wiki/Process_Module
 :)
module namespace proc = "http://basex.org/modules/proc";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare option o:requires "basex/7.3";

declare %a:since("basex", "7.3") function proc:system($cmd as xs:string) as xs:string external;
declare %a:since("basex", "7.3") function proc:system($cmd as xs:string, $args as xs:string*) as xs:string external;
declare %a:restrict-until("$options", "basex", "8.6", "xs:string")
        %a:restrict-since("$options", "basex", "8.6", "map(xs:string, xs:string)")
        %a:since("basex", "7.3") function proc:system($cmd as xs:string, $args as xs:string*, $options as (xs:string|map(xs:string, xs:string))) as xs:string external;
declare %a:since("basex", "7.3") function proc:execute($cmd as xs:string) as element(result) external;
declare %a:since("basex", "7.3") function proc:execute($cmd as xs:string, $args as xs:string*) as element(result) external;
declare %a:restrict-until("$options", "basex", "8.6", "xs:string")
        %a:restrict-since("$options", "basex", "8.6", "map(xs:string, xs:string)")
        %a:since("basex", "7.3") function proc:execute($cmd as xs:string, $args as xs:string*, $options as (xs:string|map(xs:string, xs:string))) as element(result) external;
declare %a:since("basex", "9.0") function proc:fork($cmd as xs:string) as element(result) external;
declare %a:since("basex", "9.0") function proc:fork($cmd as xs:string, $args as xs:string*) as element(result) external;
declare %a:since("basex", "9.0") function proc:fork($cmd as xs:string, $args as xs:string*, $options as map(xs:string, xs:string)) as element(result) external;
declare %a:since("basex", "8.3") function proc:property($name as xs:string) as xs:string? external;
declare %a:since("basex", "8.3") function proc:property-names() as xs:string* external;
