xquery version "3.1";
(:~
 : BaseX JSON Module functions
 :
 : @see http://docs.basex.org/wiki/JSON_Module
 :)
module namespace json = "http://basex.org/modules/json";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare option o:requires "basex/7.0";

declare %a:since("basex", "7.0") function json:parse($string (: as [basex/7.0]xs:string [basex/9.1]xs:string? :)) (: as [basex/7.0]element(json) [basex/9.1]item()? :) external;
declare %a:since("basex", "7.7.2") function json:parse($string (: as [basex/7.0]xs:string [basex/9.1]xs:string? :), $options (: as [basex/7.7.2]item() [basex/8.2.1]map(xs:string, xs:string) [basex/8.2.3]map(*) [basex/8.6.7]map(*)? :)) (: as [basex/7.7.2]item() [basex/9.1]item()? :) external;
declare %a:since("basex", "7.0") function json:serialize($input as item()?) as xs:string external;
declare %a:since("basex", "7.7.2") function json:serialize($input as item()?, $options (: as [basex/7.7.2]item() [basex/8.2.1]map(xs:string, xs:string) [basex/8.2.3]map(*) [basex/8.6.7]map(*)? :)) as xs:string external;
declare %a:since("basex", "7.0") %a:deprecated("basex", "7.7.2") function json:serialize-ml($input as node()) as xs:string external;
declare %a:since("basex", "7.0") %a:deprecated("basex", "7.7.2") function json:parse-ml($input as xs:string) as element() external;
