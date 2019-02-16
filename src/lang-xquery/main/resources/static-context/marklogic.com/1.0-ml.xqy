xquery version "1.0";
(:~
 : MarkLogic static context for xquery version "1.0-ml".
 :
 : @see https://www.w3.org/TR/xquery/#id-default-namespace
 : @see https://www.w3.org/TR/xquery/#id-basics
 : @see http://docs.marklogic.com/6.0/guide/xquery/namespaces
 : @see http://docs.marklogic.com/7.0/guide/xquery/namespaces
 : @see http://docs.marklogic.com/8.0/guide/xquery/namespaces
 : @see http://docs.marklogic.com/9.0/guide/xquery/namespaces
 :)

declare default element namespace "";
declare default function namespace "http://www.w3.org/2005/xpath-functions"; (: = 'fn:' :)

(: XQuery 1.0 :)
declare namespace xml = "http://www.w3.org/XML/1998/namespace";
declare namespace xs = "http://www.w3.org/2001/XMLSchema";
declare namespace xsi = "http://www.w3.org/2001/XMLSchema-instance";
declare namespace fn = "http://www.w3.org/2005/xpath-functions";
declare namespace local = "http://www.w3.org/2005/xquery-local-functions";

(: MarkLogic 5.0 -- NOTE: These are from the MarkLogic 6.0 namespace list that have functions from MarkLogic 5.0. :)
declare namespace cts = "http://marklogic.com/cts";
declare namespace dbg = "http://marklogic.com/xdmp/dbg";
declare namespace map = "http://marklogic.com/xdmp/map";
declare namespace math = "http://marklogic.com/xdmp/math";
declare namespace prof = "http://marklogic.com/xdmp/profile";
declare namespace spell = "http://marklogic.com/xdmp/spell";
declare namespace xdmp = "http://marklogic.com/xdmp";

(: MarkLogic 6.0 :)
declare namespace dav = "DAV:";
declare namespace dir = "http://marklogic.com/xdmp/directory";
declare namespace err = "http://www.w3.org/2005/xqt-error";
declare namespace error = "http://marklogic.com/xdmp/error";
declare namespace json = "http://marklogic.com/xdmp/json";
declare namespace lock = "http://marklogic.com/xdmp/lock";
declare namespace prop = "http://marklogic.com/xdmp/property";
declare namespace sec = "http://marklogic.com/security";
declare namespace xqe = "http://marklogic.com/xqe";
declare namespace xqterr = "http://www.w3.org/2005/xqt-error";

(: MarkLogic 7.0 :)
declare namespace rdf = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
declare namespace sc = "http://marklogic.com/xdmp/schema-components";
declare namespace sem = "http://marklogic.com/xdmp/semantics";
declare namespace sql = "http://marklogic.com/xdmp/sql";

(: MarkLogic 8.0 :)
declare namespace geo = "http://marklogic.com/geospatial";
declare namespace temporal = "http://marklogic.com/xdmp/temporal";

(: MarkLogic 9.0 :)
declare namespace tde = "http://marklogic.com/xdmp/tde";

()