xquery version "3.0";
(:~
: BaseX Process Module functions
:
: @see http://docs.basex.org/wiki/Output_Module
:)
module namespace proc = "http://basex.org/modules/proc";

import module namespace a = "http://reecedunn.co.uk/xquery/annotations" at "res://reecedunn.co.uk/xquery/annotations.xqy";

declare %a:since("basex", "7.3") function proc:system($cmd as xs:string) as xs:string external;
declare %a:since("basex", "7.3") function proc:system($cmd as xs:string, $args as xs:string*) as xs:string external;
declare %a:since("basex", "7.3") function proc:system($cmd as xs:string, $args as xs:string*, $options as map(xs:string, xs:string) (: $options [7.3] as xs:string [8.6] as map(xs:string, xs:string) :)) as xs:string external;
declare %a:since("basex", "7.3") function proc:execute($cmd as xs:string) as element(result) external;
declare %a:since("basex", "7.3") function proc:execute($cmd as xs:string, $args as xs:string*) as element(result) external;
declare %a:since("basex", "7.3") function proc:execute($cmd as xs:string, $args as xs:string*, $options as map(xs:string, xs:string) (: $options [7.3] as xs:string [8.6] as map(xs:string, xs:string) :)) as element(result) external;
declare %a:since("basex", "9.0") function proc:fork($cmd as xs:string) as element(result) external;
declare %a:since("basex", "9.0") function proc:fork($cmd as xs:string, $args as xs:string*) as element(result) external;
declare %a:since("basex", "9.0") function proc:fork($cmd as xs:string, $args as xs:string*, $options as map(xs:string, xs:string)) as element(result) external;
declare %a:since("basex", "8.3") function proc:property($name as xs:string) as xs:string? external;
declare %a:since("basex", "8.3") function proc:property-names() as xs:string* external;
