xquery version "3.0";
(:~
 : BaseX Random Module functions
 :
 : @see http://docs.basex.org/wiki/Random_Module
 :)
module namespace random = "http://basex.org/modules/random";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare option o:requires "basex/7.5";

declare %a:since("basex", "7.5") function random:double() as xs:double external;
declare %a:since("basex", "7.5") function random:integer() as xs:integer external;
declare %a:since("basex", "7.5") function random:integer($max as xs:integer) as xs:integer external;
declare %a:since("basex", "7.5") function random:seeded-double($seed as xs:integer, $num as xs:integer) as xs:double* external;
declare %a:since("basex", "7.5") function random:seeded-integer($seed as xs:integer, $num as xs:integer) as xs:integer* external;
declare %a:since("basex", "7.5") function random:seeded-integer($seed as xs:integer, $num as xs:integer, $max as xs:integer) as xs:integer* external;
declare %a:since("basex", "7.5") function random:gaussian($num as xs:integer) as xs:double* external;
declare %a:since("basex", "8.5") function random:seeded-permutation($seed as xs:integer, $items as item()*) as item()* external;
declare %a:since("basex", "7.5") function random:uuid() as xs:string external;