xquery version "1.0-ml";

(:~
 : MarkLogic math functions
 :
 : @see https://docs.marklogic.com/math
 :)
module  namespace math = "http://marklogic.com/xdmp/math";
declare namespace xs   = "http://www.w3.org/2001/XMLSchema";
declare namespace a    = "http://reecedunn.co.uk/xquery/annotations";

declare %a:since("marklogic", "5.0") function math:acos($x as xs:double) as xs:double external;
declare %a:since("marklogic", "5.0") function math:asin($x as xs:double) as xs:double external;
declare %a:since("marklogic", "5.0") function math:atan($x as xs:double) as xs:double external;
declare %a:since("marklogic", "5.0") function math:atan2($y as xs:double, $x as xs:double) as xs:double external;
declare %a:since("marklogic", "5.0") function math:ceil($x as xs:double) as xs:double external;
declare %a:since("marklogic", "5.0") function math:cos($x as xs:double) as xs:double external;
declare %a:since("marklogic", "5.0") function math:cosh($x as xs:double) as xs:double external;
declare %a:since("marklogic", "5.0") function math:exp($x as xs:double) as xs:double external;
declare %a:since("marklogic", "5.0") function math:fabs($x as xs:double) as xs:double external;
declare %a:since("marklogic", "5.0") function math:floor($x as xs:double) as xs:double external;
declare %a:since("marklogic", "5.0") function math:fmod($x as xs:double, $x as xs:double) as xs:double external;
declare %a:since("marklogic", "5.0") function math:frexp($x as xs:double) (: as (xs:double,xs:integer) :) external;
declare %a:since("marklogic", "5.0") function math:ldexp($y as xs:double, $i as xs:integer) as xs:double external;
declare %a:since("marklogic", "5.0") function math:log($x as xs:double) as xs:double external;
declare %a:since("marklogic", "5.0") function math:log10($x as xs:double) as xs:double external;
declare %a:since("marklogic", "5.0") function math:modf($x as xs:double) (: as (xs:double,xs:double) :) external;
declare %a:since("marklogic", "5.0") function math:pow($x as xs:double, $y as xs:double) as xs:double external;
declare %a:since("marklogic", "5.0") function math:sin($x as xs:double) as xs:double external;
declare %a:since("marklogic", "5.0") function math:sinh($x as xs:double) as xs:double external;
declare %a:since("marklogic", "5.0") function math:sqrt($x as xs:double) as xs:double external;
declare %a:since("marklogic", "5.0") function math:tan($x as xs:double) as xs:double external;
declare %a:since("marklogic", "5.0") function math:tanh($x as xs:double) as xs:double external;
