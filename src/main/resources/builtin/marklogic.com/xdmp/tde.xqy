xquery version "1.0-ml";
(:~
 : MarkLogic tde functions
 :
 : @see https://docs.marklogic.com/tde
 :)
module namespace tde = "http://marklogic.com/xdmp/tde";

import module namespace a = "http://reecedunn.co.uk/xquery/annotations" at "res://reecedunn.co.uk/xquery/annotations.xqy";

declare %a:since("marklogic", "9.0") function tde:get-view($schema as xs:string, $view as xs:string) as map:map external;
declare %a:since("marklogic", "9.0") function tde:node-data-extract($documents as node()*) as map:map external;
declare %a:since("marklogic", "9.0") function tde:node-data-extract($documents as node()*, $templates as element(tde:template)*) as map:map external;
declare %a:since("marklogic", "9.0") function tde:validate($templates as element(tde:template)*) as map:map external;
declare %a:since("marklogic", "9.0") function tde:validate($templates as element(tde:template)*, $excludeTemplateURIs as xs:string*) as map:map external;