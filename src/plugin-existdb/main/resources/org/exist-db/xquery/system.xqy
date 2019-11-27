xquery version "3.0";
(:~
 : eXist-db eXist system module functions
 :
 : @see http://exist-db.org/exist/apps/fundocs/view.html?uri=http://exist-db.org/xquery/system&location=java:org.exist.xquery.functions.system.SystemModule&details=true
 :)
module namespace system = "http://exist-db.org/xquery/system";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare %a:since("exist", "4.4") function system:as-user($username as xs:string, $password as xs:string?, $code-block as item()*) as item()* external;
declare %a:since("exist", "4.4") function system:clear-trace() as item() external;
declare %a:since("exist", "4.4") function system:clear-xquery-cache() as empty-sequence() external;
declare %a:since("exist", "4.4") function system:count-instances-active() as xs:integer external;
declare %a:since("exist", "4.4") function system:count-instances-available() as xs:integer external;
declare %a:since("exist", "4.4") function system:count-instances-max() as xs:integer external;
declare %a:since("exist", "4.4") function system:enable-tracing($enable as xs:boolean) as item() external;
declare %a:since("exist", "4.4") function system:enable-tracing($enable as xs:boolean, $tracelog as xs:boolean) as item() external;
declare %a:since("exist", "4.4") function system:export($dir as xs:string, $incremental as xs:boolean?, $zip as xs:boolean?) as node() external;
declare %a:since("exist", "4.4") function system:export-silently($dir as xs:string, $incremental as xs:boolean?, $zip as xs:boolean?) as xs:boolean external;
declare %a:since("exist", "4.4") function system:function-available($function-name as xs:QName, $arity as xs:integer) as xs:boolean external;
declare %a:since("exist", "4.4") function system:get-build() as xs:string external;
declare %a:since("exist", "4.4") function system:get-exist-home() as xs:string external;
declare %a:since("exist", "4.4") function system:get-index-statistics() as node()? external;
declare %a:since("exist", "4.4") function system:get-memory-free() as xs:long external;
declare %a:since("exist", "4.4") function system:get-memory-max() as xs:long external;
declare %a:since("exist", "4.4") function system:get-memory-total() as xs:long external;
declare %a:since("exist", "4.4") function system:get-module-load-path() as xs:string external;
declare %a:since("exist", "4.4") function system:get-revision() as xs:string external;
declare %a:since("exist", "4.4") function system:get-running-jobs() as item() external;
declare %a:since("exist", "4.4") function system:get-running-xqueries() as item() external;
declare %a:since("exist", "4.4") function system:get-scheduled-jobs() as item() external;
declare %a:since("exist", "4.4") function system:get-uptime() as xs:dayTimeDuration external;
declare %a:since("exist", "4.4") function system:get-version() as xs:string external;
declare %a:since("exist", "4.4") function system:import($dir-or-file as xs:string, $admin-pass as xs:string?, $new-admin-pass as xs:string?) as node() external;
declare %a:since("exist", "4.4") function system:import-silently($dir-or-file as xs:string, $admin-pass as xs:string?, $new-admin-pass as xs:string?) as node() external;
declare %a:since("exist", "4.4") function system:kill-running-xquery($xquery-id as xs:integer) as item() external;
declare %a:since("exist", "4.4") function system:kill-running-xquery($xquery-id as xs:integer, $wait-time as xs:long) as item() external;
declare %a:since("exist", "4.4") function system:restore($dir-or-file as xs:string, $admin-pass as xs:string?, $new-admin-pass as xs:string?) as node() external;
declare %a:since("exist", "4.4") function system:shutdown() as item() external;
declare %a:since("exist", "4.4") function system:shutdown($delay as xs:long) as item() external;
declare %a:since("exist", "4.4") function system:trace() as node() external;
declare %a:since("exist", "4.4") function system:tracing-enabled() as xs:boolean external;
declare %a:since("exist", "4.4") function system:trigger-system-task($java-classname as xs:string, $task-parameters as node()?) as item() external;
declare %a:since("exist", "4.4") function system:update-statistics() as empty-sequence() external;