xquery version "3.0";
(:~
 : Annotations used by the XQuery IntelliJ Plugin
 :)
module namespace a = "http://reecedunn.co.uk/xquery/annotations";

declare %a:annotation function a:annotation() external;
declare %a:annotation function a:deprecated($conformance as xs:string, $version as xs:string) external;
declare %a:annotation function a:deprecated($conformance as xs:string, $version as xs:string, $replacement as xs:string) external;
declare %a:annotation function a:language($lang as xs:string) external;
declare %a:annotation function a:missing($conformance as xs:string, $version as xs:string) external;
declare %a:annotation function a:parse-as($ebnf-symbol as xs:string) external;
declare %a:annotation function a:restrict-since($scope as xs:string, $conformance as xs:string, $version as xs:string, $type as xs:string) external;
declare %a:annotation function a:restrict-until($scope as xs:string, $conformance as xs:string, $version as xs:string, $type as xs:string) external;
declare %a:annotation function a:see-also($conformance as xs:string, $version as xs:string, $replacement as xs:string) external;
declare %a:annotation function a:since($conformance as xs:string, $version as xs:string) external;
declare %a:annotation function a:until($conformance as xs:string, $version as xs:string) external;
declare %a:annotation function a:until($conformance as xs:string, $version as xs:string, $replacement as xs:string) external;
