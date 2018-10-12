xquery version "3.0";
(:~
: BaseX Output Module functions
:
: @see http://docs.basex.org/wiki/Output_Module
:)
module namespace out = "http://basex.org/modules/out";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";

declare %a:since("basex", "9.0") function out:cr() as xs:string external;
declare %a:since("basex", "7.3") function out:nl() as xs:string external;
declare %a:since("basex", "7.3") function out:tab() as xs:string external;
declare %a:since("basex", "7.3") %a:variadic("basex", "7.3", "item()") function out:format($format as xs:string, $item as item()) as xs:string external;
