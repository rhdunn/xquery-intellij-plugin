xquery version "3.0";
(:~
: BaseX Higher Order Functions Module
:
: @see http://docs.basex.org/wiki/Higher-Order_Functions_Module
:)
module namespace hof = "http://basex.org/modules/hof";

import module namespace a = "http://reecedunn.co.uk/xquery/annotations" at "res://reecedunn.co.uk/xquery/annotations.xqy";

declare %a:since("basex", "7.0") function hof:fold-left1($seq as item()+, $f as function(item()*, item()) as item()*) as item()* external;
declare %a:since("basex", "7.0") function hof:until($pred as function(item()*) as xs:boolean, $f as function(item()*) as item()*, $start as item()*) as item()* external;
declare %a:since("basex", "8.1") function hof:scan-left($seq as item()*, $start as item()*, $f as function(item()*, item()) as item()*) as item()* external;
declare %a:since("basex", "8.1") function hof:take-while($seq as item()*, $pred as function(item()) as xs:boolean) as item()* external;
declare %a:since("basex", "7.2") function hof:top-k-by($seq as item()*, $sort-key as function(item()) as item(), $k as xs:integer) as item()* external;
declare %a:since("basex", "7.2") function hof:top-k-with($seq as item()*, $lt as function(item(), item()) as xs:boolean, $k as xs:integer) as item()* external;
declare %a:since("basex", "7.0") function hof:id($expr as item()*) as item()* external;
declare %a:since("basex", "7.0") function hof:const($expr as item()*, $ignored as item()*) as item()* external;
declare %a:since("basex", "7.0") %a:until("basex", "7.2") function hof:iterate($f as function(item()*, item()*) as item()*, $seq as item()*) as item()* external;