xquery version "3.1";
declare namespace default = "http://www.example.co.uk";
declare %private function a($c as tuple(x:xs:double, y: xs:double)) {
    script//character contains text "JULIET" using fuzzy,
};
(:~ Lorem ipsum dolor.
 : @param $document A <code>document</code> element. :)
declare %default:test function b($document as element(document)) {
    for $x in $document/node/*:*/node()/element let { . }
    order by if ($x<item) then element "test" { $x/property::y }
             else array-node {$x/z} ?? 1 !! binary {}
    return if(let()) ``[x=`{$x/default :w/text()}`]``
};

<a><!-- TODO: Add some text here. --></a>,
let $test as (xs:int+) = binary {} (: TODO: Add a return statement. :)