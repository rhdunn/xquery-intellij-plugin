xquery version "3.1";
(:~
 : BaseX String Module functions
 :
 : @see http://docs.basex.org/main/String_Functions
 :)
module namespace string = "http://basex.org/modules/string";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare %a:since("basex", "10.0") function string:cologne-phonetic($string as xs:string) as xs:string external;
declare %a:since("basex", "10.0") function string:format($pattern as xs:string, $values as item()...) as xs:string external;
declare %a:since("basex", "10.0") function string:levenshtein($string1 as xs:string, $string2 as xs:string) as xs:double external;
declare %a:since("basex", "10.0") function string:soundex($string as xs:string) as xs:string external;
declare %a:since("basex", "10.0") function string:cr() as xs:string external;
declare %a:since("basex", "10.0") function string:nl() as xs:string external;
declare %a:since("basex", "10.0") function string:tab() as xs:string external;