xquery version "3.0";
(:~
: BaseX RESTXQ Module functions
:
: @see http://docs.basex.org/wiki/RESTXQ_Module
:
:)
module namespace rest = "http://exquery.org/ns/restxq";

import module namespace a = "http://reecedunn.co.uk/xquery/annotations" at "res://reecedunn.co.uk/xquery/annotations.xqy";

declare namespace wadl = "http://wadl.dev.java.net/2009/02";

declare %a:since("basex", "7.7") function rest:base-uri() as xs:anyURI external;
declare %a:since("basex", "7.7") function rest:uri() as xs:anyURI external;
declare %a:since("basex", "7.7") function rest:wadl() as element(wadl:application) external;
declare %a:since("basex", "8.6") function rest:init() as empty-sequence() external;