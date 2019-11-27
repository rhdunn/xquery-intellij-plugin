xquery version "3.0";
(:~
 : BaseX Repository Module functions
 :
 : @see http://docs.basex.org/wiki/Repository_Module
 :)
module namespace repo = "http://basex.org/modules/repo";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare option o:requires "basex/7.1";

declare %a:since("basex", "7.1") function repo:install($path as xs:string) as empty-sequence() external;
declare %a:since("basex", "7.1") function repo:delete($pkg as xs:string) as empty-sequence() external;
declare %a:restrict-until("return", "basex", "7.2", "xs:string")
        %a:restrict-since("return", "basex", "7.2", "element(package)*")
        %a:since("basex", "7.1") function repo:list() as (xs:string|element(package)*) external;