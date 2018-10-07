xquery version "3.0";
(:~
: BaseX Inspect Module functions
:
: @see http://docs.basex.org/wiki/Inspection_Module
:)
module namespace inspect = "http://basex.org/modules/inspect";

import module namespace a = "http://reecedunn.co.uk/xquery/annotations" at "res://reecedunn.co.uk/xquery/annotations.xqy";

declare namespace xqdoc = "http://www.xqdoc.org/1.0";

declare %a:since("basex", "7.7") function inspect:functions() as function(*)* external;
declare %a:since("basex", "7.7") function inspect:functions($uri as xs:string) as function(*)* external;
declare %a:since("basex", "8.5") function inspect:function-annotations($function as function(*)?) as map(xs:QName, xs:anyAtomicType*) external;
declare %a:since("basex", "8.5") function inspect:static-context($function as function(*)?, $name as xs:string) as item()* external;
declare %a:since("basex", "7.7") function inspect:function($function as function(*)) as element(function) external;
declare %a:since("basex", "7.7") function inspect:context() as element(context) external;
declare %a:since("basex", "7.7") function inspect:module($uri as xs:string) as element(module) external;
declare %a:since("basex", "7.7") function inspect:xqdoc($uri as xs:string) as element(xqdoc:xqdoc) external;
