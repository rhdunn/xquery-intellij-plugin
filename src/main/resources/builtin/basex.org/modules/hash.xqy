xquery version "3.0";
(:~
: BaseX Hashing Module functions
:
: @see http://docs.basex.org/wiki/Hashing_Module
:)
module namespace hash = "http://basex.org/modules/hash";

import module namespace a = "http://reecedunn.co.uk/xquery/annotations" at "res://reecedunn.co.uk/xquery/annotations.xqy";

declare %a:since("basex", "7.3") function hash:md5($value as xs:anyAtomicType) as xs:base64Binary external;
declare %a:since("basex", "7.3") function hash:sha1($value as xs:anyAtomicType) as xs:base64Binary external;
declare %a:since("basex", "7.3") function hash:sha256($value as xs:anyAtomicType) as xs:base64Binary external;
declare %a:since("basex", "7.3") function hash:hash($value as xs:anyAtomicType, $algorithm as xs:string) as xs:base64Binary external;