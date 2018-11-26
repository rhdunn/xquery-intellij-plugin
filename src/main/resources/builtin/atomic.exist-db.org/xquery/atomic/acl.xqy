xquery version "3.0";
(:~
 : eXist-db atomic acl functions
 :
 : @see http://exist-db.org/exist/apps/fundocs/view.html?uri=http://atomic.exist-db.org/xquery/atomic/acl&location=/db/apps/wiki/modules/acl.xql&details=true
 :
 :)
module namespace acl ="http://atomic.exist-db.org/xquery/atomic/acl";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare %a:since("exist", "4.4") function acl:add-group-aces($path as xs:string) as item()* external;
declare %a:since("exist", "4.4") function acl:change-collection-permissions($path as xs:string) as item()* external;
declare %a:since("exist", "4.4") function acl:change-permissions($path as xs:string) as item()* external;
declare %a:since("exist", "4.4") function acl:domains($node as node(), $model as map(*)) as item()* external;
declare %a:since("exist", "4.4") function acl:get-user-name() as item()* external;
declare %a:since("exist", "4.4") function acl:get-user-name($user as item()*) as item()* external;
declare %a:since("exist", "4.4") function acl:group-select($node as node(), $model as map(*)) as item()* external;
declare %a:since("exist", "4.4") function acl:if-admin-user($node as node(), $model as map(*), $allow-manager as xs:boolean) as item()* external;
declare %a:since("exist", "4.4") function acl:show-group-permissions($node as node(), $model as map(*)) as item()* external;
declare %a:since("exist", "4.4") function acl:show-permissions($node as node(), $model as map(*), $modelItem as xs:string?) as item()* external;