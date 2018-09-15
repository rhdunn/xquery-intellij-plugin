xquery version "1.0";

(: https://www.w3.org/TR/xquery/#id-default-namespace :)
declare default element namespace "";
declare default function namespace "http://www.w3.org/2005/xpath-functions"; (: = 'fn:' :)

(: http://docs.marklogic.com/7.0/guide/xquery/namespaces :)
import module namespace cts = "http://marklogic.com/cts" at "res://marklogic.com/cts.xqy";
declare namespace dav = "DAV:";
import module namespace dbg = "http://marklogic.com/xdmp/dbg" at "res://marklogic.com/xdmp/dbg.xqy";
declare namespace dir = "http://marklogic.com/xdmp/directory";
declare namespace err = "http://www.w3.org/2005/xqt-error";
declare namespace error = "http://marklogic.com/xdmp/error";
import module namespace fn = "http://www.w3.org/2005/xpath-functions" at "res://www.w3.org/2005/xpath-functions.xqy";
declare namespace lock = "http://marklogic.com/xdmp/lock";
import module namespace map = "http://marklogic.com/xdmp/map" at "res://marklogic.com/xdmp/map.xqy";
import module namespace math = "http://marklogic.com/xdmp/math" at "res://marklogic.com/xdmp/math.xqy";
import module namespace prof = "http://marklogic.com/xdmp/profile" at "res://marklogic.com/xdmp/prof.xqy";
declare namespace prop = "http://marklogic.com/xdmp/property";
declare namespace sec = "http://marklogic.com/security";
import module namespace spell = "http://marklogic.com/xdmp/spell" at "res://marklogic.com/xdmp/spell.xqy";
declare namespace xdt = "http://www.w3.org/2003/05/xpath-datatypes";
import module namespace xdmp = "http://marklogic.com/xdmp" at "res://marklogic.com/xdmp.xqy";
declare namespace xml = "http://www.w3.org/XML/1998/namespace";
declare namespace xqe = "http://marklogic.com/xqe";
declare namespace xqterr = "http://www.w3.org/2005/xqt-error";
import module namespace xs = "http://www.w3.org/2001/XMLSchema" at "res://www.w3.org/2001/XMLSchema.xqy";

()