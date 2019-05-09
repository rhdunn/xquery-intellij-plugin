declare function local:square($x) {
    <square>{$n * $n}</square>
};

for $n in 1 to 10
return local:square($n)
