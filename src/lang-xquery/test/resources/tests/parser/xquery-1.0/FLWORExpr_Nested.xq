for $x in $y
let $a := $x
where $a gt 0
order by $a
return
for $v in $w
let $b := $v
where $b gt 0
order by $b
return $x