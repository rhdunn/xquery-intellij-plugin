xquery version "3.0";
(:~
 : eXist-db admin template functions
 :
 : @see http://exist-db.org/exist/apps/fundocs/view.html?uri=http://exist-db.org/apps/admin/templates&location=/db/apps/monex/modules/app.xql&details=true
 :
 :)
module namespace app ="http://exist-db.org/apps/admin/templates";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare %a:since("exist", "4.4") function app:active-panel($node as node(), $model as map(*), $instance as xs:string) as item()* external;
declare %a:since("exist", "4.4") function app:adjust-trace($trace as item()*, $tare as item()*) as item()* external;
declare %a:since("exist", "4.4") function app:btn-profiling($node as node(), $model as map(*)) as item()* external;
declare %a:since("exist", "4.4") function app:btn-tare($node as node(), $model as map(*)) as item()* external;
declare %a:since("exist", "4.4") function app:current-user($node as node(), $model as map(*)) as item()* external;
declare %a:since("exist", "4.4") function app:default-timeline($node as node(), $model as map(*), $instance as xs:string, $gid as item()*, $start as xs:string, $end as xs:string) as item()* external;
declare %a:since("exist", "4.4") function app:edit-source($node as node(), $model as map(*), $instance as xs:string, $timestamp as xs:long) as node()* external;
declare %a:since("exist", "4.4") function app:form-action-to-current-url($node as node(), $model as map(*)) as item()* external;
declare %a:since("exist", "4.4") function app:function-stats($node as node(), $model as map(*), $sort as xs:string) as item()* external;
declare %a:since("exist", "4.4") function app:get-instance($node as node(), $model as map(*), $instance as xs:string) as item()* external;
declare %a:since("exist", "4.4") function app:index-stats($node as node(), $model as map(*), $sort as xs:string) as item()* external;
declare %a:since("exist", "4.4") function app:instances($node as node(), $model as map(*), $instance as xs:string) as item()* external;
declare %a:since("exist", "4.4") function app:instances-data($node as node(), $model as map(*), $instance as xs:string) as item()* external;
declare %a:since("exist", "4.4") function app:java-version($node as node(), $model as map(*)) as node()? external;
declare %a:since("exist", "4.4") function app:jmxs-for-time-interval($instance as xs:string, $start as xs:dateTime, $end as xs:dateTime) as item()* external;
declare %a:since("exist", "4.4") function app:load-record($node as node(), $model as map(*), $instance as xs:string, $timestamp as xs:long) as item()* external;
declare %a:since("exist", "4.4") function app:make-timeline($instance as xs:string, $xpaths as xs:string+, $labels as xs:string+, $types as xs:string+, $start as xs:dateTime, $end as xs:dateTime) as item()* external;
declare %a:since("exist", "4.4") function app:milliseconds-to-time($timestamp as xs:long) as xs:dateTime external;
declare %a:since("exist", "4.4") function app:process-time-interval-params($pstart as xs:string, $pend as xs:string) as xs:dateTime+ external;
declare %a:since("exist", "4.4") function app:profile($node as node(), $model as map(*), $action as xs:string?) as item()* external;
declare %a:since("exist", "4.4") function app:query-stats($node as node(), $model as map(*), $sort as xs:string) as item()* external;
declare %a:since("exist", "4.4") function app:scheduler-enabled($node as node(), $model as map(*)) as item()* external;
declare %a:since("exist", "4.4") function app:serialize-to-json($result as item()*) as item()* external;
declare %a:since("exist", "4.4") function app:time-navigation-back($node as node(), $model as map(*), $instance as xs:string, $timestamp as xs:long) as item()* external;
declare %a:since("exist", "4.4") function app:time-navigation-forward($node as node(), $model as map(*), $instance as xs:string, $timestamp as xs:long) as item()* external;
declare %a:since("exist", "4.4") function app:time-to-milliseconds($dateTime as xs:dateTime) as item()* external;
declare %a:since("exist", "4.4") function app:timeline($node as node(), $model as map(*), $instance as xs:string, $select as xs:string, $labels as xs:string, $type as xs:string, $start as xs:string, $end as xs:string) as item()* external;
declare %a:since("exist", "4.4") function app:timestamp($node as node(), $model as map(*), $timestamp as xs:long) as item()*  external;
declare %a:since("exist", "4.4") function app:user-info($node as node(), $model as map(*)) as item()* external;