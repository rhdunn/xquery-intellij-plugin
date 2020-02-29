xquery version "1.0"; (: Comment :)
declare decimal-format fmt decimal-separator = "." ;
(:~ Documentation <code>Markup</code>
 : @param $a parameter A.
 :)
declare updating function update($a as xs:integer) external;

let $_ := (1234, "One "" Two &quot; Three", data/@value)
return <test comment="One "" Two &quot; Three">Lorem ipsum.</test> ~~~