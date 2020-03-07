xquery version "1.0"; (: Comment :)

import module namespace json = "http://marklogic.com/xdmp/json"
                     at "/MarkLogic/json/json.xqy";
declare namespace zip = "http://expath.org/ns/zip";

declare decimal-format fmt decimal-separator = ".";
declare option opt "lorem ipsum";

declare variable $test := 1234;

(:~ Documentation <code>Markup</code>
 : @param $a parameter A.
 :)
declare updating function update($a as xs:integer) external;

let $items := (
    "One "" Two &quot; Three",
    child::one, attribute::two, namespace::three,
    data/@value,
    fn:true(),
    (# ext Lorem Ipsum #) { }
)
return <test comment="One "" Two &quot; Three">Lorem ipsum.</test>

~~~
