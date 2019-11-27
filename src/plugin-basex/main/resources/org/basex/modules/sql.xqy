xquery version "3.1";
(:~
 : BaseX SQL Module functions
 :
 : @see http://docs.basex.org/wiki/SQL_Module
 :)
module namespace sql = "http://basex.org/modules/sql";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare option o:requires "basex/7.6";

declare %a:since("basex", "7.0") function sql:init($class as xs:string) as empty-sequence() external;
declare %a:restrict-until("return", "basex", "9.0", "xs:integer")
        %a:restrict-since("return", "basex", "9.0", "xs:anyURI")
        %a:since("basex", "7.0") function sql:connect($url as xs:string) as (xs:integer|xs:anyURI) external;
declare %a:restrict-until("return", "basex", "9.0", "xs:integer")
        %a:restrict-since("return", "basex", "9.0", "xs:anyURI")
        %a:since("basex", "7.0") function sql:connect($url as xs:string, $user as xs:string, $password as xs:string) as (xs:integer|xs:anyURI) external;
declare %a:restrict-until("return", "basex", "9.0", "xs:integer")
        %a:restrict-since("return", "basex", "9.0", "xs:anyURI")
        %a:restrict-until("return", "basex", "9.0", "item()")
        %a:restrict-since("return", "basex", "9.0", "map(*)?")
        %a:since("basex", "7.0") function sql:connect($url as xs:string, $user as xs:string, $password as xs:string, $options as item()?) as (xs:integer|xs:anyURI) external;
declare %a:restrict-until("return", "basex", "8.7", "element()*")
        %a:since("basex", "7.0") function sql:execute($id as xs:anyURI, $statement as xs:string) as item()* external;
declare %a:since("basex", "9.0") function sql:execute($id as xs:anyURI, $statement as xs:string, $options as map(*)?) as item()* external;
declare %a:restrict-until("return", "basex", "8.7", "element()*")
        %a:since("basex", "7.5") function sql:execute-prepared($id as xs:anyURI, $params as element(sql:parameters)) as item()* external;
declare %a:since("basex", "9.0") function sql:execute-prepared($id as xs:anyURI, $params as element(sql:parameters), $options as map(*)?) as item()* external;
declare %a:restrict-until("return", "basex", "9.0", "xs:integer")
        %a:restrict-since("return", "basex", "9.0", "xs:anyURI")
        %a:since("basex", "7.0") function sql:prepare($id as xs:anyURI, $statement as xs:string) as (xs:integer|xs:anyURI) external;
declare %a:since("basex", "7.0") function sql:commit($id as xs:anyURI) as empty-sequence() external;
declare %a:since("basex", "7.0") function sql:rollback($id as xs:anyURI) as empty-sequence() external;
declare %a:since("basex", "7.0") function sql:close($id as xs:anyURI) as empty-sequence() external;