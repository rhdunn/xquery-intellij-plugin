xquery version "1.0-ml";
module namespace json = "http://marklogic.com/xdmp/json";
(: Only provide a definition of json:transform-to-json for use in the tests. :)
declare function json:transform-to-json($node as node(), $config as map:map) as document-node()? external;
declare function json:transform-to-json($node as node()) as document-node()? external;
