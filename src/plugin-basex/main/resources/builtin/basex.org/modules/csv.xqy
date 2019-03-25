xquery version "3.1";
(:~
 : BaseX CSV Module functions
 :
 : @see http://docs.basex.org/wiki/CSV_Module
 :)
module namespace csv = "http://basex.org/modules/csv";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare option o:requires "basex/7.7.2";

declare %a:since("basex", "7.7.2") function csv:parse($string (: as [basex/7.7.2]xs:string [basex/9.1]xs:string? :)) (: as [basex/7.7.2]element(csv) [basex/7.8]document-node(element(csv)) [basex/9.1]item()? :) external;
declare %a:since("basex", "7.7.2") function csv:parse($string (: as [basex/7.7.2]xs:string [basex/9.1]xs:string? :), $options (: as [basex/7.7.2]item() [basex/8.2.1]map(xs:string, item()) [basex/8.6.7]map(*) :)) (: as [basex/7.7.2]element(csv) [basex/7.8]document-node(element(csv)) [basex/9.1]item()? :) external;
declare %a:since("basex", "7.7.2") function csv:serialize($input (: as [basex/7.7.2]node() [basex/8.6.7]item()? :)) as xs:string external;
declare %a:since("basex", "7.7.2") function csv:serialize($input (: as [basex/7.7.2]node() [basex/8.6.7]item()? :), $options (: as [basex/7.7.2]item() [basex/8.2.1]map(xs:string, item()) [basex/8.6.7]map(*) :)) as xs:string external;
