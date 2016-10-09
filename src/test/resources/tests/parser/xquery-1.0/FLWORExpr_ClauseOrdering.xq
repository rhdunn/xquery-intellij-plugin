for $x in $y
let $a := $x
for $v in $w
let $b := $v
where $a gt $b
order by $a, $b
return $x