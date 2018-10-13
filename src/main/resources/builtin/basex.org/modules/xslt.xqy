xquery version "3.1";
(:~
 : BaseX XSLT Module functions
 :
 : @see http://docs.basex.org/wiki/XSLT_Module
 :)
module namespace xslt = "http://basex.org/modules/xslt";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";

declare option a:requires "basex/7.0"; (: NOTE: 7.0 is the earliest version definitions are available for. :)

declare %a:since("basex", "7.0") %a:until("basex", "7.3", "xslt:processor#0") variable $xslt:processor external;
declare %a:since("basex", "7.0") %a:until("basex", "7.3", "xslt:version#0") variable $xslt:version external;

declare %a:since("basex", "7.3") function xslt:processor() as xs:string external;
declare %a:since("basex", "7.3") function xslt:version() as xs:string external;
declare %a:since("basex", "7.0") function xslt:transform($input as item(), $stylesheet as item()) as node() external;
declare %a:since("basex", "7.0") function xslt:transform($input as item(), $stylesheet as item(), $params as map(*)? (: $params [7.0] as map(xs:string, xs:string) [9.0] as map(*)? :)) as node() external;
declare %a:since("basex", "9.0") function xslt:transform($input as item(), $stylesheet as item(), $params as map(*)?, $options as map(*)?) as node() external;
declare %a:since("basex", "7.6") function xslt:transform-text($input as item(), $stylesheet as item()) as xs:string external;
declare %a:since("basex", "7.6") function xslt:transform-text($input as item(), $stylesheet as item(), $params as map(*)? (: $params [7.6] as map(xs:string, xs:string) [9.0] as map(*)? :) ) as xs:string external;
declare %a:since("basex", "9.0") function xslt:transform-text($input as item(), $stylesheet as item(), $params as map(*)?, $options as map(*)?) as xs:string external;