xquery version "1.0";

(: https://www.w3.org/TR/xquery-31/#id-basics :)
declare namespace xml = "http://www.w3.org/XML/1998/namespace";
import module namespace xs = "http://www.w3.org/2001/XMLSchema" at "res://www.w3.org/2005/xpath-functions/xs.xqy";
declare namespace xsi = "http://www.w3.org/2001/XMLSchema-instance";
import module namespace fn = "http://www.w3.org/2005/xpath-functions" at "res://www.w3.org/2005/xpath-functions/fn.xqy";
import module namespace map = "http://www.w3.org/2005/xpath-functions/map" at "res://www.w3.org/2005/xpath-functions/map.xqy";
import module namespace array = "http://www.w3.org/2005/xpath-functions/array" at "res://www.w3.org/2005/xpath-functions/array.xqy";
import module namespace math = "http://www.w3.org/2005/xpath-functions/math" at "res://www.w3.org/2005/xpath-functions/math.xqy";
declare namespace local = "http://www.w3.org/2005/xquery-local-functions";

()