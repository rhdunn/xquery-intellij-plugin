xquery version "1.0";
(:~
 : eXist 'atomic' default static context
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
declare namespace html2wiki = "http://atomic.exist-db.org/xquery/html2wiki";
declare namespace ext = "http://atomic.exist-db.org/xquery/extensions";
declare namespace dates = "http://atomic.exist-db.org/xquery/dates";
declare namespace cleanup = "http://atomic.exist-db.org/xquery/cleanup";
declare namespace theme = "http://atomic.exist-db.org/xquery/atomic/theme";
()