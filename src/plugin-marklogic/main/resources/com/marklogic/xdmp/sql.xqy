xquery version "1.0-ml";
(:~
 : MarkLogic sql functions
 :
 : @see http://docs.marklogic.com/dates
 : @see https://docs.marklogic.com/sql/sql
 :)
module namespace sql = "http://marklogic.com/xdmp/sql";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare option o:requires "marklogic/7.0";

declare %a:since("marklogic", "9.0") function sql:bit-length($str as xs:string) as xs:integer external;
declare %a:since("marklogic", "9.0") function sql:collated-string($string as xs:string, $collationURI as xs:string) as rdf:collatedString external;
declare %a:since("marklogic", "9.0") function sql:collatedString-collation($val as sql:collatedString) as xs:string external;
declare %a:since("marklogic", "9.0") function sql:columnID($schema as xs:string, $view as xs:string, $column as xs:string) as sql:columnID external;
declare %a:since("marklogic", "7.0") function sql:dateadd($datepart as xs:string, $number as xs:integer, $date as item()) as item() external;
declare %a:since("marklogic", "7.0") function sql:datediff($datepart as xs:string, $startdate as item(), $enddate as item()) as xs:integer? external;
declare %a:since("marklogic", "7.0") function sql:datepart($datepart as xs:string?, $date as xs:genericDateTimeArg) as xs:integer? external;
declare %a:since("marklogic", "9.0") function sql:day($arg as xs:genericDateTimeArg?) as xs:integer? external;
declare %a:since("marklogic", "9.0") function sql:dayname($arg as xs:genericDateTimeArg?) as xs:string? external;
declare %a:since("marklogic", "9.0") function sql:difference($arg as xs:string) as xs:string external;
declare %a:since("marklogic", "9.0") function sql:glob($input as xs:string?, $pattern as xs:string) as xs:boolean? external;
declare %a:since("marklogic", "9.0") function sql:hours($arg as xs:genericDateTimeArg?) as xs:integer? external;
declare %a:since("marklogic", "9.0") function sql:ifnull($expr1 as item()*, $expr2 as item()*) as item()* external;
declare %a:since("marklogic", "9.0") function sql:insert($str as xs:string, $start as xs:double, $length as xs:double, $str2 as xs:string) as xs:string external;
declare %a:since("marklogic", "9.0") function sql:instr($str as xs:string, $n as xs:string) as xs:unsigned external;
declare %a:since("marklogic", "9.0") function sql:left($str as item(), $n as xs:double) as xs:string external;
declare %a:since("marklogic", "9.0") function sql:like($input as xs:string?, $pattern as xs:string, $escape as xs:string) as xs:boolean? external;
declare %a:since("marklogic", "9.0") function sql:ltrim($str as xs:string) as xs:string external;
declare %a:since("marklogic", "9.0") function sql:minutes($arg as xs:genericDateTimeArg?) as xs:integer? external;
declare %a:since("marklogic", "9.0") function sql:month($arg as xs:genericDateTimeArg?) as xs:integer? external;
declare %a:since("marklogic", "9.0") function sql:monthname($arg as xs:genericDateTimeArg?) as xs:string? external;
declare %a:since("marklogic", "9.0") function sql:nullif($expr1 as item()*, $expr2 as item()*) as item()* external;
declare %a:since("marklogic", "9.0") function sql:octet-length($x as xs:string) as xs:integer external;
declare %a:since("marklogic", "9.0") function sql:quarter($arg as xs:genericDateTimeArg?) as xs:integer? external;
declare %a:since("marklogic", "9.0") function sql:rand($n as xs:unsignedLong) as xs:unsignedLong external;
declare %a:since("marklogic", "9.0") function sql:repeat($str as item(), $n as xs:double) as xs:string external;
declare %a:since("marklogic", "9.0") function sql:right($str as item(), $n as xs:double) as xs:string external;
declare %a:since("marklogic", "9.0") function sql:rtrim($str as xs:string) as xs:string external;
declare %a:since("marklogic", "9.0") function sql:seconds($arg as xs:genericDateTimeArg?) as xs:decimal? external;
declare %a:since("marklogic", "9.0") function sql:sign($x as xs:double) as xs:double external;
declare %a:since("marklogic", "9.0") function sql:soundex($arg as xs:string) as xs:string external;
declare %a:since("marklogic", "9.0") function sql:space($n as xs:double) as xs:string external;
declare %a:since("marklogic", "9.0") function sql:strpos($target as xs:string?, $test as xs:string?) as xs:integer? external;
declare %a:since("marklogic", "9.0") function sql:strpos($target as xs:string?, $test as xs:string?, $collation as xs:string?) as xs:integer? external;
declare %a:since("marklogic", "9.0") function sql:timestampadd($dateTimeType as xs:ID, $value as xs:integer, $timestamp as xs:string) as xs:integer? external;
declare %a:since("marklogic", "9.0") function sql:timestampdiff($dateTimeType as xs:ID, $timestamp1 as xs:genericDateTimeArg, $timestamp2 as xs:genericDateTimeArg) as xs:integer? external;
declare %a:since("marklogic", "9.0") function sql:trim($str as xs:string) as xs:string external;
declare %a:since("marklogic", "9.0") function sql:week($arg as (xs:dateTime?|xs:date?|xs:string?)) as xs:integer? external;
declare %a:since("marklogic", "9.0") function sql:year($arg as genericDateTimeArg?) as xs:integer? external;
declare %a:since("marklogic", "9.0") function sql:yearday($arg as xs:genericDateTimeArg?) as xs:integer? external;