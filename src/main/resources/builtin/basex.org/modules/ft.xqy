xquery version "3.1";
(:~
 : BaseX full-text functions
 :
 : @see http://docs.basex.org/Full-Text_Module
 :)
module namespace ft = "http://basex.org/modules/ft";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";

declare option a:requires "basex/7.0"; (: NOTE: 7.0 is the earliest version definitions are available for. :)

declare %a:since("basex", "7.0") function ft:search($db as xs:string, $terms as item()*) as text()* external;
declare %a:since("basex", "7.2") function ft:search($db as xs:string, $terms as item()*, $options as map(*)?) as text()* external;
declare %a:since("basex", "7.8") function ft:contains($input as item()*, $terms as item()*) as xs:boolean external;
declare %a:since("basex", "7.8") function ft:contains($input as item()*, $terms as item()*, $options as map(*)?) as xs:boolean external;
declare %a:since("basex", "7.0") function ft:mark($nodes as node()*) as node()* external;
declare %a:since("basex", "7.0") function ft:mark($nodes as node()*, $name as xs:string) as node()* external;
declare %a:since("basex", "7.0") function ft:extract($nodes as node()*) as node()* external;
declare %a:since("basex", "7.0") function ft:extract($nodes as node()*, $name as xs:string) as node()* external;
declare %a:since("basex", "7.0") function ft:extract($nodes as node()*, $name as xs:string, $length as xs:integer) as node()* external;
declare %a:since("basex", "7.0") function ft:count($nodes as node()*) as xs:integer external;
declare %a:since("basex", "7.0") function ft:score($item as item()*) as xs:double* external;
declare %a:since("basex", "7.1") function ft:tokens($db as xs:string) as element(value)* external;
declare %a:since("basex", "7.1") function ft:tokens($db as xs:string, $prefix as xs:string) as element(value)* external;
declare %a:since("basex", "7.1") function ft:tokenize($string as xs:string?) as xs:string* external;
declare %a:since("basex", "7.1") function ft:tokenize($string as xs:string?, $options as map(*)?) as xs:string* external;
declare %a:since("basex", "8.0") function ft:normalize($string as xs:string?) as xs:string external;
declare %a:since("basex", "8.0") function ft:normalize($string as xs:string?, $options as map(*)?) as xs:string external;