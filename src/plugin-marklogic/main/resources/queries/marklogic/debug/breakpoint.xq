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
xquery version "1.0-ml";
declare namespace o = "http://reecedunn.co.uk/xquery/options";
declare option o:implementation "marklogic/6.0";

(: Add/remove a breakpoint for a request. :)

declare variable $requestId as xs:unsignedLong external;
declare variable $register as xs:boolean external;
declare variable $exprUri as xs:string external;
declare variable $exprLine as xs:nonNegativeInteger external;
declare variable $exprColumn as xs:nonNegativeInteger external;

let $status := dbg:status($requestId)/dbg:request/dbg:request-status
let $_ := if ($status = "running") then dbg:break($requestId) else ()

let $result := try {
    let $expr :=
        for $expressionId in dbg:line($requestId, $exprUri, $exprLine)
        let $expr := dbg:expr($requestId, $expressionId)
        where $expr/dbg:line eq $exprLine and $expr/dbg:column eq $exprColumn
        return $expr
    return if (exists($expr)) then
        let $_ :=
            if ($register) then
                dbg:break($requestId, $expr[1]/dbg:expr-id)
            else
                dbg:clear($requestId, $expr[1]/dbg:expr-id)
        return true()
    else
        false()
} catch ($e) {
    (: Can throw a "Module not found" error. :)
    false()
}

let $_ := if ($status = "running") then dbg:continue($requestId) else ()
return $result
