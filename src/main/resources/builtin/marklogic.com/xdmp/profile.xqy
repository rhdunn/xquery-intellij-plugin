xquery version "1.0-ml";
(:~
 : MarkLogic profiling functions
 :
 : @see https://docs.marklogic.com/prof
 :)
module namespace prof = "http://marklogic.com/xdmp/profile";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";

declare option a:requires "marklogic/5.0";

declare type options-type = (
  %a:since("marklogic", "5.0") %a:until("marklogic", "8.0") for node() |
  %a:since("marklogic", "8.0") for (element()|map:map)
);

declare %a:since("marklogic", "5.0") function prof:allowed($request-id as xs:unsignedLong) as xs:boolean external;
declare %a:since("marklogic", "5.0") function prof:disable($request-id as xs:unsignedLong) as empty-sequence() external;
declare %a:since("marklogic", "5.0") function prof:enable($request-id as xs:unsignedLong) as empty-sequence() external;
declare %a:since("marklogic", "5.0") function prof:eval($xquery as xs:string) as item()* external;
declare %a:since("marklogic", "5.0") function prof:eval($xquery as xs:string, $vars as item()*) as item()* external;
declare %a:since("marklogic", "5.0") function prof:eval($xquery as xs:string, $vars as item()*, $options as options-type?) as item()* external;
declare %a:since("marklogic", "5.0") function prof:invoke($path as xs:string) as item()* external;
declare %a:since("marklogic", "5.0") function prof:invoke($path as xs:string, $vars as item()*) as item()* external;
declare %a:since("marklogic", "5.0") function prof:invoke($path as xs:string, $vars as item()*, $options as options-type?) as item()* external;
declare %a:since("marklogic", "5.0") function prof:report($request-id as xs:unsignedLong) as element(prof:report)? external;
declare %a:since("marklogic", "5.0") function prof:reset($request-id as xs:unsignedLong) as empty-sequence() external;
declare %a:since("marklogic", "5.0") function prof:value($expr as xs:string) as item()* external;
declare %a:since("marklogic", "9.0") function prof:value($expr as xs:string, $map as map:map?) as item()* external;
declare %a:since("marklogic", "9.0") function prof:value($expr as xs:string, $map as map:map?, $context as item()?) as item()* external;
declare %a:since("marklogic", "5.0") function prof:xslt-eval($stylesheet (: as [5.0]element() [8.0]node() :), $input as node()?) as item()* external;
declare %a:since("marklogic", "5.0") function prof:xslt-eval($stylesheet (: as [5.0]element() [8.0]node() :), $input as node()?, $params as map:map?) as item()* external;
declare %a:since("marklogic", "5.0") function prof:xslt-eval($stylesheet (: as [5.0]element() [8.0]node() :), $input as node()?, $params as map:map?, $options as options-type?) as item()* external;
declare %a:since("marklogic", "8.0") function prof:xslt-invoke($path as xs:string) as item()* external;
declare %a:since("marklogic", "5.0") function prof:xslt-invoke($path as xs:string, $input as node()?) as item()* external;
declare %a:since("marklogic", "5.0") function prof:xslt-invoke($path as xs:string, $input as node()?, $params as map:map?) as item()* external;
declare %a:since("marklogic", "5.0") function prof:xslt-invoke($path as xs:string, $input as node()?, $params as map:map?, $options as options-type?) as item()* external;