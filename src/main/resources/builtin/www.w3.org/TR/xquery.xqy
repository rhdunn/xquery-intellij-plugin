xquery version "1.0";

(: https://www.w3.org/TR/xquery/#id-default-namespace :)
declare default element namespace "";
declare default function namespace "http://www.w3.org/2005/xpath-functions"; (: = 'fn:' :)

(: https://www.w3.org/TR/xquery/#id-basics :)
declare namespace xml = "http://www.w3.org/XML/1998/namespace";
import module namespace xs = "http://www.w3.org/2001/XMLSchema" at "res://www.w3.org/2001/XMLSchema.xqy";
declare namespace xsi = "http://www.w3.org/2001/XMLSchema-instance";
import module namespace fn = "http://www.w3.org/2005/xpath-functions" at "res://www.w3.org/2005/xpath-functions.xqy";
declare namespace local = "http://www.w3.org/2005/xquery-local-functions";

()