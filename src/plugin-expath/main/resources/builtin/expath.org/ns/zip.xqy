xquery version "3.0";
(:~
 : ZIP Module (EXPath Candidate Module 12 October 2010)
 :
 : @see http://expath.org/spec/zip
 : @see http://docs.basex.org/wiki/ZIP_Module
 :)
module namespace zip = "http://expath.org/ns/zip";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare option o:requires "expath-zip/1.0-20101012";

declare option o:implements-module "basex/7.0 as expath-zip/1.0-20101012"; (: NOTE: 7.0 is the earliest version definitions are available for. :)

declare type uri-type = (
    %a:since("expath-zip", "1.0-20101012") for xs:anyURI |
    %a:since("basex", "7.0") for xs:string
);

declare %a:since("expath-zip", "1.0-20101012") %a:deprecated("basex", "7.3", "archive:extract-binary") function zip:binary-entry($uri as uri-type, $path as xs:string) as xs:base64Binary external;
declare %a:since("expath-zip", "1.0-20101012") %a:deprecated("basex", "7.3", "archive:extract-text") function zip:text-entry($uri as uri-type, $path as xs:string) as xs:string external;
declare %a:since("basex", "7.0") %a:deprecated("basex", "7.3", "archive:extract-text") function zip:text-entry($uri as xs:string, $path as xs:string, $encoding as xs:string) as xs:string external;
declare %a:since("expath-zip", "1.0-20101012") %a:deprecated("basex", "7.3", "archive:extract-text") function zip:xml-entry($uri as uri-type, $path as xs:string) as document-node() external;
declare %a:since("expath-zip", "1.0-20101012") %a:deprecated("basex", "7.3", "archive:extract-text") function zip:html-entry($uri as uri-type, $path as xs:string) as document-node() external;
declare %a:since("expath-zip", "1.0-20101012") %a:deprecated("basex", "7.3", "archive:entries") function zip:entries($uri as uri-type) as element(zip:file) external;
declare %a:since("expath-zip", "1.0-20101012") %a:deprecated("basex", "7.3", "archive:create") function zip:zip-file($zip as element(zip:file)) as empty-sequence() external;
declare %a:since("expath-zip", "1.0-20101012") %a:deprecated("basex", "7.3", "archive:update") function zip:update-entries($zip as element(zip:file), $output as uri-type) as empty-sequence() external;
