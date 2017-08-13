xquery version "1.0";

(: http://docs.marklogic.com/8.0/guide/xquery/namespaces :)
import module namespace cts = "http://marklogic.com/cts" at "res://marklogic.com/cts.xqy";
declare namespace dav = "DAV:";
import module namespace dbg = "http://marklogic.com/xdmp/dbg" at "res://marklogic.com/xdmp/dbg.xqy";
declare namespace dir = "http://marklogic.com/xdmp/directory";
declare namespace err = "http://www.w3.org/2005/xqt-error";
declare namespace error = "http://marklogic.com/xdmp/error";
import module namespace fn = "http://www.w3.org/2005/xpath-functions" at "res://www.w3.org/2005/xpath-functions/fn.xqy";
import module namespace json = "http://marklogic.com/xdmp/json" at "res://marklogic.com/xdmp/json.xqy";
declare namespace local = "http://www.w3.org/2005/xquery-local-functions";
declare namespace lock = "http://marklogic.com/xdmp/lock";
import module namespace map = "http://marklogic.com/xdmp/map" at "res://marklogic.com/xdmp/map.xqy";
import module namespace math = "http://marklogic.com/xdmp/math" at "res://marklogic.com/xdmp/math.xqy";
import module namespace prof = "http://marklogic.com/xdmp/profile" at "res://marklogic.com/xdmp/prof.xqy";
declare namespace prop = "http://marklogic.com/xdmp/property";
declare namespace sec = "http://marklogic.com/security";
declare namespace sem = "http://marklogic.com/semantics";
import module namespace spell = "http://marklogic.com/xdmp/spell" at "res://marklogic.com/xdmp/spell.xqy";
import module namespace xdmp = "http://marklogic.com/xdmp" at "res://marklogic.com/xdmp.xqy";
declare namespace xml = "http://www.w3.org/XML/1998/namespace";
declare namespace xqe = "http://marklogic.com/xqe";
declare namespace xqterr = "http://www.w3.org/2005/xqt-error";
import module namespace xs = "http://www.w3.org/2001/XMLSchema" at "res://www.w3.org/2005/xpath-functions/xs.xqy";
declare namespace xsi = "http://www.w3.org/2001/XMLSchema-instance";

()