xquery version "3.0";
(:~
 : eXist-db math extensions module functions
 :
 : @see http://exist-db.org/exist/apps/fundocs/view.html?uri=http://exist-db.org/xquery/math&location=java:org.exist.xquery.modules.math.MathModule&details=true
 :
 : This documentation includes material copied from or derived from the XPath and
 : XQuery Functions and Operators 3.1 specifications. Copyright © 2017 W3C®
 : (MIT, ERCIM, Keio, Beihang).
 :)
module namespace math-ext = "http://exist-db.org/xquery/math";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare %a:since("exist", "4.4") function math-ext:abs($x as xs:double) as xs:double external;
declare %a:since("exist", "4.4") %a:deprecated("exist", "4.4") %a:see-also("xpath-functions", "3.0-20140408", "math:acos") function math-ext:acos($x as xs:double) as xs:double external;
declare %a:since("exist", "4.4") %a:deprecated("exist", "4.4") %a:see-also("xpath-functions", "3.0-20140408", "math:asin") function math-ext:asin($x as xs:double) as xs:double external;
declare %a:since("exist", "4.4") %a:deprecated("exist", "4.4") %a:see-also("xpath-functions", "3.0-20140408", "math:atan") function math-ext:atan($x as xs:double) as xs:double external;
declare %a:since("exist", "4.4") %a:deprecated("exist", "4.4") %a:see-also("xpath-functions", "3.0-20140408", "math:atan2") function math-ext:atan2($y as xs:double, $x as xs:double) as xs:double external;
declare %a:since("exist", "4.4") %a:see-also("xpath-functions", "1.0-20070123", "fn:ceiling") function math-ext:ceil($x as xs:double) as xs:double external;
declare %a:since("exist", "4.4") %a:deprecated("exist", "4.4") %a:see-also("xpath-functions", "3.0-20140408", "math:cos") function math-ext:cos($x as xs:double) as xs:double external;
declare %a:since("exist", "4.4") function math-ext:degrees($radians as xs:double) as xs:double external;
(:~
 : Returns an approximation to the mathematical constant e.
 :
 : This function returns the <code>xs:double</code> value whose lexical
 : representation is 2.718281828459045e0.
 :)
declare %a:since("exist", "4.4") function math-ext:e() as xs:double external;
declare %a:since("exist", "4.4") %a:deprecated("exist", "4.4") %a:see-also("xpath-functions", "3.0-20140408", "math:cos") function math-ext:exp($x as xs:double) as xs:double external;
declare %a:since("exist", "4.4") %a:see-also("xpath-functions", "1.0-20070123", "fn:floor") function math-ext:floor($x as xs:double) as xs:double external;
declare %a:since("exist", "4.4") %a:deprecated("exist", "4.4") %a:see-also("xpath-functions", "3.0-20140408", "math:log") function math-ext:log($x as xs:double) as xs:double external;
(:~
 : Returns an approximation to the mathematical constant π.
 :
 : This function returns the <code>xs:double</code> value whose lexical
 : representation is 3.141592653589793e0.
 :)
declare %a:since("exist", "4.4") %a:deprecated("exist", "4.4") %a:see-also("xpath-functions", "3.0-20140408", "math:pi") function math-ext:pi() as xs:double external;
declare %a:since("exist", "4.4") %a:deprecated("exist", "4.4") %a:see-also("xpath-functions", "3.0-20140408", "math:pow") function math-ext:power($value as xs:double, $power as xs:double) as xs:double external;
declare %a:since("exist", "4.4") function math-ext:radians($degrees as xs:double) as xs:double external;
declare %a:since("exist", "4.4") function math-ext:random() as xs:double external;
declare %a:since("exist", "4.4") function math-ext:round($x as xs:double) as xs:double external;
declare %a:since("exist", "4.4") %a:deprecated("exist", "4.4") %a:see-also("xpath-functions", "3.0-20140408", "math:sin") function math-ext:sin($x as xs:double) as xs:double external;
declare %a:since("exist", "4.4") %a:deprecated("exist", "4.4") %a:see-also("xpath-functions", "3.0-20140408", "math:sqrt") function math-ext:sqrt($x as xs:double) as xs:double external;
declare %a:since("exist", "4.4") %a:deprecated("exist", "4.4") %a:see-also("xpath-functions", "3.0-20140408", "math:tan") function math-ext:tan($radians as xs:double) as xs:double external;
