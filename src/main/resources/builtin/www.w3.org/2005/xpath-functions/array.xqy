xquery version "3.1";
(:~
 : XPath and XQuery Functions and Operators: Functions that Operate on Arrays
 :
 : @see https://www.w3.org/TR/2017/REC-xpath-functions-31-20170321/
 :)
module namespace array = "http://www.w3.org/2005/xpath-functions/array";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";

declare option a:requires "xpath-functions/3.1";

declare %a:since("xpath-functions", "3.1-20170321") function array:append($array as array(*), $appendage as item()*) as array(*) external;
declare %a:since("xpath-functions", "3.1-20170321") function array:filter($array as array(*), $function as function(item()*) as xs:boolean) as array(*) external;
declare %a:since("xpath-functions", "3.1-20170321") function array:flatten($input as item()*) as item()* external;
declare %a:since("xpath-functions", "3.1-20170321") function array:fold-left($array as array(*), $zero as item()*, $function as function(item()*, item()*) as item()*) as item()* external;
declare %a:since("xpath-functions", "3.1-20170321") function array:fold-right($array as array(*), $zero as item()*, $function as function(item()*, item()*) as item()*) as item()* external;
declare %a:since("xpath-functions", "3.1-20170321") function array:for-each($array as array(*), $action as function(item()*) as item()*) as array(*) external;
declare %a:since("xpath-functions", "3.1-20170321") function array:for-each-pair($array1 as array(*), $array2 as array(*), $function as function(item()*, item()*) as item()*) as array(*) external;
declare %a:since("xpath-functions", "3.1-20170321") function array:get($array as array(*), $position as xs:integer) as item()* external;
declare %a:since("xpath-functions", "3.1-20170321") function array:head($array as array(*)) as item()* external;
declare %a:since("xpath-functions", "3.1-20170321") function array:insert-before($array as array(*), $position as xs:integer, $member as item()*) as array(*) external;
declare %a:since("xpath-functions", "3.1-20170321") function array:join($arrays as array(*)*) as array(*) external;
declare %a:since("xpath-functions", "3.1-20170321") function array:put($array as array(*), $position as xs:integer, $member as item()*) as array(*) external;
declare %a:since("xpath-functions", "3.1-20170321") function array:remove($array as array(*), $positions as xs:integer*) as array(*) external;
declare %a:since("xpath-functions", "3.1-20170321") function array:reverse($array as array(*)) as array(*) external;
declare %a:since("xpath-functions", "3.1-20170321") function array:size($array as array(*)) as xs:integer external;
declare %a:since("xpath-functions", "3.1-20170321") function array:sort($array as array(*)) as array(*) external;
declare %a:since("xpath-functions", "3.1-20170321") function array:sort($array as array(*), $collation as xs:string?) as array(*) external;
declare %a:since("xpath-functions", "3.1-20170321") function array:sort($array as array(*), $collation as xs:string?, $key as function(item()*) as xs:anyAtomicType*) as array(*) external;
declare %a:since("xpath-functions", "3.1-20170321") function array:subarray($array as array(*), $start as xs:integer) as array(*) external;
declare %a:since("xpath-functions", "3.1-20170321") function array:subarray($array as array(*), $start as xs:integer, $length as xs:integer) as array(*) external;
declare %a:since("xpath-functions", "3.1-20170321") function array:tail($array as array(*)) as array(*) external;
