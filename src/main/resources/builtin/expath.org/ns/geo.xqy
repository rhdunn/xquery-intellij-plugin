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

declare option o:requires "expath-crypto/1.0-20100930";

declare option o:implements-module "basex/7.6 as expath-geo/1.0-20100930";
declare option o:requires-import "basex/7.6; location-uri=(none)";

declare type boundary-type = (
    %a:since("expath-crypto", "1.0-20100930") for element()* |
    %a:since("basex", "7.6") for element()?
);

declare type geo-type = (
    %a:since("expath-crypto", "1.0-20100930") for node() |
    %a:since("basex", "7.6") for element()
);

declare type opt-binary = (
    %a:since("expath-crypto", "1.0-20100930") for xs:base64Binary? |
    %a:since("basex", "7.6") for xs:base64Binary
);

declare type basex-opt-double = (
    %a:since("expath-crypto", "1.0-20100930") for xs:double |
    %a:since("basex", "7.6") for xs:double?
);

declare type opt-int = (
    %a:since("expath-crypto", "1.0-20100930") for xs:integer? |
    %a:since("basex", "7.6") for xs:integer
);

declare type opt-qname = (
    %a:since("expath-crypto", "1.0-20100930") for xs:QName? |
    %a:since("basex", "7.6") for xs:QName
);

declare type opt-string = (
    %a:since("expath-crypto", "1.0-20100930") for xs:string? |
    %a:since("basex", "7.6") for xs:string
);

declare type srid-type = (
    %a:since("expath-crypto", "1.0-20100930") for xs:anyURI? |
    %a:since("basex", "7.6") for xs:integer
);

declare %a:since("expath-crypto", "1.0-20100930") function geo:dimension($geometry as geo-type) as opt-int external;
declare %a:since("expath-crypto", "1.0-20100930") %a:missing("basex", "7.6") function geo:coordinate-dimension($geometry as node()) as xs:integer external;
declare %a:since("expath-crypto", "1.0-20100930") function geo:geometry-type($geometry as geo-type) as opt-qname external;
declare %a:since("expath-crypto", "1.0-20100930") function geo:srid($geometry as geo-type) as srid-type external;
declare %a:since("expath-crypto", "1.0-20100930") function geo:envelope($geometry as geo-type) as element(gml:Envelope) external;
declare %a:since("expath-crypto", "1.0-20100930") function geo:as-text($geometry as geo-type) as opt-string external;
declare %a:since("expath-crypto", "1.0-20100930") function geo:as-binary($geometry as geo-type) as opt-binary external;
declare %a:since("expath-crypto", "1.0-20100930") %a:missing("basex", "7.6") function geo:is-empty($geometry as node()) as xs:boolean external;
declare %a:since("expath-crypto", "1.0-20100930") function geo:is-simple($geometry as geo-type) as xs:boolean external;
declare %a:since("expath-crypto", "1.0-20100930") %a:missing("basex", "7.6") function geo:is-3d($geometry as node()) as xs:boolean external;
declare %a:since("expath-crypto", "1.0-20100930") %a:missing("basex", "7.6") function geo:is-measured($geometry as node()) as xs:boolean external;
declare %a:since("expath-crypto", "1.0-20100930") function geo:boundary($geometry as geo-type) as element()? external;

declare %a:since("expath-crypto", "1.0-20100930") function geo:equals($geometry1 as geo-type, $geometry2 as geo-type) as xs:boolean external;
declare %a:since("expath-crypto", "1.0-20100930") function geo:disjoint($geometry1 as geo-type, $geometry2 as geo-type) as xs:boolean external;
declare %a:since("expath-crypto", "1.0-20100930") function geo:intersects($geometry1 as geo-type, $geometry2 as geo-type) as xs:boolean external;
declare %a:since("expath-crypto", "1.0-20100930") function geo:touches($geometry1 as geo-type, $geometry2 as geo-type) as xs:boolean external;
declare %a:since("expath-crypto", "1.0-20100930") function geo:crosses($geometry1 as geo-type, $geometry2 as geo-type) as xs:boolean external;
declare %a:since("expath-crypto", "1.0-20100930") function geo:within($geometry1 as geo-type, $geometry2 as geo-type) as xs:boolean external;
declare %a:since("expath-crypto", "1.0-20100930") function geo:contains($geometry1 as geo-type, $geometry2 as geo-type) as xs:boolean external;
declare %a:since("expath-crypto", "1.0-20100930") function geo:overlaps($geometry1 as geo-type, $geometry2 as geo-type) as xs:boolean external;
declare %a:since("expath-crypto", "1.0-20100930") function geo:relate($geometry1 as geo-type, $geometry2 as geo-type, $intersectionMatrix as xs:string) as xs:boolean external;

declare %a:since("expath-crypto", "1.0-20100930") function geo:distance($geometry1 as geo-type, $geometry2 as geo-type) as xs:double external;
declare %a:since("expath-crypto", "1.0-20100930") function geo:buffer($geometry as geo-type, $distance as xs:double) as element() external;
declare %a:since("expath-crypto", "1.0-20100930") function geo:convex-hull($geometry as geo-type) as element() external;
declare %a:since("expath-crypto", "1.0-20100930") function geo:intersection($geometry1 as geo-type, $geometry2 as geo-type) as element()? external;
declare %a:since("expath-crypto", "1.0-20100930") function geo:union($geometry1 as geo-type, $geometry2 as geo-type) as element() external;
declare %a:since("expath-crypto", "1.0-20100930") function geo:difference($geometry1 as geo-type, $geometry2 as geo-type) as element()? external;
declare %a:since("expath-crypto", "1.0-20100930") function geo:sym-difference($geometry1 as geo-type, $geometry2 as geo-type) as element()? external;

declare %a:since("expath-crypto", "1.0-20100930") function geo:num-geometries($geometry as geo-type) as xs:integer external;
declare %a:since("expath-crypto", "1.0-20100930") function geo:geometry-n($geometry as geo-type, $geoNumber as xs:integer) as element() external;

declare %a:since("expath-crypto", "1.0-20100930") function geo:x($point as geo-type) as xs:double external;
declare %a:since("expath-crypto", "1.0-20100930") function geo:y($point as geo-type) as basex-opt-double external;
declare %a:since("expath-crypto", "1.0-20100930") function geo:z($point as geo-type) as xs:double? external;
declare %a:since("expath-crypto", "1.0-20100930") %a:missing("basex", "7.6") function geo:m($point as node()) as xs:double? external;

declare %a:since("expath-crypto", "1.0-20100930") function geo:length($geometry as geo-type) as xs:double external;
declare %a:since("expath-crypto", "1.0-20100930") function geo:start-point($line as geo-type) as element(gml:Point) external;
declare %a:since("expath-crypto", "1.0-20100930") function geo:end-point($line as geo-type) as element(gml:Point) external;
declare %a:since("expath-crypto", "1.0-20100930") function geo:is-closed($geometry as geo-type) as xs:boolean external;
declare %a:since("expath-crypto", "1.0-20100930") function geo:is-ring($line as geo-type) as xs:boolean external;
declare %a:since("expath-crypto", "1.0-20100930") function geo:num-points($geometry as geo-type) as xs:integer external;
declare %a:since("expath-crypto", "1.0-20100930") function geo:point-n($line as geo-type, $n as xs:integer) as geo-type external;

declare %a:since("expath-crypto", "1.0-20100930") function geo:area($geometry as geo-type) as xs:double external;
declare %a:since("expath-crypto", "1.0-20100930") function geo:centroid($geometry as geo-type) as geo-type external;
declare %a:since("expath-crypto", "1.0-20100930") function geo:point-on-surface($geometry as geo-type) as geo-type external;
declare %a:since("expath-crypto", "1.0-20100930") %a:missing("basex", "7.6") function geo:num-patches($surface as node()) as xs:integer external;
declare %a:since("expath-crypto", "1.0-20100930") %a:missing("basex", "7.6") function geo:patch-n($surface as node(), $n as xs:integer) as node() external;
declare %a:since("expath-crypto", "1.0-20100930") %a:missing("basex", "7.6") function geo:bounding-polgons($surface as node(), $polygon as node()) as element(gml:Polygon)* external;

declare %a:since("expath-crypto", "1.0-20100930") function geo:exterior-ring($polygon as geo-type) as element(gml:LineString) external;
declare %a:since("expath-crypto", "1.0-20100930") function geo:num-interior-ring($polygon as geo-type) as xs:integer external;
declare %a:since("expath-crypto", "1.0-20100930") function geo:interior-ring-n($polygon as element(), $n as xs:integer) as element(gml:LineString) external;
