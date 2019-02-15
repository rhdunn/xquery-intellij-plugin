xquery version "3.0";
(:~
 : eXist-db HTTP session module functions
 :
 : @see http://exist-db.org/exist/apps/fundocs/view.html?uri=http://exist-db.org/xquery/session&location=java:org.exist.xquery.functions.session.SessionModule&details=true
 :)
module namespace session = "http://exist-db.org/xquery/session";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare %a:since("exist", "4.4") function session:clear() as xs:string external;
declare %a:since("exist", "4.4") function session:create() as item() external;
declare %a:since("exist", "4.4") function session:encode-url($url as xs:anyURI) as xs:anyURI external;
declare %a:since("exist", "4.4") function session:exists() as xs:boolean external;
declare %a:since("exist", "4.4") function session:get-attribute($name as xs:string) as item()* external;
declare %a:since("exist", "4.4") function session:get-attribute-names() as xs:string* external;
declare %a:since("exist", "4.4") function session:get-creation-time() as xs:dateTime external;
declare %a:since("exist", "4.4") function session:get-id() as xs:string? external;
declare %a:since("exist", "4.4") function session:get-last-accessed-time() as xs:dateTime external;
declare %a:since("exist", "4.4") function session:get-max-inactive-interval() as xs:int external;
declare %a:since("exist", "4.4") function session:invalidate() as item() external;
declare %a:since("exist", "4.4") function session:remove-attribute($name as xs:string) as item() external;
declare %a:since("exist", "4.4") function session:set-attribute($name as xs:string, $value as item()*) as item() external;
declare %a:since("exist", "4.4") function session:set-current-user($user-name as xs:string, $password as xs:string) as xs:boolean? external;
declare %a:since("exist", "4.4") function session:set-max-inactive-interval($interval as xs:int) as item() external;