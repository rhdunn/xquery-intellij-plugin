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

declare %a:restrict-until("return", "basex", "7.8", "element(csv)")
        %a:restrict-until("return", "basex", "9.1", "document-node(element(csv))")
        %a:restrict-until("$string", "basex", "9.1", "xs:string")
        %a:since("basex", "7.7.2") function csv:parse($string as xs:string?) as item()? external;
declare %a:restrict-until("return", "basex", "7.8", "element(csv)")
        %a:restrict-until("return", "basex", "9.1", "document-node(element(csv))")
        %a:restrict-until("$string", "basex", "9.1", "xs:string")
        %a:restrict-since("$options", "basex", "8.2.1", "map(xs:string, item())")
        %a:restrict-since("$options", "basex", "8.6.7", "map(*)")
        %a:since("basex", "7.7.2") function csv:parse($string as xs:string?, $options as item()) as item()? external;
declare %a:restrict-until("$input", "basex", "8.6.7", "node()")
        %a:since("basex", "7.7.2") function csv:serialize($input as item()?) as xs:string external;
declare %a:restrict-until("$input", "basex", "8.6.7", "node()")
        %a:restrict-since("$options", "basex", "8.2.1", "map(xs:string, item())")
        %a:restrict-since("$options", "basex", "8.6.7", "map(*)")
        %a:since("basex", "7.7.2") function csv:serialize($input as item()?, $options as item()) as xs:string external;
