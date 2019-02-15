xquery version "3.0";
(:~
 : eXist-db HTTP Client module functions
 :
 : @see http://exist-db.org/exist/apps/fundocs/view.html?uri=http://exist-db.org/xquery/httpclient&location=java:org.exist.xquery.modules.httpclient.HTTPClientModule&details=true
 :)
module namespace httpclient = "http://exist-db.org/xquery/httpclient";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare %a:since("exist", "4.4") function httpclient:clear-all() as item() external;
declare %a:since("exist", "4.4") function httpclient:delete($url as xs:anyURI, $persist as xs:boolean, $request-headers as element()?) as item() external;
declare %a:since("exist", "4.4") function httpclient:get($url as xs:anyURI, $persist as xs:boolean, $request-headers as element()?) as item() external;
declare %a:since("exist", "4.4") function httpclient:get($url as xs:anyURI, $persist as xs:boolean, $request-headers as element()?, $parser-options as element()?) as item() external;
declare %a:since("exist", "4.4") function httpclient:head($url as xs:anyURI, $persist as xs:boolean, $request-headers as element()?) as item() external;
declare %a:since("exist", "4.4") function httpclient:options($url as xs:anyURI, $persist as xs:boolean, $request-headers as element()?) as item() external;
declare %a:since("exist", "4.4") function httpclient:post($url as xs:anyURI, $content as item(), $persist as xs:boolean, $request-headers as element()?) as item() external;
declare %a:since("exist", "4.4") function httpclient:post-form($url as xs:anyURI, $content as element(), $persist as xs:boolean, $request-headers as element()?) as item() external;
declare %a:since("exist", "4.4") function httpclient:put($url as xs:anyURI, $content as item(), $persist as xs:boolean, $request-headers as element()?) as item() external;
declare %a:since("exist", "4.4") function httpclient:put($url as xs:anyURI, $content as item(), $persist as xs:boolean, $request-headers as element()?, $indentation as xs:integer) as item() external;
declare %a:since("exist", "4.4") function httpclient:set-parser-options($parser-options as element()?) as item() external;