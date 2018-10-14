xquery version "1.0-ml";
(:~
 : MarkLogic schema components functions
 :
 : @see https://docs.marklogic.com/sc
 :)
module namespace sc = "http://marklogic.com/xdmp/schema-components";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare option o:requires "marklogic/7.0";

declare %a:since("marklogic", "7.0") function sc:annotations() as element()* external;
declare %a:since("marklogic", "7.0") function sc:annotations($arg as schema-component()) as element()* external;
declare %a:since("marklogic", "7.0") function sc:attribute-decl() as attribute-decl()? external;
declare %a:since("marklogic", "7.0") function sc:attribute-decl($arg as item()) as attribute-decl()? external;
declare %a:since("marklogic", "7.0") function sc:attributes() as attribute-decl()* external;
declare %a:since("marklogic", "7.0") function sc:attributes($arg as schema-component()) as attribute-decl()* external;
declare %a:since("marklogic", "7.0") function sc:canonical-path() as xs:string? external;
declare %a:since("marklogic", "7.0") function sc:canonical-path($arg as schema-component()) as xs:string? external;
declare %a:since("marklogic", "7.0") function sc:complex-type() as complex-type()? external;
declare %a:since("marklogic", "7.0") function sc:complex-type($arg as item()) as complex-type()? external;
declare %a:since("marklogic", "7.0") function sc:component-property($propname as xs:string) as item()* external;
declare %a:since("marklogic", "7.0") function sc:component-property($propname as xs:string, $arg as schema-component()) as item()* external;
declare %a:since("marklogic", "7.0") function sc:element-decl() as element-decl()? external;
declare %a:since("marklogic", "7.0") function sc:element-decl($arg as item()) as element-decl()? external;
declare %a:since("marklogic", "7.0") function sc:facets() (: as [7.0]element()* [8.0]schema-facet() :) external;
declare %a:since("marklogic", "7.0") function sc:facets($arg as schema-component()) (: as [7.0]element()* [8.0]schema-facet() :) external;
declare %a:since("marklogic", "8.0") function sc:function-parameter-type() as schema-type()? external;
declare %a:since("marklogic", "8.0") function sc:function-parameter-type($arg as function(*)) as schema-type()? external;
declare %a:since("marklogic", "7.0") function sc:function-parameter-type($arg as function(*), $param (: as [7.0]xs:integer [8.0]xs:integer? :)) as schema-type()? external;
declare %a:since("marklogic", "7.0") function sc:function-return-type() as schema-type() external;
declare %a:since("marklogic", "7.0") function sc:function-return-type($arg as function(*)) as schema-type() external;
declare %a:since("marklogic", "7.0") function sc:name() as xs:QName? external;
declare %a:since("marklogic", "7.0") function sc:name($arg as schema-component()) as xs:QName? external;
declare %a:since("marklogic", "7.0") function sc:particles() as schema-particle()* external;
declare %a:since("marklogic", "7.0") function sc:particles($arg as schema-component()) as schema-particle()* external;
declare %a:since("marklogic", "7.0") function sc:schema() as schema-root()? external;
declare %a:since("marklogic", "7.0") function sc:schema($arg as item()) as schema-root()? external;
declare %a:since("marklogic", "7.0") function sc:schema-from-path($namespace as xs:string) (: as [7.0]xs:string* [8.0]schema-root()? :) external;
declare %a:since("marklogic", "7.0") function sc:schema-from-path($namespace as xs:string, $location as xs:string) (: as [7.0]xs:string* [8.0]schema-root()? :) external;
declare %a:since("marklogic", "7.0") function sc:simple-type() as simple-type()? external;
declare %a:since("marklogic", "7.0") function sc:simple-type($arg as item()) as simple-type()? external;
declare %a:since("marklogic", "7.0") function sc:type() as schema-type()? external;
declare %a:since("marklogic", "7.0") function sc:type($arg as item()) as schema-type()? external;
declare %a:since("marklogic", "7.0") function sc:type-apply($type as schema-type(), $arg (: as [7.0]item()* [8.0]item() :)) as item()* external;
declare %a:since("marklogic", "7.0") %a:until("marklogic", "8.0") function sc:type-named() as schema-type()? external;
declare %a:since("marklogic", "7.0") function sc:type-named($arg as xs:QName) (: as [7.0]schema-type()? [8.0]schema-type() :) external;