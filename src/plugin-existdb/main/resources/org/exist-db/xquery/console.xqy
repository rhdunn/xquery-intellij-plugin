xquery version "3.0";
(:~
 : eXist-db console module functions
 :
 : @see http://exist-db.org/exist/apps/fundocs/view.html?uri=http://exist-db.org/xquery/console&location=java:org.exist.console.xquery.ConsoleModule&details=true
 :)
module namespace console = "http://exist-db.org/xquery/console";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare %a:since("exist", "4.4") function console:dump($channel as xs:string, $vars as xs:string+) as empty-sequence() external;
declare %a:since("exist", "4.4") function console:dump($channel as xs:string) as empty-sequence() external;
declare %a:since("exist", "4.4") function console:dump() as empty-sequence() external;
declare %a:since("exist", "4.4") function console:jmx-token() as xs:string? external;
declare %a:since("exist", "4.4") function console:log($items as item()*) as empty-sequence() external;
declare %a:since("exist", "4.4") function console:log($channel as xs:string, $items as item()*) as empty-sequence() external;
declare %a:since("exist", "4.4") function console:send($channel as xs:string, $items as item()?) as empty-sequence() external;