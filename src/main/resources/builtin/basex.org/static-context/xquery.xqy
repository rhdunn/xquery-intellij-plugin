xquery version "1.0";
(:~
 : BaseX default static context.
 :
 : @see https://www.w3.org/TR/xquery/#id-basics
 : @see https://www.w3.org/TR/xquery/#id-default-namespace
 : @see http://docs.basex.org/wiki/Module_Library
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

(: BaseX prior to 7.0 :)
declare namespace db = "http://basex.org/modules/db";
declare namespace file = "http://expath.org/ns/file";
declare namespace ft = "http://basex.org/modules/ft";
declare namespace hof = "http://basex.org/modules/hof";
declare namespace xslt = "http://basex.org/modules/xslt";
declare namespace zip = "http://expath.org/ns/zip";

(: BaseX 7.0 :)
declare namespace crypto = "http://expath.org/ns/crypto";
declare namespace json = "http://basex.org/modules/json";
declare namespace sql = "http://basex.org/modules/sql";

(: BaseX 7.1 :)
declare namespace http = "http://expath.org/ns/http-client";
declare namespace index = "http://basex.org/modules/index";
declare namespace repo = "http://basex.org/modules/repo";

(: BaseX 7.3 :)
declare namespace archive = "http://basex.org/modules/archive";
declare namespace client = "http://basex.org/modules/client";
declare namespace convert = "http://basex.org/modules/convert";
declare namespace hash = "http://basex.org/modules/hash";
declare namespace out = "http://basex.org/modules/out";
declare namespace proc = "http://basex.org/modules/proc";
declare namespace prof = "http://basex.org/modules/prof";
declare namespace validate = "http://basex.org/modules/validate";
declare namespace xquery = "http://basex.org/modules/xquery";

(: BaseX 7.5 :)
declare namespace admin = "http://basex.org/modules/admin";
declare namespace random = "http://basex.org/modules/random";

(: BaseX 7.6 :)
declare namespace fetch = "http://basex.org/modules/fetch";
declare namespace html = "http://basex.org/modules/html";

(: BaseX 7.7 :)
declare namespace inspect = "http://basex.org/modules/inspect";
declare namespace stream = "http://basex.org/modules/stream";
declare namespace unit = "http://basex.org/modules/unit";

(: BaseX 7.7.2 :)
declare namespace csv = "http://basex.org/modules/csv";

(: BaseX 7.8 :)
declare namespace bin = "http://expath.org/ns/binary";

(: BaseX 8.0 :)
declare namespace user = "http://basex.org/modules/user";

(: BaseX 8.1 :)
declare namespace web = "http://basex.org/modules/web";

(: BaseX 8.3 :)
declare namespace strings = "http://basex.org/modules/strings";

(: BaseX 8.5 :)
declare namespace jobs = "http://basex.org/modules/jobs";
declare namespace util = "http://basex.org/modules/util";

(: BaseX 9.0 :)
declare namespace lazy = "http://basex.org/modules/lazy";
declare namespace update = "http://basex.org/modules/update";

(: BaseX 9.1 :)
declare namespace ws = "http://basex.org/modules/ws";

()