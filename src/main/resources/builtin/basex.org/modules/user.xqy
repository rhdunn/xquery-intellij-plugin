xquery version "3.0";
(:~
: BaseX User Module functions
:
: @see http://docs.basex.org/wiki/User_Module
:)
module namespace user = "http://basex.org/modules/user";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";

declare %a:since("basex", "8.1") function user:current() as xs:string external;
declare %a:since("basex", "8.0") function user:list() as xs:string* external;
declare %a:since("basex", "8.0") function user:list-details() as element(user)* external;
declare %a:since("basex", "8.0") function user:list-details($name as xs:string) as element(user)* external;
declare %a:since("basex", "8.0") function user:exists($name as xs:string) as xs:boolean external;
declare %a:since("basex", "8.6") function user:check($name as xs:string, $password as xs:string) as empty-sequence() external;
declare %a:since("basex", "8.6") function user:info() as element(info) external;
declare %a:since("basex", "8.0") function user:create($name as xs:string, $password as xs:string) as empty-sequence() external;
declare %a:since("basex", "8.0") function user:create($name as xs:string, $password as xs:string, $permissions as xs:string*) as empty-sequence() external;
declare %a:since("basex", "8.4") function user:create($name as xs:string, $password as xs:string, $permissions as xs:string*, $patterns as xs:string*) as empty-sequence() external;
declare %a:since("basex", "8.0") function user:grant($name as xs:string, $permissions as xs:string*) as empty-sequence() external;
declare %a:since("basex", "8.4") function user:grant($name as xs:string, $permissions as xs:string*, $patterns as xs:string*) as empty-sequence() external;
declare %a:since("basex", "8.0") function user:drop($name as xs:string) as empty-sequence() external;
declare %a:since("basex", "8.4") function user:drop($name as xs:string, $patterns as xs:string*) as empty-sequence() external;
declare %a:since("basex", "8.0") function user:alter($name as xs:string, $newname as xs:string) as empty-sequence() external;
declare %a:since("basex", "8.0") function user:password($name as xs:string, $password as xs:string) as empty-sequence() external;
declare %a:since("basex", "8.6") function user:update-info($info as element(info)) as empty-sequence() external;