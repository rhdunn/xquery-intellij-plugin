xquery version "3.0";
(:~
: BaseX Database Module functions
:
: @see http://docs.basex.org/wiki/Database_Module
:)
module namespace db = "http://basex.org/modules/db";

import module namespace a = "http://reecedunn.co.uk/xquery/annotations" at "res://reecedunn.co.uk/xquery/annotations.xqy";

declare %a:since("basex", "7.0") function db:system() as element(system) external;
declare %a:since("basex", "9.0") function db:option($name as xs:string) as xs:string external;
declare %a:since("basex", "7.0") function db:info($db as xs:string) as element(database) external;
declare %a:since("basex", "8.6") function db:property($db as xs:string, $name as xs:string) as xs:anyAtomicType external;
declare %a:since("basex", "7.0") function db:list() as xs:string* external;
declare %a:since("basex", "7.0") function db:list($db as xs:string) as xs:string* external;
declare %a:since("basex", "7.0") function db:list($db as xs:string, $path as xs:string) as xs:string* external;
declare %a:since("basex", "7.1") function db:list-details() as element(database)* external;
declare %a:since("basex", "7.1") function db:list-details($db as xs:string) as element(resource)* external;
declare %a:since("basex", "7.1") function db:list-details($db as xs:string, $path as xs:string) as element(resource)* external;
declare %a:since("basex", "7.5") function db:backups() as element(backup)* external;
declare %a:since("basex", "7.5") function db:backups($db as xs:string) as element(backup)* external;
declare %a:since("basex", "7.0") function db:open($db as xs:string) as document-node()* external;
declare %a:since("basex", "7.0") function db:open($db as xs:string, $path as xs:string) as document-node()* external;
declare %a:since("basex", "7.0") function db:open-pre($db as xs:string, $pre as xs:integer) as node() external;
declare %a:since("basex", "7.0") function db:open-id($db as xs:string, $id as xs:integer) as node() external;
declare %a:since("basex", "7.0") function db:node-pre($nodes as node()*) as xs:integer* external;
declare %a:since("basex", "7.0") function db:node-id($nodes as node()*) as xs:integer* external;
declare %a:since("basex", "7.0") function db:retrieve($db as xs:string, $path as xs:string) as xs:base64Binary external;
declare %a:since("basex", "7.0") function db:retrieve($db as xs:string, $path as xs:string) as xs:base64Binary external;
declare %a:since("basex", "7.0") function db:text($db as xs:string, $strings as xs:string*) as text()* external;
declare %a:since("basex", "7.2.1") function db:text-range($db as xs:string, $min as xs:string, $max as xs:string) as text()* external;
declare %a:since("basex", "7.0") function db:attribute($db as xs:string, $strings as xs:string*) as attribute()* external;
declare %a:since("basex", "7.0") function db:attribute($db as xs:string, $strings as xs:strings*, $name as xs:string) as attribute()* external;
declare %a:since("basex", "7.2.1") function db:attribute-range($db as xs:string, $min as xs:string, $max as xs:string) as attribute()* external;
declare %a:since("basex", "7.2.1") function db:attribute-range($db as xs:string, $min as xs:string, $max as xs:string, $name as xs:string) as attribute()* external;
declare %a:since("basex", "8.4") function db:token($db as xs:string, $tokens as xs:string*) as attribute()* external;
declare %a:since("basex", "8.4") function db:token($db as xs:string, $tokens as xs:string*, $name as xs:string) as attribute()* external;
declare %a:since("basex", "7.5") function db:create($db as xs:string) as empty-sequence() external;
declare %a:since("basex", "7.5") function db:create($db as xs:string, $inputs as item()*) as empty-sequence() external;
declare %a:since("basex", "7.5") function db:create($db as xs:string, $inputs as item()*, $paths as xs:string*) as empty-sequence() external;
declare %a:since("basex", "7.5") function db:create($db as xs:string, $inputs as item()*, $paths as xs:string*, $options as map(*)?) as empty-sequence() external;
declare %a:since("basex", "7.5") function db:drop($db as xs:string) as empty-sequence() external;
declare %a:since("basex", "7.0") function db:add($db as xs:string, $input as item()) as empty-sequence() external;
declare %a:since("basex", "7.0") function db:add($db as xs:string, $input as item(), $path as xs:string) as empty-sequence() external;
declare %a:since("basex", "7.0") function db:add($db as xs:string, $input as item(), $path as xs:string, $options as map(*)?) as empty-sequence() external;
declare %a:since("basex", "7.0") function db:delete($db as xs:string, $path as xs:string) as empty-sequence() external;
declare %a:since("basex", "7.8.2") function db:copy($db as xs:string, $name as xs:string) as empty-sequence() external;
declare %a:since("basex", "7.8.2") function db:alter($db as xs:string, $name as xs:string) as empty-sequence() external;
declare %a:since("basex", "7.8.2") function db:create-backup($db as xs:string) as empty-sequence() external;
declare %a:since("basex", "7.8.2") function db:drop-backup($name as xs:string) as empty-sequence() external;
declare %a:since("basex", "7.8.2") function db:restore($name as xs:string) as empty-sequence() external;
declare %a:since("basex", "7.0") function db:optimize($db as xs:string) as empty-sequence() external;
declare %a:since("basex", "7.0") function db:optimize($db as xs:string, $all as xs:boolean) as empty-sequence() external;
declare %a:since("basex", "7.0") function db:optimize($db as xs:string, $all as xs:boolean, $options as map(*)?) as empty-sequence() external;
declare %a:since("basex", "7.0") function db:rename($db as xs:string, $source as xs:string, $target as xs:string) as empty-sequence() external;
declare %a:since("basex", "7.0") function db:replace($db as xs:string, $path as xs:string, $input as item()) as empty-sequence() external;
declare %a:since("basex", "7.0") function db:replace($db as xs:string, $path as xs:string, $input as item(), $options as map(*)?) as empty-sequence() external;
declare %a:since("basex", "7.0") function db:store($db as xs:string, $path as xs:string, $input as item()) as empty-sequence() external;
declare %a:since("basex", "7.3") function db:flush($db as xs:string) as empty-sequence() external;
declare %a:since("basex", "7.7") function db:name($node as node()) as xs:string external;
declare %a:since("basex", "7.7") function db:path($node as node()) as xs:string external;
declare %a:since("basex", "7.0") function db:exists($db as xs:string) as xs:boolean external;
declare %a:since("basex", "7.0") function db:exists($db as xs:string, $path as xs:string) as xs:boolean external;
declare %a:since("basex", "7.0") function db:is-raw($db as xs:string, $path as xs:string) as xs:boolean external;
declare %a:since("basex", "7.0") function db:is-xml($db as xs:string, $path as xs:string) as xs:boolean external;
declare %a:since("basex", "7.1") function db:content-type($db as xs:string, $path as xs:string) as xs:string external;
declare %a:since("basex", "7.2.1") %a:until("basex", "9.0") %a:see-also("basex", "9.0", "update:output") function db:output() external;
declare %a:since("basex", "8.2") %a:until("basex", "9.0") %a:see-also("basex", "9.0", "update:cache") function db:output-cache() external;
declare %a:since("basex", "7.7") %a:until("basex", "..") function db:export() external;
declare %a:since("basex", "7.0") %a:until("basex", "7.8") %a:see-also("basex", "7.8", "ft:search") function db:fulltext() external;
declare %a:since("basex", "..") %a:until("basex", "8.2") function db:event() external;