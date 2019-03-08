for $x in $y
let $a := $x
where $a gt 0
order by $a
for $v in $w
let $b := $v
where $v gt 0
order by $b
return $a