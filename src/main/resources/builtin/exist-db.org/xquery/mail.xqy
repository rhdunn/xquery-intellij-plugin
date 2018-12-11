xquery version "3.0";
(:~
 : eXist-db mail module functions
 :
 : @see http://exist-db.org/exist/apps/fundocs/view.html?uri=http://exist-db.org/xquery/mail&location=java:org.exist.xquery.modules.mail.MailModule&details=true
 :
 :)
module namespace mail = "http://exist-db.org/xquery/mail";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare %a:since("exist", "4.4") function mail:close-mail-folder($mail-folder-handle as xs:integer, $expunge as xs:boolean) as item() external;
declare %a:since("exist", "4.4") function mail:close-mail-store($mail-store-handle as xs:integer) as item() external;
declare %a:since("exist", "4.4") function mail:close-message-list($message-list-handle as xs:integer) as item() external;
declare %a:since("exist", "4.4") function mail:get-mail-folder($mail-store-handle as xs:integer, $foldername as xs:string) as xs:long? external;
declare %a:since("exist", "4.4") function mail:get-mail-session($properties as element()?) as xs:long? external;
declare %a:since("exist", "4.4") function mail:get-mail-store($mail-handle as xs:integer) as xs:long? external;
declare %a:since("exist", "4.4") function mail:get-message-list($mail-folder-handle as xs:integer) as xs:long? external;
declare %a:since("exist", "4.4") function mail:get-message-list-as-xml($message-list-handle as xs:integer, $include-headers as xs:boolean) as element()? external;
declare %a:since("exist", "4.4") function mail:get-messages($message-list-handle as xs:integer, $message-numbers as xs:integer*) as element()? external;
declare %a:since("exist", "4.4") function mail:search-message-list($mail-folder-handle as xs:integer, $search-parameters as element()) as xs:long? external;
declare %a:since("exist", "4.4") function mail:send-email($mail-handle as xs:long, $email as element()+) as item() external;
declare %a:since("exist", "4.4") function mail:send-email($email as element()+, $server as xs:string?, $charset as xs:string?) as xs:boolean+ external;