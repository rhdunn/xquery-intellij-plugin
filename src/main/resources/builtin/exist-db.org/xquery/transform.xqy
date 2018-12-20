xquery version "3.0";
(:~
 : eXist-db XSL transform module functions
 :
 : @see http://exist-db.org/exist/apps/fundocs/view.html?uri=http://exist-db.org/xquery/transform&location=java:org.exist.xquery.functions.transform.TransformModule&details=true
 :
 :)
module namespace transform = "http://exist-db.org/xquery/transform";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare %a:since("exist", "4.4") function transform:stream-transform($node-tree as node()*, $stylesheet as item(), $parameters as node()?) as item() external;
declare %a:since("exist", "4.4") function transform:stream-transform($node-tree as node()*, $stylesheet as item(), $parameters as node()?, $attributes as node()?, $serialization-options as xs:string?) as item() external;
declare %a:since("exist", "4.4") function transform:transform($node-tree as node()*, $stylesheet as item(), $parameters as node()?) as node()? external;
declare %a:since("exist", "4.4") function transform:transform($node-tree as node()*, $stylesheet as item(), $parameters as node()?, $attributes as node()?, $serialization-options as xs:string?) as node()? external;