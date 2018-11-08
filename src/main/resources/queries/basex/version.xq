(:
 : Copyright (C) 2018 Reece H. Dunn
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
declare option o:implementation "basex/6.0";

(: Return the version string for BaseX. :)

let $info := db:system()
return if ($info instance of element(system)) then (: BaseX >= 7.1 :)
    $info/generalinformation/version/string()
else (: BaseX == 7.0 :)
    for $line in fn:tokenize($info, "(\r\n?|\n)")
    where fn:starts-with($line, " Version: ")
    return fn:substring-after($line, " Version: ")
