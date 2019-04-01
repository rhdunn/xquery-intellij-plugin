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

(:~
 : Returns the arc cosine of the argument.
 :
 : If <code>$arg</code> is the empty sequence, the function returns the empty
 : sequence.
 :
 : Otherwise the result is the arc cosine of <code>$arg</code>, as defined in
 : the IEEE 754-2008 specification of the acos function applied to 64-bit binary
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
declare %a:since("xpath-functions", "3.0-20140408") function math:acos($arg as xs:double?) as xs:double? external;
(:~
 : Returns the arc sine of the argument.
 :
 : If <code>$arg</code> is the empty sequence, the function returns the empty
 : sequence.
 :
 : Otherwise the result is the arc sine of <code>$arg</code>, as defined in
 : the IEEE 754-2008 specification of the asin function applied to 64-bit binary
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
declare %a:since("xpath-functions", "3.0-20140408") function math:asin($arg as xs:double?) as xs:double? external;
(:~
 : Returns the arc tangent of the argument.
 :
 : If <code>$arg</code> is the empty sequence, the function returns the empty
 : sequence.
 :
 : Otherwise the result is the arc tangent of <code>$arg</code>, as defined in
 : the IEEE 754-2008 specification of the atan function applied to 64-bit binary
 : floating point values. The result is in the range -π/2 to +π/2 radians.
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
declare %a:since("xpath-functions", "3.0-20140408") function math:atan($arg as xs:double?) as xs:double? external;
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
declare %a:since("xpath-functions", "3.0-20140408") function math:atan2($y as xs:double, $x as xs:double) as xs:double external;
(:~
 : Returns the cosine of the argument. The argument is an angle in radians.
 :
 : If <code>$θ</code> is the empty sequence, the function returns the empty
 : sequence.
 :
 : Otherwise the result is the cosine of <code>$θ</code> (which is treated as
 : an angle in radians) as defined in the IEEE 754-2008 specification of the
 : <code>cos</code> function applied to 64-bit binary floating point values.
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
declare %a:since("xpath-functions", "3.0-20140408") function math:cos($θ as xs:double?) as xs:double? external;
declare %a:since("xpath-functions", "3.0-20140408") function math:exp($arg as xs:double?) as xs:double? external;
declare %a:since("xpath-functions", "3.0-20140408") function math:exp10($arg as xs:double?) as xs:double? external;
declare %a:since("xpath-functions", "3.0-20140408") function math:log($arg as xs:double?) as xs:double? external;
declare %a:since("xpath-functions", "3.0-20140408") function math:log10($arg as xs:double?) as xs:double? external;
(:~
 : Returns an approximation to the mathematical constant π.
 :
 : This function returns the <code>xs:double</code> value whose lexical
 : representation is 3.141592653589793e0.
 :)
declare %a:since("xpath-functions", "3.0-20140408") function math:pi() as xs:double external;
declare %a:since("xpath-functions", "3.0-20140408") function math:pow($x as xs:double?, $y as xs:numeric) as xs:double? external;
(:~
 : Returns the sine of the argument. The argument is an angle in radians.
 :
 : If <code>$θ</code> is the empty sequence, the function returns the empty
 : sequence.
 :
 : Otherwise the result is the sine of <code>$θ</code> (which is treated as
 : an angle in radians) as defined in the IEEE 754-2008 specification of the
 : <code>sin</code> function applied to 64-bit binary floating point values.
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
declare %a:since("xpath-functions", "3.0-20140408") function math:sin($θ as xs:double?) as xs:double? external;
declare %a:since("xpath-functions", "3.0-20140408") function math:sqrt($arg as xs:double?) as xs:double? external;
(:~
 : Returns the tangent of the argument. The argument is an angle in radians.
 :
 : If <code>$θ</code> is the empty sequence, the function returns the empty
 : sequence.
 :
 : Otherwise the result is the tangent of <code>$θ</code> (which is treated as
 : an angle in radians) as defined in the IEEE 754-2008 specification of the
 : <code>tan</code> function applied to 64-bit binary floating point values.
 :
 : <h1>Notes</h1>
 :
 : If <code>$θ</code> is positive or negative zero, the result is <code>$θ</code>.
 :
 : If <code>$θ</code> is positive or negative infinity, or <code>NaN</code>,
 : then the result is <code>NaN</code>.
 :)
declare %a:since("xpath-functions", "3.0-20140408") function math:tan($θ as xs:double?) as xs:double? external;
