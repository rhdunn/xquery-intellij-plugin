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

declare %a:restrict-until("return", "basex", "9.1", "element(json)")
        %a:restrict-since("return", "basex", "9.1", "item()?")
        %a:restrict-until("$string", "basex", "9.1", "xs:string")
        %a:since("basex", "7.0") function json:parse($string as xs:string?) as (element(json)|item()?) external;
declare %a:restrict-until("return", "basex", "9.1", "item()")
        %a:restrict-until("$string", "basex", "9.1", "xs:string")
        %a:restrict-until("$options", "basex", "8.2.1", "item()")
        %a:restrict-until("$options", "basex", "8.2.3", "map(xs:string, xs:string)")
        %a:restrict-until("$options", "basex", "8.6.7", "map(*)")
        %a:restrict-since("$options", "basex", "8.6.7", "map(*)?")
        %a:since("basex", "7.7.2") function json:parse($string as xs:string?, $options as item()?) as item()? external;
declare %a:since("basex", "7.0") function json:serialize($input as item()?) as xs:string external;
declare %a:restrict-until("$options", "basex", "8.2.1", "item()")
        %a:restrict-until("$options", "basex", "8.2.3", "map(xs:string, xs:string)")
        %a:restrict-until("$options", "basex", "8.6.7", "map(*)")
        %a:restrict-since("$options", "basex", "8.6.7", "map(*)?")
        %a:since("basex", "7.7.2") function json:serialize($input as item()?, $options as item()?) as xs:string external;
declare %a:since("basex", "7.0") %a:deprecated("basex", "7.7.2") function json:serialize-ml($input as node()) as xs:string external;
declare %a:since("basex", "7.0") %a:deprecated("basex", "7.7.2") function json:parse-ml($input as xs:string) as element() external;
