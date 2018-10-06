xquery version "1.0";

(: https://www.w3.org/TR/xquery/#id-default-namespace :)
declare default element namespace "";
declare default function namespace "http://www.w3.org/2005/xpath-functions"; (: = 'fn:' :)

(: http://docs.marklogic.com/7.0/guide/xquery/namespaces :)
declare namespace err = "http://www.w3.org/2005/xqt-error";
declare namespace fn = "http://www.w3.org/2005/xpath-functions";
declare namespace local = "http://www.w3.org/2005/xquery-local-functions";
declare namespace xml = "http://www.w3.org/XML/1998/namespace";
declare namespace xs = "http://www.w3.org/2001/XMLSchema";
declare namespace xsi = "http://www.w3.org/2001/XMLSchema-instance";

()