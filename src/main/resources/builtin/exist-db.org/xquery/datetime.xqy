xquery version "3.0";
(:~
 : eXist-db datetime module functions
 :
 : @see http://exist-db.org/exist/apps/fundocs/view.html?uri=http://exist-db.org/xquery/datetime&location=java:org.exist.xquery.modules.datetime.DateTimeModule&details=true
 :)
module namespace datetime ="http://exist-db.org/xquery/datetime";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare %a:since("exist", "4.4") function datetime:count-day-in-month($weekday as xs:integer, $date as xs:date) as xs:integer external;
declare %a:since("exist", "4.4") function datetime:date-for($year as xs:integer, $month as xs:integer, $week as xs:integer, $weekday as xs:integer) as xs:date external;
declare %a:since("exist", "4.4") function datetime:date-from-dateTime($date-time as xs:dateTime) as xs:date external;
declare %a:since("exist", "4.4") function datetime:date-range($start-date as xs:date, $increment as xs:duration, $iterations as xs:integer) as xs:date* external;
declare %a:since("exist", "4.4") function datetime:datetime-range($start-date-time as xs:dateTime, $increment as xs:duration, $iterations as xs:integer) as xs:dateTime* external;
declare %a:since("exist", "4.4") function datetime:day-in-week($date as xs:date) as xs:integer external;
declare %a:since("exist", "4.4") function datetime:days-in-month($date as xs:date) as xs:integer external;
declare %a:since("exist", "4.4") function datetime:format-date($date as xs:date, $simple-date-format as xs:string) as xs:string external;
declare %a:since("exist", "4.4") function datetime:format-dateTime($date-time as xs:dateTime, $simple-date-format as xs:string) as xs:string external;
declare %a:since("exist", "4.4") function datetime:format-time($time as xs:time, $simple-date-format as xs:string) as xs:string external;
declare %a:since("exist", "4.4") function datetime:parse-date($date-string as xs:string, $simple-date-format as xs:string) as xs:date external;
declare %a:since("exist", "4.4") function datetime:parse-dateTime($dateTime-string as xs:string, $simple-date-format as xs:string) as xs:dateTime external;
declare %a:since("exist", "4.4") function datetime:parse-time($time-string as xs:string, $simple-date-format as xs:string) as xs:time external;
declare %a:since("exist", "4.4") function datetime:time-from-dateTime($date-time as xs:dateTime) as xs:time external;
declare %a:since("exist", "4.4") function datetime:time-range($start-time as xs:time, $increment as xs:dayTimeDuration, $iterations as xs:integer) as xs:time* external;
declare %a:since("exist", "4.4") function datetime:timestamp() as xs:unsignedLong external;
declare %a:since("exist", "4.4") function datetime:timestamp-to-datetime($ms as xs:unsignedLong) as xs:date external;
declare %a:since("exist", "4.4") function datetime:week-in-month($date as xs:date) as xs:integer external;