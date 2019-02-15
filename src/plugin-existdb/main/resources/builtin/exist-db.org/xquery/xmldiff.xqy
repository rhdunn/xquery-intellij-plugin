xquery version "3.0";
(:~
 : eXist-db xml diff operation function
 :
 : @see http://exist-db.org/exist/apps/fundocs/view.html?uri=http://exist-db.org/xquery/xmldiff&location=java:org.exist.xquery.modules.xmldiff.XmlDiffModule&details=true
 :)
module namespace xmldiff = "http://exist-db.org/xquery/xmldiff";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare %a:since("exist", "4.4") function xmldiff:compare($node-set-1 as node()*, $node-set-2 as node()*) as xs:boolean? external;