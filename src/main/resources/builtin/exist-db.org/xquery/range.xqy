xquery version "3.0";
(:~
 : eXist-db range index module functions
 :
 : @see http://exist-db.org/exist/apps/fundocs/view.html?uri=http://exist-db.org/xquery/range&location=java:org.exist.xquery.modules.range.RangeIndexModule&details=true
 :
 :)
module namespace range = "http://exist-db.org/xquery/range";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare %a:since("exist", "4.4") function range:contains($nodes as node()*, $key as xs:anyAtomicType*) as node()* external;
declare %a:since("exist", "4.4") function range:ends-with($nodes as node()*, $key as xs:anyAtomicType*) as node()* external;
declare %a:since("exist", "4.4") function range:eq($nodes as node()*, $key as xs:anyAtomicType*) as node()* external;
declare %a:since("exist", "4.4") function range:field($fields as xs:string+, $operators as xs:string+, $keys as xs:anyAtomicType*) as node()* external;
declare %a:since("exist", "4.4") function range:field-contains($fields as xs:string+, $keys as xs:anyAtomicType*) as node()* external;
declare %a:since("exist", "4.4") function range:field-ends-with($fields as xs:string+, $keys as xs:anyAtomicType*) as node()* external;
declare %a:since("exist", "4.4") function range:field-eq($fields as xs:string+, $keys as xs:anyAtomicType*) as node()* external;
declare %a:since("exist", "4.4") function range:field-ge($fields as xs:string+, $keys as xs:anyAtomicType*) as node()* external;
declare %a:since("exist", "4.4") function range:field-gt($fields as xs:string+, $keys as xs:anyAtomicType*) as node()* external;
declare %a:since("exist", "4.4") function range:field-le($fields as xs:string+, $keys as xs:anyAtomicType*) as node()* external;
declare %a:since("exist", "4.4") function range:field-lt($fields as xs:string+, $keys as xs:anyAtomicType*) as node()* external;
declare %a:since("exist", "4.4") function range:field-matches($fields as xs:string+, $keys as xs:anyAtomicType*) as node()* external;
declare %a:since("exist", "4.4") function range:field-ne($fields as xs:string+, $keys as xs:anyAtomicType*) as node()* external;
declare %a:since("exist", "4.4") function range:field-starts-with($fields as xs:string+, $keys as xs:anyAtomicType*) as node()* external;
declare %a:since("exist", "4.4") function range:ge($nodes as node()*, $key as xs:anyAtomicType*) as node()* external;
declare %a:since("exist", "4.4") function range:gt($nodes as node()*, $key as xs:anyAtomicType*) as node()* external;
declare %a:since("exist", "4.4") function range:index-keys-for-field($field as xs:string, $function-reference as function(*), $max-number-returned as xs:int?) as item()* external;
declare %a:since("exist", "4.4") function range:index-keys-for-field($field as xs:string, $start-value as xs:anyAtomicType?, $function-reference as function(*), $max-number-returned as xs:int?) as item()* external;
declare %a:since("exist", "4.4") function range:le($nodes as node()*, $key as xs:anyAtomicType*) as node()* external;
declare %a:since("exist", "4.4") function range:lt($nodes as node()*, $key as xs:anyAtomicType*) as node()* external;
declare %a:since("exist", "4.4") function range:matches($nodes as node()*, $regex as xs:string*) as node()* external;
declare %a:since("exist", "4.4") function range:ne($nodes as node()*, $key as xs:anyAtomicType*) as node()* external;
declare %a:since("exist", "4.4") function range:optimize() as empty-sequence() external;
declare %a:since("exist", "4.4") function range:starts-with($nodes as node()*, $key as xs:anyAtomicType*) as node()* external;