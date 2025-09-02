xquery version "3.0";
(:~
 : BaseX Output Module functions
 :
 : @see http://docs.basex.org/wiki/Output_Module
 :)
module namespace out = "http://basex.org/modules/out";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare option o:requires "basex/7.3";

declare %a:since("basex", "9.0")
        %a:deprecated("basex", "10.0", "string:cr")
        %a:see-also("basex", "11.0", "fn:char") function out:cr() as xs:string external;
declare %a:since("basex", "7.3")
        %a:deprecated("basex", "10.0", "string:nl")
        %a:see-also("basex", "11.0", "fn:char") function out:nl() as xs:string external;
declare %a:since("basex", "7.3")
        %a:deprecated("basex", "10.0", "string:tab")
        %a:see-also("basex", "11.0", "fn:char") function out:tab() as xs:string external;
declare %a:since("basex", "7.3")
        %a:deprecated("basex", "10.0", "string:format") function out:format($format as xs:string, $item1 as item(), $items as item()...) as xs:string external;
