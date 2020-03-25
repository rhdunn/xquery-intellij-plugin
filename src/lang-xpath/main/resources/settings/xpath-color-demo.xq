(: Comment :)
let $items := (1, 2, 3)
return lorem[fn:position() = 2 and fn:true()]//self::ipsum[
    @value = "a""b" and
    function ($a as xs:integer) { $a }(1) and
    child::one and attribute::two and namespace::three and
    . contains text (# ext Lorem ipsum #) { }
]

\^
