xquery version "3.0";
(:~
 : eXist-db file module functions
 :
 : @see http://exist-db.org/exist/apps/fundocs/view.html?uri=http://exist-db.org/xquery/file&location=java:org.exist.xquery.modules.file.FileModule&details=true
 :
 :)
module namespace file = "http://exist-db.org/xquery/file";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare %a:since("exist", "4.4") function file:delete($path as item()) as xs:boolean external;
declare %a:since("exist", "4.4") function file:directory-list($path as item(), $pattern as xs:string) as node()? external;
declare %a:since("exist", "4.4") function file:exists($path as item()) as xs:boolean external;
declare %a:since("exist", "4.4") function file:is-directory($path as item()) as xs:boolean external;
declare %a:since("exist", "4.4") function file:is-readable($path as item()) as xs:boolean external;
declare %a:since("exist", "4.4") function file:is-writeable($path as item()) as xs:boolean external;
declare %a:since("exist", "4.4") function file:list($path as item()) as node()* external;
declare %a:since("exist", "4.4") function file:mkdir($path as item()) as xs:boolean external;
declare %a:since("exist", "4.4") function file:mkdirs($path as item()) as xs:boolean external;
declare %a:since("exist", "4.4") function file:move($original as item(), $destination as item()) as xs:boolean external;
declare %a:since("exist", "4.4") function file:read($path as item()) as xs:string? external;
declare %a:since("exist", "4.4") function file:read($path as item(), $encoding as xs:string) as xs:string? external;
declare %a:since("exist", "4.4") function file:read-binary($path as item()) as xs:base64Binary? external;
declare %a:since("exist", "4.4") function file:read-unicode($path as item()) as xs:string? external;
declare %a:since("exist", "4.4") function file:read-unicode($path as item(), $encoding as xs:string) as xs:string? external;
declare %a:since("exist", "4.4") function file:serialize($node-set as node()*, $path as item(), $parameters as item()*) as xs:boolean? external;
declare %a:since("exist", "4.4") function file:serialize($node-set as node()*, $path as item(), $parameters as item()*, $append as xs:boolean) as xs:boolean? external;
declare %a:since("exist", "4.4") function file:serialize-binary($binarydata as xs:base64Binary, $path as item()) as xs:boolean external;
declare %a:since("exist", "4.4") function file:serialize-binary($binarydata as xs:base64Binary, $path as item(), $append as xs:boolean) as xs:boolean external;
declare %a:since("exist", "4.4") function file:sync($collection as xs:string, $targetPath as item(), $dateTime as xs:dateTime?) as xs:boolean external;