xquery version "1.0";
(:~
 : Saxon extension functions
 : @see http://www.saxonica.com/html/documentation/functions/saxon
 :
 :)
module namespace saxon = "http://saxon.sf.net/";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare %a:since("saxon", "9.2") function saxon:adjust-to-civil-time($in as xs:dateTime, $tz as xs:string) as xs:dateTime? external; (: reqs PE or EE :)
declare %a:since("saxon", "9.8") function saxon:array-member($value as item()*) as jt:com.saxonica.functions.extfn.ArrayMemberValue external; (: http://www.saxonica.com/html/documentation/functions/saxon/array-member.html? how to indicate namespaces there? :)
declare %a:since("saxon", "8.1") function saxon:base64Binary-to-octets($input as xs:base64Binary) as xs:integer* external;
declare %a:since("saxon", "8.5") function saxon:base64Binary-to-string($input as xs:base64Binary?, $encoding as xs:string) as xs:string external;

