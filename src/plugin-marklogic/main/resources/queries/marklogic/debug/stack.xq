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

(: Get the current call stack for a request. :)

declare variable $requestId as xs:unsignedLong external;

if (dbg:status($requestId)/dbg:request/dbg:request-status = "stopped") then
    dbg:stack($requestId)
else
    (: Stack frames are only available for stopped queries.
     :
     : The request may not be in the stopped state if the following happens:
     : 1.  the user pauses the request;
     : 2.  IntelliJ initiates a request for the stack frame on a separate thread;
     : 3.  the user resumes the request before the stack frame has been queried;
     : 4.  the stack frame is queried on a resumed request.
     :)
    <dbg:stack/>
