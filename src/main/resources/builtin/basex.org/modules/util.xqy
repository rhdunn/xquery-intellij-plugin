xquery version "3.0";
(:~
 : BaseX Utility Module functions
 :
 : @see http://docs.basex.org/wiki/Utility_Module
 :)
module namespace util = "http://basex.org/modules/util";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";

declare option a:requires "basex/8.5";

declare %a:since("basex", "9.1") function util:if($condition as item()*, $then as item()*) as item()* external;
declare %a:since("basex", "9.1") function util:if($condition as item()*, $then as item()*, $else as item()*) as item()* external;
declare %a:since("basex", "9.1") function util:or($items as item()*, $default as item()*) as item()* external;
declare %a:since("basex", "8.5") function util:item-at($sequence as item()*, $position as xs:double) as item()? external;
declare %a:since("basex", "8.5") function util:item-range($sequence as item()*, $first as xs:double, $last as xs:double) as item()* external;
declare %a:since("basex", "8.5") function util:last-from($sequence as item()*) as item()? external;
declare %a:since("basex", "9.0") function util:replicate($sequence as item()*, $count as xs:integer) as item()* external;
