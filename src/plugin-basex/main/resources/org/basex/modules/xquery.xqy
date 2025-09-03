xquery version "3.1";
(:~
 : BaseX XQuery Module functions
 :
 : @see http://docs.basex.org/wiki/XQuery_Module
 :)
module namespace xquery = "http://basex.org/modules/xquery";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare option o:requires "basex/7.3";

declare %a:since("basex", "7.3") function xquery:eval($query as xs:string) as item()* external;
declare %a:since("basex", "7.3") function xquery:eval($query as xs:string, $bindings as map(*)?) as item()* external;
declare %a:since("basex", "7.8.2") function xquery:eval($query as xs:string, $bindings as map(*)?, $options as map(*)?) as item()* external;
declare %a:since("basex", "7.3") function xquery:eval-update($query as xs:string) as item()* external;
declare %a:since("basex", "7.3") function xquery:eval-update($query as xs:string, $bindings as map(*)?) as item()* external;
declare %a:since("basex", "7.8.2") function xquery:eval-update($query as xs:string, $bindings as map(*)?, $options as map(*)?) as item() external;
declare %a:since("basex", "7.3") %a:until("basex", "9.2", "xquery:eval") function xquery:invoke($uri as xs:string) as item()* external;
declare %a:since("basex", "7.3") %a:until("basex", "9.2", "xquery:eval") function xquery:invoke($uri as xs:string, $bindings as map(*)?) as item()* external;
declare %a:since("basex", "7.3") %a:until("basex", "9.2", "xquery:eval") function xquery:invoke($uri as xs:string, $bindings as map(*)?, $options as map(*)?) as item()* external;
declare %a:since("basex", "9.0") %a:until("basex", "9.2", "xquery:eval-update") function xquery:invoke-update($uri as xs:string) as item()* external;
declare %a:since("basex", "9.0") %a:until("basex", "9.2", "xquery:eval-update") function xquery:invoke-update($uri as xs:string, $bindings as map(*)?) as item()* external;
declare %a:since("basex", "9.0") %a:until("basex", "9.2", "xquery:eval-update") function xquery:invoke-update($uri as xs:string, $bindings as map(*)?, $options as map(*)?) as item()* external;
declare %a:since("basex", "8.0")
        %a:restrict-until("$query", "basex", "10.0", "xs:string") function xquery:parse($query as (xs:string|xs:anyURI)) as item()? external;
declare %a:since("basex", "8.0")
        %a:restrict-until("$query", "basex", "10.0", "xs:string") function xquery:parse($query as (xs:string|xs:anyURI), $options as map(*)?) as item()? external;
declare %a:since("basex", "7.3") %a:until("basex", "10.0") %a:see-also("basex", "10.0", "xquery:parse#1") function xquery:parse-uri($uri as xs:string) as item()? external;
declare %a:since("basex", "7.3") %a:until("basex", "10.0") %a:see-also("basex", "10.0", "xquery:parse#2") function xquery:parse-uri($uri as xs:string, $options as map(*)?) as item()? external;
declare %a:since("basex", "8.5") function xquery:fork-join($functions as function(*)*) as item()* external;
declare %a:since("basex", "7.8") %a:until("basex", "8.0") function xquery:evaluate() as item()* external;
declare %a:since("basex", "7.3") %a:until("basex", "8.5") %a:see-also("basex", "8.5", "prof:type") function xquery:type() as item()* external;
declare %a:since("basex", "8.0") %a:until("basex", "9.0") %a:see-also("basex", "9.0", "xquery:eval-update") function xquery:update() as item()* external;
