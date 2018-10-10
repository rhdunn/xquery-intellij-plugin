xquery version "3.0";
(:~
: BaseX Session Module functions
:
: @see http://docs.basex.org/wiki/Session_Module
:)
module namespace session = "http://basex.org/modules/session";

import module namespace a = "http://reecedunn.co.uk/xquery/annotations" at "res://reecedunn.co.uk/xquery/annotations.xqy";

declare %a:since("basex", "7.5") function session:id() as xs:string external;
declare %a:since("basex", "7.5") function session:created() as xs:dateTime external;
declare %a:since("basex", "7.5") function session:accessed() as xs:dateTime external;
declare %a:since("basex", "7.5") function session:names() as xs:string* external;
declare %a:since("basex", "7.5") function session:get($name as xs:string) as item()* (: [7.5] as xs:string? [7.9] as item()? [8.0] as item()* :) external;
declare %a:since("basex", "7.5") function session:get($name as xs:string, $default as item()* (: $default [7.5] as xs:string [8.0] as item()* :)) as item()* (: [7.5] as xs:string:) external;
declare %a:since("basex", "7.5") function session:set($name as xs:string, $value as item()* (: $value [7.5] as item() [8.0] as item()* :)) as empty-sequence() external;
declare %a:since("basex", "7.5") function session:delete($name as xs:string) as empty-sequence() external;
declare %a:since("basex", "7.5") function session:close() as empty-sequence() external;
