xquery version "1.0-ml" encoding "UTF-8";
(:~
 : MarkLogic math functions
 :
 : @see https://docs.marklogic.com/math
 :
 : This documentation includes material copied from or derived from the XPath and
 : XQuery Functions and Operators 3.1 specifications. Copyright © 2017 W3C®
 : (MIT, ERCIM, Keio, Beihang).
 :)
module namespace math = "http://marklogic.com/xdmp/math";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare option o:requires "marklogic/5.0";

(:~
 : Returns an approximation to the mathematical constant π.
 :
 : This function returns the <code>xs:double</code> value whose lexical
 : representation is 3.14159265358979e0.
 :)
declare %a:since("marklogic", "6.0") %a:until("marklogic", "7.0", "math:pi#0") function math:PI() as xs:double external;
(:~
 : Returns the arc cosine of the argument.
 :
 : The result is the arc cosine of <code>$arg</code>, as defined in the IEEE
 : 754-2008 specification of the acos function applied to 64-bit binary
 : floating point values. The result is in the range zero to +π radians.
 :
 : <h1>Notes</h1>
 :
 : If <code>$arg</code> is outside the domain of the function, that is the
 : absolute value is greater than one, or if an overflow or underflow occurs,
 : the dynamic error <code>XDMP-DOMAIN</code> is produced.
 :
 : If <code>$arg</code> is <code>NaN</code>, then the result is <code>NaN</code>.
 :
 : In other cases the result is an <code>xs:double</code> value representing an
 : angle θ in radians in the range 0 &lt;= θ &lt;= +π.
 :)
declare %a:since("marklogic", "5.0") function math:acos($arg as xs:double) as xs:double external;
(:~
 : Returns the arc sine of the argument.
 :
 : The result is the arc sine of <code>$arg</code>, as defined in the IEEE
 : 754-2008 specification of the asin function applied to 64-bit binary
 : floating point values. The result is in the range -π/2 to +π/2 radians.
 :
 : <h1>Notes</h1>
 :
 : If <code>$arg</code> is outside the domain of the function, that is the
 : absolute value is greater than one, or if an overflow or underflow occurs,
 : the dynamic error <code>XDMP-DOMAIN</code> is produced.
 :
 : If <code>$arg is positive or negative zero, or <code>NaN</code>, the result
 : is <code>$arg</code>.
 :
 : In other cases the result is an <code>xs:double</code> value representing an
 : angle θ in radians in the range -π/2 &lt;= θ &lt;= +π/2.
 :)
declare %a:since("marklogic", "5.0") function math:asin($arg as xs:double) as xs:double external;
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
declare %a:since("marklogic", "5.0") function math:atan($arg as xs:double) as xs:double external;
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
declare %a:since("marklogic", "5.0") function math:atan2($y as xs:double, $x as xs:double) as xs:double external;
(:~
 : Rounds <code>$arg</code> upwards to a whole number.
 :
 : The function returns the smallest (closest to negative infinity) number with
 : no fractional part that is not less than the value of <code>$arg</code>.
 :
 : If the argument is positive zero, then positive zero is returned. If the
 : argument is negative zero, then negative zero is returned. If the argument
 : is less than zero and greater than -1, negative zero is returned.
 :)
declare %a:since("marklogic", "5.0") %a:see-also("xpath-functions", "1.0-20070123", "fn:ceil") function math:ceil($arg as xs:double) as xs:double external;
declare %a:since("marklogic", "6.0") function math:correlation($arg as json:array*) as xs:double? external;
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
 : If <code>$θ</code> is positive or negative infinity, the dynamic error
 : <code>XDMP-DOMAIN</code> is produced.
 :
 : If <code>$θ</code> is <code>NaN</code>, then the result is <code>NaN</code>.
 :
 : Otherwise the result is always in the range -1.0e0 to +1.0e0.
 :)
declare %a:since("marklogic", "5.0") function math:cos($θ as xs:double) as xs:double external;
(:~
 : Returns the hyperbolic cosine of the argument. The argument is an angle in
 : radians.
 :
 : The result is the hyperbolic cosine of <code>$θ</code> (which is treated as
 : an angle in radians) as defined in the IEEE 754-2008 specification of the
 : <code>cosh</code> function applied to 64-bit binary floating point values.
 :
 : <h1>Notes</h1>
 :
 : If <code>$θ</code> is positive or negative zero, <code>NaN</code>, or positive
 : or negative infinity, the result is <code>$θ</code>.
 :)
declare %a:since("marklogic", "5.0") function math:cosh($θ as xs:double) as xs:double external;
(:~
 : Returns the cotangent of the argument. The argument is an angle in radians.
 :
 : The result is the cotangent of <code>$θ</code> (which is treated as an angle in
 : radians), which is the reciprocal of the argument.
 :
 : This function is equivalent to <code>1 div math:tan($θ)</code>.
 :
 : <h1>Notes</h1>
 :
 : If <code>$θ</code> is positive or negative zero, the result is positive or
 : negative infinity, depending on the sign of <code>$θ</code>.
 :
 : If <code>$θ</code> is positive or negative infinity, the dynamic error
 : <code>XDMP-DOMAIN</code> is produced.
 :
 : If <code>$θ</code> is <code>NaN</code>, then the result is <code>NaN</code>.
 :)
declare %a:since("marklogic", "6.0") function math:cot($θ as xs:double) as xs:double external;
declare %a:since("marklogic", "6.0") function math:covariance($arg as json:array*) as xs:double? external;
declare %a:since("marklogic", "6.0") function math:covariance-p($arg as json:array*) as xs:double? external;
(:~
 : Returns the radians argument in degrees.
 :
 : This function converts <code>$arg</code>, specified in radians, to degrees.
 :
 : <h1>Notes</h1>
 :
 : If <code>$arg</code> is <code>NaN</code>, positive or negative zero, or
 : positive or negative infinity, then the result is the same as the argument.
 :
 : Otherwise, the result is the argument divided by π/180.
 :
 : <h1>Notes</h1>
 :
 : The result is not wrapped, so <code>math:degrees(math:pi() * 4)</code> is
 : <code>720</code>.
 :)
declare %a:since("marklogic", "6.0") function math:degrees($arg as xs:double) as xs:double external;
(:~
 : Returns the value of e<sup>x</sup>.
 :
 : The result is the mathematical constant <code>e</code> raised to the power of
 : <code>$arg</code>, as defined in the IEEE 754-2008 specification of the
 : <code>exp</code> function applied to 64-bit binary floating point values.
 :)
declare %a:since("marklogic", "5.0") function math:exp($arg as xs:double) as xs:double external;
(:~
 : Returns the absolute value of <code>$arg</code>.
 :
 : If <code>$arg</code> is negative the function returns <code>-$arg</code>,
 : otherwise it returns <code>$arg</code>.
 :
 : If the argument is positive zero or negative zero, then positive zero is
 : returned. If the argument is positive or negative infinity, positive
 : infinity is returned.
 :)
declare %a:since("marklogic", "5.0") %a:see-also("xpath-functions", "1.0-20070123", "fn:abs") function math:fabs($x as xs:double) as xs:double external;
(:~
 : Rounds <code>$arg</code> downwards to a whole number.
 :
 : The return is the largest (closest to positive infinity) number with no
 : fractional part that is not greater than the value of <code>$arg</code>.
 :
 : If the argument is positive zero, then positive zero is returned. If the
 : argument is negative zero, then negative zero is returned.
 :)
declare %a:since("marklogic", "5.0") function math:floor($arg as xs:double) as xs:double external;
(:~
 : Returns the remainder of dividing x by y.
 :
 : This function returns the value of the expression <code>$x mod $y</code>.
 :
 : If <code>$y</code> is <code>0e0</code>, the dynamic error XDMP-DOMAIN is
 : produced instead of the FOAR0001 division by zero dynamic error that
 : <code>mod</code> raises.
 :)
declare %a:since("marklogic", "5.0") function math:fmod($x as xs:double, $y as xs:double) as xs:double external;
(:~
 : Returns the mantissa and exponent of the argument.
 :
 : This function returns a two value sequence, where the first value is the
 : mntissa of <code>$arg</code> and the second value is the exponent of
 : <code>$arg</code>.
 :
 : <h1>Notes</h1>
 :
 : If <code>$arg</code> is positive or negative infinity, or <code>NaN</code>,
 : the result is <code>($arg, -1)</code>.
 :
 : If <code>$arg</code> is positive or negative zero, the first item in the
 : sequence is <code>$arg</code> and the second item is 0e0.
 :)
declare %a:since("marklogic", "5.0") function math:frexp($arg as xs:double) as (xs:double, xs:integer) external;
(:~
 : Returns the floating point that corresponds to the given mantissa and exponent.
 :
 : This function returns the floating point number that has <code>$mantissa</code>
 : as the mantissa of the floating point number, and <code>$exponent</code> as the
 : exponent.
 :
 : This function is equivalent to <code>$mantissa * math:pow(2, $exponent)</code>.
 :
 : <h1>Notes</h1>
 :
 : If <code>$mantissa</code> is positive or negative infinity, <code>NaN</code>,
 : or positive or negative zero, the result is <code>$mantissa</code>.
 :)
declare %a:since("marklogic", "5.0") function math:ldexp($mantissa as xs:double, $exponent as xs:integer) as xs:double external;
declare %a:since("marklogic", "6.0") function math:linear-model($arg as json:array*) as math:linear-model? external;
declare %a:since("marklogic", "6.0") function math:linear-model-coeff($linear-model as math:linear-model) as xs:double* external;
declare %a:since("marklogic", "6.0") function math:linear-model-intercept($linear-model as math:linear-model) as xs:double external;
declare %a:since("marklogic", "6.0") function math:linear-model-rsquared($linear-model as math:linear-model) as xs:double external;
(:~
 : Returns the natural logarithm of the argument.
 :
 : The result is the natural logarithm of <code>$arg</code>, as defined in the
 : IEEE 754-2008 specification of the <code>log</code> function applied to
 : 64-bit binary floating point values.
 :)
declare %a:since("marklogic", "5.0") function math:log($arg as xs:double) as xs:double external;
(:~
 : Returns the base-ten logarithm of the argument.
 :
 : The result is the base-10 logarithm of <code>$arg</code>, as defined in the
 : IEEE 754-2008 specification of the <code>log10</code> function applied to
 : 64-bit binary floating point values.
 :)
declare %a:since("marklogic", "5.0") function math:log10($arg as xs:double) as xs:double external;
declare %a:since("marklogic", "6.0") function math:median($arg as xs:double*) as xs:double? external;
declare %a:since("marklogic", "6.0") function math:mode($arg as xs:anyAtomicType*) as xs:anyAtomicType* external;
declare %a:since("marklogic", "6.0") function math:mode($arg as xs:anyAtomicType*, $options as xs:string*) as xs:anyAtomicType* external;
(:~
 : Returns the fraction and integral part of the argument.
 :
 : This function returns a two value sequence, where the first value is the
 : fraction of <code>$arg</code> and the second value is the integral part
 : of <code>$arg</code>.
 :
 : This function is equivalent to <code>($arg mod 1, math:floor($arg))</code>.
 :)
declare %a:restrict-until("return", "marklogic", "8.0", "(xs:double,xs:double)")
        %a:restrict-since("return", "marklogic", "8.0", "(xs:double,xs:integer)")
        %a:since("marklogic", "5.0") function math:modf($arg as xs:double) as (xs:double,(xs:double|xs:integer)) external;
declare %a:since("marklogic", "6.0") function math:percent-rank($arg as xs:anyAtomicType*, $value as xs:anyAtomicType) as xs:double? external;
declare %a:since("marklogic", "6.0") function math:percent-rank($arg as xs:anyAtomicType*, $value as xs:anyAtomicType, $options as xs:string*) as xs:double? external;
declare %a:since("marklogic", "6.0") function math:percentile($arg as xs:double*, $p as xs:double*) as xs:double* external;
(:~
 : Returns an approximation to the mathematical constant π.
 :
 : This function was replaced by <code>math:pi()</code> in MarkLogic 7.0.
 :
 : This function returns the <code>xs:double</code> value whose lexical
 : representation is 3.14159265358979e0.
 :)
declare %a:since("marklogic", "7.0") function math:pi() as xs:double external;
(:~
 : Returns the value of x<sup>y</sup>.
 :
 : The result is the value of <code>$x</code> raised to the power of <code>$y</code>
 : as defined in the IEEE 754-2008 specification of the <code>pow</code> function
 : applied to two 64-bit binary floating point values.
 :)
declare %a:since("marklogic", "5.0") function math:pow($x as xs:double, $y as xs:double) as xs:double external;
(:~
 : Returns the degrees argument in radians.
 :
 : This function converts <code>$arg</code>, specified in degrees, to radians.
 :
 : <h1>Notes</h1>
 :
 : If <code>$arg</code> is <code>NaN</code>, positive or negative zero, or
 : positive or negative infinity, then the result is the same as the argument.
 :
 : Otherwise, the result is the argument multiplied by π/180.
 :
 : <h1>Notes</h1>
 :
 : The result is not wrapped, so <code>math:radians(720)</code> is
 : <code>math:pi() * 4</code>.
 :)
declare %a:since("marklogic", "6.0") function math:radians($x as xs:double) as xs:double external;
declare %a:since("marklogic", "6.0") function math:rank($arg1 as xs:anyAtomicType*, $arg2 as xs:anyAtomicType) as xs:integer? external;
declare %a:since("marklogic", "6.0") function math:rank($arg1 as xs:anyAtomicType*, $arg2 as xs:anyAtomicType, $options as xs:string*) as xs:integer? external;
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
 : If <code>$θ</code> is positive or negative infinity, the dynamic error
 : <code>XDMP-DOMAIN</code> is produced.
 :
 : If <code>$θ</code> is <code>NaN</code>, then the result is <code>NaN</code>.
 :
 : Otherwise the result is always in the range -1.0e0 to +1.0e0.
 :)
declare %a:since("marklogic", "5.0") function math:sin($θ as xs:double) as xs:double external;
(:~
 : Returns the hyperbolic sine of the argument. The argument is an angle in
 : radians.
 :
 : The result is the hyperbolic sine of <code>$θ</code> (which is treated as an
 : angle in radians) as defined in the IEEE 754-2008 specification of the
 : <code>sinh</code> function applied to 64-bit binary floating point values.
 :
 : <h1>Notes</h1>
 :
 : If <code>$θ</code> is positive or negative zero, <code>NaN</code>, or positive
 : or negative infinity, the result is <code>$θ</code>.
 :)
declare %a:since("marklogic", "5.0") function math:sinh($θ as xs:double) as xs:double external;
(:~
 : Returns the non-negative square root of the argument.
 :
 : The result is the mathematical non-negative square root of <code>$arg</code>
 : as defined in the IEEE 754-2008 specification of the <code>squareRoot</code>
 : function applied to 64-bit binary floating point values.
 :
 : <h1>Notes</h1>
 :
 : If <code>$arg</code> is positive or negative zero, positive infinity, or
 : <code>NaN</code>, then the result is <code>$arg</code>. (Negative zero is
 : the only case where the result can have negative sign.)
 :
 : If <code>$arg</code> is negative infinity, the dynamic error
 : <code>XDMP-DOMAIN</code> is produced.
 :)
declare %a:since("marklogic", "5.0") function math:sqrt($x as xs:double) as xs:double external;
declare %a:since("marklogic", "6.0") function math:stddev($arg as xs:double*) as xs:double? external;
declare %a:since("marklogic", "6.0") function math:stddev-p($arg as xs:double*) as xs:double? external;
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
 : If <code>$θ</code> is positive or negative infinity, the dynamic error
 : <code>XDMP-DOMAIN</code> is produced.
 :
 : If <code>$θ</code> is <code>NaN</code>, then the result is <code>NaN</code>.
 :)
declare %a:since("marklogic", "5.0") function math:tan($θ as xs:double) as xs:double external;
(:~
 : Returns the hyperbolic tangent of the argument. The argument is an angle in
 : radians.
 :
 : The result is the hyperbolic tangent of <code>$θ</code> (which is treated as
 : an angle in radians) as defined in the IEEE 754-2008 specification of the
 : <code>tanh</code> function applied to 64-bit binary floating point values.
 :
 : <h1>Notes</h1>
 :
 : If <code>$θ</code> is positive or negative zero, or <code>NaN</code>, the
 : result is <code>$θ</code>.
 :
 : If <code>$θ</code> is positive or negative infinity, the result is positive
 : or negative one depending on the sign of <code>$θ</code>.
 :)
declare %a:since("marklogic", "5.0") function math:tanh($θ as xs:double) as xs:double external;
(:~
 : Rounds a value to the nearest whole number, rounding upwards if two such
 : values are equally near.
 :
 : If <code>$arg</code> is the empty sequence, the function returns the empty
 : sequence.
 :
 : The function returns the nearest whole number value to <code>$arg</code>.
 : If two such values are equally near (for example, if the fractional part
 : in <code>$arg</code> is exactly <code>.5</code>), the function returns the
 : one that is closest to positive infinity.
 :
 : For the four types <code>xs:float</code>, <code>xs:double</code>,
 : <code>xs:decimal</code> and <code>xs:integer</code>, it is guaranteed that
 : if the type of <code>$arg</code> is an instance of type T then the result
 : will also be an instance of T. The result may also be an instance of a type
 : derived from one of these four by restriction. For example, if
 : <code>$arg</code> is an instance of <code>xs:decimal</code>, then the result
 : may be an instance of <code>xs:integer</code>.
 :
 : This function is equivalent to <code>math:round($arg, 0)</code>.
 :
 : When <code>$arg</code> is of type <code>xs:float</code> and <code>xs:double</code>:
 :
 : <ol>
 :    <li>If <code>$arg</code> is <code>NaN</code>, positive or negative zero,
 :        or positive or negative infinity, then the result is the same as the
 :        argument.</li>
 :    <li>For other values, the argument is cast to <code>xs:decimal</code>
 :        using an implementation of <code>xs:decimal</code> that imposes no
 :        limits on the number of digits that can be represented. The function
 :        is applied to this <code>xs:decimal</code> value, and the resulting
 :        <code>xs:decimal</code> is cast back to <code>xs:float</code> or
 :        <code>xs:double</code> as appropriate to form the function result. If
 :        the resulting <code>xs:decimal</code> value is zero, then positive or
 :        negative zero is returned according to the sign of <code>$arg</code>.</li>
 : </ol>
 :)
declare %a:since("marklogic", "8.0") function math:trunc($arg as xs:numeric?) as xs:numeric? external;
(:~
 : Rounds a value to a specified number of decimal places, rounding upwards if
 : two such values are equally near.
 :
 : If <code>$arg</code> is the empty sequence, the function returns the empty
 : sequence.
 :
 : The function returns the nearest (that is, numerically closest) value to
 : <code>$arg</code> that is a multiple of ten to the power of minus
 : <code>$precision</code>. If two such values are equally near (for example,
 : if the fractional part in <code>$arg</code> is exactly <code>.5</code>),
 : the function returns the one that is closest to positive infinity.
 :
 : For the four types <code>xs:float</code>, <code>xs:double</code>,
 : <code>xs:decimal</code> and <code>xs:integer</code>, it is guaranteed that
 : if the type of <code>$arg</code> is an instance of type T then the result
 : will also be an instance of T. The result may also be an instance of a type
 : derived from one of these four by restriction. For example, if
 : <code>$arg</code> is an instance of <code>xs:decimal</code> and
 : <code>$precision</code> is less than one, then the result may be an
 : instance of <code>xs:integer</code>.
 :
 : When <code>$arg</code> is of type <code>xs:float</code> and <code>xs:double</code>:
 :
 : <ol>
 :    <li>If <code>$arg</code> is <code>NaN</code>, positive or negative zero,
 :        or positive or negative infinity, then the result is the same as the
 :        argument.</li>
 :    <li>For other values, the argument is cast to <code>xs:decimal</code>
 :        using an implementation of <code>xs:decimal</code> that imposes no
 :        limits on the number of digits that can be represented. The function
 :        is applied to this <code>xs:decimal</code> value, and the resulting
 :        <code>xs:decimal</code> is cast back to <code>xs:float</code> or
 :        <code>xs:double</code> as appropriate to form the function result. If
 :        the resulting <code>xs:decimal</code> value is zero, then positive or
 :        negative zero is returned according to the sign of <code>$arg</code>.</li>
 : </ol>
 :
 : <h1>Notes</h1>
 :
 : This function is typically used with a non-zero <code>$precision</code> in
 : financial applications where the argument is of type <code>xs:decimal</code>.
 : For arguments of type <code>xs:float</code> and <code>xs:double</code> the
 : results may be counter-intuitive. For example, consider
 : <code>math:round(35.425e0, 2)</code>. The result is not <code>35.43</code>, as
 : might be expected, but <code>35.42</code>. This is because the
 : <code>xs:double</code> written as <code>35.425e0</code> has an exact value
 : equal to <code>35.42499999999...</code>, which is closer to
 : <code>35.42</code> than to <code>35.43</code>.
 :)
declare %a:since("marklogic", "6.0") function math:trunc($arg as xs:numeric?, $precision as xs:integer) as xs:numeric? external;
declare %a:since("marklogic", "6.0") function math:variance($arg as xs:double*) as xs:double? external;
declare %a:since("marklogic", "6.0") function math:variance-p($arg as xs:double*) as xs:double? external;
