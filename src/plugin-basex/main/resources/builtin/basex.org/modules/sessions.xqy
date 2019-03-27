xquery version "3.0";
(:~
 : BaseX Sessions Module functions
 :
 : @see http://docs.basex.org/wiki/Sessions_Module
 :)
module namespace sessions = "http://basex.org/modules/sessions";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare option o:requires-import "basex/7.5; location-uri=(none)";

declare %a:since("basex", "7.5") function sessions:ids() as xs:string external;
declare %a:since("basex", "7.5") function sessions:created($id as xs:string) as xs:dateTime external;
declare %a:since("basex", "7.5") function sessions:accessed($id as xs:string) as xs:dateTime external;
declare %a:since("basex", "7.5") function sessions:names($id as xs:string) as xs:string* external;
declare %a:restrict-until("return", "basex", "7.9", "xs:string?")
        %a:restrict-until("return", "basex", "8.0", "item()?")
        %a:since("basex", "7.5") function sessions:get($id as xs:string, $name as xs:string) as item()* external;
declare %a:restrict-until("return", "basex", "7.9", "xs:string")
        %a:restrict-until("return", "basex", "8.0", "item()")
        %a:restrict-until("$default", "basex", "7.9", "xs:string")
        %a:restrict-until("$default", "basex", "8.0", "item()")
        %a:since("basex", "7.5") function sessions:get($id as xs:string, $name as xs:string, $default as item()*) as item()* external;
declare %a:restrict-until("$value", "basex", "7.9", "xs:string")
        %a:restrict-until("$value", "basex", "8.0", "item()")
        %a:since("basex", "7.5") function sessions:set($name as xs:string, $value as item()*) as empty-sequence() external;
declare %a:since("basex", "7.5") function sessions:delete($name as xs:string) as empty-sequence() external;
declare %a:since("basex", "7.5") function sessions:close() as empty-sequence() external;
