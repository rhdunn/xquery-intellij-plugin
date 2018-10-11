xquery version "3.0";
(:~
: BaseX Validate Module functions
:
: @see http://docs.basex.org/wiki/Validate_Module
:)
module namespace validate = "http://basex.org/modules/validate";

import module namespace a = "http://reecedunn.co.uk/xquery/annotations" at "res://reecedunn.co.uk/xquery/annotations.xqy";

declare %a:since("basex", "7.3") function validate:dtd($input as item()) as empty-sequence() external;
declare %a:since("basex", "7.3") function validate:dtd($input as item(), $schema as xs:string?) as empty-sequence() external;
declare %a:since("basex", "7.6") function validate:dtd-info($input as item()) as xs:string* external;
declare %a:since("basex", "7.6") function validate:dtd-info($input as item(), $schema as xs:string?) as xs:string* external;
declare %a:since("basex", "8.3") function validate:dtd-report($input as item()) as element(report) external;
declare %a:since("basex", "8.3") function validate:dtd-report($input as item(), $schema as xs:string?) as element(report) external;
declare %a:since("basex", "7.3") function validate:xsd($input as item()) as empty-sequence() external;
declare %a:since("basex", "7.3") function validate:xsd($input as item(), $schema as item()?) as empty-sequence() external;
declare %a:since("basex", "7.3") function validate:xsd($input as item(), $schema as item()?, $version as xs:string) as empty-sequence() external;
declare %a:since("basex", "7.6") function validate:xsd-info($input as item()) as xs:string* external;
declare %a:since("basex", "7.6") function validate:xsd-info($input as item(), $schema as item()?) as xs:string* external;
declare %a:since("basex", "7.6") function validate:xsd-info($input as item(), $schema as item()?, $version as xs:string) as xs:string* external;
declare %a:since("basex", "8.3") function validate:xsd-report($input as item()) as element(report) external;
declare %a:since("basex", "8.3") function validate:xsd-report($input as item(), $schema as xs:string?) as element(report) external;
declare %a:since("basex", "8.3") function validate:xsd-report($input as item(), $schema as xs:string?, $version as xs:string) as element(report) external;
declare %a:since("basex", "8.3") function validate:rng($input as item(), $schema as item()) as empty-sequence() external;
declare %a:since("basex", "8.3") function validate:rng($input as item(), $schema as item(), $compact as xs:boolean) as empty-sequence() external;
declare %a:since("basex", "8.3") function validate:rng-info($input as item(), $schema as item()) as xs:string* external;
declare %a:since("basex", "8.3") function validate:rng-info($input as item(), $schema as item(), $compact as xs:boolean) as xs:string* external;
declare %a:since("basex", "8.3") function validate:rng-report($input as item(), $schema as xs:string) as element(report) external;
declare %a:since("basex", "8.3") function validate:rng-report($input as item(), $schema as xs:string, $compact as xs:boolean) as element(report) external;