xquery version "1.0-ml";
(:~
 : MarkLogic debug functions
 :
 : @see https://docs.marklogic.com/dbg
 :)
module namespace dbg = "http://marklogic.com/xdmp/dbg";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare option o:requires "marklogic/5.0";

declare %a:since("marklogic", "5.0") function dbg:attach($request-id as xs:unsignedLong) as empty-sequence() external;
declare %a:since("marklogic", "5.0") function dbg:attached() as xs:unsignedLong* external;
declare %a:since("marklogic", "5.0") function dbg:attached($server as xs:unsignedLong) as xs:unsignedLong* external;
declare %a:since("marklogic", "5.0") function dbg:break($request as xs:unsignedLong) as empty-sequence() external;
declare %a:since("marklogic", "5.0") function dbg:break($request as xs:unsignedLong, $expression as xs:unsignedLong) as empty-sequence() external;
declare %a:since("marklogic", "5.0") function dbg:breakpoints($request as xs:unsignedLong) as xs:unsignedLong* external;
declare %a:since("marklogic", "5.0") function dbg:clear($request as xs:unsignedLong, $expression as xs:unsignedLong) as empty-sequence() external;
declare %a:since("marklogic", "5.0") function dbg:connect($server as xs:unsignedLong) as empty-sequence() external;
declare %a:since("marklogic", "5.0") function dbg:connected() as xs:unsignedLong* external;
declare %a:since("marklogic", "5.0") function dbg:continue($request as xs:unsignedLong) as empty-sequence() external;
declare %a:since("marklogic", "5.0") function dbg:detach($request-id as xs:unsignedLong) as empty-sequence() external;
declare %a:since("marklogic", "5.0") function dbg:disconnect($server as xs:unsignedLong) as empty-sequence() external;
declare %a:since("marklogic", "5.0") function dbg:eval($xquery as xs:string) as xs:unsignedLong external;
declare %a:since("marklogic", "5.0") function dbg:eval($xquery as xs:string, $vars as item()*) as xs:unsignedLong external;
declare %a:restrict-until("$options", "marklogic", "8.0", "node()?")
        %a:restrict-since("$options", "marklogic", "8.0", "(element()?|map:map?)")
        %a:since("marklogic", "5.0") function dbg:eval($xquery as xs:string, $vars as item()*, $options as (node()?|map:map?)) as xs:unsignedLong external;
declare %a:since("marklogic", "5.0") function dbg:expr($request as xs:unsignedLong, $expression as xs:unsignedLong) as element(dbg:expression) external;
declare %a:since("marklogic", "5.0") function dbg:finish($request as xs:unsignedLong) as empty-sequence() external;
declare %a:since("marklogic", "5.0") function dbg:function($request as xs:unsignedLong, $uri as xs:string, $function as xs:QName) as xs:unsignedLong external;
declare %a:since("marklogic", "5.0") function dbg:invoke($uri as xs:string) as xs:unsignedLong external;
declare %a:since("marklogic", "5.0") function dbg:invoke($uri as xs:string, $vars as item()*) as xs:unsignedLong external;
declare %a:restrict-until("$options", "marklogic", "8.0", "node()?")
        %a:restrict-since("$options", "marklogic", "8.0", "(element()?|map:map?)")
        %a:since("marklogic", "5.0") function dbg:invoke($uri as xs:string, $vars as item()*, $options as (node()?|map:map?)) as xs:unsignedLong external;
declare %a:restrict-since("line", "marklogic", "8.0", "xs:unsignedInt")
        %a:since("marklogic", "5.0") function dbg:line($request as xs:unsignedLong, $uri as xs:string, $line as xs:nonNegativeInteger) as xs:unsignedLong* external;
declare %a:since("marklogic", "5.0") function dbg:next($request as xs:unsignedLong) as empty-sequence() external;
declare %a:since("marklogic", "5.0") function dbg:out($request as xs:unsignedLong) as empty-sequence() external;
declare %a:since("marklogic", "5.0") function dbg:stack($request as xs:unsignedLong) as element(dbg:stack) external;
declare %a:since("marklogic", "5.0") function dbg:status($request-id as xs:unsignedLong*) as element(dbg:requests)? external;
declare %a:since("marklogic", "5.0") function dbg:step($request as xs:unsignedLong) as empty-sequence() external;
declare %a:since("marklogic", "5.0") function dbg:stop() as empty-sequence() external;
declare %a:since("marklogic", "5.0") function dbg:stopped() as xs:unsignedLong* external;
declare %a:since("marklogic", "5.0") function dbg:stopped($server as xs:unsignedLong) as xs:unsignedLong* external;
declare %a:since("marklogic", "5.0") function dbg:value($request as xs:unsignedLong) as item()* external;
declare %a:since("marklogic", "5.0") function dbg:value($request as xs:unsignedLong, $expr as xs:string) as item()* external;
declare %a:since("marklogic", "5.0") function dbg:wait($request-id as xs:unsignedLong*, $timeout as xs:unsignedLong) as xs:unsignedLong? external;