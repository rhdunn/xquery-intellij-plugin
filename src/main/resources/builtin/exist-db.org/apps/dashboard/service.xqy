xquery version "3.0";
(:~
 : eXist-db dashboard service functions
 :
 : @see http://exist-db.org/exist/apps/fundocs/view.html?uri=http://exist-db.org/apps/dashboard/service&location=/db/apps/dashboard/plugins/browsing/service.xql&details=true
 :
 :)
module namespace service ="http://exist-db.org/apps/dashboard/service";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare %a:since("exist", "4.4") function service:change-properties($resources as xs:string, $owner as xs:string?, $group as xs:string?, $mime as xs:string?) as item()* external;
declare %a:since("exist", "4.4") function service:copyOrMove($target as xs:string, $sources as xs:string*, $action as xs:string) as item()* external;
declare %a:since("exist", "4.4") function service:create-collection($collection as xs:string, $create as xs:string) as item()* external;
declare %a:since("exist", "4.4") function service:delete-resources($resources as xs:string*) as item()* external;
declare %a:since("exist", "4.4") function service:get-acl($id as xs:string, $acl-id as xs:string) as element() external;
declare %a:since("exist", "4.4") function service:get-permissions($id as xs:string, $class as xs:string) as element() external;
declare %a:since("exist", "4.4") function service:resources($collection as xs:string) as item()* external;
declare %a:since("exist", "4.4") function service:save-permissions($id as xs:string, $class as xs:string) as item()* external;
declare %a:since("exist", "4.4") function service:get-running-jobs() as item()* external;
declare %a:since("exist", "4.4") function service:get-running-xqueries() as item()* external;
declare %a:since("exist", "4.4") function service:get-scheduled-jobs() as item()* external;
declare %a:since("exist", "4.4") function service:kill($id as item()*) as item()* external;