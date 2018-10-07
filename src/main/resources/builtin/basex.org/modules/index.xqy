xquery version "3.0";
(:~
: BaseX Index Module functions
:
: @see http://docs.basex.org/wiki/Index_Module
:)
module namespace index = "http://basex.org/modules/index";

import module namespace a = "http://reecedunn.co.uk/xquery/annotations" at "res://reecedunn.co.uk/xquery/annotations.xqy";

declare %a:since("basex", "7.1") function index:facets($db as xs:string) as xs:string external;
declare %a:since("basex", "7.1") function index:facets($db as xs:string, $type as xs:string) as xs:string external;
declare %a:since("basex", "7.1") function index:texts($db as xs:string) as element(value)* external;
declare %a:since("basex", "7.1") function index:texts($db as xs:string, $prefix as xs:string) as element(value)* external;
declare %a:since("basex", "7.3") function index:texts($db as xs:string, $start as xs:string, $ascending as xs:boolean) as element(value)* external;
declare %a:since("basex", "7.1") function index:attributes($db as xs:string) as element(value)* external;
declare %a:since("basex", "7.3") function index:attributes($db as xs:string, $prefix as xs:string) as element(value)* external;
declare %a:since("basex", "7.1") function index:attributes($db as xs:string, $start as xs:string, $ascending as xs:boolean) as element(value)* external;
declare %a:since("basex", "8.4") function index:tokens($db as xs:string) as element(value)* external;
declare %a:since("basex", "7.1") function index:element-names($db as xs:string) as element(value)* external;
declare %a:since("basex", "7.1") function index:attribute-names($db as xs:string) as element(value)* external;