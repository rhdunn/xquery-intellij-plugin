xquery version "3.0";
(:~
: BaseX Database Module functions
:
: @see http://docs.basex.org/wiki/Fetch_Module
:)
module namespace fetch = "http://basex.org/modules/fetch";

import module namespace a = "http://reecedunn.co.uk/xquery/annotations" at "res://reecedunn.co.uk/xquery/annotations.xqy";

declare %a:since("basex", "7.6") function fetch:binary($uri as xs:string) as xs:base64Binary external;
declare %a:since("basex", "7.6") function fetch:text($uri as xs:string) as xs:string external;
declare %a:since("basex", "7.6") function fetch:text($uri as xs:string, $encoding as xs:string) as xs:string external;
declare %a:since("basex", "8.5") function fetch:text($uri as xs:string, $encoding as xs:string, $fallback as xs:boolean) as xs:string  external;
declare %a:since("basex", "8.0") function fetch:xml($uri as xs:string) as document-node() external;
declare %a:since("basex", "8.0") function fetch:xml($uri as xs:string, $options as map(*)? (: $options [8.0] as map(*) [8.6.7] as map(*)? :)) as document-node() external;
declare %a:since("basex", "9.0") function fetch:xml-binary($data as xs:base64Binary) as document-node() external;
declare %a:since("basex", "9.0") function fetch:xml-binary($data as xs:base64Binary, $options as map(*)?) as document-node() external;
declare %a:since("basex", "7.6") function fetch:content-type($uri as xs:string) as xs:string external;