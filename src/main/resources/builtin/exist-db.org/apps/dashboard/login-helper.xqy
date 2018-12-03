xquery version "3.0";
(:~
 : eXist-db dashboard login-helper functions
 :
 : @see http://exist-db.org/exist/apps/fundocs/view.html?uri=http://exist-db.org/apps/dashboard/login-helper&location=/db/apps/dashboard/modules/login-helper.xql&details=true
 :
 :)
module namespace login-helper ="http://exist-db.org/apps/dashboard/login-helper";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

(:
 : in the context of eXist-db, this function can appear in two places?
 : see
 :)

declare %a:since("exist", "4.4") function login-helper:get-login-method() as function(*) external;