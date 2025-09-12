xquery version "3.1";
(:~
 : XPath and XQuery Functions and Operators: Functions that Operate on Arrays
 :
 : @see https://www.w3.org/TR/2017/REC-xpath-functions-31-20170321/
 : @see https://qt4cg.org/branch/master/xpath-functions-40/Overview.html
 :
 : This software includes material copied from or derived from the XPath and
 : XQuery Functions and Operators 3.1 specifications. Copyright © 2017 W3C®
 : (MIT, ERCIM, Keio, Beihang).
 :)
module namespace array = "http://www.w3.org/2005/xpath-functions/array";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare option o:requires "xpath-functions/3.1";

declare %a:since("xpath-functions", "3.1-20170321") function array:append(
  $array as array(*),
  $appendage as item()*
) as array(*) external;

declare %a:since("xpath-functions", "4.0-20250825") function array:build(
  $array as item()*,
  $action as (function(item(),xs:integer) as item()*)?
) as array(*) external;

declare %a:since("xpath-functions", "4.0-20250825") function array:empty(
  $array as array(*)
) as xs:boolean external;

declare %a:since("xpath-functions", "3.1-20170321") function array:filter(
  $array as array(*),
  $predicate as function(item()*) as xs:boolean
) as array(*) external;

declare %a:since("xpath-functions", "3.1-20170321") function array:flatten(
  $input as item()*
) as item()* external;

declare %a:since("xpath-functions", "3.1-20170321") function array:fold-left(
  $array as array(*),
  $zero as item()*,
  $action as function(item()*, item()*) as item()*
) as item()* external;

declare %a:since("xpath-functions", "3.1-20170321") function array:fold-right(
  $array as array(*),
  $zero as item()*,
  $action as function(item()*, item()*) as item()*
) as item()* external;

declare %a:since("xpath-functions", "4.0-20250825") function array:foot(
  $array as array(*)
) as item()* external;

declare %a:since("xpath-functions", "3.1-20170321") function array:for-each(
  $array as array(*),
  $action as function(item()*) as item()*
) as array(*) external;

declare %a:since("xpath-functions", "3.1-20170321") function array:for-each-pair(
  $array1 as array(*),
  $array2 as array(*),
  $action as function(item()*, item()*) as item()*
) as array(*) external;

declare %a:since("xpath-functions", "4.0-20210113") function array:from-sequence(
  $input as item()*
) as array(*) external;

declare %a:since("xpath-functions", "4.0-20210113") function array:from-sequence(
  $input as item()*,
  $action as function(item()) as item()*
) as array(*) external;

declare %a:since("xpath-functions", "3.1-20170321") function array:get(
  $array as array(*),
  $position as xs:integer
) as item()* external;

declare %a:since("xpath-functions", "4.0-20250825") function array:get(
  $array as array(*),
  $position as xs:integer,
  $default as item()*
) as item()* external;

declare %a:since("xpath-functions", "3.1-20170321") function array:head(
  $array as array(*)
) as item()* external;

declare %a:since("xpath-functions", "3.1-20170321") function array:insert-before(
  $array as array(*),
  $position as xs:integer,
  $member as item()*
) as array(*) external;

declare %a:since("xpath-functions", "4.0-20250825") function array:index-of(
  $array as array(*),
  $target as item()*
) as xs:integer* external;

declare %a:since("xpath-functions", "4.0-20250825") function array:index-of(
  $array as array(*),
  $target as item()*,
  $collation as xs:string?
) as xs:integer* external;

declare %a:since("xpath-functions", "4.0-20250825") function array:index-where(
  $array as array(*),
  $predicate as function(item()*, xs:integer) as xs:boolean
) as xs:integer* external;

declare %a:since("xpath-functions", "4.0-20250825") function array:items(
  $array as array(*)
) as item()* external;

declare %a:since("xpath-functions", "3.1-20170321") function array:join(
  $arrays as array(*)*
) as array(*) external;

declare %a:since("xpath-functions", "4.0-20250825") function array:join(
  $arrays as array(*)*,
  $separator as array(*)?
) as array(*) external;

declare %a:since("xpath-functions", "4.0-20210113") function array:partition(
  $input as item()*,
  $break-when as function(item()*, item()) as xs:boolean
) as array(*)+ external;

declare %a:since("xpath-functions", "3.1-20170321") function array:put(
  $array as array(*),
  $position as xs:integer,
  $member as item()*
) as array(*) external;

declare %a:since("xpath-functions", "3.1-20170321") function array:remove(
  $array as array(*),
  $positions as xs:integer*
) as array(*) external;

declare %a:since("xpath-functions", "4.0-20210113") function array:replace(
  $array as array(*),
  $position as xs:integer,
  $action as function(item()*) as item()*
) as array(*) external;

declare %a:since("xpath-functions", "3.1-20170321") function array:reverse(
  $array as array(*)
) as array(*) external;

declare %a:since("xpath-functions", "3.1-20170321") function array:size(
  $array as array(*)
) as xs:integer external;

declare %a:since("xpath-functions", "4.0-20210113") function array:slice(
  $input as array(*),
  $start as xs:integer?,
  $end as xs:integer?,
  $step as xs:integer?
) as array(*) external;

declare %a:since("xpath-functions", "3.1-20170321") function array:sort(
  $array as array(*)
) as array(*) external;

declare %a:since("xpath-functions", "3.1-20170321") function array:sort(
  $array as array(*),
  $collation as xs:string?
) as array(*) external;

declare %a:since("xpath-functions", "3.1-20170321") function array:sort(
  $array as array(*),
  $collation as xs:string?,
  $key as function(item()*) as xs:anyAtomicType*
) as array(*) external;

declare %a:since("xpath-functions", "4.0-20250825") function array:split(
  $array as array(*)
) as array(*)* external;

declare %a:since("xpath-functions", "3.1-20170321") function array:subarray(
  $array as array(*),
  $start as xs:integer
) as array(*) external;

declare %a:since("xpath-functions", "3.1-20170321") function array:subarray(
  $array as array(*),
  $start as xs:integer,
  $length as xs:integer
) as array(*) external;

declare %a:since("xpath-functions", "3.1-20170321") function array:tail(
  $array as array(*)
) as array(*) external;

declare %a:since("xpath-functions", "4.0-20250825") function array:trunk(
  $array as array(*)
) as array(*) external;
