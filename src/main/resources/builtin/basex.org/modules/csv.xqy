xquery version "3.0";
(:~
: BaseX CSV Module functions
:
: @see http://docs.basex.org/wiki/CSV_Module
:)
module namespace csv = "http://basex.org/modules/csv";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";

declare %a:since("basex", "7.7.2") function csv:parse($string as xs:string? (: $string [7.7.2] as xs:string [9.1] as xs:string?:)) as item()? (: [7.7.2] as element(csv) [7.8] as document-node(element(csv)) [9.1] as item()? :) external;
declare %a:since("basex", "7.7.2") function csv:parse($string as xs:string? (: $string [7.7.2] as xs:string [9.1] as xs:string?	:), $options as map(*)? (: $options [7.7.2] as item() [8.2.1] as map(xs:string, item()) [8.6.7] as map(*)? :)) as item()? (: [7.7.2] as element(csv) [7.8] as document-node(element(csv)) [9.1] as item()? :) external;
declare %a:since("basex", "7.7.2") function csv:serialize($input as item()? (:$input [7.7.2] as node() [8.6.7] as item()? :)) as xs:string external;
declare %a:since("basex", "7.7.2") function csv:serialize($input as item()? (:$input [7.7.2] as node() [8.6.7] as item()? :), $options as map(*)? (: $options [7.7.2] as item() [8.2.1] as map(xs:string, item()) [8.6.7] as map(*)? :)) as xs:string external;