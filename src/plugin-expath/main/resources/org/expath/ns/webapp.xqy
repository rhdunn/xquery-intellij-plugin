xquery version "3.0";
(:~
 : Web Applications (EXPath Candidate Module 1 April 2013)
 :
 : @see http://expath.org/spec/webapp
 :)
module namespace web = "http://expath.org/ns/webapp";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare option o:requires "expath-webapp/1.0-20130401";

declare %a:since("expath-webapp", "1.0-20130401") function web:config-param($name as xs:string) as xs:string? external;
declare %a:since("expath-webapp", "1.0-20130401") function web:config-param($name as xs:string, $default as xs:string?) as xs:string? external;
declare %a:since("expath-webapp", "1.0-20130401") function web:config-doc($name as xs:string) as document-node()? external;
declare %a:since("expath-webapp", "1.0-20130401") function web:get-request-field($name as xs:string) as item()* external;
declare %a:since("expath-webapp", "1.0-20130401") function web:set-request-field($name as xs:string, $value as item()*) as empty-sequence() external;
declare %a:since("expath-webapp", "1.0-20130401") function web:get-request-field-names() as xs:string* external;
declare %a:since("expath-webapp", "1.0-20130401") function web:get-session-field($name as xs:string) as item()* external;
declare %a:since("expath-webapp", "1.0-20130401") function web:set-session-field($name as xs:string, $value as item()*) as empty-sequence() external;
declare %a:since("expath-webapp", "1.0-20130401") function web:get-session-field-names() as xs:string* external;
declare %a:since("expath-webapp", "1.0-20130401") function web:get-webapp-field($name as xs:string) as item()* external;
declare %a:since("expath-webapp", "1.0-20130401") function web:set-webapp-field($name as xs:string, $value as item()*) as empty-sequence() external;
declare %a:since("expath-webapp", "1.0-20130401") function web:get-webapp-field-names() as xs:string* external;
declare %a:since("expath-webapp", "1.0-20130401") function web:get-container-field($name as xs:string) as item()* external;
declare %a:since("expath-webapp", "1.0-20130401") function web:set-container-field($name as xs:string, $value as item()*) as empty-sequence() external;
declare %a:since("expath-webapp", "1.0-20130401") function web:get-container-field-names() as xs:string* external;
declare %a:since("expath-webapp", "1.0-20130401") function web:parse-header-value($header as xs:string) as element(web:header) external;
declare %a:since("expath-webapp", "1.0-20130401") function web:parse-basic-auth($header as xs:string) as element(web:basic-auth) external;
