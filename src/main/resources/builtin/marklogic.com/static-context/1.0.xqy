xquery version "1.0";
(:~
 : MarkLogic static context for xquery version "1.0".
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

(: MarkLogic 6.0 :)
declare namespace err = "http://www.w3.org/2005/xqt-error";

()