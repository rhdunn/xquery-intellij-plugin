xquery version "1.0";
(:~
 : Saxon extension functions
 : @see http://www.saxonica.com/html/documentation/functions/saxon
 :
 :)
module namespace saxon = "http://saxon.sf.net/";

declare namespace jt = "http://saxon.sf.net/java-type";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare %a:since("saxon/pe", "9.2") %a:since("saxon/ee", "9.2") function saxon:adjust-to-civil-time($in as xs:dateTime, $tz as xs:string) as xs:dateTime? external; (: reqs PE or EE :)
declare %a:since("saxon/pe", "9.8") function saxon:array-member($value as item()*) as jt:com.saxonica.functions.extfn.ArrayMemberValue external;
declare %a:since("saxon/pe", "8.1") %a:since("saxon/ee", "8.1") function saxon:base64Binary-to-octets($input as xs:base64Binary) as xs:integer* external;
declare %a:since("saxon/pe", "8.5") %a:since("saxon/ee", "8.5") function saxon:base64Binary-to-string($input as xs:base64Binary?, $encoding as xs:string) as xs:string external;
