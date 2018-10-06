xquery version "1.0-ml";
(:~
 : MarkLogic rdf functions
 :
 : @see https://docs.marklogic.com/rdf
 :)
module namespace rdf = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";

declare %a:since("marklogic", "7.0") function rdf:langString($string as xs:string, $lang as xs:string) as rdf:langString external;
declare %a:since("marklogic", "7.0") function rdf:langString-language($val (: as [7.0]sem:unknown [8.0]rdf:langString :)) as xs:string external;