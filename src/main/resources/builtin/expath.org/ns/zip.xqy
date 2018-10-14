xquery version "3.0";
(:~
 : BaseX ZIP module functions
 :
 : @see http://docs.basex.org/wiki/ZIP_Module
 :
 :)
module namespace zip = "http://expath.org/ns/zip";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare option o:requires "basex/7.0"; (: NOTE: 7.0 is the earliest version definitions are available for. :)

declare %a:since("basex", "7.0") %a:deprecated("basex", "7.3", "archive:extract-binary") function zip:binary-entry($uri as xs:string, $path as xs:string) as xs:base64Binary external;
declare %a:since("basex", "7.0") %a:deprecated("basex", "7.3", "archive:extract-text") function zip:text-entry($uri as xs:string, $path as xs:string) as xs:string external;
declare %a:since("basex", "7.0") %a:deprecated("basex", "7.3", "archive:extract-text") function zip:text-entry($uri as xs:string, $path as xs:string, $encoding as xs:string) as xs:string external;
declare %a:since("basex", "7.0") %a:deprecated("basex", "7.3", "archive:extract-text") function zip:xml-entry($uri as xs:string, $path as xs:string) as document-node() external;
declare %a:since("basex", "7.0") %a:deprecated("basex", "7.3", "archive:extract-text") function zip:html-entry($uri as xs:string, $path as xs:string) as document-node() external;
declare %a:since("basex", "7.0") %a:deprecated("basex", "7.3", "archive:entries") function zip:entries($uri as xs:string) as element(zip:file) external;
declare %a:since("basex", "7.0") %a:deprecated("basex", "7.3", "archive:create") function zip:zip-file($zip as element(zip:file)) as empty-sequence() external;
declare %a:since("basex", "7.0") %a:deprecated("basex", "7.3", "archive:update") function zip:update-entries($zip as element(zip:file), $output as xs:string) as empty-sequence() external;
