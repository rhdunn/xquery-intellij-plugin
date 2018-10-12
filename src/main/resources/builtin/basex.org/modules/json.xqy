xquery version "3.0";
(:~
: BaseX JSON Module functions
:
: @see http://docs.basex.org/wiki/JSON_Module
:)
module namespace json = "http://basex.org/modules/json";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";

declare %a:since("basex", "7.0") function json:parse($string as xs:string? (: $string [7.0] as xs:string [9.1] as xs:string? :)) as item()? (: [7.7.2] as element(json) [9.1] as item()? :) external;
declare %a:since("basex", "7.7.2") function json:parse($string as xs:string?, $options as map(*)? (: $options [7.7.2] as item() [8.2.1] as map(xs:string, xs:string) [8.2.3] as map(*) [8.6.7] as map(*)? :)) as item()? external;
declare %a:since("basex", "7.0") function json:serialize($input as item()?) as xs:string external;
declare %a:since("basex", "7.7.2") function json:serialize($input as item()?, $options as map(*)? (: $options [7.7.2] as item() [8.2.1] as map(xs:string, xs:string) [8.2.3] as map(*) [8.6.7] as map(*)? :)) as xs:string external;
declare %a:since("basex", "7.0") %a:deprecated("basex", "7.7.2") function json:serialize-ml($input as node()) as xs:string external;
declare %a:since("basex", "7.0") %a:deprecated("basex", "7.7.2") function json:parse-ml($input as xs:string) as element() external;
