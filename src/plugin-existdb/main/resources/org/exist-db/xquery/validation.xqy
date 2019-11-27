xquery version "3.0";
(:~
 : eXist-db validation module functions
 :
 : @see http://exist-db.org/exist/apps/fundocs/view.html?uri=http://exist-db.org/xquery/validation&location=java:org.exist.xquery.functions.validation.ValidationModule&details=true
 :)
module namespace validation = "http://exist-db.org/xquery/validation";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare %a:since("exist", "4.4") function validation:clear-grammar-cache() as xs:integer external;
declare %a:since("exist", "4.4") function validation:jaxp($instance as item(), $cache-grammars as xs:boolean) as xs:boolean external;
declare %a:since("exist", "4.4") function validation:jaxp($instance as item(), $cache-grammars as xs:boolean, $catalogs as item()*) as xs:boolean external;
declare %a:since("exist", "4.4") function validation:jaxp-parse($instance as item(), $enable-grammar-cache as xs:boolean, $catalogs as item()*) as node() external;
declare %a:since("exist", "4.4") function validation:jaxp-report($instance as item(), $enable-grammar-cache as xs:boolean) as node() external;
declare %a:since("exist", "4.4") function validation:jaxp-report($instance as item(), $enable-grammar-cache as xs:boolean, $catalogs as item()*) as node() external;
declare %a:since("exist", "4.4") function validation:jaxv($instance as item(), $grammars as item()+) as xs:boolean external;
declare %a:since("exist", "4.4") function validation:jaxv($instance as item(), $grammars as item()+, $language as xs:string) as xs:boolean external;
declare %a:since("exist", "4.4") function validation:jaxv-report($instance as item(), $grammars as item()+) as node() external;
declare %a:since("exist", "4.4") function validation:jaxv-report($instance as item(), $grammars as item()+, $language as xs:string) as node() external;
declare %a:since("exist", "4.4") function validation:jing($instance as item(), $grammar as item()) as xs:boolean external;
declare %a:since("exist", "4.4") function validation:jing-report($instance as item(), $grammar as item()) as node() external;
declare %a:since("exist", "4.4") function validation:pre-parse-grammar($grammar as xs:anyURI*) as xs:string* external;
declare %a:since("exist", "4.4") function validation:show-grammar-cache() as node() external;
declare %a:since("exist", "4.4") function validation:validate($instance as item()) as xs:boolean external;
declare %a:since("exist", "4.4") function validation:validate($instance as item(), $grammar as xs:anyURI) as node() external;
declare %a:since("exist", "4.4") function validation:validate-report($instance as item()) as node() external;
declare %a:since("exist", "4.4") function validation:validate-report($instance as item(), $grammar as item()) as node() external;