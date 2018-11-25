xquery version "3.0";
(:~
 : eXist-db html2wiki functions
 : @see http://exist-db.org/exist/apps/fundocs/view.html?uri=http://atomic.exist-db.org/xquery/html2wiki&location=/db/apps/wiki/modules/html2wiki.xql
 :
 :)
module namespace html2wiki = "http://atomic.exist-db.org/xquery/html2wiki";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare %a:since("exist", "4.4") function html2wiki:html2wiki($nodes as element()*) as item()* external;
declare %a:since("exist", "4.4") function html2wiki:macro-parameters($paramStr as xs:string?) as item()* external;
declare %a:since("exist", "4.4") function html2wiki:text($text as xs:string) as item()* external;
declare %a:since("exist", "4.4") function html2wiki:transform($nodes as node()*) as item()* external;