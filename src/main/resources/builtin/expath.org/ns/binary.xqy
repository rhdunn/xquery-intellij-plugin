xquery version "3.0";
(:~
 : Binary Module 1.0 (EXPath Module 3 December 2013)
 :
 : @see http://expath.org/spec/binary/1.0
 : @see http://docs.basex.org/wiki/Binary_Module
 :)
module namespace bin = "http://expath.org/ns/binary";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare option o:requires "expath-binary/1.0";

declare option o:implements-module "basex/7.8 as expath-binary/1.0-20131203";

declare %a:since("expath-binary", "1.0-20131203") function bin:hex($in as xs:string?) as xs:base64Binary? external;
declare %a:since("expath-binary", "1.0-20131203") function bin:bin($in as xs:string?) as xs:base64Binary? external;
declare %a:since("expath-binary", "1.0-20131203") function bin:octal($in as xs:string?) as xs:base64Binary? external;
declare %a:since("expath-binary", "1.0-20131203") function bin:to-octets($in as xs:base64Binary) as xs:integer* external;
declare %a:since("expath-binary", "1.0-20131203") function bin:from-octets($in as xs:integer*) as xs:base64Binary external;
declare %a:since("expath-binary", "1.0-20131203") function bin:length($in as xs:base64Binary) as xs:integer external;
declare %a:since("expath-binary", "1.0-20131203") function bin:part($in as xs:base64Binary?, $offset as xs:integer) as xs:base64Binary? external;
declare %a:since("expath-binary", "1.0-20131203") function bin:part($in as xs:base64Binary?, $offset as xs:integer, $size as xs:integer) as xs:base64Binary? external;
declare %a:since("expath-binary", "1.0-20131203") function bin:join($in as xs:base64Binary*) as xs:base64Binary external;
declare %a:since("expath-binary", "1.0-20131203") function bin:insert-before($in as xs:base64Binary?, $offset as xs:integer, $extra as xs:base64Binary?) as xs:base64Binary external;
declare %a:since("expath-binary", "1.0-20131203") function bin:pad-left($in as xs:base64Binary?, $size as xs:integer) as xs:base64Binary? external;
declare %a:since("expath-binary", "1.0-20131203") function bin:pad-left($in as xs:base64Binary?, $size as xs:integer, $octect as xs:integer) as xs:base64Binary? external;
declare %a:since("expath-binary", "1.0-20131203") function bin:pad-right($in as xs:base64Binary?, $size as xs:integer) as xs:base64Binary? external;
declare %a:since("expath-binary", "1.0-20131203") function bin:pad-right($in as xs:base64Binary?, $size as xs:integer, $octect as xs:integer) as xs:base64Binary? external;
declare %a:since("expath-binary", "1.0-20131203") function bin:find($in as xs:base64Binary?, $offset as xs:integer, $search as xs:base64Binary) as xs:integer? external;
declare %a:since("expath-binary", "1.0-20131203") function bin:decode-string($in as xs:base64Binary?, $encoding as xs:string) as xs:string? external;
declare %a:since("expath-binary", "1.0-20131203") function bin:decode-string($in as xs:base64Binary?, $encoding as xs:string, $offset as xs:integer) as xs:string? external;
declare %a:since("expath-binary", "1.0-20131203") function bin:decode-string($in as xs:base64Binary?, $encoding as xs:string, $offset as xs:integer, $size as xs:integer) as xs:string? external;
declare %a:since("expath-binary", "1.0-20131203") %a:missing("basex", "7.8") function bin:encode-string($in as xs:string?) as xs:base64Binary? external;
declare %a:since("expath-binary", "1.0-20131203") function bin:encode-string($in as xs:string?, $encoding as xs:string) as xs:base64Binary? external;
declare %a:since("expath-binary", "1.0-20131203") function bin:pack-double($in as xs:double) as xs:base64Binary external;
declare %a:since("expath-binary", "1.0-20131203") function bin:pack-double($in as xs:double, $octet-order as xs:string) as xs:base64Binary external;
declare %a:since("expath-binary", "1.0-20131203") function bin:pack-float($in as xs:float) as xs:base64Binary external;
declare %a:since("expath-binary", "1.0-20131203") function bin:pack-float($in as xs:float, $octet-order as xs:string) as xs:base64Binary external;
declare %a:since("expath-binary", "1.0-20131203") function bin:pack-integer($in as xs:integer, $size as xs:integer) as xs:base64Binary external;
declare %a:since("expath-binary", "1.0-20131203") function bin:pack-integer($in as xs:integer, $size as xs:integer, $octet-order as xs:string) as xs:base64Binary external;
declare %a:since("expath-binary", "1.0-20131203") function bin:unpack-double($in as xs:base64Binary, $offset as xs:integer) as xs:double external;
declare %a:since("expath-binary", "1.0-20131203") function bin:unpack-double($in as xs:base64Binary, $offset as xs:integer, $octet-order as xs:string) as xs:double external;
declare %a:since("expath-binary", "1.0-20131203") function bin:unpack-float($in as xs:base64Binary, $offset as xs:integer) as xs:float external;
declare %a:since("expath-binary", "1.0-20131203") function bin:unpack-float($in as xs:base64Binary, $offset as xs:integer, $octet-order as xs:string) as xs:float external;
declare %a:since("expath-binary", "1.0-20131203") function bin:unpack-integer($in as xs:base64Binary, $offset as xs:integer, $size as xs:integer) as xs:integer external;
declare %a:since("expath-binary", "1.0-20131203") function bin:unpack-integer($in as xs:base64Binary, $offset as xs:integer, $size as xs:integer, $octet-order as xs:string) as xs:integer external;
declare %a:since("expath-binary", "1.0-20131203") function bin:unpack-unsigned-integer($in as xs:base64Binary, $offset as xs:integer, $size as xs:integer) as xs:integer external;
declare %a:since("expath-binary", "1.0-20131203") function bin:unpack-unsigned-integer($in as xs:base64Binary, $offset as xs:integer, $size as xs:integer, $octet-order as xs:string) as xs:integer external;
declare %a:since("expath-binary", "1.0-20131203") function bin:or($a as xs:base64Binary?, $b as xs:base64Binary?) as xs:base64Binary? external;
declare %a:since("expath-binary", "1.0-20131203") function bin:xor($a as xs:base64Binary?, $b as xs:base64Binary?) as xs:base64Binary? external;
declare %a:since("expath-binary", "1.0-20131203") function bin:and($a as xs:base64Binary?, $b as xs:base64Binary?) as xs:base64Binary? external;
declare %a:since("expath-binary", "1.0-20131203") function bin:not($in as xs:base64Binary?) as xs:base64Binary? external;
declare %a:since("expath-binary", "1.0-20131203") function bin:shift($in as xs:base64Binary?, $by as xs:integer) as xs:base64Binary? external;
