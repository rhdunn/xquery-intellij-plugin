xquery version "3.1";
(:~
:  BaseX array module functions
:
: @see http://docs.basex.org/wiki/Arry_Module
:)
module namespace array = "http://www.w3.org/2005/xpath-functions/array";

import module namespace a = "http://reecedunn.co.uk/xquery/annotations" at "res://reecedunn.co.uk/xquery/annotations.xqy";

declare %a:since("basex", "8.0") function array:size($input as array(*)) as xs:integer external;
declare %a:since("basex", "8.0") function array:get($array as array(*), $position as xs:integer) as item()* external;
declare %a:since("basex", "8.0") function array:append($array as array(*), $member as item()*) as array(*) external;
declare %a:since("basex", "8.0") function array:subarray($array as array(*), $position as xs:integer) as array(*) external;
declare %a:since("basex", "8.0") function array:subarray($array as array(*), $position as xs:integer, $length as xs:integer) as array(*) external;
declare %a:since("basex", "8.5") function array:put($array as array(*), $position as xs:integer, $member as item()*) as array(*) external;
declare %a:since("basex", "8.0") function array:remove($array as array(*), $positions as xs:integer*) as array(*) external;
declare %a:since("basex", "8.0") function array:insert-before($array as array(*), $position as xs:integer, $member as item()*) as array(*) external;
declare %a:since("basex", "8.0") function array:head($array as array(*)) as item()* external;
declare %a:since("basex", "8.0") function array:tail($array as array(*)) as array(*) external;
declare %a:since("basex", "8.0") function array:reverse($array as array(*)) as array(*) external;
declare %a:since("basex", "8.0") function array:join($arrays as array(*)*) as array(*) external;
declare %a:since("basex", "8.0") function array:flatten($items as item()*) as item()* external;
declare %a:since("basex", "8.0") function array:for-each($array as array(*), $function as function(item()*) as item()*) as array(*) external;
declare %a:since("basex", "8.0") function array:filter($array as array(*), $function as function(item()*) as xs:boolean) as array(*) external;
declare %a:since("basex", "8.0") function array:fold-left($array as array(*), $zero as item()*, $function as function(item()*, item()*) as item()*) as item()* external;
declare %a:since("basex", "8.0") function array:fold-right($array as array(*), $zero as item()*, $function as function(item()*, item()*) as item()*) as item()* external;
declare %a:since("basex", "8.0") function array:for-each-pair($array1 as array(*), $array2 as array(*), $function as function(item()*) as item()*) as array(*) external;
declare %a:since("basex", "8.0") function array:sort($array as array(*)) as array(*) external;
declare %a:since("basex", "8.6") function array:sort($array as array(*), $collation as xs:string?) as array(*) external;
declare %a:since("basex", "8.0") %a:until("basex", "8.6") function array:sort($array as array(*), $key as function(item()*) as xs:anyAtomicType*) as array(*) external;
declare %a:since("basex", "8.6") function array:sort($array as array(*), $collation as xs:string?, $key as function(item()*) as xs:anyAtomicType*) as array(*) external;
declare %a:since("basex", "8.0") %a:until("basex", "8.4") %a:see-also("basex", "8.4", "fn:serialize") function array:serialize($input as map(*)) as xs:string external;