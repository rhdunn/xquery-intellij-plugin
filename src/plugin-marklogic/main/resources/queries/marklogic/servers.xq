(:
 : Copyright (C) 2019, 2021 Reece H. Dunn
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

declare variable $database as xs:string external := "emerald-test";

(: Return the servers on the MarkLogic server. :)

let $database :=
    try {
        if (string-length($database) eq 0) then
            ()
        else
            xdmp:database($database)
    } catch ($e) {
        ()
    }
let $servers :=
    for $server in xdmp:servers()
    where empty($database) or $database eq xdmp:server-database($server)
    return $server
return if (exists($servers)) then
    for $server in $servers
    let $name := xdmp:server-name($server)
    order by $name
    return $name
else
    "App-Services"
