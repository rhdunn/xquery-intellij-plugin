xquery version "3.1";
(:~
 : BaseX WebSocket Module functions
 :
 : @see http://docs.basex.org/wiki/WebSocket_Module
 :)
module namespace ws = "http://basex.org/modules/web";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare option o:requires "basex/9.1";

declare %a:since("basex", "9.1") function ws:id() as xs:string external;
declare %a:since("basex", "9.1") function ws:ids() as xs:string* external;
declare %a:since("basex", "9.1") function ws:path($id as xs:string) as xs:string external;
declare %a:since("basex", "9.1") function ws:close($id as xs:string) as empty-sequence() external;
declare %a:since("basex", "9.1") function ws:send($message as item(), $ids as xs:string*) as empty-sequence() external;
declare %a:since("basex", "9.1") function ws:broadcast($message as xs:anyAtomicType) as empty-sequence() external;
declare %a:since("basex", "9.1") function ws:emit($message as xs:anyAtomicType) as empty-sequence() external;
declare %a:since("basex", "9.1") function ws:get($id as xs:string, $name as xs:string) as item()* external;
declare %a:since("basex", "9.1") function ws:get($id as xs:string, $name as xs:string, $default as item()*) as item()* external;
declare %a:since("basex", "9.1") function ws:set($id as xs:string, $name as xs:string, $value as item()*) as empty-sequence() external;
declare %a:since("basex", "9.1") function ws:delete($id as xs:string, $name as xs:string) as empty-sequence() external;