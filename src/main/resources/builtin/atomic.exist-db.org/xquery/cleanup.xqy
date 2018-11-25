xquery version "3.0";
(:~
 : eXist-db cleanup function
 :
 : @see http://exist-db.org/exist/apps/fundocs/view.html?uri=http://atomic.exist-db.org/xquery/cleanup&location=/db/apps/wiki/modules/cleanup.xql&details=true
 :
 :)
module namespace cleanup ="http://atomic.exist-db.org/xquery/cleanup";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare %a:since("exist", "4.4") function cleanup:clean($nodes as node()*) as item()* external;
