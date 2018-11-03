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
declare %a:since("saxon/pe", "9.1") %a:since("saxon/ee", "9.1") function saxon:column-number() as xs:integer external;
declare %a:since("saxon/pe", "9.1") %a:since("saxon/ee", "9.1") function saxon:column-number($node as node()) as xs:integer external;
declare %a:since("saxon/pe", "9.0") %a:since("saxon/ee", "9.0") function saxon:compile-query($query as xs:string) as jt:net.sf.saxon.query.XQueryExpression external;
declare %a:since("saxon/pe", "8.5") %a:since("saxon/ee", "8.5") function saxon:compile-stylesheet($stylesheet as document-node()) as jt.net.sf.saxon.PreparedStylesheet external;
declare %a:since("saxon/pe", "9.3") %a:since("saxon/ee", "9.3") function saxon:current-mode-name() as xs:QName? external; (: @TODO the documentation claims that this was designed for XSLT :)
declare %a:since("saxon/pe", "8.0") %a:since("saxon/ee", "8.0") function saxon:decimal-divide($arg1 as xs:decimal?, $arg2 as xs:decimal, $precision as xs:integer) as xs:decimal? external;
declare %a:since("saxon/pe", "8.6.1") %a:since("saxon/ee", "8.6.1") function saxon:deep-equal($arg1 as item()*, $arg2 as item()*, $collation as xs:string?, $flags as xs:string) as xs:boolean external;
declare %a:since("saxon/pe", "8.0") %a:since("saxon/ee", "8.0") function saxon:discard-document($doc as document-node()) as document-node() external;
declare %a:since("saxon/pe", "7.2") %a:since("saxon/ee", "7.2") function saxon:eval($stored-expression as jt:net.sf.saxon.functions.Evaluate-PreparedExpression, $param1 as item()*, $param2 as item()*, $param3 as item()*) as xs:double external;
declare %a:since("saxon/pe", "7.2") %a:since("saxon/ee", "7.2") function saxon:evaluate($xpath as xs:string, $param1 as item()*, $param2 as item()*, $param3 as item()*) as item()* external;
