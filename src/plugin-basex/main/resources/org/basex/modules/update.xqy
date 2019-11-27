xquery version "3.1";
(:~
 : BaseX Update Module functions
 :
 : @see http://docs.basex.org/wiki/Update_Module
 :)
module namespace update = "http://basex.org/modules/update";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare option o:requires "basex/9.0";

declare %a:since("basex", "9.0") function update:apply($function as function(*), $arguments as array(*)) as empty-sequence() external;
declare %a:since("basex", "9.0") function update:for-each($seq as item()*, $function as function(item()) as item()*) as empty-sequence() external;
declare %a:since("basex", "9.0") function update:for-each-pair($seq1 as item()*, $function as function(item()) as item()*) as empty-sequence() external;
declare %a:since("basex", "9.0") function update:map-for-each($map as map(*), $function as function(xs:anyAtomicType, item()*) as item()*) as item()* external;
declare %a:since("basex", "9.0") function update:output($result as item()*) as empty-sequence() external;
declare %a:since("basex", "9.0") function update:cache() as item()* external;

