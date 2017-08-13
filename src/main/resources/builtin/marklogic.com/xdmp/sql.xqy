xquery version "1.0-ml";
(:~
 : MarkLogic sql functions
 :
 : @see http://docs.marklogic.com/dates
 : @see https://docs.marklogic.com/sql/sql
 :)
module namespace sql = "http://marklogic.com/xdmp/sql";

import module namespace a = "http://reecedunn.co.uk/xquery/annotations" at "res://reecedunn.co.uk/xquery/annotations.xqy";

declare %a:since("marklogic", "7.0") function sql:dateadd($datepart as xs:string, $number as xs:integer, $date as item()) as item() external;
declare %a:since("marklogic", "7.0") function sql:datediff($datepart as xs:string, $startdate as item(), $enddate as item()) as xs:integer? external;
declare %a:since("marklogic", "7.0") function sql:datepart($datepart as xs:string?, $date as xs:genericDateTimeArg) as xs:integer? external;