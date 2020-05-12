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
xquery version "3.0";
declare namespace o = "http://reecedunn.co.uk/xquery/options";
declare option o:implementation "exist-db/3.0";

(:~ Run a query on an eXist-db server.
 :
 : @param $username The database user to run the query as.
 : @param $password The password of the database user.
 : @param $query The query script to evaluate.
 :
 : This script has additional logic to map the semantics of the REST API to
 : the semantics of the API implemented in the xquery-intellij-plugin to support
 : the Run/Debug Configurations of the IntelliJ IDEs. Specifically:
 :
 : 1. The user passed to the HTTP authentication is not logged in when the
 :    given query is run.
 :)

declare variable $username as xs:string external;
declare variable $password as xs:string external;
declare variable $query as xs:string external;

if (xmldb:login("", $username, $password, false())) then
    util:eval($query)
else
    response:set-status-code(403)
