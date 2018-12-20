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
declare namespace compression = "http://exist-db.org/xquery/compression";
declare namespace console = "http://exist-db.org/xquery/console";
declare namespace contentextraction = "http://exist-db.org/xquery/contentextraction";
declare namespace counter = "http://exist-db.org/xquery/counter";
declare namespace datetime = "http://exist-db.org/xquery/datetime";
declare namespace example = "http://exist-db.org/xquery/example";
declare namespace file = "http://exist-db.org/xquery/file";
declare namespace httpclient = "http://exist-db.org/xquery/httpclient";
declare namespace image = "http://exist-db.org/xquery/image";
declare namespace inspect = "http://exist-db.org/xquery/inspect";
declare namespace ft = "http://exist-db.org/xquery/lucene";
declare namespace mail = "http://exist-db.org/xquery/mail";
declare namespace math-ext = "http://exist-db.org/xquery/math";
declare namespace ngram = "http://exist-db.org/xquery/ngram";
declare namespace process = "http://exist-db.org/xquery/process";
declare namespace range = "http://exist-db.org/xquery/range";
declare namespace repo = "http://exist-db.org/xquery/repo";
declare namespace request = "http://exist-db.org/xquery/request";
declare namespace response = "http://exist-db.org/xquery/response";
declare namespace scheduler = "http://exist-db.org/xquery/scheduler";
declare namespace sm = "http://exist-db.org/xquery/securitymanager";
declare namespace session = "http://exist-db.org/xquery/session";
declare namespace sort = "http://exist-db.org/xquery/sort";
declare namespace sql = "http://exist-db.org/xquery/sql";
declare namespace system = "http://exist-db.org/xquery/system";
declare namespace transform = "http://exist-db.org/xquery/transform";
declare namespace util = "http://exist-db.org/xquery/util";
declare namespace validation = "http://exist-db.org/xquery/validation";
declare namespace xmldb = "http://exist-db.org/xquery/xmldb";
declare namespace xmldiff = "http://exist-db.org/xquery/xmldiff";
declare namespace xqdm = "http://exist-db.org/xquery/xqdoc";
declare namespace xslfo = "http://exist-db.org/xquery/xslfo";

()