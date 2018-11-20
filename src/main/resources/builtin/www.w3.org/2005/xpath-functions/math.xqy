xquery version "3.0";
(:~
 : XPath and XQuery Functions and Operators: Trigonometric and exponential functions
 :
 : @see https://www.w3.org/TR/2014/REC-xpath-functions-30-20140408/#trigonometry
 :
 : This software includes material copied from or derived from the XPath and
 : XQuery Functions and Operators 3.0 specification. Copyright © 2017 W3C®
 : (MIT, ERCIM, Keio, Beihang).
 :)
module namespace math = "http://www.w3.org/2005/xpath-functions/math";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare option o:requires "xpath-functions/3.0";

declare %a:since("xpath-functions", "3.0-20140408") function math:acos($arg as xs:double?) as xs:double? external;
declare %a:since("xpath-functions", "3.0-20140408") function math:asin($arg as xs:double?) as xs:double? external;
declare %a:since("xpath-functions", "3.0-20140408") function math:atan($arg as xs:double?) as xs:double? external;
declare %a:since("xpath-functions", "3.0-20140408") function math:atan2($y as xs:double, $x as xs:double) as xs:double external;
declare %a:since("xpath-functions", "3.0-20140408") function math:cos($θ as xs:double?) as xs:double? external;
declare %a:since("xpath-functions", "3.0-20140408") function math:exp($arg as xs:double?) as xs:double? external;
declare %a:since("xpath-functions", "3.0-20140408") function math:exp10($arg as xs:double?) as xs:double? external;
declare %a:since("xpath-functions", "3.0-20140408") function math:log($arg as xs:double?) as xs:double? external;
declare %a:since("xpath-functions", "3.0-20140408") function math:log10($arg as xs:double?) as xs:double? external;
declare %a:since("xpath-functions", "3.0-20140408") function math:pi() as xs:double external;
declare %a:since("xpath-functions", "3.0-20140408") function math:pow($x as xs:double?, $y as xs:numeric) as xs:double? external;
declare %a:since("xpath-functions", "3.0-20140408") function math:sin($θ as xs:double?) as xs:double? external;
declare %a:since("xpath-functions", "3.0-20140408") function math:sqrt($arg as xs:double?) as xs:double? external;
declare %a:since("xpath-functions", "3.0-20140408") function math:tan($θ as xs:double?) as xs:double? external;
