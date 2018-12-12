xquery version "3.0";
(:~
 : eXist-db SQL module functions
 :
 : @see http://exist-db.org/exist/apps/fundocs/view.html?uri=http://exist-db.org/xquery/sql&location=java:org.exist.xquery.modules.sql.SQLModule&details=true
 :
 :)
module namespace sql = "http://exist-db.org/xquery/sql";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare %a:since("exist", "4.4") function sql:execute($connection-handle as xs:long, $sql-statement as xs:string, $make-node-from-column-name as xs:boolean) as node()? external;
declare %a:since("exist", "4.4") function sql:execute($connection-handle as xs:long, $statement-handle as xs:integer, $parameters as element()?, $make-node-from-column-name as xs:boolean) as node()? external;
declare %a:since("exist", "4.4") function sql:get-connection($driver-classname as xs:string, $url as xs:string) as xs:long? external;
declare %a:since("exist", "4.4") function sql:get-connection($driver-classname as xs:string, $url as xs:string, $properties as element()?) as xs:long? external;
declare %a:since("exist", "4.4") function sql:get-connection($driver-classname as xs:string, $url as xs:string, $username as xs:string, $password as xs:string) as xs:long? external;
declare %a:since("exist", "4.4") function sql:get-jndi-connection($jndi-name as xs:string) as xs:long? external;
declare %a:since("exist", "4.4") function sql:get-jndi-connection($jndi-name as xs:string, $username as xs:string, $password as xs:string) as xs:long? external;
declare %a:since("exist", "4.4") function sql:prepare($handle as xs:long, $sql-statement as xs:string) as xs:long? external;