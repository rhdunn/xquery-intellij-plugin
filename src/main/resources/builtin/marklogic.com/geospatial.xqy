xquery version "1.0-ml";
(:~
 : MarkLogic geospatial operations functions
 :
 : @see https://docs.marklogic.com/geo/geospatial-operations
 :)
module namespace geo = "http://marklogic.com/geospatial";

import module namespace a = "http://reecedunn.co.uk/xquery/annotations" at "res://reecedunn.co.uk/xquery/annotations.xqy";

declare %a:since("marklogic", "8.0") function geo:approx-center($region as cts:region) as cts:point external;
declare %a:since("marklogic", "8.0") function geo:approx-center($region as cts:region, $options as xs:string*) as cts:point external;
declare %a:since("marklogic", "8.0") function geo:arc-intersection($p1 as cts:point, $p2 as cts:point, $q1 as cts:point, $q2 as cts:point) as cts:point external;
declare %a:since("marklogic", "8.0") function geo:arc-intersection($p1 as cts:point, $p2 as cts:point, $q1 as cts:point, $q2 as cts:point, $options as xs:string*) as cts:point external;
declare %a:since("marklogic", "8.0") function geo:bearing($p1 as cts:point, $p2 as cts:point) as xs:double external;
declare %a:since("marklogic", "8.0") function geo:bearing($p1 as cts:point, $p2 as cts:point, $options as xs:string*) as xs:double external;
declare %a:since("marklogic", "8.0") function geo:bounding-boxes($region as cts:region) as cts:box* external;
declare %a:since("marklogic", "8.0") function geo:bounding-boxes($region as cts:region, $options as xs:string*) as cts:box* external;
declare %a:since("marklogic", "8.0") function geo:box-intersects($box as cts:box, $region as cts:region*) as xs:boolean external;
declare %a:since("marklogic", "8.0") function geo:box-intersects($box as cts:box, $region as cts:region*, $options as xs:string*) as xs:boolean external;
declare %a:since("marklogic", "8.0") function geo:circle-intersects($circle as cts:circle, $region as cts:region*) as xs:boolean external;
declare %a:since("marklogic", "8.0") function geo:circle-intersects($circle as cts:circle, $region as cts:region*, $options as xs:string*) as xs:boolean external;
declare %a:since("marklogic", "8.0") function geo:circle-polygon($circle as cts:circle, $arc-tolerance as xs:double) as cts:region external;
declare %a:since("marklogic", "8.0") function geo:circle-polygon($circle as cts:circle, $arc-tolerance as xs:double, $options as xs:string*) as cts:region external;
declare %a:since("marklogic", "8.0") function geo:complex-polygon-contains($complex-polygon as cts:complex-polygon, $region as cts:region*) as xs:boolean external;
declare %a:since("marklogic", "8.0") function geo:complex-polygon-contains($complex-polygon as cts:complex-polygon, $region as cts:region*, $options as xs:string*) as xs:boolean external;
declare %a:since("marklogic", "8.0") function geo:complex-polygon-intersects($complex-polygon as cts:complex-polygon, $region as cts:region*) as xs:boolean external;
declare %a:since("marklogic", "8.0") function geo:complex-polygon-intersects($complex-polygon as cts:complex-polygon, $region as cts:region*, $options as xs:string*) as xs:boolean external;
declare %a:since("marklogic", "9.0") function geo:coordinate-system-canonical($name as xs:string) as xs:string external;
declare %a:since("marklogic", "9.0") function geo:coordinate-system-canonical($name as xs:string, $precision as xs:string) as xs:string external;
declare %a:since("marklogic", "8.0") function geo:count-distinct-vertices($region as cts:region) as xs:integer external;
declare %a:since("marklogic", "8.0") function geo:count-distinct-vertices($region as cts:region, $options as xs:string*) as xs:integer external;
declare %a:since("marklogic", "8.0") function geo:count-vertices($region as cts:region) as xs:integer external;
declare %a:since("marklogic", "9.0") function geo:default-coordinate-system() as xs:string external;
declare %a:since("marklogic", "8.0") function geo:destination($p as cts:point, $bearing as xs:double, $distance as xs:double) as cts:point external;
declare %a:since("marklogic", "8.0") function geo:destination($p as cts:point, $bearing as xs:double, $distance as xs:double, $options as xs:string*) as cts:point external;
declare %a:since("marklogic", "8.0") function geo:distance($p1 as cts:point, $p2 as cts:point) as xs:double external;
declare %a:since("marklogic", "8.0") function geo:distance($p1 as cts:point, $p2 as cts:point, $options as xs:string*) as xs:double external;
declare %a:since("marklogic", "8.0") function geo:distance-convert($distance as xs:double, $unit1 as xs:string, $unit2 as xs:string) as xs:double external;
declare %a:since("marklogic", "8.0") function geo:ellipse-polygon($center as cts:point, $semi-major-axis as xs:double, $semi-minor-axis as xs:double, $azimuth as xs:double, $arc-tolerance as xs:double) as cts:region external;
declare %a:since("marklogic", "8.0") function geo:ellipse-polygon($center as cts:point, $semi-major-axis as xs:double, $semi-minor-axis as xs:double, $azimuth as xs:double, $arc-tolerance as xs:double, $options as xs:string*) as cts:region external;
declare %a:since("marklogic", "9.0") function geo:geohash-decode($hash as xs:string) as cts:box external;
declare %a:since("marklogic", "9.0") function geo:geohash-decode-point($hash as xs:string) as cts:point external;
declare %a:since("marklogic", "9.0") function geo:geohash-encode($region as cts:region) as xs:string external;
declare %a:since("marklogic", "9.0") function geo:geohash-encode($region as cts:region, $geohash-precision as xs:integer?) as xs:string external;
declare %a:since("marklogic", "9.0") function geo:geohash-encode($region as cts:region, $geohash-precision as xs:integer?, $options as xs:string*) as xs:string external;
declare %a:since("marklogic", "9.0") function geo:geohash-neighbors($hash as xs:string) as map:map external;
declare %a:since("marklogic", "9.0") function geo:geohash-precision-dimensions($precision as xs:integer) as xs:double+ external;
declare %a:since("marklogic", "9.0") function geo:geohash-subhashes($hash as xs:string, $which as xs:string) as xs:string* external;
declare %a:since("marklogic", "8.0") function geo:interior-point($region as cts:region) as cts:point? external;
declare %a:since("marklogic", "8.0") function geo:interior-point($region as cts:region, $options as xs:string*) as cts:point? external;
declare %a:since("marklogic", "8.0") function geo:parse-wkb($wkb as binary()) as cts:region* external;
declare %a:since("marklogic", "8.0") function geo:parse-wkt($wkt as xs:string*) as cts:region* external;
declare %a:since("marklogic", "8.0") function geo:polygon-contains($polygon as cts:polygon, $region as cts:region*) as xs:boolean external;
declare %a:since("marklogic", "8.0") function geo:polygon-contains($polygon as cts:polygon, $region as cts:region*, $options as xs:string*) as xs:boolean external;
declare %a:since("marklogic", "8.0") function geo:polygon-intersects($polygon as cts:polygon, $region as cts:region*) as xs:boolean external;
declare %a:since("marklogic", "8.0") function geo:polygon-intersects($polygon as cts:polygon, $region as cts:region*, $options as xs:string*) as xs:boolean external;
declare %a:since("marklogic", "8.0") function geo:region-affine-transform($region as cts:region, $transform as map:map*) as cts:region external;
declare %a:since("marklogic", "8.0") function geo:region-affine-transform($region as cts:region, $transform as map:map*, $options as xs:string*) as cts:region external;
declare %a:since("marklogic", "8.0") function geo:region-approximate($region as cts:region, $threshold as xs:double) as cts:region external;
declare %a:since("marklogic", "8.0") function geo:region-approximate($region as cts:region, $threshold as xs:double, $options as xs:string*) as cts:region external;
declare %a:since("marklogic", "8.0") function geo:region-clean($region as cts:region) as cts:region external;
declare %a:since("marklogic", "8.0") function geo:region-clean($region as cts:region, $options as xs:string*) as cts:region external;
declare %a:since("marklogic", "8.0") function geo:region-contains($region as cts:region, $region as cts:region*) as xs:boolean external;
declare %a:since("marklogic", "8.0") function geo:region-contains($region as cts:region, $region as cts:region*, $options as xs:string*) as xs:boolean external;
declare %a:since("marklogic", "8.0") function geo:region-intersects($target as cts:region, $region as cts:region*) as xs:boolean external;
declare %a:since("marklogic", "8.0") function geo:region-intersects($target as cts:region, $region as cts:region*, $options as xs:string*) as xs:boolean external;
declare %a:since("marklogic", "9.0") function geo:region-de9im($region-1 as cts:region, $region-2 as cts:region) as xs:string external;
declare %a:since("marklogic", "9.0") function geo:region-de9im($region-1 as cts:region, $region-2 as cts:region, $options as xs:string*) as xs:string external;
declare %a:since("marklogic", "9.0") function geo:region-relate($region-1 as cts:region, $operation as xs:string, $region-2 as cts:region) as xs:boolean external;
declare %a:since("marklogic", "9.0") function geo:region-relate($region-1 as cts:region, $operation as xs:string, $region-2 as cts:region, $options as xs:string*) as xs:boolean external;
declare %a:since("marklogic", "8.0") function geo:remove-duplicate-vertices($region as cts:region) as cts:region external;
declare %a:since("marklogic", "8.0") function geo:remove-duplicate-vertices($region as cts:region, $options as xs:string*) as cts:region external;
declare %a:since("marklogic", "8.0") function geo:shortest-distance($p1 as cts:point, $region as cts:region+) as xs:double external;
declare %a:since("marklogic", "8.0") function geo:shortest-distance($p1 as cts:point, $region as cts:region+, $options as xs:string*) as xs:double external;
declare %a:since("marklogic", "8.0") function geo:to-wkb($wkb as cts:region*) as xs:string* external;
declare %a:since("marklogic", "8.0") function geo:to-wkt($wkt as cts:region*) as xs:string* external;
declare %a:since("marklogic", "8.0") function geo:validate-wkb($wkb as binary()) as xs:boolean external;
declare %a:since("marklogic", "8.0") function geo:validate-wkt($wkt as xs:string) as xs:boolean external;