xquery version "3.0";
(:~
: BaseX Profiling Module functions
:
: @see http://docs.basex.org/wiki/Profiling_Module
:)
module namespace prof = "http://basex.org/modules/prof";

import module namespace a = "http://reecedunn.co.uk/xquery/annotations" at "res://reecedunn.co.uk/xquery/annotations.xqy";

declare %a:since("basex", "9.0") function prof:track($expression as item()) as item()* external;
declare %a:since("basex", "9.0") function prof:track($expression as item(), $options as map(*)?) as item()* external;
declare %a:since("basex", "7.3") function prof:time($expr as item()) as item()* external;
declare %a:since("basex", "7.3") %a:until("basex", "9.0") function prof:time($expr as item(), $cache as xs:boolean) as item()* external;
declare %a:since("basex", "7.3") %a:until("basex", "9.0") function prof:time($expr as item(), $cache as xs:boolean, $label as xs:string) as item()* external;
declare %a:since("basex", "7.3") function prof:time($expr as item(), $label as xs:string) as item()* external;
declare %a:since("basex", "7.3") function prof:memory($expr as item()) as item()* external;
declare %a:since("basex", "7.3") function prof:memory($expr as item(), $label as xs:string) as item()* external;
declare %a:since("basex", "7.5") function prof:current-ms() as xs:integer external;
declare %a:since("basex", "7.5") function prof:current-ns() as xs:integer external;
declare %a:since("basex", "7.5") function prof:dump($expr as item()) as empty-sequence() external;
declare %a:since("basex", "7.5") function prof:dump($expr as item(), $label as xs:string) as empty-sequence() external;
declare %a:since("basex", "8.1") function prof:variables() as empty-sequence() external;
declare %a:since("basex", "8.5") function prof:type($expr as item()*) as item()* external;
declare %a:since("basex", "7.7") function prof:void($value as item()*) as empty-sequence() external;
declare %a:since("basex", "7.3") function prof:sleep($ms as xs:integer) as empty-sequence() external;
declare %a:since("basex", "7.6") function prof:human($number as xs:integer) as xs:string external;
