xquery version "3.0";
(:~
 : eXist-db monex notification functions
 :
 : @see http://exist-db.org/exist/apps/fundocs/view.html?uri=http://exist-db.org/apps/homepage/app&location=/db/apps/homepage/modules/app.xql&details=true
 :
 :)
module namespace notification = "http://exist-db.org/apps/monex/notification";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare %a:since("exist", "4.4") function notification:notify($root as xs:string, $instance as element(), $status as xs:string, $response as element(), $attachment as element()?) as item()* external;
declare %a:since("exist", "4.4") function notification:send-email($receiver as xs:string, $subject as xs:string, $data as node()*, $settings as element(), $attachment as element()?) as item()* external;