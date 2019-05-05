xquery version "3.1";
(:~
 : BaseX Permissions annotations
 :
 : @see http://docs.basex.org/wiki/Permissions
 :)
module namespace sql = "http://basex.org/modules/perm";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare option o:requires "basex/9.0";

declare %a:since("basex", "9.0") %a:annotation function perm:allow($permission as xs:string ...) external;
declare %a:since("basex", "9.0") %a:annotation function perm:check() external;
declare %a:since("basex", "9.0") %a:annotation function perm:check($path as xs:string) external;
declare %a:since("basex", "9.0") %a:annotation %a:parse-as("$variable", "EnclosedExpr(VarDecl)") function perm:check($path as xs:string, $variable as xs:string) external;
