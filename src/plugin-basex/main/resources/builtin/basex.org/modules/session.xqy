xquery version "3.0";
(:~
 : BaseX Session Module functions
 :
 : @see http://docs.basex.org/wiki/Session_Module
 :)
module namespace session = "http://basex.org/modules/session";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare option o:requires-import "basex/7.5; until=basex/9.2; location-uri=(none)";

declare %a:since("basex", "7.5") function session:id() as xs:string external;
declare %a:since("basex", "7.5") function session:created() as xs:dateTime external;
declare %a:since("basex", "7.5") function session:accessed() as xs:dateTime external;
declare %a:since("basex", "7.5") function session:names() as xs:string* external;
declare %a:restrict-until("return", "basex", "7.9", "xs:string?")
        %a:restrict-until("return", "basex", "8.0", "item()?")
        %a:since("basex", "7.5") function session:get($name as xs:string) as item()* external;
declare %a:restrict-until("return", "basex", "7.9", "xs:string")
        %a:restrict-until("return", "basex", "8.0", "item()")
        %a:restrict-until("$default", "basex", "7.9", "xs:string")
        %a:restrict-until("$default", "basex", "8.0", "item()")
        %a:since("basex", "7.5") function session:get($name as xs:string, $default as item()*) as item()* external;
declare %a:restrict-until("$value", "basex", "7.9", "xs:string")
        %a:restrict-until("$value", "basex", "8.0", "item()")
        %a:since("basex", "7.5") function session:set($name as xs:string, $value as item()*) as empty-sequence() external;
declare %a:since("basex", "7.5") function session:delete($name as xs:string) as empty-sequence() external;
declare %a:since("basex", "7.5") function session:close() as empty-sequence() external;
