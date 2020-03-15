(:
 : Copyright (C) 2020 Reece H. Dunn
 :
 : Licensed under the Apache License, Version 2.0 (the "License");
 : you may not use this file except in compliance with the License.
 : You may obtain a copy of the License at
 :
 : http://www.apache.org/licenses/LICENSE-2.0
 :
 : Unless required by applicable law or agreed to in writing, software
 : distributed under the License is distributed on an "AS IS" BASIS,
 : WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 : See the License for the specific language governing permissions and
 : limitations under the License.
 :)
xquery version "3.1";

declare namespace apidoc = "http://marklogic.com/xdmp/apidoc";

declare variable $apidoc as element(apidoc:apidoc) :=
    typeswitch (.)
    case xs:string return fn:parse-xml(.)/element()
    case document-node() return ./element()
    default return .;

(: functions --------------------------------------------------------------- :)

declare variable $functions as element(apidoc:function)* := $apidoc//apidoc:function;

declare function local:function-name($function as element(apidoc:function)) as xs:string {
    ``[`{$function/@lib}`:`{$function/@name}`]``
};

declare function local:function-parameters($function as element(apidoc:function)) as xs:string* {
    $function/apidoc:params/apidoc:param/@name
};

declare function local:function-parameter-type($function as element(apidoc:function), $name as xs:string) as xs:string {
    $function/apidoc:params/apidoc:param[@name = $name]/@type
};

declare function local:function-return-type($function as element(apidoc:function)) as xs:string {
    $function/apidoc:return
};

declare function local:function-xquery-signatures($function as element(apidoc:function)) as xs:string* {
    let $name := local:function-name($function)
    let $params :=
        for $name in local:function-parameters($function)
        let $type := local:function-parameter-type($function, $name)
        return ``[$`{$name}` as `{$type}`]``
    let $type := local:function-return-type($function)
    return ``[declare function `{$name}`(`{string-join($params, ", ")}`) as `{$type}` external;]``
};

(: query ------------------------------------------------------------------- :)

for $function in $functions
order by $function/@lib, $function/@name
return local:function-xquery-signatures($function)
