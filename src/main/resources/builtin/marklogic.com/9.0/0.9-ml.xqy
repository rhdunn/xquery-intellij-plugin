xquery version "1.0";

(: https://www.w3.org/TR/xquery/#id-default-namespace :)
declare default element namespace "";
declare default function namespace "http://www.w3.org/2005/xpath-functions"; (: = 'fn:' :)

(: http://docs.marklogic.com/9.0/guide/xquery/namespaces :)
declare namespace cts = "http://marklogic.com/cts";
declare namespace dav = "DAV:";
declare namespace dbg = "http://marklogic.com/xdmp/dbg";
declare namespace dir = "http://marklogic.com/xdmp/directory";
declare namespace err = "http://www.w3.org/2005/xqt-error";
declare namespace error = "http://marklogic.com/xdmp/error";
declare namespace fn = "http://www.w3.org/2005/xpath-functions";
declare namespace lock = "http://marklogic.com/xdmp/lock";
declare namespace map = "http://marklogic.com/xdmp/map";
declare namespace math = "http://marklogic.com/xdmp/math";
declare namespace prof = "http://marklogic.com/xdmp/profile";
declare namespace prop = "http://marklogic.com/xdmp/property";
declare namespace sec = "http://marklogic.com/security";
declare namespace spell = "http://marklogic.com/xdmp/spell";
declare namespace xdt = "http://www.w3.org/2003/05/xpath-datatypes";
declare namespace xdmp = "http://marklogic.com/xdmp";
declare namespace xml = "http://www.w3.org/XML/1998/namespace";
declare namespace xqe = "http://marklogic.com/xqe";
declare namespace xqterr = "http://www.w3.org/2005/xqt-error";
declare namespace xs = "http://www.w3.org/2001/XMLSchema";

()