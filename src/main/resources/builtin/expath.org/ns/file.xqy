xquery version "3.0";
(:~
 : BaseX File Module functions
 :
 : @see http://docs.basex.org/wiki/File_Module
 :)
module namespace file = "http://expath.org/ns/file";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare namespace output = "http://www.w3.org/2010/xslt-xquery-serialization";

declare option o:requires "basex/7.0"; (: NOTE: 7.0 is the earliest version definitions are available for. :)

declare %a:since("basex", "7.0") %a:until("basex", "7.3", "file:dir-separator#0") variable $file:directory-separator external;
declare %a:since("basex", "7.0") %a:until("basex", "7.3", "file:path-separator#0") variable $file:path-separator external;

declare %a:since("basex", "7.0", "file:name#1") %a:until("basex", "7.8") function file:base-name($path as xs:string) as xs:string external;
declare %a:since("basex", "7.0") %a:until("basex", "7.8") function file:base-name($path as xs:string, $suffix as xs:string) as xs:string external;
declare %a:since("basex", "7.0", "file:parent#1") %a:until("basex", "7.8") function file:dir-name($path as xs:string) as xs:string external;
declare %a:since("basex", "7.0") function file:list($dir as xs:string) as xs:string external;
declare %a:since("basex", "7.0") function file:list($dir as xs:string, $recursive as xs:boolean) as xs:string external;
declare %a:since("basex", "7.0") function file:list($dir as xs:string, $recursive as xs:boolean, $pattern as xs:string) as xs:string external;
declare %a:since("basex", "8.0") function file:children($dir as xs:string) as xs:string* external;
declare %a:since("basex", "7.0") function file:read-binary($path as xs:string) as xs:base64Binary external;
declare %a:since("basex", "7.8") function file:read-binary($path as xs:string, $offset as xs:integer) as xs:base64Binary external;
declare %a:since("basex", "7.8") function file:read-binary($path as xs:string, $offset as xs:integer, $length as xs:integer) as xs:base64Binary external;
declare %a:since("basex", "7.0") function file:read-text($path as xs:string) as xs:string external;
declare %a:since("basex", "7.0") function file:read-text($path as xs:string, $encoding as xs:string) as xs:string external;
declare %a:since("basex", "8.5") function file:read-text($path as xs:string, $encoding as xs:string, $fallback as xs:boolean) as xs:string external;
declare %a:since("basex", "7.0") function file:read-text-lines($path as xs:string) as xs:string external;
declare %a:since("basex", "7.0") function file:read-text-lines($path as xs:string, $encoding as xs:string) as xs:string* external;
declare %a:since("basex", "8.5") function file:read-text-lines($path as xs:string, $encoding as xs:string, $fallback as xs:boolean) as xs:string* external;
declare %a:since("basex", "9.0") function file:read-text-lines($path as xs:string, $encoding as xs:string, $fallback as xs:boolean, $offset as xs:integer) as xs:string* external;
declare %a:since("basex", "9.0") function file:read-text-lines($path as xs:string, $encoding as xs:string, $fallback as xs:boolean, $offset as xs:integer, $length as xs:integer) as xs:string* external;
declare %a:since("basex", "7.0") %a:until("basex", "7.3", "file:create-dir#1") function file:create-directory($dir as xs:string) as xs:boolean external;
declare %a:since("basex", "7.3") function file:create-dir($dir as xs:string) as empty-sequence() external;
declare %a:since("basex", "7.7") function file:create-temp-dir($prefix as xs:string, $suffix as xs:string) as xs:string external;
declare %a:since("basex", "7.7") function file:create-temp-dir($prefix as xs:string, $suffix as xs:string, $dir as xs:string) as xs:string external;
declare %a:since("basex", "7.7") function file:create-temp-file($prefix as xs:string, $suffix as xs:string) as xs:string external;
declare %a:since("basex", "7.7") function file:create-temp-file($prefix as xs:string, $suffix as xs:string, $dir as xs:string) as xs:string external;
declare %a:since("basex", "7.0") function file:delete($path as xs:string) as empty-sequence() external;
declare %a:since("basex", "7.2.1") function file:delete($path as xs:string, $recursive as xs:boolean) as empty-sequence() external;
declare %a:since("basex", "7.0") function file:write($path as xs:string, $items as item()*) as empty-sequence() external;
declare %a:since("basex", "7.0") function file:write($path as xs:string, $items as item()*, $params as item()) as empty-sequence() external;
declare %a:since("basex", "7.0") function file:write-binary($path as xs:string, $value as xs:anyAtomicType) as empty-sequence() external;
declare %a:since("basex", "7.8") function file:write-binary($path as xs:string, $value as xs:anyAtomicType, $offset as xs:integer) as empty-sequence() external;
declare %a:since("basex", "7.3") function file:write-text($path as xs:string, $value as xs:string) as empty-sequence() external;
declare %a:since("basex", "7.3") function file:write-text($path as xs:string, $value as xs:string, $encoding as xs:string) as empty-sequence() external;
declare %a:since("basex", "7.3") function file:write-text-lines($path as xs:string, $values as xs:string*) as empty-sequence() external;
declare %a:since("basex", "7.3") function file:write-text-lines($path as xs:string, $values as xs:string*, $encoding as xs:string) as empty-sequence() external;
declare %a:since("basex", "7.0") function file:append($path as xs:string, $items as item()*) as empty-sequence() external;
declare %a:since("basex", "7.0") function file:append($path as xs:string, $items as item()*, $params as item()) as empty-sequence() external;
declare %a:since("basex", "7.0") function file:append-binary($path as xs:string, $value as xs:anyAtomicType) as empty-sequence() external;
declare %a:since("basex", "7.3") function file:append-text($path as xs:string, $value as xs:string) as empty-sequence() external;
declare %a:since("basex", "7.3") function file:append-text($path as xs:string, $value as xs:string, $encoding as xs:string) as empty-sequence() external;
declare %a:since("basex", "7.3") function file:append-text-lines($path as xs:string, $values as xs:string*) as empty-sequence() external;
declare %a:since("basex", "7.3") function file:append-text-lines($path as xs:string, $values as xs:string*, $encoding as xs:string) as empty-sequence() external;
declare %a:since("basex", "7.0") function file:copy($source as xs:string, $target as xs:string) as empty-sequence() external;
declare %a:since("basex", "7.0") function file:move($source as xs:string, $target as xs:string) as empty-sequence() external;
declare %a:since("basex", "7.0") function file:exists($path as xs:string) as xs:boolean external;
declare %a:since("basex", "7.0") %a:until("basex", "7.3", "file:is-dir#1") function file:is-directory($path as xs:string) as xs:boolean external;
declare %a:since("basex", "7.3") function file:is-dir($path as xs:string) as xs:boolean external;
declare %a:since("basex", "8.2") function file:is-absolute($path as xs:string) as xs:boolean external;
declare %a:since("basex", "7.0") function file:is-file($path as xs:string) as xs:boolean external;
declare %a:since("basex", "7.0") function file:last-modified($path as xs:string) as xs:dateTime external;
declare %a:since("basex", "7.0") function file:size($file as xs:string) as xs:integer external;
declare %a:since("basex", "7.8") function file:name($path as xs:string) as xs:string external;
declare %a:since("basex", "7.8") function file:parent($path as xs:string) as xs:string? external;
declare %a:since("basex", "7.0") function file:path-to-native($path as xs:string) as xs:string external;
declare %a:since("basex", "8.2") function file:resolve-path($path as xs:string) as xs:string external;
declare %a:since("basex", "8.2") function file:resolve-path($path as xs:string, $base as xs:string) as xs:string external;
declare %a:since("basex", "7.0") function file:path-to-uri($path as xs:string) as xs:string external;
declare %a:since("basex", "7.0") function file:dir-separator() as xs:string external;
declare %a:since("basex", "7.0") function file:path-separator() as xs:string external;
declare %a:since("basex", "7.3") function file:line-separator() as xs:string external;
declare %a:since("basex", "7.7") function file:temp-dir() as xs:string external;
declare %a:since("basex", "8.0") function file:current-dir() as xs:string external;
declare %a:since("basex", "8.0") function file:base-dir() as xs:string? external;
