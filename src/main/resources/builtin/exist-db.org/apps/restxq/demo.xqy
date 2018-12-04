xquery version "3.0";
(:~
 : eXist-db restxq demo app functions
 :
 : @see http://exist-db.org/exist/apps/fundocs/view.html?uri=http://exist-db.org/apps/restxq/demo&location=/db/apps/demo/examples/xforms/restxq-demo.xql&details=true
 :
 :)
module namespace demo ="http://exist-db.org/apps/restxq/demo";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare %a:since("exist", "4.4") function demo:addresses() as item()* external;
declare %a:since("exist", "4.4") function demo:create-or-edit-address($content as node()*) as item()* external;
declare %a:since("exist", "4.4") function demo:delete-address($id as xs:string*) as item()* external;
declare %a:since("exist", "4.4") function demo:get-address($id as xs:string*) as item()* external;
declare %a:since("exist", "4.4") function demo:search-addresses($query as xs:string*, $field as xs:string*) as item()* external;