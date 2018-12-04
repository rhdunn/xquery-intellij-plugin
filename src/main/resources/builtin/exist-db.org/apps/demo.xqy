xquery version "3.0";
(:~
 : eXist-db demo app functions
 :
 : @see http://exist-db.org/exist/apps/fundocs/view.html?uri=http://exist-db.org/apps/demo&location=/db/apps/demo/modules/demo.xql&details=true
 :
 :)
module namespace demo ="http://exist-db.org/apps/demo";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare %a:since("exist", "4.4") function demo:display-source($node as node(), $model as map(*), $lang as xs:string?, $type as xs:string?) as item()* external;
declare %a:since("exist", "4.4") function demo:error-handler-test($node as node(), $model as map(*), $number as xs:string?) as item()* external;
declare %a:since("exist", "4.4") function demo:link-to-home($node as node(), $model as map(*)) as item()* external;
declare %a:since("exist", "4.4") function demo:run-tests($node as node(), $model as map(*)) as item()* external;