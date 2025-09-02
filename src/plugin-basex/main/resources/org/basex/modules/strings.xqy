xquery version "3.0";
(:~
 : BaseX Strings Module functions
 :
 : @see http://docs.basex.org/wiki/Strings_Module
 :)
module namespace strings = "http://basex.org/modules/strings";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare option o:requires "basex/8.3";

declare %a:since("basex", "8.3")
        %a:until("basex", "10.0", "string:levenshtein") function strings:levenshtein($string1 as xs:string, $string2 as xs:string) as xs:double external;
declare %a:since("basex", "8.3")
        %a:until("basex", "10.0", "string:soundex") function strings:soundex($string as xs:string) as xs:string external;
declare %a:since("basex", "8.3")
        %a:until("basex", "10.0", "string:cologne-phonetic") function strings:cologne-phonetic($string as xs:string) as xs:string external;
