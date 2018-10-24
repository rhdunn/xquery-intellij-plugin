(: Return the version string for BaseX.
 :)
xquery version "3.0";
declare namespace o = "http://reecedunn.co.uk/xquery/options";
declare option o:implementation "basex/6.0";

let $info := db:system()
return if ($info instance of element(system)) then (: BaseX >= 7.1 :)
    $info/generalinformation/version/string()
else (: BaseX == 7.0 :)
    for $line in fn:tokenize($info, "(\r\n?|\n)")
    where fn:starts-with($line, " Version: ")
    return fn:substring-after($line, " Version: ")
