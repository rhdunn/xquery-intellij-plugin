xquery version "3.0";
(:~
: BaseX binary module functions
: 
:)
module namespace bin = "http://expath.org/ns/binary";

import module namespace a = "http://reecedunn.co.uk/xquery/annotations" at "res://reecedunn.co.uk/xquery/annotations.xqy";

declare %a:since("basex", "9.0") function bin:hex($in as xs:string?) as xs:base64Binary? external;
declare %a:since("basex", "9.0") function bin:bin($in as xs:string?) as xs:base64Binary? external;
declare %a:since("basex", "9.0") function bin:octal($in as xs:string?) as xs:base64Binary? external;
declare %a:since("basex", "9.0") function bin:to-octets($in as xs:base64Binary) as xs:integer* external;
declare %a:since("basex", "9.0") function bin:from-octets($in as xs:integer*) as xs:base64Binary external;
declare %a:since("basex", "9.0") function bin:length($in as xs:base64Binary) as xs:integer external;
declare %a:since("basex", "9.0") function bin:part($in as xs:base64Binary?, $offset as xs:integer) as xs:base64Binary? external;
declare %a:since("basex", "9.0") function bin:part($in as xs:base64Binary?, $offset as xs:integer, $size as xs:integer) as xs:base64Binary? external;
declare %a:since("basex", "9.0") function bin:join($in as xs:base64Binary*) as xs:base64Binary external;
declare %a:since("basex", "9.0") function bin:insert-before($in as xs:base64Binary?, $offset as xs:integer, $extra as xs:base64Binary?) as xs:base64Binary external;
declare %a:since("basex", "9.0") function bin:pad-left($in as xs:base64Binary?, $size as xs:integer) as xs:base64Binary? external;
declare %a:since("basex", "9.0") function bin:pad-left($in as xs:base64Binary?, $size as xs:integer, $octect as xs:integer) as xs:base64Binary? external;
declare %a:since("basex", "9.0") function bin:pad-right($in as xs:base64Binary?, $size as xs:integer) as xs:base64Binary? external;
declare %a:since("basex", "9.0") function bin:pad-right($in as xs:base64Binary?, $size as xs:integer, $octect as xs:integer) as xs:base64Binary? external;
declare %a:since("basex", "9.0") function bin:find($in as xs:base64Binary?, $offset as xs:integer, $search as xs:base64Binary) as xs:integer? external;
declare %a:since("basex", "9.0") function bin:decode-string($in as xs:base64Binary?, $encoding as xs:string) as xs:string? external;
declare %a:since("basex", "9.0") function bin:decode-string($in as xs:base64Binary?, $encoding as xs:string, $offset as xs:integer) as xs:string? external;
declare %a:since("basex", "9.0") function bin:decode-string($in as xs:base64Binary?, $encoding as xs:string, $offset as xs:integer, $size as xs:integer) as xs:string? external;
declare %a:since("basex", "9.0") function bin:encode-string($in as xs:string?, $encoding as xs:string) as xs:base64Binary? external;
declare %a:since("basex", "9.0") function bin:pack-double($in as xs:double) as xs:base64Binary external;
declare %a:since("basex", "9.0") function bin:pack-double($in as xs:double, $octet-order as xs:string) as xs:base64Binary external;
declare %a:since("basex", "9.0") function bin:pack-float($in as xs:float) as xs:base64Binary external;
declare %a:since("basex", "9.0") function bin:pack-float($in as xs:float, $octet-order as xs:string) as xs:base64Binary external;
declare %a:since("basex", "9.0") function bin:pack-integer($in as xs:integer, $size as xs:integer) as xs:base64Binary external;
declare %a:since("basex", "9.0") function bin:pack-integer($in as xs:integer, $size as xs:integer, $octet-order as xs:string) as xs:base64Binary external;
declare %a:since("basex", "9.0") function bin:unpack-double($in as xs:base64Binary, $offset as xs:integer) as xs:double external;
declare %a:since("basex", "9.0") function bin:unpack-double($in as xs:base64Binary, $offset as xs:integer, $octet-order as xs:string) as xs:double external;
