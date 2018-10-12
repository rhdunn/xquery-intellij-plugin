xquery version "3.0";
(:~
: BaseX File Module functions
:
: @see http://docs.basex.org/wiki/File_Module
:)
module namespace file = "http://expath.org/ns/file";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace output = "http://www.w3.org/2010/xslt-xquery-serialization";

declare %a:since("basex", "7.8") function file:list($dir as xs:string) as xs:string external;
declare %a:since("basex", "7.8") function file:list($dir as xs:string, $recursive as xs:boolean) as xs:string external;
declare %a:since("basex", "7.8") function file:list($dir as xs:string, $recursive as xs:boolean, $pattern as xs:string) as xs:string external;
declare %a:since("basex", "7.8") function file:children($dir as xs:string) as xs:string* external;
declare %a:since("basex", "7.8") function file:read-binary($path as xs:string) as xs:base64Binary external;
declare %a:since("basex", "7.8") function file:read-binary($path as xs:string, $offset as xs:integer) as xs:base64Binary external;
declare %a:since("basex", "7.8") function file:read-binary($path as xs:string, $offset as xs:integer, $length as xs:integer) as xs:base64Binary external;
declare %a:since("basex", "7.8") function file:read-text($path as xs:string) as xs:string external;
declare %a:since("basex", "7.8") function file:read-text($path as xs:string, $encoding as xs:string) as xs:string external;
declare %a:since("basex", "7.8") function file:read-text($path as xs:string, $encoding as xs:string, $fallback as xs:boolean) as xs:string external;
declare %a:since("basex", "7.8") function file:read-text-lines($path as xs:string) as xs:string external;
declare %a:since("basex", "7.8") function file:read-text-lines($path as xs:string, $encoding as xs:string) as xs:string* external;
declare %a:since("basex", "7.8") function file:read-text-lines($path as xs:string, $encoding as xs:string, $fallback as xs:boolean) as xs:string* external;
declare %a:since("basex", "7.8") function file:read-text-lines($path as xs:string, $encoding as xs:string, $fallback as xs:boolean, $offset as xs:integer) as xs:string* external;
declare %a:since("basex", "7.8") function file:read-text-lines($path as xs:string, $encoding as xs:string, $fallback as xs:boolean, $offset as xs:integer, $length as xs:integer) as xs:string* external;
declare %a:since("basex", "7.8") function file:create-dir($dir as xs:string) as empty-sequence() external;
declare %a:since("basex", "7.8") function file:create-temp-dir($prefix as xs:string, $suffix as xs:string) as xs:string external;
declare %a:since("basex", "7.8") function file:create-temp-dir($prefix as xs:string, $suffix as xs:string, $dir as xs:string) as xs:string external;
declare %a:since("basex", "7.8") function file:create-temp-file($prefix as xs:string, $suffix as xs:string) as xs:string external;
declare %a:since("basex", "7.8") function file:create-temp-file($prefix as xs:string, $suffix as xs:string, $dir as xs:string) as xs:string external;
declare %a:since("basex", "7.8") function file:delete($path as xs:string) as empty-sequence() external;
declare %a:since("basex", "7.8") function file:delete($path as xs:string, $recursive as xs:boolean) as empty-sequence() external;
declare %a:since("basex", "7.8") function file:write($path as xs:string, $items as item()*) as empty-sequence() external;
declare %a:since("basex", "7.8") function file:write($path as xs:string, $items as item()*, $params as item()) as empty-sequence() external;
declare %a:since("basex", "7.8") function file:write-binary($path as xs:string, $value as xs:anyAtomicType) as empty-sequence() external;
declare %a:since("basex", "7.8") function file:write-binary($path as xs:string, $value as xs:anyAtomicType, $offset as xs:integer) as empty-sequence() external;
declare %a:since("basex", "7.8") function file:write-text($path as xs:string, $value as xs:string) as empty-sequence() external;
declare %a:since("basex", "7.8") function file:write-text($path as xs:string, $value as xs:string, $encoding as xs:string) as empty-sequence() external;
declare %a:since("basex", "7.8") function file:write-text-lines($path as xs:string, $values as xs:string*) as empty-sequence() external;
declare %a:since("basex", "7.8") function file:write-text-lines($path as xs:string, $values as xs:string*, $encoding as xs:string) as empty-sequence() external;
declare %a:since("basex", "7.8") function file:append($path as xs:string, $items as item()*) as empty-sequence() external;
declare %a:since("basex", "7.8") function file:append($path as xs:string, $items as item()*, $params as item()) as empty-sequence() external;
declare %a:since("basex", "7.8") function file:append-binary($path as xs:string, $value as xs:anyAtomicType) as empty-sequence() external;
declare %a:since("basex", "7.8") function file:append-text($path as xs:string, $value as xs:string) as empty-sequence() external;
declare %a:since("basex", "7.8") function file:append-text($path as xs:string, $value as xs:string, $encoding as xs:string) as empty-sequence() external;
declare %a:since("basex", "7.8") function file:append-text-lines($path as xs:string, $values as xs:string*) as empty-sequence() external;
declare %a:since("basex", "7.8") function file:append-text-lines($path as xs:string, $values as xs:string*, $encoding as xs:string) as empty-sequence() external;
declare %a:since("basex", "7.8") function file:copy($source as xs:string, $target as xs:string) as empty-sequence() external;
declare %a:since("basex", "7.8") function file:move($source as xs:string, $target as xs:string) as empty-sequence() external;
declare %a:since("basex", "7.8") function file:exists($path as xs:string) as xs:boolean external;
declare %a:since("basex", "7.8") function file:is-dir($path as xs:string) as xs:boolean external;
declare %a:since("basex", "7.8") function file:is-absolute($path as xs:string) as xs:boolean external;
declare %a:since("basex", "7.8") function file:is-file($path as xs:string) as xs:boolean external;
declare %a:since("basex", "7.8") function file:last-modified($path as xs:string) as xs:dateTime external;
declare %a:since("basex", "7.8") function file:size($file as xs:string) as xs:integer external;
declare %a:since("basex", "7.8") function file:name($path as xs:string) as xs:string external;
declare %a:since("basex", "7.8") function file:parent($path as xs:string) as xs:string? external;
declare %a:since("basex", "7.8") function file:path-to-native($path as xs:string) as xs:string external;
declare %a:since("basex", "7.8") function file:resolve-path($path as xs:string) as xs:string external;
declare %a:since("basex", "7.8") function file:resolve-path($path as xs:string, $base as xs:string) as xs:string external;
declare %a:since("basex", "7.8") function file:path-to-uri($path as xs:string) as xs:string external;
declare %a:since("basex", "7.8") function file:dir-separator() as xs:string external;
declare %a:since("basex", "7.8") function file:path-separator() as xs:string external;
declare %a:since("basex", "7.8") function file:line-separator() as xs:string external;
declare %a:since("basex", "7.8") function file:temp-dir() as xs:string external;
declare %a:since("basex", "7.8") function file:current-dir() as xs:string external;
declare %a:since("basex", "7.8") function file:base-dir() as xs:string? external;
