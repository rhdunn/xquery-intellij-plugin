xquery version "3.0" encoding "UTF-8";
(:~
 : XPath and XQuery Functions and Operators: Trigonometric and exponential functions
 :
 : @see https://www.w3.org/TR/2014/REC-xpath-functions-30-20140408/#trigonometry
 :
 : This documentation includes material copied from or derived from the XPath and
 : XQuery Functions and Operators 3.0 specification. Copyright © 2017 W3C®
 : (MIT, ERCIM, Keio, Beihang).
 :)
module namespace math = "http://www.w3.org/2005/xpath-functions/math";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare option o:requires "xpath-functions/3.0";

declare %a:since("xpath-functions", "3.0-20140408") function math:acos(
  $value as xs:double?
) as xs:double? external;

declare %a:since("xpath-functions", "3.0-20140408") function math:asin(
  $value as xs:double?
) as xs:double? external;

declare %a:since("xpath-functions", "3.0-20140408") function math:atan(
  $value as xs:double?
) as xs:double? external;

declare %a:since("xpath-functions", "3.0-20140408") function math:atan2(
  $y as xs:double,
  $x as xs:double
) as xs:double external;

declare %a:since("xpath-functions", "3.0-20140408") function math:cos(
  $radians as xs:double?
) as xs:double? external;

declare %a:since("xpath-functions", "3.0-20140408") function math:exp(
  $value as xs:double?
) as xs:double? external;

declare %a:since("xpath-functions", "3.0-20140408") function math:exp10(
  $value as xs:double?
) as xs:double? external;

declare %a:since("xpath-functions", "3.0-20140408") function math:log(
  $value as xs:double?
) as xs:double? external;

declare %a:since("xpath-functions", "3.0-20140408") function math:log10(
  $value as xs:double?
) as xs:double? external;

declare %a:since("xpath-functions", "3.0-20140408") function math:pi() as xs:double external;

declare %a:since("xpath-functions", "3.0-20140408") function math:pow(
  $x as xs:double?,
  $y as xs:numeric
) as xs:double? external;

declare %a:since("xpath-functions", "3.0-20140408") function math:sin(
  $radians as xs:double?
) as xs:double? external;

declare %a:since("xpath-functions", "3.0-20140408") function math:sqrt(
  $value as xs:double?
) as xs:double? external;

declare %a:since("xpath-functions", "3.0-20140408") function math:tan(
  $radians as xs:double?
) as xs:double? external;

declare %a:since("basex", "7.0") function math:e() as xs:double external;

declare %a:since("basex", "7.0") function math:sinh(
  $value as xs:double?
) as xs:double? external;

declare %a:since("basex", "7.0") function math:cosh(
  $value as xs:double?
) as xs:double? external;

declare %a:since("basex", "7.0") function math:tanh(
  $value as xs:double?
) as xs:double? external;

declare %a:restrict-until("$string", "basex", "9.1", "xs:string")
        %a:since("basex", "7.3")
        %a:until("basex", "11.0", "fn:hash") function math:crc32(
  $string as xs:string?
) as xs:hexBinary? external;

declare %a:since("basex", "7.3") %a:until("basex", "7.5", "random:uuid#0") function math:uuid() external;

declare %a:since("basex", "7.0") %a:until("basex", "7.5", "random:double#0") function math:random() external;
