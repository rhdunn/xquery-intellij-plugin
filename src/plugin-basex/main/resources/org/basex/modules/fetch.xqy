xquery version "3.1";
(:~
 : BaseX Fetch Module functions
 :
 : @see http://docs.basex.org/wiki/Fetch_Module
 :)
module namespace fetch = "http://basex.org/modules/fetch";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare option o:requires "basex/7.6";

declare %a:since("basex", "7.6") function fetch:binary($uri as xs:string) as xs:base64Binary external;
declare %a:since("basex", "7.6") function fetch:text($uri as xs:string) as xs:string external;
declare %a:since("basex", "7.6") function fetch:text($uri as xs:string, $encoding as xs:string) as xs:string external;
declare %a:since("basex", "8.5") function fetch:text($uri as xs:string, $encoding as xs:string, $fallback as xs:boolean) as xs:string  external;
declare %a:since("basex", "8.0") function fetch:xml($uri as xs:string) as document-node() external;
declare %a:restrict-until("$options", "basex", "8.6.7", "map(*)")
        %a:since("basex", "8.0") function fetch:xml($uri as xs:string, $options as map(*)?) as document-node() external;
declare %a:since("basex", "9.0") function fetch:xml-binary($data as xs:base64Binary) as document-node() external;
declare %a:since("basex", "9.0") function fetch:xml-binary($data as xs:base64Binary, $options as map(*)?) as document-node() external;
declare %a:since("basex", "7.6") function fetch:content-type($uri as xs:string) as xs:string external;