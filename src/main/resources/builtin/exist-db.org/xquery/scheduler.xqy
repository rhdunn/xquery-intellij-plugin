xquery version "3.0";
(:~
 : eXist-db Quartz Scheduler module functions
 :
 : @see http://exist-db.org/exist/apps/fundocs/view.html?uri=http://exist-db.org/xquery/scheduler&location=java:org.exist.xquery.modules.scheduler.SchedulerModule&details=true
 :)
module namespace scheduler = "http://exist-db.org/xquery/scheduler";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

scheduler:delete-scheduled-job($job-name as xs:string) as xs:boolean
scheduler:get-scheduled-jobs() as node()
scheduler:pause-scheduled-job($job-name as xs:string) as xs:boolean
scheduler:resume-scheduled-job($job-name as xs:string) as xs:boolean
scheduler:schedule-java-cron-job($java-classname as xs:string, $cron-expression as xs:string, $job-name as xs:string) as xs:boolean
scheduler:schedule-java-cron-job($java-classname as xs:string, $cron-expression as xs:string, $job-name as xs:string, $job-parameters as element()?) as xs:boolean
scheduler:schedule-java-periodic-job($java-classname as xs:string, $period as xs:integer, $job-name as xs:string, $job-parameters as element()?, $delay as xs:integer, $repeat as xs:integer) as xs:boolean
scheduler:schedule-xquery-cron-job($xquery-resource as xs:string, $cron-expression as xs:string, $job-name as xs:string) as xs:boolean
scheduler:schedule-xquery-cron-job($xquery-resource as xs:string, $cron-expression as xs:string, $job-name as xs:string, $job-parameters as element()?) as xs:boolean
scheduler:schedule-xquery-cron-job($xquery-resource as xs:string, $cron-expression as xs:string, $job-name as xs:string, $job-parameters as element()?, $unschedule as xs:boolean) as xs:boolean
scheduler:schedule-xquery-periodic-job($xquery-resource as xs:string, $period as xs:integer, $job-name as xs:string, $job-parameters as element()?, $delay as xs:integer, $repeat as xs:integer) as xs:boolean
scheduler:schedule-xquery-periodic-job($xquery-resource as xs:string, $period as xs:integer, $job-name as xs:string, $job-parameters as element()?, $delay as xs:integer, $repeat as xs:integer, $unschedule as xs:boolean) as xs:boolean