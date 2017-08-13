xquery version "1.0";

(: http://docs.marklogic.com/8.0/guide/xquery/namespaces :)
declare namespace err = "http://www.w3.org/2005/xqt-error";
import module namespace fn = "http://www.w3.org/2005/xpath-functions" at "res://www.w3.org/2005/xpath-functions.xqy";
declare namespace local = "http://www.w3.org/2005/xquery-local-functions";
declare namespace xml = "http://www.w3.org/XML/1998/namespace";
import module namespace xs = "http://www.w3.org/2001/XMLSchema" at "res://www.w3.org/2001/XMLSchema.xqy";
declare namespace xsi = "http://www.w3.org/2001/XMLSchema-instance";

()