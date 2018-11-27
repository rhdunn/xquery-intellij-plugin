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

()