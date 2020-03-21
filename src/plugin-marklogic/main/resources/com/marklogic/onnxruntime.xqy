xquery version "1.0-ml";
(:~
 : MarkLogic onnxruntime functions
 :
 : @see https://docs.marklogic.com/ort
 :)
module namespace ort = "http://marklogic.com/onnxruntime";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare option o:requires "marklogic/10.0-3";

declare %a:since("marklogic", "10.0-3") function ort:run($session as node, $inputs as node) as map:map external;
declare %a:since("marklogic", "10.0-3") function ort:run($session as node, $inputs as node, $options as map:map) as map:map external;
declare %a:since("marklogic", "10.0-3") function ort:session($model as node) as ort:session external;
declare %a:since("marklogic", "10.0-3") function ort:session($model as node, $options as map:map) as ort:session external;
declare %a:since("marklogic", "10.0-3") function ort:session-input-count($session as ort:session) as xs:unsignedLong external;
declare %a:since("marklogic", "10.0-3") function ort:session-input-name($session as ort:session, $index as xs:unsignedLong) as xs:string external;
declare %a:since("marklogic", "10.0-3") function ort:session-input-type($session as ort:session, $index as xs:unsignedLong) as map:map external;
declare %a:since("marklogic", "10.0-3") function ort:session-output-count($session as ort:session) as xs:unsignedLong external;
declare %a:since("marklogic", "10.0-3") function ort:session-output-name($session as ort:session, $index as xs:unsignedLong) as xs:string external;
declare %a:since("marklogic", "10.0-3") function ort:session-output-type($session as ort:session, $index as xs:unsignedLong) as map:map external;
declare %a:since("marklogic", "10.0-3") function ort:value($data as xs:numeric*, $shape as xs:long*, $type as xs:string) as ort:value external;
declare %a:since("marklogic", "10.0-3") function ort:value-get-array($value as ort:value) as xs:numeric* external;
declare %a:since("marklogic", "10.0-3") function ort:value-get-shape($value as ort:value) as xs:long* external;
declare %a:since("marklogic", "10.0-3") function ort:value-get-type($value as ort:value) as xs:string external;
