xquery version "3.0";
(:~
: BaseX convert module functions
:
: @see http://docs.basex.org/wiki/Conversion_Module
:)
module namespace convert = "http://basex.org/modules/convert";

import module namespace a = "http://reecedunn.co.uk/xquery/annotations" at "res://reecedunn.co.uk/xquery/annotations.xqy";

declare %a:since("basex", "7.3") function convert:binary-to-string($bytes as xs:anyAtomicType) as xs:string external;
declare %a:since("basex", "7.3") function convert:binary-to-string($bytes as xs:anyAtomicType, $encoding as xs:string) as xs:string external;
declare %a:since("basex", "7.3") function convert:binary-to-string($bytes as xs:anyAtomicType, $encoding as xs:string, $fallback as xs:boolean) as xs:string external;
declare %a:since("basex", "7.3") function convert:string-to-base64($string as xs:string) as xs:base64Binary external;
declare %a:since("basex", "7.3") function convert:string-to-base64($string as xs:string, $encoding as xs:string) as xs:base64Binary external;
declare %a:since("basex", "7.3") function convert:string-to-hex($string as xs:string) as xs:hexBinary external;
declare %a:since("basex", "7.3") function convert:string-to-hex($string as xs:string, $encoding as xs:string) as xs:hexBinary external;
declare %a:since("basex", "7.3") function convert:integers-to-base64($integers as xs:integer*) as xs:base64Binary external;
declare %a:since("basex", "7.3") function convert:integers-to-hex($integers as xs:integer*) as xs:hexBinary external;
declare %a:since("basex", "7.3") function convert:binary-to-integer($binary as xs:anyAtomicType) as xs:integer* external;
declare %a:since("basex", "7.3") function convert:binary-to-bytes($binary as xs:anyAtomicType) as xs:byte* external;
declare %a:since("basex", "7.3") function convert:integer-to-base($number as xs:integer, $base as xs:integer) as xs:string external;
declare %a:since("basex", "7.3") function convert:integer-from-base($string as xs:string, $base as xs:integer) as xs:integer external;
declare %a:since("basex", "7.5") function convert:integer-to-dateTime($milliseconds as xs:integer) as xs:dateTime external;
declare %a:since("basex", "7.5") function convert:dateTime-to-integer($dateTime as xs:dateTime) as xs:integer external;
declare %a:since("basex", "7.5") function convert:integer-to-dayTime($milliseconds as xs:integer) as xs:dayTimeDuration external;
declare %a:since("basex", "7.5") function convert:dayTime-to-integer($dayTime as xs:dayTimeDuration) as xs:integer external;