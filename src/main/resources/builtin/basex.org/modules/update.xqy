xquery version "3.0";
(:~
: BaseX Update Module functions
:
: @see http://docs.basex.org/wiki/Update_Module
:)
module namespace update = "http://basex.org/modules/update";

import module namespace a = "http://reecedunn.co.uk/xquery/annotations" at "res://reecedunn.co.uk/xquery/annotations.xqy";

declare %a:since("basex", "9.0") function update:apply($function as function(*), $arguments as array(*)) as empty-sequence() external;
declare %a:since("basex", "9.0") function update:for-each($seq as item()*, $function as function(item()) as item()*) as empty-sequence() external;
declare %a:since("basex", "9.0") function update:for-each-pair($seq1 as item()*, $function as function(item()) as item()*) as empty-sequence() external;
declare %a:since("basex", "9.0") function update:map-for-each($map as map(*), $function as function(xs:anyAtomicType, item()*) as item()*) as item()* external;
declare %a:since("basex", "9.0") function update:output($result as item()*) as empty-sequence() external;
declare %a:since("basex", "9.0") function update:cache() as item()* external;

