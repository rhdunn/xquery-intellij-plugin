(: Comment :)
xquery version "1.0";
(:~ Documentation <code>Markup</code>
 : @param $a parameter A.
 : @return A value.
 :)
declare updating function update($a as <nsprefix>xs</nsprefix>:integer) external;
let $_ := (1234, "One "" Two &quot; Three", value)
return <test comment="One "" Two &quot; Three">Lorem ipsum dolor.</test>
~~~
