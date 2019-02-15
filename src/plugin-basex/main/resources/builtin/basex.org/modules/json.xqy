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

declare type json-string = (
    %a:since("basex", "7.0") %a:until("basex", "9.1") for xs:string |
    %a:since("basex", "9.1") for xs:string?
);

declare type json-options = (
    %a:since("basex", "7.7.2") %a:until("basex", "8.2.1") for item() |
    %a:since("basex", "8.2.1") %a:until("basex", "8.2.3") for map(xs:string, xs:string) |
    %a:since("basex", "8.2.3") %a:until("basex", "8.6.7") for map(*) |
    %a:since("basex", "8.6.7") for map(*)?
);

declare type json-result = (
    %a:since("basex", "7.0") %a:until("basex", "9.1") for element(json) |
    %a:since("basex", "9.1") for item()?
);

declare type json-result-item = (
    %a:since("basex", "7.7.2") %a:until("basex", "9.1") for item() |
    %a:since("basex", "9.1") for item()?
);

declare %a:since("basex", "7.0") function json:parse($string as json-string) as json-result external;
declare %a:since("basex", "7.7.2") function json:parse($string as json-string, $options as json-options) as json-result-item external;
declare %a:since("basex", "7.0") function json:serialize($input as item()?) as xs:string external;
declare %a:since("basex", "7.7.2") function json:serialize($input as item()?, $options as json-options) as xs:string external;
declare %a:since("basex", "7.0") %a:deprecated("basex", "7.7.2") function json:serialize-ml($input as node()) as xs:string external;
declare %a:since("basex", "7.0") %a:deprecated("basex", "7.7.2") function json:parse-ml($input as xs:string) as element() external;
