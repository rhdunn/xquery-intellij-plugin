xquery version "3.0";
(:~
 : eXist-db demo app templating example functions
 :
 : @see http://exist-db.org/exist/apps/fundocs/view.html?uri=http://exist-db.org/apps/demo/templating/examples&location=/db/apps/demo/examples/templating/examples.xql&details=true
 :
 :)
module namespace ex ="http://exist-db.org/apps/demo/templating/examples";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare %a:since("exist", "4.4") function ex:addresses($node as node(), $model as map(*)) as map(*) external;
declare %a:since("exist", "4.4") function ex:hello($node as node()*, $model as map(*)) as element() external;
declare %a:since("exist", "4.4") function ex:hello-world($node as node(), $model as map(*), $language as xs:string, $user as xs:string) as xs:string external;
declare %a:since("exist", "4.4") function ex:multiply($node as node()*, $model as map(*), $n1 as xs:int, $n2 as xs:int) as item()* external;
declare %a:since("exist", "4.4") function ex:print-city($node as node(), $model as map(*)) as item()* external;
declare %a:since("exist", "4.4") function ex:print-name($node as node(), $model as map(*)) as item()* external;
declare %a:since("exist", "4.4") function ex:print-street($node as node(), $model as map(*)) as item()* external;