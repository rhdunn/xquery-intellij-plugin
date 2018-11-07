xquery version "3.0";
(:~
 : File Module 1.0 (EXPath Module 20 February 2015)
 :
 : @see http://expath.org/spec/file/1.0
 : @see http://docs.basex.org/wiki/File_Module
 :)
module namespace file = "http://expath.org/ns/file";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare namespace output = "http://www.w3.org/2010/xslt-xquery-serialization";

declare option o:requires "expath-file/1.0-20100517";

declare option o:implements-module "basex/7.0 as expath-file/1.0-20100517"; (: NOTE: 7.0 is the earliest version definitions are available for. :)
declare option o:implements-module "basex/7.3 as expath-file/1.0-20120614";
declare option o:implements-module "basex/7.8 as expath-file/1.0-20131203";
declare option o:implements-module "basex/8.0 as expath-file/1.0-20150220";

declare option o:implements-module "saxon/pe/9.5 as expath-file/1.0-20150220"; (: TODO: Map saxon versions to the correct file versions. :)
declare option o:implements-module "saxon/ee/9.5 as expath-file/1.0-20150220"; (: TODO: Map saxon versions to the correct file versions. :)

declare type append-params = (
    %a:since("expath-file", "1.0-20120614") for element(output:serialization-parameters) |
    %a:since("basex", "7.0") for item()
);

declare type copy-write-result = (
    %a:since("expath-file", "1.0-20100517") %a:until("expath-file", "1.0-20120614") for xs:boolean |
    %a:since("expath-file", "1.0-20120614") %a:since("basex", "7.0") for empty-sequence()
);

declare type path-uri-result = (
    %a:since("expath-file", "1.0-20100517") for xs:anyURI |
    %a:since("basex", "7.0") for xs:string
);

declare type write-params = (
    %a:since("expath-file", "1.0-20100517") for node()* |
    %a:since("basex", "7.0") for item()
);

declare type any-binary-type = (
    %a:since("expath-file", "1.0-20120614") for xs:base64Binary |
    %a:since("basex", "7.0") for (xs:base64Binary | xs:hexBinary)
);

declare %a:since("basex", "7.0") %a:until("basex", "7.3", "file:dir-separator#0") variable $file:directory-separator external;
declare %a:since("basex", "7.0") %a:until("basex", "7.3", "file:path-separator#0") variable $file:path-separator external;

declare %a:since("expath-file", "1.0-20120614") %a:since("basex", "7.0") function file:append($path as xs:string, $items as item()*) as empty-sequence() external;
declare %a:since("expath-file", "1.0-20120614") %a:since("basex", "7.0") function file:append($path as xs:string, $items as item()*, $params as append-params) as empty-sequence() external;
declare %a:since("expath-file", "1.0-20120614") %a:since("basex", "7.0") function file:append-binary($path as xs:string, $value as any-binary-type) as empty-sequence() external;
declare %a:since("expath-file", "1.0-20120614") function file:append-text($path as xs:string, $value as xs:string) as empty-sequence() external;
declare %a:since("expath-file", "1.0-20120614") function file:append-text($path as xs:string, $value as xs:string, $encoding as xs:string) as empty-sequence() external;
declare %a:since("expath-file", "1.0-20120614") function file:append-text-lines($path as xs:string, $values as xs:string*) as empty-sequence() external;
declare %a:since("expath-file", "1.0-20120614") function file:append-text-lines($path as xs:string, $values as xs:string*, $encoding as xs:string) as empty-sequence() external;
declare %a:until("expath-file", "1.0-20150220") function file:base-dir() as xs:string? external;
declare %a:since("expath-file", "1.0-20120614") %a:since("basex", "7.0") %a:since("expath-file", "1.0-20131203", "file:name#1") %a:until("basex", "7.8", "file:name#1") function file:base-name($path as xs:string) as xs:string external;
declare %a:since("expath-file", "1.0-20120614") %a:since("basex", "7.0") %a:since("expath-file", "1.0-20131203") %a:until("basex", "7.8") function file:base-name($path as xs:string, $suffix as xs:string) as xs:string external;
declare %a:until("expath-file", "1.0-20150220") function file:children($dir as xs:string) as xs:string* external;
declare %a:since("expath-file", "1.0-20100517") function file:copy($source as xs:string, $destination as xs:string) as copy-write-result external;
declare %a:since("expath-file", "1.0-20100517") %a:until("expath-file", "1.0-20120614") %a:missing("basex", "7.0") function file:copy($source as xs:string, $destination as xs:string, $overwrite as xs:boolean) as xs:boolean external;
declare %a:since("expath-file", "1.0-20120614") function file:create-dir($dir as xs:string) as empty-sequence() external;
declare %a:since("basex", "7.0") %a:until("basex", "7.3", "file:create-dir#1") function file:create-directory($dir as xs:string) as xs:boolean external;
declare %a:since("expath-file", "1.0-20131203") %a:since("basex", "7.7") function file:create-temp-dir($prefix as xs:string, $suffix as xs:string) as xs:string external;
declare %a:since("expath-file", "1.0-20131203") %a:since("basex", "7.7") function file:create-temp-dir($prefix as xs:string, $suffix as xs:string, $dir as xs:string) as xs:string external;
declare %a:since("expath-file", "1.0-20131203") %a:since("basex", "7.7") function file:create-temp-file($prefix as xs:string, $suffix as xs:string) as xs:string external;
declare %a:since("expath-file", "1.0-20131203") %a:since("basex", "7.7") function file:create-temp-file($prefix as xs:string, $suffix as xs:string, $dir as xs:string) as xs:string external;
declare %a:until("expath-file", "1.0-20150220") function file:current-dir() as xs:string external;
declare %a:since("expath-file", "1.0-20120614") %a:since("basex", "7.0") function file:delete($path as xs:string) as empty-sequence() external;
declare %a:since("expath-file", "1.0-20120614") %a:since("basex", "7.2.1") function file:delete($path as xs:string, $recursive as xs:boolean) as empty-sequence() external;
declare %a:since("expath-file", "1.0-20120614") %a:since("basex", "7.0") %a:until("expath-file", "1.0-20131203", "file:parent#1") %a:until("basex", "7.8", "file:parent#1") function file:dir-name($path as xs:string) as xs:string external;
declare %a:since("expath-file", "1.0-20120614") %a:since("basex", "7.0") function file:dir-separator() as xs:string external;
declare %a:since("expath-file", "1.0-20100517") function file:exists($path as xs:string) as xs:boolean external;
declare %a:since("expath-file", "1.0-20100517") %a:until("expath-file", "1.0-20120614") %a:missing("basex", "7.0") function file:files($path as xs:string) as xs:string* external;
declare %a:since("expath-file", "1.0-20100517") %a:until("expath-file", "1.0-20120614") %a:missing("basex", "7.0") function file:files($path as xs:string, $pattern as xs:string) as xs:string* external;
declare %a:since("expath-file", "1.0-20100517") %a:until("expath-file", "1.0-20120614") %a:missing("basex", "7.0") function file:files($path as xs:string, $pattern as xs:string, $recursive as xs:boolean) as xs:string* external;
declare %a:since("basex", "8.2") function file:is-absolute($path as xs:string) as xs:boolean external;
declare %a:since("expath-file", "1.0-20120614") function file:is-dir($path as xs:string) as xs:boolean external;
declare %a:since("expath-file", "1.0-20100517") %a:since("basex", "7.0") %a:until("expath-file", "1.0-20120614", "file:is-dir#1") %a:until("basex", "7.3", "file:is-dir#1") function file:is-directory($path as xs:string) as xs:boolean external;
declare %a:since("expath-file", "1.0-20100517") function file:is-file($path as xs:string) as xs:boolean external;
declare %a:since("expath-file", "1.0-20100517") function file:last-modified($path as xs:string) as xs:dateTime external;
declare %a:until("expath-file", "1.0-20120614") %a:since("basex", "7.3") function file:line-separator() as xs:string external;
declare %a:until("expath-file", "1.0-20120614") %a:since("basex", "7.0") function file:list($dir as xs:string) as xs:string* external;
declare %a:until("expath-file", "1.0-20120614") %a:since("basex", "7.0") function file:list($dir as xs:string, $recursive as xs:boolean) as xs:string* external;
declare %a:until("expath-file", "1.0-20120614") %a:since("basex", "7.0") function file:list($dir as xs:string, $recursive as xs:boolean, $pattern as xs:string) as xs:string* external;
declare %a:since("expath-file", "1.0-20100517") %a:until("expath-file", "1.0-20120614") %a:missing("basex", "7.0") function file:mkdir($dir as xs:string) as xs:boolean external;
declare %a:since("expath-file", "1.0-20100517") %a:until("expath-file", "1.0-20120614") %a:missing("basex", "7.0") function file:mkdir($dir as xs:string, $create as xs:boolean) as xs:boolean external;
declare %a:since("expath-file", "1.0-20100517") %a:until("expath-file", "1.0-20120614") %a:missing("basex", "7.0") function file:mkdirs($dir as xs:string) as xs:boolean external;
declare %a:since("expath-file", "1.0-20100517") %a:until("expath-file", "1.0-20120614") %a:missing("basex", "7.0") function file:mkdirs($dir as xs:string, $create as xs:boolean) as xs:boolean external;
declare %a:since("expath-file", "1.0-20120614") %a:since("basex", "7.0") function file:move($source as xs:string, $target as xs:string) as empty-sequence() external;
declare %a:since("expath-file", "1.0-20131203") function file:name($path as xs:string) as xs:string external;
declare %a:since("expath-file", "1.0-20131203") function file:parent($path as xs:string) as xs:string? external;
declare %a:since("expath-file", "1.0-20100517") function file:path-separator() as xs:string external;
declare %a:since("expath-file", "1.0-20100517") %a:until("expath-file", "1.0-20120614", "file:path-to-native#1") function file:path-to-full-path($path as xs:string) as xs:string external;
declare %a:since("expath-file", "1.0-20120614") %a:since("basex", "7.0") function file:path-to-native($path as xs:string) as xs:string external;
declare %a:since("expath-file", "1.0-20100517") function file:path-to-uri($path as xs:string) as path-uri-result external;
declare %a:since("expath-file", "1.0-20100517") %a:until("expath-file", "1.0-20120614", "file:read-binary#1") function file:read($file as xs:string) as xs:base64Binary external;
declare %a:since("expath-file", "1.0-20120614") %a:since("basex", "7.0") function file:read-binary($path as xs:string) as xs:base64Binary external;
declare %a:since("expath-file", "1.0-20131203") function file:read-binary($path as xs:string, $offset as xs:integer) as xs:base64Binary external;
declare %a:since("expath-file", "1.0-20131203") function file:read-binary($path as xs:string, $offset as xs:integer, $length as xs:integer) as xs:base64Binary external;
declare %a:since("expath-file", "1.0-20100517") %a:until("expath-file", "1.0-20120614") function file:read-html($file as xs:string, $tidyOptions as xs:string) as xs:string external;
declare %a:since("expath-file", "1.0-20100517") function file:read-text($file as xs:string) as xs:string external;
declare %a:since("expath-file", "1.0-20120614") %a:since("basex", "7.0") function file:read-text($path as xs:string, $encoding as xs:string) as xs:string external;
declare %a:since("basex", "8.5") function file:read-text($path as xs:string, $encoding as xs:string, $fallback as xs:boolean) as xs:string external;
declare %a:since("expath-file", "1.0-20120614") %a:since("basex", "7.0") function file:read-text-lines($path as xs:string) as xs:string* external;
declare %a:since("expath-file", "1.0-20120614") %a:since("basex", "7.0") function file:read-text-lines($path as xs:string, $encoding as xs:string) as xs:string* external;
declare %a:since("basex", "8.5") function file:read-text-lines($path as xs:string, $encoding as xs:string, $fallback as xs:boolean) as xs:string* external;
declare %a:since("basex", "9.0") function file:read-text-lines($path as xs:string, $encoding as xs:string, $fallback as xs:boolean, $offset as xs:integer) as xs:string* external;
declare %a:since("basex", "9.0") function file:read-text-lines($path as xs:string, $encoding as xs:string, $fallback as xs:boolean, $offset as xs:integer, $length as xs:integer) as xs:string* external;
declare %a:since("expath-file", "1.0-20100517") %a:until("expath-file", "1.0-20120614") %a:missing("basex", "7.0") function file:read-xml($file as xs:string) as node() external;
declare %a:since("expath-file", "1.0-20100517") %a:until("expath-file", "1.0-20120614") %a:missing("basex", "7.0") function file:read-xml($file as xs:string, $tidy as xs:boolean) as node() external;
declare %a:since("expath-file", "1.0-20100517") %a:until("expath-file", "1.0-20120614") %a:missing("basex", "7.0") function file:remove($path as xs:string) as xs:boolean external;
declare %a:since("expath-file", "1.0-20120614") %a:since("basex", "8.2") function file:resolve-path($path as xs:string) as xs:string external;
declare %a:since("basex", "8.2") function file:resolve-path($path as xs:string, $base as xs:string) as xs:string external;
declare %a:since("expath-file", "1.0-20120614") %a:since("basex", "7.0") function file:size($file as xs:string) as xs:integer external;
declare %a:since("expath-file", "1.0-20131203") %a:since("basex", "7.7") function file:temp-dir() as xs:string external;
declare %a:since("expath-file", "1.0-20120614") %a:since("basex", "7.0") function file:write($path as xs:string, $items as item()*) as empty-sequence() external;
declare %a:since("expath-file", "1.0-20100517") function file:write($file as xs:string, $content as item()*, $params as write-params) as copy-write-result external;
declare %a:since("expath-file", "1.0-20100517") %a:until("expath-file", "1.0-20120614") %a:missing("basex", "7.0") function file:write($file as xs:string, $content as item()*, $params as node()*, $append as xs:boolean) as xs:boolean external;
declare %a:since("expath-file", "1.0-20120614") %a:since("basex", "7.0") function file:write-binary($path as xs:string, $value as any-binary-type) as empty-sequence() external;
declare %a:since("expath-file", "1.0-20131203") function file:write-binary($path as xs:string, $value as any-binary-type, $offset as xs:integer) as empty-sequence() external;
declare %a:since("expath-file", "1.0-20120614") function file:write-text($path as xs:string, $value as xs:string) as empty-sequence() external;
declare %a:since("expath-file", "1.0-20120614") function file:write-text($path as xs:string, $value as xs:string, $encoding as xs:string) as empty-sequence() external;
declare %a:since("expath-file", "1.0-20120614") function file:write-text-lines($path as xs:string, $values as xs:string*) as empty-sequence() external;
declare %a:since("expath-file", "1.0-20120614") function file:write-text-lines($path as xs:string, $values as xs:string*, $encoding as xs:string) as empty-sequence() external;
