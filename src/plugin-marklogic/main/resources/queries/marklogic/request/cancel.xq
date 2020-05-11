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
declare namespace ss = "http://marklogic.com/xdmp/status/server";

declare option o:implementation "marklogic/6.0";

(: Cancel a running request. :)

declare variable $hostId as xs:unsignedLong external := xdmp:host();
declare variable $server as xs:string external := "TaskServer";
declare variable $queryId as xs:string? external := (); (: from a run/profile request :)
declare variable $requestId as xs:unsignedLong? external := (); (: from a debug request :)

let $serverId := xdmp:server($server)
return if (exists($queryId)) then
    let $ss := xdmp:server-status($hostId, $serverId)/ss:request-statuses/ss:request-status
    let $req := $ss[ss:request-kind = "eval" and starts-with(ss:request-text, $queryId)]
    let $requestId := xs:unsignedLong($req/ss:request-id)
    return if (exists($requestId)) then
        xdmp:request-cancel($hostId, $serverId, $requestId)
    else
        ()
else if (exists($requestId)) then
    xdmp:request-cancel($hostId, $serverId, $requestId)
else
    ()
