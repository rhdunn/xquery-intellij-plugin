xquery version "3.0";
(:~
 : Annotations used by the XQuery IntelliJ Plugin
 :)
module namespace a = "http://reecedunn.co.uk/xquery/annotations";

declare %a:annotation function a:annotation() external;
declare %a:annotation function a:since($conformance as xs:string, $version as xs:string) external;
declare %a:annotation function a:variadic($type as xs:string) external;
