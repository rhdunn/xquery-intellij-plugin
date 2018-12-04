xquery version "3.0";
(:~
 : eXist-db demo app shakespeare test functions
 :
 : @see http://exist-db.org/exist/apps/fundocs/view.html?uri=http://exist-db.org/apps/demo/shakespeare/tests&location=/db/apps/demo/examples/tests/shakespeare-tests.xql&details=true
 :
 :)
module namespace t ="http://exist-db.org/apps/demo/shakespeare/tests";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare %a:since("exist", "4.4") function t:create-query($queryStr as xs:string?, $mode as xs:string) as item()* external;
declare %a:since("exist", "4.4") function t:query($queryStr as xs:string?, $mode as xs:string) as item()* external;
declare %a:since("exist", "4.4") function t:show-hits($queryStr as xs:string?, $mode as xs:string) as item()* external;