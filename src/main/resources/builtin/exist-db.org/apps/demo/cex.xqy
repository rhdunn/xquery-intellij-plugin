xquery version "3.0";
(:~
 : eXist-db demo cex app functions
 :
 : @see http://exist-db.org/exist/apps/fundocs/view.html?uri=http://exist-db.org/apps/demo/cex&location=/db/apps/demo/modules/cex.xql&details=true
 :
 :)
module namespace cex ="http://exist-db.org/apps/demo/cex";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare %a:since("exist", "4.4") function cex:query($node as node()*, $model as map(*), $query as xs:string?) as item()* external;