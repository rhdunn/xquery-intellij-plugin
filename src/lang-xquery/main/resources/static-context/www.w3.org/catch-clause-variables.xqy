xquery version "1.0";
(:~
 : XQuery 3.1 (3.17) Try/Catch Expressions
 :
 : @see https://www.w3.org/TR/xquery-31/#id-try-catch
 :)
module namespace err = "http://www.w3.org/2005/xqt-errors";
declare namespace xs = "http://www.w3.org/2001/XMLSchema";

declare variable $err:code as xs:QName external;
declare variable $err:description as xs:string? external;
declare variable $err:value as item()* external;
declare variable $err:module as xs:string? external;
declare variable $err:line-number as xs:integer? external;
declare variable $err:column-number as xs:integer? external;
declare variable $err:additional as item()* external;
