xquery version "3.0";
(:~
:  BaseX admin module functions
:
: @see http://docs.basex.org/wiki/Admin_Module
:)
module namespace admin = "http://basex.org/modules/admin";

import module namespace a = "http://reecedunn.co.uk/xquery/annotations" at "res://reecedunn.co.uk/xquery/annotations.xqy";

declare %a:since("basex", "7.5") function admin:sessions() as element(session)* external;
declare %a:since("basex", "7.5") function admin:logs() as element(file)* external;
declare %a:since("basex", "7.5") function admin:logs($date as xs:string) as element(entry)* external;
declare %a:since("basex", "7.8.2") function admin:logs($date as xs:string, $merge as xs:boolean) as element(entry)* external;
declare %a:since("basex", "8.0") function admin:write-log($text as xs:string) as empty-sequence() external;
declare %a:since("basex", "8.3") function admin:write-log($text as xs:string, $type as xs:string) as empty-sequence() external;
declare %a:since("basex", "8.2") function admin:delete-logs($date as xs:string) as empty-sequence() external;
declare %a:since("basex", "7.5") %a:deprecated("basex", "8.0", "user:list-details#1") function admin:users() as element(user)* external;
declare %a:since("basex", "7.5") %a:deprecated("basex", "8.0", "user:list-details#2") function admin:users($db as xs:string) as element(user)* external;

