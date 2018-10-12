xquery version "3.0";
(:~
: BaseX Repository Module functions
:
: @see http://docs.basex.org/wiki/Repository_Module
:)
module namespace repo = "http://basex.org/modules/repo";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";

declare %a:since("basex", "7.1") function repo:install($path as xs:string) as empty-sequence() external;
declare %a:since("basex", "7.1") function repo:delete($pkg as xs:string) as empty-sequence() external;
declare %a:since("basex", "7.1") function repo:list() as element(package)* (: [7.1] as xs:string [7.2] as element(package)* :) external;