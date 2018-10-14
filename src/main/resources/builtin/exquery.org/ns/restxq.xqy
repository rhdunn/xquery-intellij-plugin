xquery version "3.0";
(:~
 : BaseX RESTXQ Module functions
 :
 : @see http://docs.basex.org/wiki/RESTXQ_Module
 :)
module namespace rest = "http://exquery.org/ns/restxq";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare namespace wadl = "http://wadl.dev.java.net/2009/02";

declare option o:requires-import "basex/7.7; location-uri=(none)";

declare %a:since("basex", "7.7") function rest:base-uri() as xs:anyURI external;
declare %a:since("basex", "7.7") function rest:uri() as xs:anyURI external;
declare %a:since("basex", "7.7") function rest:wadl() as element(wadl:application) external;
declare %a:since("basex", "8.6") function rest:init() as empty-sequence() external;