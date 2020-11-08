xquery version "1.0";
(:
 : cts:highlight Built-in Variables
 :
 : @see https://docs.marklogic.com/10.0/cts:highlight
 :)
module namespace cts = "http://marklogic.com/cts";
declare namespace xs = "http://www.w3.org/2001/XMLSchema";

declare variable $cts:action as xs:string external;
declare variable $cts:node as text() external;
declare variable $cts:queries as cts:query* external;
declare variable $cts:start as xs:integer external;
declare variable $cts:text as xs:string external;
