xquery version "3.0";
(:~
 : Geo Module (EXPath Candidate Module 30 September 2010)
 :
 : @see http://expath.org/spec/geo
 : @see http://docs.basex.org/wiki/Geo_Module
 :)
module namespace geo = "http://expath.org/ns/geo";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare namespace gml = "http://www.opengis.net/gml";

declare option o:requires "expath-geo/1.0-20100930";

declare option o:implements-module "basex/7.6 as expath-geo/1.0-20100930";
declare option o:requires-import "basex/7.6; location-uri=(none)";

declare %a:since("expath-geo", "1.0-20100930") function geo:dimension($geometry (: as [expath-geo/1.0-20100930]node() [basex/7.6]element() :)) (: as [expath-geo/1.0-20100930]xs:integer? [basex/7.6]xs:integer :) external;
declare %a:since("expath-geo", "1.0-20100930") %a:missing("basex", "7.6") function geo:coordinate-dimension($geometry as node()) as xs:integer external;
declare %a:since("expath-geo", "1.0-20100930") function geo:geometry-type($geometry (: as [expath-geo/1.0-20100930]node() [basex/7.6]element() :)) (: as [expath-geo/1.0-20100930]xs:QName? [basex/7.6]xs:QName :) external;
declare %a:since("expath-geo", "1.0-20100930") function geo:srid($geometry (: as [expath-geo/1.0-20100930]node() [basex/7.6]element() :)) (: as [expath-geo/1.0-20100930]xs:anyURI? [basex/7.6]xs:integer :) external;
declare %a:since("expath-geo", "1.0-20100930") function geo:envelope($geometry (: as [expath-geo/1.0-20100930]node() [basex/7.6]element() :)) as element(gml:Envelope) external;
declare %a:since("expath-geo", "1.0-20100930") function geo:as-text($geometry (: as [expath-geo/1.0-20100930]node() [basex/7.6]element() :)) (: as [expath-geo/1.0-20100930]xs:string? [basex/7.6]xs:string :) external;
declare %a:since("expath-geo", "1.0-20100930") function geo:as-binary($geometry (: as [expath-geo/1.0-20100930]node() [basex/7.6]element() :)) (: as [expath-geo/1.0-20100930]xs:base64Binary? [basex/7.6]xs:base64Binary :) external;
declare %a:since("expath-geo", "1.0-20100930") %a:missing("basex", "7.6") function geo:is-empty($geometry as node()) as xs:boolean external;
declare %a:since("expath-geo", "1.0-20100930") function geo:is-simple($geometry (: as [expath-geo/1.0-20100930]node() [basex/7.6]element() :)) as xs:boolean external;
declare %a:since("expath-geo", "1.0-20100930") %a:missing("basex", "7.6") function geo:is-3d($geometry as node()) as xs:boolean external;
declare %a:since("expath-geo", "1.0-20100930") %a:missing("basex", "7.6") function geo:is-measured($geometry as node()) as xs:boolean external;
declare %a:since("expath-geo", "1.0-20100930") function geo:boundary($geometry (: as [expath-geo/1.0-20100930]node() [basex/7.6]element() :)) as element()? external;

declare %a:since("expath-geo", "1.0-20100930") function geo:equals($geometry1 (: as [expath-geo/1.0-20100930]node() [basex/7.6]element() :), $geometry2 (: as [expath-geo/1.0-20100930]node() [basex/7.6]element() :)) as xs:boolean external;
declare %a:since("expath-geo", "1.0-20100930") function geo:disjoint($geometry1 (: as [expath-geo/1.0-20100930]node() [basex/7.6]element() :), $geometry2 (: as [expath-geo/1.0-20100930]node() [basex/7.6]element() :)) as xs:boolean external;
declare %a:since("expath-geo", "1.0-20100930") function geo:intersects($geometry1 (: as [expath-geo/1.0-20100930]node() [basex/7.6]element() :), $geometry2 (: as [expath-geo/1.0-20100930]node() [basex/7.6]element() :)) as xs:boolean external;
declare %a:since("expath-geo", "1.0-20100930") function geo:touches($geometry1 (: as [expath-geo/1.0-20100930]node() [basex/7.6]element() :), $geometry2 (: as [expath-geo/1.0-20100930]node() [basex/7.6]element() :)) as xs:boolean external;
declare %a:since("expath-geo", "1.0-20100930") function geo:crosses($geometry1 (: as [expath-geo/1.0-20100930]node() [basex/7.6]element() :), $geometry2 (: as [expath-geo/1.0-20100930]node() [basex/7.6]element() :)) as xs:boolean external;
declare %a:since("expath-geo", "1.0-20100930") function geo:within($geometry1 (: as [expath-geo/1.0-20100930]node() [basex/7.6]element() :), $geometry2 (: as [expath-geo/1.0-20100930]node() [basex/7.6]element() :)) as xs:boolean external;
declare %a:since("expath-geo", "1.0-20100930") function geo:contains($geometry1 (: as [expath-geo/1.0-20100930]node() [basex/7.6]element() :), $geometry2 (: as [expath-geo/1.0-20100930]node() [basex/7.6]element() :)) as xs:boolean external;
declare %a:since("expath-geo", "1.0-20100930") function geo:overlaps($geometry1 (: as [expath-geo/1.0-20100930]node() [basex/7.6]element() :), $geometry2 (: as [expath-geo/1.0-20100930]node() [basex/7.6]element() :)) as xs:boolean external;
declare %a:since("expath-geo", "1.0-20100930") function geo:relate($geometry1 (: as [expath-geo/1.0-20100930]node() [basex/7.6]element() :), $geometry2 (: as [expath-geo/1.0-20100930]node() [basex/7.6]element() :), $intersectionMatrix as xs:string) as xs:boolean external;

declare %a:since("expath-geo", "1.0-20100930") function geo:distance($geometry1 (: as [expath-geo/1.0-20100930]node() [basex/7.6]element() :), $geometry2 (: as [expath-geo/1.0-20100930]node() [basex/7.6]element() :)) as xs:double external;
declare %a:since("expath-geo", "1.0-20100930") function geo:buffer($geometry (: as [expath-geo/1.0-20100930]node() [basex/7.6]element() :), $distance as xs:double) as element() external;
declare %a:since("expath-geo", "1.0-20100930") function geo:convex-hull($geometry (: as [expath-geo/1.0-20100930]node() [basex/7.6]element() :)) as element() external;
declare %a:since("expath-geo", "1.0-20100930") function geo:intersection($geometry1 (: as [expath-geo/1.0-20100930]node() [basex/7.6]element() :), $geometry2 (: as [expath-geo/1.0-20100930]node() [basex/7.6]element() :)) as element()? external;
declare %a:since("expath-geo", "1.0-20100930") function geo:union($geometry1 (: as [expath-geo/1.0-20100930]node() [basex/7.6]element() :), $geometry2 (: as [expath-geo/1.0-20100930]node() [basex/7.6]element() :)) as element() external;
declare %a:since("expath-geo", "1.0-20100930") function geo:difference($geometry1 (: as [expath-geo/1.0-20100930]node() [basex/7.6]element() :), $geometry2 (: as [expath-geo/1.0-20100930]node() [basex/7.6]element() :)) as element()? external;
declare %a:since("expath-geo", "1.0-20100930") function geo:sym-difference($geometry1 (: as [expath-geo/1.0-20100930]node() [basex/7.6]element() :), $geometry2 (: as [expath-geo/1.0-20100930]node() [basex/7.6]element() :)) as element()? external;

declare %a:since("expath-geo", "1.0-20100930") function geo:num-geometries($geometry (: as [expath-geo/1.0-20100930]node() [basex/7.6]element() :)) as xs:integer external;
declare %a:since("expath-geo", "1.0-20100930") function geo:geometry-n($geometry (: as [expath-geo/1.0-20100930]node() [basex/7.6]element() :), $geoNumber as xs:integer) as element() external;

declare %a:since("expath-geo", "1.0-20100930") function geo:x($point (: as [expath-geo/1.0-20100930]node() [basex/7.6]element() :)) as xs:double external;
declare %a:since("expath-geo", "1.0-20100930") function geo:y($point (: as [expath-geo/1.0-20100930]node() [basex/7.6]element() :)) (: as [expath-geo/1.0-20100930]xs:double [basex/7.6]xs:double? :) external;
declare %a:since("expath-geo", "1.0-20100930") function geo:z($point (: as [expath-geo/1.0-20100930]node() [basex/7.6]element() :)) as xs:double? external;
declare %a:since("expath-geo", "1.0-20100930") %a:missing("basex", "7.6") function geo:m($point as node()) as xs:double? external;

declare %a:since("expath-geo", "1.0-20100930") function geo:length($geometry (: as [expath-geo/1.0-20100930]node() [basex/7.6]element() :)) as xs:double external;
declare %a:since("expath-geo", "1.0-20100930") function geo:start-point($line (: as [expath-geo/1.0-20100930]node() [basex/7.6]element() :)) as element(gml:Point) external;
declare %a:since("expath-geo", "1.0-20100930") function geo:end-point($line (: as [expath-geo/1.0-20100930]node() [basex/7.6]element() :)) as element(gml:Point) external;
declare %a:since("expath-geo", "1.0-20100930") function geo:is-closed($geometry (: as [expath-geo/1.0-20100930]node() [basex/7.6]element() :)) as xs:boolean external;
declare %a:since("expath-geo", "1.0-20100930") function geo:is-ring($line (: as [expath-geo/1.0-20100930]node() [basex/7.6]element() :)) as xs:boolean external;
declare %a:since("expath-geo", "1.0-20100930") function geo:num-points($geometry (: as [expath-geo/1.0-20100930]node() [basex/7.6]element() :)) as xs:integer external;
declare %a:since("expath-geo", "1.0-20100930") function geo:point-n($line (: as [expath-geo/1.0-20100930]node() [basex/7.6]element() :), $n as xs:integer) (: as [expath-geo/1.0-20100930]node() [basex/7.6]element() :) external;

declare %a:since("expath-geo", "1.0-20100930") function geo:area($geometry (: as [expath-geo/1.0-20100930]node() [basex/7.6]element() :)) as xs:double external;
declare %a:since("expath-geo", "1.0-20100930") function geo:centroid($geometry (: as [expath-geo/1.0-20100930]node() [basex/7.6]element() :)) (: as [expath-geo/1.0-20100930]node() [basex/7.6]element() :) external;
declare %a:since("expath-geo", "1.0-20100930") function geo:point-on-surface($geometry (: as [expath-geo/1.0-20100930]node() [basex/7.6]element() :)) (: as [expath-geo/1.0-20100930]node() [basex/7.6]element() :) external;
declare %a:since("expath-geo", "1.0-20100930") %a:missing("basex", "7.6") function geo:num-patches($surface as node()) as xs:integer external;
declare %a:since("expath-geo", "1.0-20100930") %a:missing("basex", "7.6") function geo:patch-n($surface as node(), $n as xs:integer) as node() external;
declare %a:since("expath-geo", "1.0-20100930") %a:missing("basex", "7.6") function geo:bounding-polgons($surface as node(), $polygon as node()) as element(gml:Polygon)* external;

declare %a:since("expath-geo", "1.0-20100930") function geo:exterior-ring($polygon (: as [expath-geo/1.0-20100930]node() [basex/7.6]element() :)) as element(gml:LineString) external;
declare %a:since("expath-geo", "1.0-20100930") function geo:num-interior-ring($polygon (: as [expath-geo/1.0-20100930]node() [basex/7.6]element() :)) as xs:integer external;
declare %a:since("expath-geo", "1.0-20100930") function geo:interior-ring-n($polygon as element(), $n as xs:integer) as element(gml:LineString) external;
