xquery version "3.1";
(:~
 : BaseX Database Module functions
 :
 : @see http://docs.basex.org/wiki/Database_Module
 :)
module namespace db = "http://basex.org/modules/db";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare option o:requires "basex/7.0"; (: NOTE: 7.0 is the earliest version definitions are available for. :)

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
declare %a:since("basex", "9.2") function db:dir($db as xs:string, $path as xs:string) as element()* external;
declare %a:since("basex", "7.0") function db:backups() as element(backup)* external;
declare %a:since("basex", "7.0") function db:backups($db as xs:string) as element(backup)* external;
declare %a:since("basex", "7.0") function db:open($db as xs:string) as document-node()* external;
declare %a:since("basex", "7.0") function db:open($db as xs:string, $path as xs:string) as document-node()* external;
declare %a:restrict-since("$pre", "basex", "9.3", "xs:integer*")
        %a:since("basex", "7.0")
        %a:until("basex", "10.0")
        %a:see-also("basex", "10.0", "db:get-pre")function db:open-pre($db as xs:string, $pre as xs:integer) as node()* external;
declare %a:restrict-since("$id", "basex", "9.3", "xs:integer*")
        %a:since("basex", "7.0")
        %a:until("basex", "10.0")
        %a:see-also("basex", "10.0", "db:get-id")function db:open-id($db as xs:string, $id as xs:integer) as node()* external;
declare %a:since("basex", "7.0") function db:node-pre($nodes as node()*) as xs:integer* external;
declare %a:since("basex", "7.0") function db:node-id($nodes as node()*) as xs:integer* external;
declare %a:since("basex", "7.0") function db:retrieve($db as xs:string, $path as xs:string) as xs:base64Binary external;
declare %a:since("basex", "7.0") function db:retrieve($db as xs:string, $path as xs:string) as xs:base64Binary external;
declare %a:since("basex", "7.0") function db:text($db as xs:string, $strings as xs:string*) as text()* external;
declare %a:since("basex", "7.2.1") function db:text-range($db as xs:string, $min as xs:string, $max as xs:string) as text()* external;
declare %a:since("basex", "7.0") function db:attribute($db as xs:string, $strings as xs:string*) as attribute()* external;
declare %a:since("basex", "7.0") function db:attribute($db as xs:string, $strings as xs:string*, $name as xs:string) as attribute()* external;
declare %a:since("basex", "7.2.1") function db:attribute-range($db as xs:string, $min as xs:string, $max as xs:string) as attribute()* external;
declare %a:since("basex", "7.0") function db:attribute-range($db as xs:string, $min as xs:string, $max as xs:string, $name as xs:string) as attribute()* external;
declare %a:since("basex", "8.4") function db:token($db as xs:string, $tokens as xs:string*) as attribute()* external;
declare %a:since("basex", "8.4") function db:token($db as xs:string, $tokens as xs:string*, $name as xs:string) as attribute()* external;
declare %a:since("basex", "7.5") function db:create($db as xs:string) as empty-sequence() external;
declare %a:restrict-until("$inputs", "basex", "7.6", "item()")
        %a:since("basex", "7.5") function db:create($db as xs:string, $inputs as item()*) as empty-sequence() external;
declare %a:restrict-until("$inputs", "basex", "7.6", "item()")
        %a:restrict-until("$paths", "basex", "7.6", "xs:string")
        %a:since("basex", "7.5") function db:create($db as xs:string, $inputs as item()*, $paths as xs:string*) as empty-sequence() external;
declare %a:restrict-until("$inputs", "basex", "7.6", "item()")
        %a:restrict-until("$paths", "basex", "7.6", "xs:string")
        %a:restrict-until("$options", "basex", "8.2.1", "item()")
        %a:restrict-since("$options", "basex", "8.2.1", "map(xs:string, xs:string)")
        %a:restrict-since("$options", "basex", "8.2.3", "map(*)")
        %a:restrict-since("$options", "basex", "8.6.7", "map(*)?")
        %a:since("basex", "7.0") function db:create($db as xs:string, $inputs as item()*, $paths as xs:string*, $options as item()?) as empty-sequence() external;
declare %a:since("basex", "7.5") function db:drop($db as xs:string) as empty-sequence() external;
declare %a:since("basex", "7.0") function db:add($db as xs:string, $input as item()) as empty-sequence() external;
declare %a:restrict-since("$path", "basex", "9.2", "xs:string?")
        %a:since("basex", "7.0") function db:add($db as xs:string, $input as item(), $path as xs:string) as empty-sequence() external;
declare %a:restrict-until("$options", "basex", "8.2.1", "item()")
        %a:restrict-since("$options", "basex", "8.2.1", "map(xs:string, xs:string)")
        %a:restrict-since("$options", "basex", "8.2.3", "map(*)")
        %a:restrict-since("$options", "basex", "8.6.7", "map(*)?")
        %a:restrict-since("$path", "basex", "9.2", "xs:string?")
        %a:since("basex", "7.0") function db:add($db as xs:string, $input as item(), $path as xs:string, $options as item()?) as empty-sequence() external;
declare %a:since("basex", "7.0") function db:delete($db as xs:string, $path as xs:string) as empty-sequence() external;
declare %a:since("basex", "7.8.2") function db:copy($db as xs:string, $name as xs:string) as empty-sequence() external;
declare %a:since("basex", "7.8.2") function db:alter($db as xs:string, $name as xs:string) as empty-sequence() external;
declare %a:since("basex", "7.8.2") function db:create-backup($db as xs:string) as empty-sequence() external;
declare %a:since("basex", "10.0") function db:create-backup($db as xs:string, $options as map(*)) as empty-sequence() external;
declare %a:since("basex", "7.8.2") function db:drop-backup($name as xs:string) as empty-sequence() external;
declare %a:since("basex", "9.3") function db:alter-backup($name as xs:string, $new-name as xs:string) as empty-sequence() external;
declare %a:since("basex", "7.8.2") function db:restore($name as xs:string) as empty-sequence() external;
declare %a:since("basex", "7.0") function db:optimize($db as xs:string) as empty-sequence() external;
declare %a:since("basex", "7.0") function db:optimize($db as xs:string, $all as xs:boolean) as empty-sequence() external;
declare %a:restrict-until("$options", "basex", "8.2.1", "item()")
        %a:restrict-since("$options", "basex", "8.2.1", "map(xs:string, xs:string)")
        %a:restrict-since("$options", "basex", "8.2.3", "map(*)")
        %a:restrict-since("$options", "basex", "8.6.7", "map(*)?")
        %a:since("basex", "7.0") function db:optimize($db as xs:string, $all as xs:boolean, $options as item()?) as empty-sequence() external;
declare %a:since("basex", "7.0") function db:rename($db as xs:string, $source as xs:string, $target as xs:string) as empty-sequence() external;
declare %a:since("basex", "7.0")
        %a:see-also("basex", "10.0", "db:put") function db:replace($db as xs:string, $path as xs:string, $input as item()) as empty-sequence() external;
declare %a:restrict-until("$options", "basex", "8.2.1", "item()")
        %a:restrict-since("$options", "basex", "8.2.1", "map(xs:string, xs:string)")
        %a:restrict-since("$options", "basex", "8.2.3", "map(*)")
        %a:restrict-since("$options", "basex", "8.6.7", "map(*)?")
        %a:since("basex", "7.0")
        %a:see-also("basex", "10.0", "db:put") function db:replace($db as xs:string, $path as xs:string, $input as item(), $options as item()?) as empty-sequence() external;
declare %a:since("basex", "7.0") function db:store($db as xs:string, $path as xs:string, $input as item()) as empty-sequence() external;
declare %a:since("basex", "7.3") function db:flush($db as xs:string) as empty-sequence() external;
declare %a:since("basex", "7.7") function db:name($node as node()) as xs:string external;
declare %a:since("basex", "7.7") function db:path($node as node()) as xs:string external;
declare %a:since("basex", "7.0") function db:exists($db as xs:string) as xs:boolean external;
declare %a:since("basex", "7.0") function db:exists($db as xs:string, $path as xs:string) as xs:boolean external;
declare %a:since("basex", "7.0")
        %a:see-also("basex", "10.0", "db:type") function db:is-raw($db as xs:string, $path as xs:string) as xs:boolean external;
declare %a:since("basex", "7.0")
        %a:see-also("basex", "10.0", "db:type") function db:is-xml($db as xs:string, $path as xs:string) as xs:boolean external;
declare %a:since("basex", "7.1") function db:content-type($db as xs:string, $path as xs:string) as xs:string external;
declare %a:since("basex", "7.2.1") %a:until("basex", "9.0") %a:see-also("basex", "9.0", "update:output") function db:output() external;
declare %a:since("basex", "8.2") %a:until("basex", "9.0") %a:see-also("basex", "9.0", "update:cache") function db:output-cache() external;
declare %a:since("basex", "7.7") function db:export($db as xs:string, $path as xs:string) as empty-sequence() external;
declare %a:since("basex", "7.7") function db:export($db as xs:string, $path as xs:string, $params as item()) as empty-sequence() external;
declare %a:since("basex", "7.0") %a:until("basex", "8.2") function db:event($name as xs:string, $query as item()) as empty-sequence() external;
declare %a:since("basex", "7.0") %a:until("basex", "7.8") function db:fulltext($db as item(), $terms as xs:string) as text()* external;

(: updates for BaseX v10 :)
declare %a:since("basex", "10.0")
        %a:see-also("basex", "10.0", "db:open") function db:get($db as xs:string) as document-node()* external;
declare %a:since("basex", "10.0")
        %a:see-also("basex", "10.0", "db:open") function db:get($db as xs:string, $path as xs:string) as document-node() external;
declare %a:since("basex", "10.0")
        %a:see-also("basex", "10.0", "db:open-pre") function db:get-pre($db as xs:string, $pres as xs:integer*) as node()* external;
declare %a:since("basex", "10.0")
        %a:see-also("basex", "10.0", "db:open-id") function db:get-id($db as xs:string, $ids as xs:integer*) as node()* external;
declare %a:since("basex", "10.0")
        %a:see-also("basex", "10.0", "db:retrieve") function db:get-binary($db as xs:string, $path as xs:string) as item() external;
declare %a:since("basex", "10.0") function db:get-value($db as xs:string, $path as xs:string) as item()* external;
declare %a:since("basex", "10.0")
        %a:see-also("basex", "10.0", "db:replace")
        %a:see-also("basex", "10.0", "db:add") function db:put($db as xs:string, $input as item(), $path as xs:string) as empty-sequence() external;
declare %a:since("basex", "10.0")
        %a:see-also("basex", "10.0", "db:replace")
        %a:see-also("basex", "10.0", "db:add") function db:put($db as xs:string, $input as item(), $path as xs:string, $options as map(*)?) as empty-sequence() external;
declare %a:since("basex", "10.0")
        %a:see-also("basex", "10.0", "db:put") function db:put-binary($db as xs:string, $input as item(), $path as xs:string) as empty-sequence() external;
declare %a:since("basex", "10.0") function db:put-value($db as xs:string, $input as item()*, $path as xs:string) as empty-sequence() external;
declare %a:since("basex", "10.0") function db:create-backup($db as xs:string, $options as map(*)) as empty-sequence() external;
declare %a:since("basex", "10.0")
        %a:see-also("basex", "10.0", "db:is-raw")
        %a:see-also("basex", "10.0", "db:is-xml") function db:type($db as xs:string, $path as xs:string) as xs:boolean external;
declare %a:since("basex", "12.0") function db:option-map($name as xs:string) as map(xs:string,item()) external;
declare %a:since("basex", "12.0") function db:property-map($database as xs:string, $name as xs:string) as element(database) external;