xquery version "1.0-ml";
(:~
 : MarkLogic spell functions
 :
 : @see https://docs.marklogic.com/spell/spell
 :)
module namespace spell = "http://marklogic.com/xdmp/spell";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare option o:requires "marklogic/5.0";

declare %a:since("marklogic", "5.0") function spell:double-metaphone($word as xs:string) as xs:string* external;
declare %a:since("marklogic", "5.0") function spell:is-correct($uri as xs:string*, $word as xs:string) as xs:boolean external;
declare %a:restrict-since("return", "marklogic", "6.0", "xs:integer")
        %a:since("marklogic", "5.0") function spell:levenshtein-distance($str1 as xs:string, $str2 as xs:string) as xs:integer? external;
declare %a:since("marklogic", "8.0") function spell:romanize($string as xs:string) as xs:string external;
declare %a:since("marklogic", "5.0") function spell:suggest($uri as xs:string*, $word as xs:string) as xs:string* external;
declare %a:restrict-until("$options", "marklogic", "8.0", "node()?")
        %a:restrict-since("$options", "marklogic", "8.0", "(element()?|map:map?)")
        %a:since("marklogic", "5.0") function spell:suggest($uri as xs:string*, $word as xs:string, $options as (node()?|map:map?)) as xs:string* external;
declare %a:restrict-until("return", "marklogic", "6.0", "spell:suggestion*")
        %a:restrict-since("return", "marklogic", "6.0", "element(spell:suggestion)*")
        %a:since("marklogic", "5.0") function spell:suggest-detailed($dictionary_uris as xs:string*, $word as xs:string) as (spell:suggestion*|element(spell:suggestion)*) external;
declare %a:restrict-until("return", "marklogic", "6.0", "spell:suggestion*")
        %a:restrict-since("return", "marklogic", "6.0", "element(spell:suggestion)*")
        %a:restrict-until("$options", "marklogic", "8.0", "node()?")
        %a:restrict-since("$options", "marklogic", "8.0", "(element()?|map:map?)")
        %a:since("marklogic", "5.0") function spell:suggest-detailed($dictionary_uris as xs:string*, $word as xs:string, $options as (node()?|map:map?)) as (spell:suggestion*|element(spell:suggestion)*) external;