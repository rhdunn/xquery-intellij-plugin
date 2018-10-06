xquery version "3.1";
(:~
 : XPath and XQuery Functions and Operators: Functions that Operate on Maps
 :
 : @see https://www.w3.org/TR/2017/REC-xpath-functions-31-20170321/
 :)
module namespace map = "http://www.w3.org/2005/xpath-functions/map";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";

declare %a:since("xpath-functions", "3.1-20170321") function map:contains($map as map(*), $key as xs:anyAtomicType) as xs:boolean external;
declare %a:since("xpath-functions", "3.1-20170321") function map:entry($key as xs:anyAtomicType, $value as item()*) as map(*) external;
declare %a:since("xpath-functions", "3.1-20170321") function map:find($input as item()*, $key as xs:anyAtomicType) as array(*) external;
declare %a:since("xpath-functions", "3.1-20170321") function map:for-each($map as map(*), $action as function(xs:anyAtomicType, item()*) as item()*) as item()* external;
declare %a:since("xpath-functions", "3.1-20170321") function map:get($map as map(*), $key as xs:anyAtomicType) as item()* external;
declare %a:since("xpath-functions", "3.1-20170321") function map:keys($map as map(*)) as xs:anyAtomicType* external;
declare %a:since("xpath-functions", "3.1-20170321") function map:merge($maps as map(*)*) as map(*) external;
declare %a:since("xpath-functions", "3.1-20170321") function map:merge($maps as map(*)*, $options as map(*)) as map(*) external;
declare %a:since("xpath-functions", "3.1-20170321") function map:put($map as map(*), $key as xs:anyAtomicType, $value as item()*) as map(*) external;
declare %a:since("xpath-functions", "3.1-20170321") function map:remove($map as map(*), $keys as xs:anyAtomicType*) as map(*) external;
declare %a:since("xpath-functions", "3.1-20170321") function map:size($map as map(*)) as xs:integer external;
