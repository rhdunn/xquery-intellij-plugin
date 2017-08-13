xquery version "1.0-ml";

(:~
 : MarkLogic json functions
 :
 : @see https://docs.marklogic.com/xdmp/json
 :)
module  namespace json = "http://marklogic.com/xdmp/json";
declare namespace xs   = "http://www.w3.org/2001/XMLSchema";

import module namespace a = "http://reecedunn.co.uk/xquery/annotations" at "res://reecedunn.co.uk/xquery/annotations.xqy";

declare %a:since("marklogic", "6.0") function json:array() as json:array external;
declare %a:since("marklogic", "6.0") function json:array($array as element(json:array)) as json:array external;
declare %a:since("marklogic", "6.0") function json:array-pop($array as json:array) as item()* external;
declare %a:since("marklogic", "6.0") function json:array-push($array as json:array, $item as item()*) as empty-sequence() external;
declare %a:since("marklogic", "6.0") function json:array-resize($array as json:array, $newSize as xs:unsignedLong) as empty-sequence() external;
declare %a:since("marklogic", "6.0") function json:array-resize($array as json:array, $newSize as xs:unsignedLong, $zero as item()?) as empty-sequence() external;
declare %a:since("marklogic", "6.0") function json:array-size($array as json:array?) as xs:unsignedLong? external;
declare %a:since("marklogic", "6.0") function json:array-values($array as json:array) as item()* external;
declare %a:since("marklogic", "6.0") function json:array-values($array as json:array, $flatten as xs:boolean?) as item()* external;
declare %a:since("marklogic", "6.0") function json:object() as map:map external;
declare %a:since("marklogic", "6.0") function json:object($map as element(json:object)) as map:map external;
declare %a:since("marklogic", "6.0") function json:object-define() as json:object external;
declare %a:since("marklogic", "6.0") function json:object-define($keys as xs:string*) as json:object external;
declare %a:since("marklogic", "6.0") function json:set-item-at($array as json:array, $pos as xs:double, $value as item()*) as empty-sequence() external;
declare %a:since("marklogic", "6.0") function json:subarray($array as json:array, $startingLoc as xs:double) as json:array external;
declare %a:since("marklogic", "6.0") function json:subarray($array as json:array, $startingLoc as xs:double, $length as xs:double) as json:array external;
declare %a:since("marklogic", "6.0") function json:to-array() as json:array external;
declare %a:since("marklogic", "6.0") function json:to-array($items as item()*) as json:array external;
declare %a:since("marklogic", "6.0") function json:to-array($items as item()*, $limit as xs:double?) as json:array external;
declare %a:since("marklogic", "6.0") function json:to-array($items as item()*, $limit as xs:double?, $zero as item()?) as json:array external;