xquery version "1.0-ml";
(:~
 : MarkLogic temporal functions
 :
 : @see https://docs.marklogic.com/cts/temporal
 : @see https://docs.marklogic.com/temporal/temporal
 :)
module namespace sql = "http://marklogic.com/xdmp/temporal";

import module namespace a = "http://reecedunn.co.uk/xquery/annotations" at "res://reecedunn.co.uk/xquery/annotations.xqy";

declare %a:since("marklogic", "8.0") %a:until("marklogic", "9.0") function temporal:advance-lsqt($temporal-collection as xs:string) as xs:dateTime external;
declare %a:since("marklogic", "8.0") function temporal:advance-lsqt($temporal-collection as xs:string, $lag (: as [8.0]xs:unsignedInt [9.0]xs:string :)) as xs:dateTime external;
declare %a:since("marklogic", "8.0") function temporal:document-period($temporal-collection as xs:string, $axis as xs:string, $root as node()) as xs:dateTime* external;
declare %a:since("marklogic", "9.0") function temporal:document-protect($temporal-collection as xs:string, $uri as xs:string) as empty-sequence() external;
declare %a:since("marklogic", "9.0") function temporal:document-protect($temporal-collection as xs:string, $uri as xs:string, $options (: as (element()|map:map)? :)) as empty-sequence() external;
declare %a:since("marklogic", "9.0") function temporal:document-wipe($temporal-collection as xs:string, $uri as xs:string) as empty-sequence() external;
declare %a:since("marklogic", "8.0") function temporal:get-lsqt($temporal-collection as xs:string) as xs:dateTime external;
declare %a:since("marklogic", "8.0") function temporal:get-lsqt-automation($temporal-collection as xs:string) as xs:boolean external;
declare %a:since("marklogic", "8.0") function temporal:get-lsqt-automation-lag($temporal-collection as xs:string) as xs:long external;
declare %a:since("marklogic", "8.0") function temporal:get-lsqt-automation-period($temporal-collection as xs:string) as xs:long external;
declare %a:since("marklogic", "8.0") function temporal:get-use-lsqt($temporal-collection as xs:string) as xs:boolean external;
declare %a:since("marklogic", "8.0") function temporal:set-lsqt-automation($temporal-collection as xs:string, $on as xs:boolean) as empty-sequence() external;
declare %a:since("marklogic", "8.0") function temporal:set-lsqt-automation($temporal-collection as xs:string, $on as xs:boolean, $period as xs:unsignedInt) as empty-sequence() external;
declare %a:since("marklogic", "8.0") function temporal:set-lsqt-automation($temporal-collection as xs:string, $on as xs:boolean, $period as xs:unsignedInt, $lag as xs:unsignedInt) as empty-sequence() external;
declare %a:since("marklogic", "8.0") function temporal:set-use-lsqt($temporal-collection as xs:string, $on as xs:boolean) as empty-sequence() external;
