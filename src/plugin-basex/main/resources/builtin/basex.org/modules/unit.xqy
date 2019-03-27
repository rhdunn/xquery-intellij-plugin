xquery version "3.0";
(:~
 : BaseX Unit Module functions
 :
 : @see http://docs.basex.org/wiki/Unit_Module
 :)
module namespace unit = "http://basex.org/modules/unit";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare option o:requires "basex/7.7";

declare %a:since("basex", "7.7") %a:annotation function unit:after() external;
declare %a:since("basex", "7.7") %a:annotation %a:parse-as("$function", "EQName") function unit:after($function as xs:string) external;
declare %a:since("basex", "7.7") %a:annotation function unit:after-module() external;
declare %a:since("basex", "7.7") %a:annotation function unit:before() external;
declare %a:since("basex", "7.7") %a:annotation %a:parse-as("$function", "EQName") function unit:before($function as xs:string) external;
declare %a:since("basex", "7.7") %a:annotation function unit:before-module() external;
declare %a:since("basex", "7.7") %a:annotation function unit:ignore() external;
declare %a:since("basex", "7.7") %a:annotation function unit:ignore($message as xs:string) external;
declare %a:since("basex", "7.7") %a:annotation function unit:test() external;
declare %a:since("basex", "7.7") %a:annotation %a:parse-as("$code", "EQName") function unit:test($error as xs:string, $code as xs:string) external;

declare %a:since("basex", "7.7") function unit:assert($test as item()*) as empty-sequence() external;
declare %a:since("basex", "7.7") function unit:assert($test as item()*, $info as item()) as empty-sequence() external;
declare %a:since("basex", "7.8") function unit:assert-equals($returned as item()*, $expected as item()*) as empty-sequence() external;
declare %a:since("basex", "7.8") function unit:assert-equals($returned as item()*, $expected as item()*, $info as item()) as empty-sequence() external;
declare %a:since("basex", "8.0") function unit:fail() as empty-sequence() external;
declare %a:since("basex", "7.7") function unit:fail($info as item()) as empty-sequence() external;
declare %a:since("basex", "7.7") %a:until("basex", "7.9") function unit:test() as element(testsuite)* external;
declare %a:since("basex", "7.7") %a:until("basex", "7.9") function unit:test($functions as function(*)*) as element(testsuite)* external;
declare %a:since("basex", "7.7") %a:until("basex", "7.9") function unit:test-uris($uris as xs:string*) as element(testsuites) external;