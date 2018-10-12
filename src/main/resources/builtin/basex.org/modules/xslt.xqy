xquery version "3.0";
(:~
: BaseX XSLT Module functions
:
: @see http://docs.basex.org/wiki/XSLT_Module
:)
module namespace xslt = "http://basex.org/modules/xslt";

import module namespace a = "http://reecedunn.co.uk/xquery/annotations" at "res://reecedunn.co.uk/xquery/annotations.xqy";

declare %a:since("basex", "7.3") function xslt:processor() as xs:string external;
declare %a:since("basex", "7.3") function xslt:version() as xs:string external;
declare %a:since("basex", "7.3") function xslt:transform($input as item(), $stylesheet as item()) as node() external;
declare %a:since("basex", "7.3") function xslt:transform($input as item(), $stylesheet as item(), $params as map(*)? (: $params [7.3] as map(xs:string, xs:string) [9.0] as map(*)? :)) as node() external;
declare %a:since("basex", "9.0") function xslt:transform($input as item(), $stylesheet as item(), $args as map(*)?, $options as map(*)?) as node() external;
declare %a:since("basex", "7.6") function xslt:transform-text($input as item(), $stylesheet as item()) as xs:string external;
declare %a:since("basex", "7.6") function xslt:transform-text($input as item(), $stylesheet as item(), $params as map(*)? (: $params [7.6] as map(xs:string, xs:string) [9.0] as map(*)? :) ) as xs:string external;
declare %a:since("basex", "9.0") function xslt:transform-text($input as item(), $stylesheet as item(), $params as map(*)?, $options as map(*)?) as xs:string external;