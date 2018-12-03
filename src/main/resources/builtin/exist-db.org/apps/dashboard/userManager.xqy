xquery version "3.0";
(:~
 : eXist-db dashboard userManager functions
 :
 : @see http://exist-db.org/exist/apps/fundocs/view.html?uri=http://exist-db.org/apps/dashboard/userManager&location=/db/apps/dashboard/plugins/userManager/userManager.xqm&details=true
 :
 :)
module namespace usermanager ="http://exist-db.org/apps/dashboard/userManager";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare %a:since("exist", "4.4") function usermanager:create-group($group-json as element()) as xs:string? external;
declare %a:since("exist", "4.4") function usermanager:create-user($user-json as element()) as xs:string? external;
declare %a:since("exist", "4.4") function usermanager:delete-group($group as item()*) as empty-sequence() external;
declare %a:since("exist", "4.4") function usermanager:delete-user($user as item()*) as empty-sequence() external;
declare %a:since("exist", "4.4") function usermanager:get-group($group as item()*) as element() external;
declare %a:since("exist", "4.4") function usermanager:get-user($user as item()*) as element() external;
declare %a:since("exist", "4.4") function usermanager:group-exists($group as item()*) as xs:boolean external;
declare %a:since("exist", "4.4") function usermanager:list-groups() as element() external;
declare %a:since("exist", "4.4") function usermanager:list-groups($pattern as xs:string) as element() external;
declare %a:since("exist", "4.4") function usermanager:list-users() as element() external;
declare %a:since("exist", "4.4") function usermanager:list-users($pattern as xs:string) as element() external;
declare %a:since("exist", "4.4") function usermanager:update-group($group-name as xs:string, $group-json as element()) as xs:boolean external;
declare %a:since("exist", "4.4") function usermanager:update-user($user-name as xs:string, $user-json as element()) as xs:boolean external;
declare %a:since("exist", "4.4") function usermanager:user-exists($user as item()*) as xs:boolean external;