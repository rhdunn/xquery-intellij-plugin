xquery version "3.1";
(:~
 : BaseX archive module functions
 :
 : @see http://docs.basex.org/wiki/Archive_Module
 :)
module namespace archive = "http://basex.org/modules/archive";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare option o:requires "basex/7.3";

declare %a:since("basex", "7.3") function archive:create($entries as item(), $contents as item()*) as xs:base64Binary external;
declare %a:since("basex", "7.3") function archive:create($entries as item(), $contents as item()*, $options as map(*)?) as xs:base64Binary external;
declare %a:since("basex", "8.3") function archive:create-from($path as xs:string) as xs:base64Binary external;
declare %a:since("basex", "8.3") function archive:create-from($path as xs:string, $options as map(*)?) as xs:base64Binary external;
declare %a:since("basex", "8.3") function archive:create-from($path as xs:string, $options as map(*)?, $entries as item()*) as xs:base64Binary external;
declare %a:since("basex", "7.3") function archive:entries($archive as xs:base64Binary) as element(archive:entry)* external;
declare %a:restrict-until("return", "basex", "8.5", "element(archive:options)")
        %a:restrict-since("return", "basex", "8.5", "map(*)")
        %a:since("basex", "7.3") function archive:options($archive as xs:base64Binary) as (element(archive:options)|map(*)) external;
declare %a:since("basex", "7.3") function archive:extract-text($archive as xs:base64Binary) as xs:string* external;
declare %a:since("basex", "7.3") function archive:extract-text($archive as xs:base64Binary, $entries as item()*) as xs:string* external;
declare %a:since("basex", "7.3") function archive:extract-text($archive as xs:base64Binary, $entries as item()*, $encoding as xs:string) as xs:string* external;
declare %a:since("basex", "7.3") function archive:extract-binary($archive as xs:base64Binary) as xs:base64Binary* external;
declare %a:since("basex", "7.3") function archive:extract-binary($archive as xs:base64Binary, $entries as item()*) as xs:base64Binary* external;
declare %a:since("basex", "8.3") function archive:extract-to($path as xs:string, $archive as xs:base64Binary) as empty-sequence() external;
declare %a:since("basex", "8.3") function archive:extract-to($path as xs:string, $archive as xs:base64Binary, $entries as item()*) as empty-sequence() external;
declare %a:since("basex", "7.3") function archive:update($archive as xs:base64Binary, $entries as item()*, $contents as item()*) as xs:base64Binary external;
declare %a:since("basex", "7.3") function archive:delete($archive as xs:base64Binary, $entries as item()*) as xs:base64Binary external;
declare %a:since("basex", "7.7") %a:until("basex", "8.3", "archive:create-from#2", "archive:extract-to#1") function archive:write($path as xs:string, $archive as xs:base64Binary) as empty-sequence() external;
declare %a:since("basex", "7.7") %a:until("basex", "8.3", "archive:create-from#3", "archive:extract-to#2") function archive:write($path as xs:string, $archive as xs:base64Binary, $entries as item()*) as empty-sequence() external;
