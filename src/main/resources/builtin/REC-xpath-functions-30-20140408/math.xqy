(: XPath and XQuery Functions and Operators 3.0
 : W3C Recommendation 08 April 2014
 :
 : Reference: https://www.w3.org/TR/2014/REC-xpath-functions-30-20140408/
 :)
xquery version "3.0";

module  namespace math = "http://www.w3.org/2005/xpath-functions/math";
declare namespace xs   = "http://www.w3.org/2001/XMLSchema";

declare function math:acos($θ as xs:double?) as xs:double? external; (: §4.8.12 :)
declare function math:asin($θ as xs:double?) as xs:double? external; (: §4.8.11 :)
declare function math:atan($θ as xs:double?) as xs:double? external; (: §4.8.13 :)
declare function math:atan2($y as xs:double, $x as xs:double) as xs:double external; (: §4.8.14 :)
declare function math:cos($θ as xs:double?) as xs:double? external; (: §4.8.9 :)
declare function math:exp($arg as xs:double?) as xs:double? external; (: §4.8.2 :)
declare function math:exp10($arg as xs:double?) as xs:double? external; (: §4.8.3 :)
declare function math:log($arg as xs:double?) as xs:double? external; (: §4.8.4 :)
declare function math:log10($arg as xs:double?) as xs:double? external; (: §4.8.5 :)
declare function math:pi() as xs:double external; (: §4.8.1 :)
declare function math:pow($x as xs:double?, $y as xs:numeric) as xs:double? external; (: §4.8.6 :)
declare function math:sin($θ as xs:double?) as xs:double? external; (: §4.8.8 :)
declare function math:sqrt($arg as xs:double?) as xs:double? external; (: §4.8.7 :)
declare function math:tan($θ as xs:double?) as xs:double? external; (: §4.8.10 :)
