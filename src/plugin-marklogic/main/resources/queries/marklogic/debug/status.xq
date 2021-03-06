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

(: Query the state of a request. :)

declare variable $requestId as xs:unsignedLong external;

try {
    (dbg:status($requestId)/dbg:request/dbg:request-status/text(), "none")[1]
} catch ($e) {
    (: The DBG-REQUESTRECORD "Request record not found" error may be raised in
     : the case when, e.g.:
     : 1.  the user has entered a breakpoint at the end of the query body;
     : 2.  the user then resumed the program.
     :
     : In this case, the run loop waits for 100ms before requessting the status,
     : by which time the request has stopped and has been cleaned up.
     :)
    "none"
}
