xquery version "1.0-ml";
(:~
 : MarkLogic math functions
 :
 : @see https://docs.marklogic.com/math
 :)
module namespace math = "http://marklogic.com/xdmp/math";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";

declare option a:requires "marklogic/5.0";

declare %a:since("marklogic", "6.0") %a:until("marklogic", "7.0", "math:pi#0") function math:PI() as xs:double external;
declare %a:since("marklogic", "5.0") function math:acos($x as xs:double) as xs:double external;
declare %a:since("marklogic", "5.0") function math:asin($x as xs:double) as xs:double external;
declare %a:since("marklogic", "5.0") function math:atan($x as xs:double) as xs:double external;
declare %a:since("marklogic", "5.0") function math:atan2($y as xs:double, $x as xs:double) as xs:double external;
declare %a:since("marklogic", "5.0") function math:ceil($x as xs:double) as xs:double external;
declare %a:since("marklogic", "6.0") function math:correlation($arg as json:array*) as xs:double? external;
declare %a:since("marklogic", "5.0") function math:cos($x as xs:double) as xs:double external;
declare %a:since("marklogic", "5.0") function math:cosh($x as xs:double) as xs:double external;
declare %a:since("marklogic", "6.0") function math:cot($x as xs:double) as xs:double external;
declare %a:since("marklogic", "6.0") function math:covariance($arg as json:array*) as xs:double? external;
declare %a:since("marklogic", "6.0") function math:covariance-p($arg as json:array*) as xs:double? external;
declare %a:since("marklogic", "6.0") function math:degrees($x as xs:double) as xs:double external;
declare %a:since("marklogic", "5.0") function math:exp($x as xs:double) as xs:double external;
declare %a:since("marklogic", "5.0") function math:fabs($x as xs:double) as xs:double external;
declare %a:since("marklogic", "5.0") function math:floor($x as xs:double) as xs:double external;
declare %a:since("marklogic", "5.0") function math:fmod($x as xs:double, $y as xs:double) as xs:double external;
declare %a:since("marklogic", "5.0") function math:frexp($x as xs:double) as (xs:double, xs:integer) external;
declare %a:since("marklogic", "5.0") function math:ldexp($y as xs:double, $i as xs:integer) as xs:double external;
declare %a:since("marklogic", "6.0") function math:linear-model($arg as json:array*) as math:linear-model? external;
declare %a:since("marklogic", "6.0") function math:linear-model-coeff($linear-model as math:linear-model) as xs:double* external;
declare %a:since("marklogic", "6.0") function math:linear-model-intercept($linear-model as math:linear-model) as xs:double external;
declare %a:since("marklogic", "6.0") function math:linear-model-rsquared($linear-model as math:linear-model) as xs:double external;
declare %a:since("marklogic", "5.0") function math:log($x as xs:double) as xs:double external;
declare %a:since("marklogic", "5.0") function math:log10($x as xs:double) as xs:double external;
declare %a:since("marklogic", "6.0") function math:median($arg as xs:double*) as xs:double? external;
declare %a:since("marklogic", "6.0") function math:mode($arg as xs:anyAtomicType*) as xs:anyAtomicType* external;
declare %a:since("marklogic", "6.0") function math:mode($arg as xs:anyAtomicType*, $options as xs:string*) as xs:anyAtomicType* external;
declare %a:since("marklogic", "5.0") function math:modf($x as xs:double) (: as [5.0](xs:double,xs:double) [8.0](xs:double,xs:integer) :) external;
declare %a:since("marklogic", "6.0") function math:percent-rank($arg as xs:anyAtomicType*, $value as xs:anyAtomicType) as xs:double? external;
declare %a:since("marklogic", "6.0") function math:percent-rank($arg as xs:anyAtomicType*, $value as xs:anyAtomicType, $options as xs:string*) as xs:double? external;
declare %a:since("marklogic", "6.0") function math:percentile($arg as xs:double*, $p as xs:double*) as xs:double* external;
declare %a:since("marklogic", "7.0") function math:pi() as xs:double external;
declare %a:since("marklogic", "5.0") function math:pow($x as xs:double, $y as xs:double) as xs:double external;
declare %a:since("marklogic", "6.0") function math:radians($x as xs:double) as xs:double external;
declare %a:since("marklogic", "6.0") function math:rank($arg1 as xs:anyAtomicType*, $arg2 as xs:anyAtomicType) as xs:integer? external;
declare %a:since("marklogic", "6.0") function math:rank($arg1 as xs:anyAtomicType*, $arg2 as xs:anyAtomicType, $options as xs:string*) as xs:integer? external;
declare %a:since("marklogic", "5.0") function math:sin($x as xs:double) as xs:double external;
declare %a:since("marklogic", "5.0") function math:sinh($x as xs:double) as xs:double external;
declare %a:since("marklogic", "5.0") function math:sqrt($x as xs:double) as xs:double external;
declare %a:since("marklogic", "6.0") function math:stddev($arg as xs:double*) as xs:double? external;
declare %a:since("marklogic", "6.0") function math:stddev-p($arg as xs:double*) as xs:double? external;
declare %a:since("marklogic", "5.0") function math:tan($x as xs:double) as xs:double external;
declare %a:since("marklogic", "5.0") function math:tanh($x as xs:double) as xs:double external;
declare %a:since("marklogic", "8.0") function math:trunc($arg as xs:numeric?) as xs:numeric? external;
declare %a:since("marklogic", "6.0") function math:trunc($arg as xs:numeric?, $n as xs:integer) as xs:numeric? external;
declare %a:since("marklogic", "6.0") function math:variance($arg as xs:double*) as xs:double? external;
declare %a:since("marklogic", "6.0") function math:variance-p($arg as xs:double*) as xs:double? external;