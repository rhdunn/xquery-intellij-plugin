xquery version "1.0-ml";
(:~
 : MarkLogic rdf functions
 :
 : @see https://docs.marklogic.com/rdf
 :)
module namespace rdf = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare option o:requires "marklogic/7.0";

declare %a:since("marklogic", "7.0") function rdf:langString($string as xs:string, $lang as xs:string) as rdf:langString external;
declare %a:restrict-until("$options", "marklogic", "8.0", "sem:unknown")
        %a:restrict-since("$options", "marklogic", "8.0", "rdf:langString")
        %a:since("marklogic", "7.0") function rdf:langString-language($val as (sem:unknown|rdf:langString)) as xs:string external;