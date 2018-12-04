xquery version "3.0";
(:~
 : eXist-db demo app shakespeare functions
 :
 : @see http://exist-db.org/exist/apps/fundocs/view.html?uri=http://exist-db.org/apps/demo/shakespeare&location=/db/apps/demo/examples/web/shakespeare.xql&details=true
 :
 :)
module namespace shakes ="http://exist-db.org/apps/demo/shakespeare";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare %a:since("exist", "4.4") function shakes:create-query($queryStr as xs:string?, $mode as xs:string?) as item()* external;
declare %a:since("exist", "4.4") function shakes:do-query($queryStr as xs:string?, $mode as xs:string?) as item()* external;
declare %a:since("exist", "4.4") function shakes:from-session($node as node()*, $model as map(*)) as item()* external;
declare %a:since("exist", "4.4") function shakes:hit-count($node as node()*, $model as map(*)) as item()* external;
declare %a:since("exist", "4.4") function shakes:query($node as node()*, $model as map(*), $query as xs:string?, $mode as xs:string?) as item()* external;
declare %a:since("exist", "4.4") function shakes:show-hits($node as node()*, $model as map(*), $start as xs:int) as item()* external;