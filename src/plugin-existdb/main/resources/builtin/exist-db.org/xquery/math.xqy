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

declare %a:since("exist", "4.4") function math-ext:abs($x as xs:double) as xs:double external;
(:~
 : Returns the arc cosine of the argument.
 :
 : The result is the arc cosine of <code>$arg</code>, as defined in the IEEE
 : 754-2008 specification of the acos function applied to 64-bit binary
 : floating point values. The result is in the range zero to +π radians.
 :
 : <h1>Notes</h1>
 :
 : If <code>$arg</code> is <code>NaN</code>, or if its absolute value is greater
 : than one, then the result is <code>NaN</code>.
 :
 : In other cases the result is an <code>xs:double</code> value representing an
 : angle θ in radians in the range 0 &lt;= θ &lt;= +π.
 :)
declare %a:since("exist", "4.4") %a:deprecated("exist", "4.4") %a:see-also("xpath-functions", "3.0-20140408", "math:acos") function math-ext:acos($x as xs:double) as xs:double external;
(:~
 : Returns the arc sine of the argument.
 :
 : The result is the arc sine of <code>$arg</code>, as defined in the IEEE
 : 754-2008 specification of the asin function applied to 64-bit binary
 : floating point values. The result is in the range -π/2 to +π/2 radians.
 :
 : <h1>Notes</h1>
 :
 : If <code>$arg</code> is <code>NaN</code>, or if its absolute value is greater
 : than one, then the result is <code>NaN</code>.
 :
 : In other cases the result is an <code>xs:double</code> value representing an
 : angle θ in radians in the range -π/2 &lt;= θ &lt;= +π/2.
 :)
declare %a:since("exist", "4.4") %a:deprecated("exist", "4.4") %a:see-also("xpath-functions", "3.0-20140408", "math:asin") function math-ext:asin($x as xs:double) as xs:double external;
(:~
 : Returns the arc tangent of the argument.
 :
 : The result is the arc tangent of <code>$arg</code>, as defined in the IEEE
 : 754-2008 specification of the atan function applied to 64-bit binary floating
 : point values. The result is in the range -π/2 to +π/2 radians.
 :
 : <h1>Notes</h1>
 :
 : If <code>$arg</code> is positive or negative infinity, the result is
 : positive or negative π/2.
 :
 : If <code>$arg</code> is positive or negative zero, the result is
 : <code>$arg</code>.
 :
 : If <code>$arg</code> is <code>NaN</code>, then the result is <code>NaN</code>.
 :
 : In other cases the result is an <code>xs:double</code> value representing an
 : angle θ in radians in the range -π/2 &lt;= θ &lt;= +π/2.
 :)
declare %a:since("exist", "4.4") %a:deprecated("exist", "4.4") %a:see-also("xpath-functions", "3.0-20140408", "math:atan") function math-ext:atan($x as xs:double) as xs:double external;
(:~
 : Returns the angle in radians subtended at the origin by the point on a plane
 : with coordinates (x, y) and the positive x-axis.
 :
 : The result is the value of <code>atan2(y, x)</code> as defined in the IEEE
 : 754-2008 specification of the <code>atan2</code> function applied to 64-bit
 : binary floating point values. The result is in the range -π to +π radians.
 :
 : <h1>Notes</h1>
 :
 : If either argument is <code>NaN</code> then the result is <code>NaN</code>.
 :
 : If <code>$y</code> is positive or negative, and <code>$x</code> is positive
 : and finite, then (subject to rules for overflow, underflow and approximation)
 : the value of <code>math:atan2($y, $x)</code> is <code>math:atan($y div $x)</code>.
 :
 : If <code>$y</code> is positive and <code>$x</code> is negative and finite,
 : then (subject to the same caveats) the value of <code>math:atan2($y, $x)</code>
 : is <code>math:atan($y div $x) + math:pi()</code>.
 :
 : If <code>$y</code> is negative and <code>$x</code> is negative and finite,
 : then (subject to the same caveats) the value of <code>math:atan2($y, $x)</code>
 : is <code>math:atan($y div $x) - math:pi()</code>.
 :)
declare %a:since("exist", "4.4") %a:deprecated("exist", "4.4") %a:see-also("xpath-functions", "3.0-20140408", "math:atan2") function math-ext:atan2($y as xs:double, $x as xs:double) as xs:double external;
declare %a:since("exist", "4.4") %a:see-also("xpath-functions", "1.0-20070123", "fn:ceiling") function math-ext:ceil($x as xs:double) as xs:double external;
(:~
 : Returns the cosine of the argument. The argument is an angle in radians.
 :
 : The result is the cosine of <code>$θ</code> (which is treated as an angle in
 : radians) as defined in the IEEE 754-2008 specification of the <code>cos</code>
 : function applied to 64-bit binary floating point values.
 :
 : <h1>Notes</h1>
 :
 : If <code>$θ</code> is positive or negative zero, the result is <code>$θ</code>.
 :
 : If <code>$θ</code> is positive or negative infinity, or <code>NaN</code>,
 : then the result is <code>NaN</code>.
 :
 : Otherwise the result is always in the range -1.0e0 to +1.0e0.
 :)
declare %a:since("exist", "4.4") %a:deprecated("exist", "4.4") %a:see-also("xpath-functions", "3.0-20140408", "math:cos") function math-ext:cos($θ as xs:double) as xs:double external;
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
(:~
 : Returns the sine of the argument. The argument is an angle in radians.
 :
 : The result is the sine of <code>$θ</code> (which is treated as an angle in
 : radians) as defined in the IEEE 754-2008 specification of the <code>sin</code>
 : function applied to 64-bit binary floating point values.
 :
 : <h1>Notes</h1>
 :
 : If <code>$θ</code> is positive or negative zero, the result is <code>$θ</code>.
 :
 : If <code>$θ</code> is positive or negative infinity, or <code>NaN</code>,
 : then the result is <code>NaN</code>.
 :
 : Otherwise the result is always in the range -1.0e0 to +1.0e0.
 :)
declare %a:since("exist", "4.4") %a:deprecated("exist", "4.4") %a:see-also("xpath-functions", "3.0-20140408", "math:sin") function math-ext:sin($θ as xs:double) as xs:double external;
declare %a:since("exist", "4.4") %a:deprecated("exist", "4.4") %a:see-also("xpath-functions", "3.0-20140408", "math:sqrt") function math-ext:sqrt($x as xs:double) as xs:double external;
(:~
 : Returns the tangent of the argument. The argument is an angle in radians.
 :
 : The result is the tangent of <code>$θ</code> (which is treated as an angle in
 : radians) as defined in the IEEE 754-2008 specification of the <code>tan</code>
 : function applied to 64-bit binary floating point values.
 :
 : <h1>Notes</h1>
 :
 : If <code>$θ</code> is positive or negative zero, the result is <code>$θ</code>.
 :
 : If <code>$θ</code> is positive or negative infinity, or <code>NaN</code>,
 : then the result is <code>NaN</code>.
 :)
declare %a:since("exist", "4.4") %a:deprecated("exist", "4.4") %a:see-also("xpath-functions", "3.0-20140408", "math:tan") function math-ext:tan($θ as xs:double) as xs:double external;
