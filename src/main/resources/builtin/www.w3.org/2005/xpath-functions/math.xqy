xquery version "3.0";

(:~
 : XPath and XQuery Functions and Operators: Trigonometric and exponential functions
 :
 : @see https://www.w3.org/TR/2014/REC-xpath-functions-30-20140408/#trigonometry
 :)
module  namespace math = "http://www.w3.org/2005/xpath-functions/math";
declare namespace xs   = "http://www.w3.org/2001/XMLSchema";

declare function math:acos($arg as xs:double?) as xs:double? external;
declare function math:asin($arg as xs:double?) as xs:double? external;
declare function math:atan($arg as xs:double?) as xs:double? external;
declare function math:atan2($y as xs:double, $x as xs:double) as xs:double external;
declare function math:cos($θ as xs:double?) as xs:double? external;
declare function math:exp($arg as xs:double?) as xs:double? external;
declare function math:exp10($arg as xs:double?) as xs:double? external;
declare function math:log($arg as xs:double?) as xs:double? external;
declare function math:log10($arg as xs:double?) as xs:double? external;
declare function math:pi() as xs:double external;
declare function math:pow($x as xs:double?, $y as xs:numeric) as xs:double? external;
declare function math:sin($θ as xs:double?) as xs:double? external;
declare function math:sqrt($arg as xs:double?) as xs:double? external;
declare function math:tan($θ as xs:double?) as xs:double? external;
