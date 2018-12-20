xquery version "3.0";
(:~
 : eXist-db XSL-FO module functions
 :
 : @see http://exist-db.org/exist/apps/fundocs/view.html?uri=http://exist-db.org/xquery/xslfo&location=java:org.exist.xquery.modules.xslfo.XSLFOModule&details=true
 :)
module namespace xslfo = "http://exist-db.org/xquery/xslfo";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare %a:since("exist", "4.4") function xslfo:render($document as node(), $mime-type as xs:string, $parameters as node()?) as xs:base64Binary? external;
declare %a:since("exist", "4.4") function xslfo:render($document as node(), $mime-type as xs:string, $parameters as node()?, $config-file as node()?) as xs:base64Binary? external;