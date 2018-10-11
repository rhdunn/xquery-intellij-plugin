xquery version "3.0";
(:~
: BaseX Strings Module functions
:
: @see http://docs.basex.org/wiki/Strings_Module
:)
module namespace strings = "http://basex.org/modules/strings";

import module namespace a = "http://reecedunn.co.uk/xquery/annotations" at "res://reecedunn.co.uk/xquery/annotations.xqy";

declare %a:since("basex", "8.3") function strings:levenshtein($string1 as xs:string, $string2 as xs:string) as xs:double external;
declare %a:since("basex", "8.3") function strings:soundex($string as xs:string) as xs:string external;
declare %a:since("basex", "8.3") function strings:cologne-phonetic($string as xs:string) as xs:string external;