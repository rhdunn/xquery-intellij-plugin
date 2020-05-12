xquery version "3.0" encoding "UTF-8";
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

declare %a:since("exist", "4.4") %a:see-also("xpath-functions", "1.0-20070123", "fn:abs") function math-ext:abs($arg as xs:double) as xs:double external;
declare %a:since("exist", "4.4") %a:deprecated("exist", "4.4") %a:see-also("xpath-functions", "3.0-20140408", "math:acos") function math-ext:acos($arg as xs:double) as xs:double external;
declare %a:since("exist", "4.4") %a:deprecated("exist", "4.4") %a:see-also("xpath-functions", "3.0-20140408", "math:asin") function math-ext:asin($arg as xs:double) as xs:double external;
declare %a:since("exist", "4.4") %a:deprecated("exist", "4.4") %a:see-also("xpath-functions", "3.0-20140408", "math:atan") function math-ext:atan($arg as xs:double) as xs:double external;
declare %a:since("exist", "4.4") %a:deprecated("exist", "4.4") %a:see-also("xpath-functions", "3.0-20140408", "math:atan2") function math-ext:atan2($y as xs:double, $x as xs:double) as xs:double external;
declare %a:since("exist", "4.4") %a:see-also("xpath-functions", "1.0-20070123", "fn:ceiling") function math-ext:ceil($arg as xs:double) as xs:double external;
declare %a:since("exist", "4.4") %a:deprecated("exist", "4.4") %a:see-also("xpath-functions", "3.0-20140408", "math:cos") function math-ext:cos($θ as xs:double) as xs:double external;
declare %a:since("exist", "4.4") function math-ext:degrees($arg as xs:double) as xs:double external;
declare %a:since("exist", "4.4") function math-ext:e() as xs:double external;
declare %a:since("exist", "4.4") %a:deprecated("exist", "4.4") %a:see-also("xpath-functions", "3.0-20140408", "math:exp") function math-ext:exp($arg as xs:double) as xs:double external;
declare %a:since("exist", "4.4") %a:see-also("xpath-functions", "1.0-20070123", "fn:floor") function math-ext:floor($arg as xs:double) as xs:double external;
declare %a:since("exist", "4.4") %a:deprecated("exist", "4.4") %a:see-also("xpath-functions", "3.0-20140408", "math:log") function math-ext:log($arg as xs:double) as xs:double external;
declare %a:since("exist", "4.4") %a:deprecated("exist", "4.4") %a:see-also("xpath-functions", "3.0-20140408", "math:pi") function math-ext:pi() as xs:double external;
declare %a:since("exist", "4.4") %a:deprecated("exist", "4.4") %a:see-also("xpath-functions", "3.0-20140408", "math:pow") function math-ext:power($x as xs:double, $y as xs:double) as xs:double external;
declare %a:since("exist", "4.4") function math-ext:radians($arg as xs:double) as xs:double external;
declare %a:since("exist", "4.4") function math-ext:random() as xs:double external;
declare %a:since("exist", "4.4") function math-ext:round($arg as xs:double) as xs:double external;
declare %a:since("exist", "4.4") %a:deprecated("exist", "4.4") %a:see-also("xpath-functions", "3.0-20140408", "math:sin") function math-ext:sin($θ as xs:double) as xs:double external;
declare %a:since("exist", "4.4") %a:deprecated("exist", "4.4") %a:see-also("xpath-functions", "3.0-20140408", "math:sqrt") function math-ext:sqrt($arg as xs:double) as xs:double external;
declare %a:since("exist", "4.4") %a:deprecated("exist", "4.4") %a:see-also("xpath-functions", "3.0-20140408", "math:tan") function math-ext:tan($θ as xs:double) as xs:double external;
