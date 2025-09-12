xquery version "3.1";
(:~
 : BaseX Profiling Module functions
 :
 : @see http://docs.basex.org/wiki/Profiling_Module
 :)
module namespace prof = "http://basex.org/modules/prof";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare option o:requires "basex/7.3";

declare %a:since("basex", "9.0") function prof:track($expression as item()) as item()* external;
declare %a:since("basex", "9.0") function prof:track($expression as item(), $options as map(*)?) as item()* external;
declare %a:since("basex", "7.3") function prof:time($expr as item()) as item()* external;
declare %a:since("basex", "7.3") %a:until("basex", "9.0") function prof:time($expr as item(), $cache as xs:boolean) as item()* external;
declare %a:since("basex", "7.3") %a:until("basex", "9.0") function prof:time($expr as item(), $cache as xs:boolean, $label as xs:string) as item()* external;
declare %a:since("basex", "12.0") function prof:time($expr as item(), $label as xs:string?) as item()* external;
declare %a:since("basex", "12.0") function prof:time($expr as item(), $label as xs:string?, $aggregate as xs:boolean?) as item()* external;
declare %a:since("basex", "7.3") function prof:time($expr as item(), $label as xs:string) as item()* external;
declare %a:since("basex", "7.3") function prof:memory($expr as item()) as item()* external;
declare %a:since("basex", "7.3")
        %a:restrict-until("$label", "basex", "12.0", "xs:string") function prof:memory($expr as item(), $label as xs:string?) as item()* external;
declare %a:since("basex", "12.0") function prof:memory($expr as item(), $label as xs:string?, $aggregate as xs:boolean?) as item()* external;
declare %a:since("basex", "7.5") function prof:current-ms() as xs:integer external;
declare %a:since("basex", "7.5") function prof:current-ns() as xs:integer external;
declare %a:since("basex", "7.5")
        %a:until("basex", "11.0")
        %a:see-also("w3", "4.0", "fn:message") function prof:dump($expr as item()) as empty-sequence() external;
declare %a:since("basex", "7.5")
        %a:until("basex", "11.0")
        %a:see-also("w3", "4.0", "fn:message") function prof:dump($expr as item(), $label as xs:string) as empty-sequence() external;
declare %a:since("basex", "8.1") function prof:variables() as empty-sequence() external;
declare %a:since("basex", "8.5") function prof:type($expr as item()*) as item()* external;
declare %a:since("basex", "12.0") function prof:type($expr as item()*, $label as xs:string?) as item()* external;
declare %a:since("basex", "9.2") function prof:gc() as empty-sequence() external;
declare %a:since("basex", "9.2") function prof:gc($count) as empty-sequence() external;
declare %a:since("basex", "9.2") function prof:runtime($name as xs:string) as xs:integer external;
declare %a:since("basex", "7.7")
        %a:until("basex", "11.0")
        %a:see-also("w3", "4.0", "fn:void") function prof:void($value as item()*) as empty-sequence() external;
declare %a:since("basex", "7.3") function prof:sleep($ms as xs:integer) as empty-sequence() external;
declare %a:since("basex", "7.6") function prof:human($number as xs:integer) as xs:string external;
declare %a:since("basex", "12.0") function prof:shrink($input as item()*) as item()* external;
declare %a:since("basex", "12.0") function prof:variables() as empty-sequence() external;
declare %a:since("basex", "12.0") function prof:runtime($option as enum('used', 'total', 'max', 'processors')?) as xs:integer external;

