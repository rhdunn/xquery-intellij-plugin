(:
 : Copyright (C) 2018, 2020 Reece H. Dunn
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

(: Return the version string for eXist-db. :)

let $get-product-name := fn:function-lookup(xs:QName("system:get-product-name"), 0)
return if (exists($get-product-name)) then $get-product-name() else "eXist-db"
,
system:get-version()
