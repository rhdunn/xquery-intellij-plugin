xquery version "3.0";
(:~
 : eXist-db atomic image-link-generator functions
 :
 : @see http://exist-db.org/exist/apps/fundocs/view.html?uri=http://atomic.exist-db.org/xquery/atomic/image-link-generator&location=/db/apps/wiki/modules/display/image-link-generator.xqm&details=true
 :
 :)
module namespace image-link-generator ="http://atomic.exist-db.org/xquery/atomic/image-link-generator";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare %a:since("exist", "4.4") function image-link-generator:generate-href($image-uuid as item()*, $uri-name as item()*) as item()* external;
declare %a:since("exist", "4.4") function image-link-generator:get-resource($id as item()*) as item()* external;