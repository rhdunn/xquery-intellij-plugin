xquery version "1.0";
(:
 : cts:element-walk Built-in Variables
 :
 : @see https://docs.marklogic.com/10.0/cts:element-walk
 :)
module namespace cts = "http://marklogic.com/cts";
declare namespace xs = "http://www.w3.org/2001/XMLSchema";

declare variable $cts:action as xs:string external;
declare variable $cts:node as element() external;
