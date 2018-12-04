xquery version "3.0";
(:~
 : eXist-db demo app contact functions
 :
 : @see http://exist-db.org/exist/apps/fundocs/view.html?uri=http://exist-db.org/apps/demo/restxq/contacts&location=/db/apps/demo/examples/contacts/contacts.xql&details=true
 :
 :)
module namespace contacts ="http://exist-db.org/apps/demo/restxq/contacts";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";
(:declare namespace functx = "http://www.functx.com";:)

declare %a:since("exist", "4.4") function contacts:delete($id as xs:string) as item()* external;
declare %a:since("exist", "4.4") function contacts:get($id as xs:string) as item()* external;
declare %a:since("exist", "4.4") function contacts:get-image($id as xs:string) as item()* external;
declare %a:since("exist", "4.4") function contacts:get-image-by-resource-id($resource-id as xs:string) as item()* external;
declare %a:since("exist", "4.4") function contacts:get-image-from-uri-json($uri as xs:string*) as item()* external;
declare %a:since("exist", "4.4") function contacts:get-image-from-uri-xml($uri as xs:string*) as item()* external;
declare %a:since("exist", "4.4") function contacts:get-multiple($skip as xs:integer*, $take as xs:integer*) as item()* external;
declare %a:since("exist", "4.4") function contacts:post($json-payload as xs:string) as item()* external;
declare %a:since("exist", "4.4") function contacts:put($json-payload as xs:string, $id as xs:string) as item()* external;
declare %a:since("exist", "4.4") function contacts:put-image-from-uri($id as xs:string, $uri as xs:string*) as item()* external;
(: @TODO the following function is included in the documentation; it seems incorrect to have it expressed here, but maybe that's an incorrect impression :)
(:declare %a:since("exist", "4.4") function functx:if-empty($arg as item()?, $value as item()*) as item()* external;:)