xquery version "1.0-ml";
(:~
 : MarkLogic spell functions
 :
 : @see https://docs.marklogic.com/spell/spell
 :)
module namespace spell = "http://marklogic.com/xdmp/spell";

import module namespace a = "http://reecedunn.co.uk/xquery/annotations" at "res://reecedunn.co.uk/xquery/annotations.xqy";

declare %a:since("marklogic", "5.0") function spell:double-metaphone($word as xs:string) as xs:string* external;
declare %a:since("marklogic", "5.0") function spell:is-correct($uri as xs:string*, $word as xs:string) as xs:boolean external;
declare %a:since("marklogic", "5.0") function spell:levenshtein-distance($str1 as xs:string, $str2 as xs:string) (: as [5.0]xs:integer? [6.0]xs:integer :) external;
declare %a:since("marklogic", "5.0") function spell:suggest($uri as xs:string*, $word as xs:string) as xs:string* external;
declare %a:since("marklogic", "5.0") function spell:suggest($uri as xs:string*, $word as xs:string, $options as node()?) as xs:string* external;
declare %a:since("marklogic", "5.0") function spell:suggest-detailed($uri as xs:string*, $word as xs:string) (: as [5.0]spell:suggestion* [6.0]element(spell:suggestion)* :) external;
declare %a:since("marklogic", "5.0") function spell:suggest-detailed($uri as xs:string*, $word as xs:string, $options as node()?) (: as [5.0]spell:suggestion* [6.0]element(spell:suggestion)* :) external;