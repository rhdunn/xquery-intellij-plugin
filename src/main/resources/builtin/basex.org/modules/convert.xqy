xquery version "3.0";
(:~
 : BaseX convert module functions
 :
 : @see http://docs.basex.org/wiki/Conversion_Module
 :)
module namespace convert = "http://basex.org/modules/convert";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";

declare option a:requires "basex/7.3";

declare %a:since("basex", "7.3") function convert:binary-to-string($bytes as xs:anyAtomicType) as xs:string external;
declare %a:since("basex", "7.3") function convert:binary-to-string($bytes as xs:anyAtomicType, $encoding as xs:string) as xs:string external;
declare %a:since("basex", "8.5") function convert:binary-to-string($bytes as xs:anyAtomicType, $encoding as xs:string, $fallback as xs:boolean) as xs:string external;
declare %a:since("basex", "7.3") function convert:string-to-base64($string as xs:string) as xs:base64Binary external;
declare %a:since("basex", "7.3") function convert:string-to-base64($string as xs:string, $encoding as xs:string) as xs:base64Binary external;
declare %a:since("basex", "7.3") function convert:string-to-hex($string as xs:string) as xs:hexBinary external;
declare %a:since("basex", "7.3") function convert:string-to-hex($string as xs:string, $encoding as xs:string) as xs:hexBinary external;
declare %a:since("basex", "7.3") %a:until("basex", "9.0") %a:see-also("basex", "9.0", "convert:integers-to-base64") function convert:bytes-to-base64($input as xs:byte*) as xs:base64Binary (: $input as [7.3]xs:byte [8.7]xs:integer :) external;
declare %a:since("basex", "9.0") function convert:integers-to-base64($integers as xs:integer*) as xs:base64Binary external;
declare %a:since("basex", "7.3") %a:until("basex", "9.0") %a:see-also("basex", "9.0", "convert:integers-to-hex") function convert:bytes-to-hex($input as xs:integer*) as xs:hexBinary (: $input as [7.3]xs:byte [8.7]xs:integer :) external;
declare %a:since("basex", "7.3") function convert:integers-to-hex($integers as xs:integer*) as xs:hexBinary external;
declare %a:since("basex", "9.0") function convert:binary-to-integers($binary as xs:anyAtomicType) as xs:integer* external;
declare %a:since("basex", "7.3") function convert:binary-to-bytes($binary as xs:anyAtomicType) as xs:byte* external;
declare %a:since("basex", "7.3") function convert:integer-to-base($number as xs:integer, $base as xs:integer) as xs:string external;
declare %a:since("basex", "7.3") function convert:integer-from-base($string as xs:string, $base as xs:integer) as xs:integer external;
declare %a:since("basex", "7.5") function convert:integer-to-dateTime($milliseconds as xs:integer) as xs:dateTime external;
declare %a:since("basex", "7.5") function convert:dateTime-to-integer($dateTime as xs:dateTime) as xs:integer external;
declare %a:since("basex", "7.5") function convert:integer-to-dayTime($milliseconds as xs:integer) as xs:dayTimeDuration external;
declare %a:since("basex", "7.5") function convert:dayTime-to-integer($dayTime as xs:dayTimeDuration) as xs:integer external;