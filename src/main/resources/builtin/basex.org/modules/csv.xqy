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

declare type csv-string = (
    %a:since("basex", "7.7.2") %a:until("basex", "9.1") for xs:string |
    %a:since("basex", "9.1") for xs:string?
);

declare type csv-result = (
    %a:since("basex", "7.7.2") %a:until("basex", "7.8") for element(csv) |
    %a:since("basex", "7.8") %a:until("basex", "9.1") for document-node(element(csv)) |
    %a:since("basex", "9.1") for item()?
);

declare type csv-options = (
    %a:since("basex", "7.7.2") %a:until("basex", "8.2.1") for item() |
    %a:since("basex", "8.2.1") %a:until("basex", "8.6.7") for map(xs:string, item()) |
    %a:since("basex", "8.6.7") for map(*)?
);

declare type csv-data = (
    %a:since("basex", "7.7.2") %a:until("basex", "8.6.7") for node() |
    %a:since("basex", "8.6.7") for item()?
);

declare %a:since("basex", "7.7.2") function csv:parse($string as csv-string) as csv-result external;
declare %a:since("basex", "7.7.2") function csv:parse($string as csv-string, $options as csv-options) as csv-result external;
declare %a:since("basex", "7.7.2") function csv:serialize($input as csv-data) as xs:string external;
declare %a:since("basex", "7.7.2") function csv:serialize($input as csv-data, $options as csv-options) as xs:string external;
