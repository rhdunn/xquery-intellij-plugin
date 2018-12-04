xquery version "3.0";
(:~
 : eXist-db monex hipchat functions
 :
 : @see http://exist-db.org/exist/apps/fundocs/view.html?uri=http://exist-db.org/apps/monex/hipchat&location=/db/apps/monex/modules/hipchat.xql&details=true
 :
 :)
module namespace hipchat = "http://exist-db.org/apps/monex/hipchat";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare %a:since("exist", "4.4") function hipchat:notify($root as xs:string, $instance as element(), $status as xs:string, $data as element(), $attachment as element()?) as item()* external;