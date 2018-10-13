xquery version "3.0";
(:~
: BaseX Geo Module functions
:
: @see http://docs.basex.org/wiki/Geo_Module
:)
module namespace geo = "http://expath.org/ns/geo";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";

declare option a:requires-import "basex/7.6; location-uri=(none)";

declare %a:since("basex", "7.6") function geo:dimension($geometry as element(*)) as xs:integer external;
declare %a:since("basex", "7.6") function geo:geometry-type($geometry as element(*)) as xs:QName external;
declare %a:since("basex", "7.6") function geo:srid($geometry as element(*)) as xs:integer external;
declare %a:since("basex", "7.6") function geo:envelope($geometry as element(*)) as element(*) external;
declare %a:since("basex", "7.6") function geo:as-text($geometry as element(*)) as xs:string external;
declare %a:since("basex", "7.6") function geo:as-binary($geometry as element(*)) as xs:base64Binary external;
declare %a:since("basex", "7.6") function geo:is-simple($geometry as element(*)) as xs:boolean external;
declare %a:since("basex", "7.6") function geo:boundary($geometry as element(*)) as element(*)? external;
declare %a:since("basex", "7.6") function geo:num-geometries($geometry as element(*)) as xs:integer external;
declare %a:since("basex", "7.6") function geo:geometry-n($geometry as element(*), $geoNumber as xs:integer) as element(*) external;
declare %a:since("basex", "7.6") function geo:length($geometry as element(*)) as xs:double external;
declare %a:since("basex", "7.6") function geo:num-points($geometry as element(*)) as xs:integer external;
declare %a:since("basex", "7.6") function geo:area($geometry as element(*)) as xs:double external;
declare %a:since("basex", "7.6") function geo:centroid($geometry as element(*)) as element(*) external;
declare %a:since("basex", "7.6") function geo:point-on-surface($geometry as element(*)) as element(*) external;
declare %a:since("basex", "7.6") function geo:equals($geometry1 as element(*), $geometry2 as element(*)) as xs:boolean external;
declare %a:since("basex", "7.6") function geo:disjoint($geometry1 as element(*), $geometry2 as element(*)) as xs:boolean external;
declare %a:since("basex", "7.6") function geo:intersects($geometry1 as element(*), $geometry2 as element(*)) as xs:boolean external;
declare %a:since("basex", "7.6") function geo:touches($geometry1 as element(*), $geometry2 as element(*)) as xs:boolean external;
declare %a:since("basex", "7.6") function geo:crosses($geometry1 as element(*), $geometry2 as element(*)) as xs:boolean external;
declare %a:since("basex", "7.6") function geo:within($geometry1 as element(*), $geometry2 as element(*)) as xs:boolean external;
declare %a:since("basex", "7.6") function geo:contains($geometry1 as element(*), $geometry2 as element(*)) as xs:boolean external;
declare %a:since("basex", "7.6") function geo:overlaps($geometry1 as element(*), $geometry2 as element(*)) as xs:boolean external;
declare %a:since("basex", "7.6") function geo:relate($geometry1 as element(*), $geometry2 as element(*), $intersectionMatrix as xs:string) as xs:boolean external;
declare %a:since("basex", "7.6") function geo:distance($geometry1 as element(*), $geometry2 as element(*)) as xs:double external;
declare %a:since("basex", "7.6") function geo:buffer($geometry as element(*), $distance as xs:double) as element(*) external;
declare %a:since("basex", "7.6") function geo:convex-hull($geometry as element(*)) as element(*) external;
declare %a:since("basex", "7.6") function geo:intersection($geometry1 as element(*), $geometry2 as element(*)) as element(*)? external;
declare %a:since("basex", "7.6") function geo:union($geometry1 as element(*), $geometry2 as element(*)) as element(*) external;
declare %a:since("basex", "7.6") function geo:difference($geometry1 as element(*), $geometry2 as element(*)) as element(*)? external;
declare %a:since("basex", "7.6") function geo:sym-difference($geometry1 as element(*), $geometry2 as element(*)) as element(*)? external;
declare %a:since("basex", "7.6") function geo:x($point as element(*)) as xs:double external;
declare %a:since("basex", "7.6") function geo:y($point as element(*)) as xs:double? external;
declare %a:since("basex", "7.6") function geo:z($point as element(*)) as xs:double? external;
declare %a:since("basex", "7.6") function geo:start-point($line as element(*)) as element(*) external;
declare %a:since("basex", "7.6") function geo:end-point($line as element(*)) as element(*) external;
declare %a:since("basex", "7.6") function geo:is-closed($line as element(*)) as xs:boolean external;
declare %a:since("basex", "7.6") function geo:is-ring($line as element(*)) as xs:boolean external;
declare %a:since("basex", "7.6") function geo:point-n($line as element(*)) as element(*) external;
declare %a:since("basex", "7.6") function geo:exterior-ring($polygon as element(*)) as element(*) external;
declare %a:since("basex", "7.6") function geo:num-interior-ring($polygon as element(*)) as xs:integer external;
declare %a:since("basex", "7.6") function geo:interior-ring-n($polygon as element(*)) as element(*) external;
