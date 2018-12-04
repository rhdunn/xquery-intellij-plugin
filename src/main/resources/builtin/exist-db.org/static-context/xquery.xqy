xquery version "1.0";
(:~
 : eXist default static context
 :
 : @see http://
 :)

declare default element namespace "";
declare default function namespace "http://www.w3.org/2005/xpath-functions"; (: = 'fn:' :)

(: XQuery 1.0 :)
declare namespace xml = "http://www.w3.org/XML/1998/namespace";
declare namespace xs = "http://www.w3.org/2001/XMLSchema";
declare namespace xsi = "http://www.w3.org/2001/XMLSchema-instance";
declare namespace fn = "http://www.w3.org/2005/xpath-functions";
declare namespace local = "http://www.w3.org/2005/xquery-local-functions";

(: XQuery 3.0 :)
declare namespace math = "http://www.w3.org/2005/xpath-functions/math";

(: XQuery 3.1 :)
declare namespace map = "http://www.w3.org/2005/xpath-functions/map";
declare namespace array = "http://www.w3.org/2005/xpath-functions/array";

(: eXist :)
declare namespace anno = "http://exist-db.org/annotations";
declare namespace config = "http://exist-db.org/apps/admin/config";
declare namespace app = "http://exist-db.org/apps/admin/templates";
declare namespace testapp = "http://exist-db.org/apps/admin/testapp";
declare namespace menu = "http://exist-db.org/apps/atomic/menu";
declare namespace dash = "http://exist-db.org/apps/dashboard";
declare namespace login-helper = "http://exist-db.org/apps/dashboard/login-helper";
declare namespace packages = "http://exist-db.org/apps/dashboard/packages/rest";
declare namespace service = "http://exist-db.org/apps/dashboard/service";
declare namespace usermanager = "http://exist-db.org/apps/dashboard/userManager";
declare namespace demo = "http://exist-db.org/apps/demo";
declare namespace cex = "http://exist-db.org/apps/demo/cex";
declare namespace contacts = "http://exist-db.org/apps/demo/restxq/contacts";
declare namespace shakes = "http://exist-db.org/apps/demo/shakespeare";
declare namespace t = "http://exist-db.org/apps/demo/shakespeare/tests";
declare namespace ex = "http://exist-db.org/apps/demo/templating/examples";
declare namespace apputil = "http://exist-db.org/apps/eXide/apputil";
declare namespace app = "http://exist-db.org/apps/homepage/app";
declare namespace hipchat = "http://exist-db.org/apps/monex/hipchat";
declare namespace notification = "http://exist-db.org/apps/monex/notification";
declare namespace app = "http://exist-db.org/apps/preconferece/templates";
declare namespace demo = "http://exist-db.org/apps/restxq/demo";

()