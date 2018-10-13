xquery version "3.0";
(:~
 : BaseX Unit Module functions
 :
 : @see http://docs.basex.org/wiki/Unit_Module
 :)
module namespace unit = "http://basex.org/modules/unit";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";

declare option a:requires "basex/7.7";

(:
BaseX Unit Module annotations
@TODO - do these need to be included?
%unit:test
%unit:test("expected", CODE) %unit:test("expected" as xs:string, xs:QName)???
%unit:before
%unit:before(FUNCTION)
%unit:after
%unit:after(FUNCTION)
%unit:before-module
%unit:after-module
%unit:ignore
%unit:ignore(MESSAGE)
:)

declare %a:since("basex", "7.7") function unit:assert($test as item()*) as empty-sequence() external;
declare %a:since("basex", "7.7") function unit:assert($test as item()*, $info as item()) as empty-sequence() external;
declare %a:since("basex", "7.8") function unit:assert-equals($returned as item()*, $expected as item()*) as empty-sequence() external;
declare %a:since("basex", "7.8") function unit:assert-equals($returned as item()*, $expected as item()*, $info as item()) as empty-sequence() external;
declare %a:since("basex", "8.0") function unit:fail() as empty-sequence() external;
declare %a:since("basex", "7.7") function unit:fail($info as item()) as empty-sequence() external;
declare %a:since("basex", "7.7") %a:until("basex", "7.9") function unit:test() as element(testsuite)* external;
declare %a:since("basex", "7.7") %a:until("basex", "7.9") function unit:test($functions as function(*)*) as element(testsuite)* external;
declare %a:since("basex", "7.7") %a:until("basex", "7.9") function unit:test-uris($uris as xs:string*) as element(testsuites) external;