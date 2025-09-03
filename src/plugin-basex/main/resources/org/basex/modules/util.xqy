xquery version "3.1";
(:~
 : BaseX Utility Module functions
 :
 : @see http://docs.basex.org/wiki/Utility_Module
 :)
module namespace util = "http://basex.org/modules/util";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare option o:requires "basex/8.5";

declare %a:since("basex", "9.1") function util:if($condition as item()*, $then as item()*) as item()* external;
declare %a:since("basex", "9.1") function util:if($condition as item()*, $then as item()*, $else as item()*) as item()* external;
declare %a:since("basex", "9.1")
        %a:deprecated("basex", "11.0", "otherwise keyword") function util:or($items as item()*, $default as item()*) as item()* external;
declare %a:since("basex", "8.5") %a:until("basex", "9.2", "util:item#2") function util:item-at($sequence as item()*, $position as xs:double) as item()? external;
declare %a:since("basex", "9.2")
        %a:deprecated("basex", "11.0", "fn:items-at") function util:item($sequence as item()*, $position as xs:double) as item()? external;
declare %a:since("basex", "8.5") %a:until("basex", "9.2", "util:range#3") function util:item-range($sequence as item()*, $first as xs:double, $last as xs:double) as item()* external;
declare %a:since("basex", "9.2") function util:range($sequence as item()*, $first as xs:double, $last as xs:double) as item()* external;
declare %a:since("basex", "8.5") %a:until("basex", "9.2", "util:last#1") function util:last-from($sequence as item()*) as item()? external;
declare %a:since("basex", "9.2")
        %a:deprecated("basex", "11.0", "fn:foot") function util:last($sequence as item()*) as item()? external;
declare %a:since("basex", "9.2")
        %a:deprecated("basex", "11.0", "fn:trunk") function util:init($sequence as item()*) as item()* external;
declare %a:since("basex", "9.0")
        %a:deprecated("basex", "11.0", "fn:replicate") function util:replicate($sequence as item()*, $count as xs:integer) as item()* external;
declare %a:since("basex", "9.5")
        %a:deprecated("basex", "11.0", "fn:replicate") function util:replicate($sequence as item()*, $count as xs:integer, $multiple as xs:boolean) as item()* external;
declare %a:since("basex", "9.2")
        %a:deprecated("basex", "11.0", "fn:characters") function util:chars($string as xs:string) as xs:string* external;
declare %a:since("basex", "9.3")
        %a:deprecated("basex", "11.0", "fn:distinct-ordered-nodes") function util:ddo($nodes as node()*) as node()* external;
declare %a:since("basex", "9.4") function util:root($nodes as node()*) as document-node() external;
declare %a:since("basex", "9.5")
        %a:deprecated("basex", "11.0", "fn:intersperse") function util:intersperse($items as item()*, $separator as item()*) as item()* external;
declare %a:since("basex", "9.5") %a:until("basex", "9.7", "util:count-within#1") function util:within($sequence as item()*, $min as xs:integer) as xs:boolean external;
declare %a:since("basex", "9.5") %a:until("basex", "9.7", "util:count-within#2") function util:within($sequence as item()*, $min as xs:integer, $max as xs:integer) as xs:boolean external;
(: @TODO add util:duplicates, util:array-members, util:array-values, util:map-entries, util:map-values from v9.5 :)
declare %a:since("basex", "9.7") function util:count-within($sequence as item()*, $min as xs:integer) as xs:boolean external;
declare %a:since("basex", "9.7") function util:count-within($sequence as item()*, $min as xs:integer, $max as xs:integer) as xs:boolean external;
declare %a:since("basex", "9.7") function util:strip-namespace($node as node()) as node() external;
declare %a:since("basex", "9.7") function util:strip-namespaces($node as node(), $prefixes as xs:string*) as node() external;

declare %a:since("basex", "9.5")
        %a:deprecated("basex", "11.0", "fn:duplicate-values") function util:duplicates($sequence as item()*) as xs:anyAtomicType* external;
declare %a:since("basex", "9.5")
        %a:deprecated("basex", "11.0", "fn:duplicate-values") function util:duplicates($sequence as item()*, $collation as xs:string) as xs:anyAtomicType* external;
declare %a:since("basex", "9.5")
        %a:deprecated("basex", "11.0", "array:members") function util:array-members($array as array(*)) as array(*) external;
declare %a:since("basex", "9.5")
        %a:deprecated("basex", "11.0", "array:items") function util:array-values($array as array(*)) as item()* external;
declare %a:since("basex", "9.5")
        %a:deprecated("basex", "11.0", "map:entries") function util:map-entries($map as map(*)) as map(xs:string, item()*)* external;
declare %a:since("basex", "9.5")
        %a:deprecated("basex", "11.0", "map:items") function util:map-values($map as map(*)) as item()* external;

