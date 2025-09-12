xquery version "3.0";
(:~
 : BaseX Validate Module functions
 :
 : @see http://docs.basex.org/wiki/Validate_Module
 :)
module namespace validate = "http://basex.org/modules/validate";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare option o:requires "basex/7.3";

declare %a:since("basex", "7.3") function validate:dtd($input as item()) as empty-sequence() external;
declare %a:since("basex", "7.3") function validate:dtd($input as item(), $schema as xs:string?) as empty-sequence() external;
declare %a:since("basex", "7.6") function validate:dtd-info($input as item()) as xs:string* external;
declare %a:since("basex", "7.6") function validate:dtd-info($input as item(), $schema as xs:string?) as xs:string* external;
declare %a:since("basex", "8.3") function validate:dtd-report($input as item()) as element(report) external;
declare %a:since("basex", "8.3") function validate:dtd-report($input as item(), $schema as xs:string?) as element(report) external;
declare %a:since("basex", "7.3") function validate:xsd($input as item()) as empty-sequence() external;
declare %a:since("basex", "7.3") function validate:xsd($input as item(), $schema as item()?) as empty-sequence() external;
declare %a:restrict-until("$version-features", "basex", "9.2", "xs:string")
        %a:restrict-since("$version-features", "basex", "9.2", "map(*)")
        %a:since("basex", "7.3") function validate:xsd($input as item(), $schema as item()?, $version-features as (xs:string|map(*))) as empty-sequence() external;
declare %a:since("basex", "7.6") function validate:xsd-info($input as item()) as xs:string* external;
declare %a:since("basex", "7.6") function validate:xsd-info($input as item(), $schema as item()?) as xs:string* external;
declare %a:restrict-until("$version-features", "basex", "9.2", "xs:string")
        %a:restrict-since("$version-features", "basex", "9.2", "map(*)")
        %a:since("basex", "7.6") function validate:xsd-info($input as item(), $schema as item()?, $version-features as (xs:string|map(*))) as xs:string* external;
declare %a:since("basex", "11.0") function validate:xsd-init() as empty-sequence() external;
declare %a:since("basex", "8.3") function validate:xsd-report($input as item()) as element(report) external;
declare %a:since("basex", "8.3") function validate:xsd-report($input as item(), $schema as xs:string?) as element(report) external;
declare %a:restrict-until("$version-features", "basex", "9.2", "xs:string")
        %a:restrict-since("$version-features", "basex", "9.2", "map(*)")
        %a:since("basex", "8.3") function validate:xsd-report($input as item(), $schema as xs:string?, $version-feattures as (xs:string|map(*))) as element(report) external;
declare %a:since("basex", "9.2") function validate:xsd-processor() as xs:string external;
declare %a:since("basex", "9.2") function validate:xsd-version() as xs:string external;
declare %a:since("basex", "8.3") function validate:rng($input as item(), $schema as item()) as empty-sequence() external;
declare %a:since("basex", "8.3") function validate:rng($input as item(), $schema as item(), $compact as xs:boolean) as empty-sequence() external;
declare %a:since("basex", "8.3") function validate:rng-info($input as item(), $schema as item()) as xs:string* external;
declare %a:since("basex", "8.3") function validate:rng-info($input as item(), $schema as item(), $compact as xs:boolean) as xs:string* external;
declare %a:since("basex", "8.3") function validate:rng-report($input as item(), $schema as xs:string) as element(report) external;
declare %a:since("basex", "8.3") function validate:rng-report($input as item(), $schema as xs:string, $compact as xs:boolean) as element(report) external;
