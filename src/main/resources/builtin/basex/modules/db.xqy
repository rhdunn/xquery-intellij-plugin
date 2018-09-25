xquery version "3.0";
(:~
: BaseX Database Module functions
:
: @see http://docs.basex.org/wiki/Database_Module
:)
module namespace db = "http://basex.org/modules/db";

import module namespace a = "http://reecedunn.co.uk/xquery/annotations" at "res://reecedunn.co.uk/xquery/annotations.xqy";

declare %a:since("basex", "7.0") function db:system() as element(system) external;
declare %a:since("basex", "7.0") function db:option($name as xs:string) as xs:string external;
declare %a:since("basex", "7.0") function db:info($db as xs:string) as element(database) external;
declare %a:since("basex", "7.0") function db:property($db as xs:string, $name as xs:string) as xs:anyAtomicType external;