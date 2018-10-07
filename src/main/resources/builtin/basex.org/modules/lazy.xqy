xquery version "3.0";
(:~
: BaseX Lazy Module functions
:
: @see http://docs.basex.org/wiki/Lazy_Module
:)
module namespace lazy = "http://basex.org/modules/lazy";

import module namespace a = "http://reecedunn.co.uk/xquery/annotations" at "res://reecedunn.co.uk/xquery/annotations.xqy";

declare %a:since("basex", "9.0") function lazy:cache($items as item()*) as item()* external;
declare %a:since("basex", "9.1") function lazy:cache($items as item()*, $lazy as xs:boolean) as item()* external;
declare %a:since("basex", "9.0") function lazy:is-lazy($item as item()) as xs:boolean external;
declare %a:since("basex", "9.0") function lazy:is-cached($item as item()) as xs:boolean external;