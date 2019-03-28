xquery version "1.0-ml";
(:~
 : MarkLogic map functions
 :
 : @see https://docs.marklogic.com/map
 :
 : This documentation includes material copied from or derived from the XPath and
 : XQuery Functions and Operators 3.1 specifications. Copyright © 2017 W3C®
 : (MIT, ERCIM, Keio, Beihang).
 :)
module namespace map = "http://marklogic.com/xdmp/map";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare option o:requires "marklogic/5.0";

(:~
 : Removes all the entries in the supplied map.
 :
 : This function updates the map supplied as <code>$map</code> so that each
 : entry is removed from the map.
 :
 : The effect of the function call <code>map:clear($MAP)</code> can be described
 : more formally as the result of the expression below:
 :
 : <pre><code>map:keys($MAP) ! map:delete($MAP, .)</code></pre>
 :)
declare %a:since("marklogic", "5.0") function map:clear($map as map:map) as empty-sequence() external;
(:~
 : Tests whether a supplied map contains an entry for a given key.
 :
 : This function returns true if the map supplied as <code>$map</code> contains
 : an entry with the same key as the supplied value of <code>$key</code>;
 : otherwise it returns false.
 :)
declare %a:since("marklogic", "6.0") function map:contains($map as map:map, $key as xs:string) as xs:boolean external;
(:~
 : Returns the number of entries in the supplied map.
 :
 : This function takes any map as its <code>$map</code> argument and returns
 : the number of entries that are present in the map.
 :)
declare %a:since("marklogic", "5.0") function map:count($map as map:map) as xs:unsignedInt external;
(:~
 : Removes the entry with the specified key from the supplied map.
 :
 : This function removes the entry from <code>$map</code> whose key is the same
 : key as <code>$key</code>.
 :)
declare %a:since("marklogic", "5.0") function map:delete($map as map:map, $key as xs:string) as empty-sequence() external;
(:~
 : Returns a map that contains a single entry (a key-value pair).
 :
 : This function returns a map which contains a single entry. The key of the
 : entry in the new map is <code>$key</code>, and its associated value is
 : <code>$value</code>.
 :
 : <h1>Notes</h1>
 :
 : This function is intended primarily for use in conjunction with the function
 : <code>map:new</code>. For example, a map containing seven entries may be
 : constructed like this:
 :
 : <pre><code>map:new((
 :    map:entry("Su", "Sunday"),
 :    map:entry("Mo", "Monday"),
 :    map:entry("Tu", "Tuesday"),
 :    map:entry("We", "Wednesday"),
 :    map:entry("Th", "Thursday"),
 :    map:entry("Fr", "Friday"),
 :    map:entry("Sa", "Saturday")
 :    ))</code></pre>
 :
 : The <code>map:new</code> function can be used to construct a map with a
 : variable number of entries, for example:
 :
 : <pre><code>map:new(for $b in //book return map:entry($b/isbn, $b))</code></pre>
 :)
declare %a:since("marklogic", "7.0") function map:entry($key as xs:string, $value as item()*) as map:map external;
(:~
 : Returns the value associated with a supplied key in a given map.
 :
 : This function attempts to find an entry within the map supplied as
 : <code>$map</code> that has the same key as the supplied value of
 : <code>$key</code>. If there is such an entry, it returns the associated value;
 : otherwise it returns an empty sequence.
 :
 : <h1>Notes</h1>
 :
 : A return value of <code>()</code> from <code>map:get</code> could indicate that
 : the key is present in the map with an associated value of <code>()</code>, or it
 : could indicate that the key is not present in the map. The two cases can be
 : distinguished by calling <code>map:contains</code>.
 :
 : MarkLogic does not support the XQuery 3.1 ability to invoke the map as a function
 : item. Attempting this generates a dynamic error XDMP-NOTFUNC [err:XPTY0004].
 :)
declare %a:since("marklogic", "5.0") function map:get($map as map:map, $key as xs:string) as item()* external;
(:~
 : Returns a sequence containing all the keys present in a map.
 :
 : This function takes any map as its <code>$map</code> argument and returns
 : the keys that are present in the map as a sequence of string values, in
 : implementation-dependent order.
 :
 : This function is non-deterministic with respect to ordering. This means that
 : two calls with the same argument are not guaranteed to produce the results
 : in the same order.
 :
 : <h1>Notes</h1>
 :
 : The number of items in the result will be the same as the number of entries
 : in the map, and the result sequence will contain no duplicate values.
 :)
declare %a:since("marklogic", "5.0") function map:keys($map as map:map) as xs:string* external;
(:~
 : Returns a map that contains no entries.
 :
 : This function returns a map which contains no entries.
 :
 : <h1>Notes</h1>
 :
 : The function call <code>map:map()</code> produces the same result as the
 : expression <code>map:map(<map xmlns="http://marklogic.com/xdmp/map"/>)</code>
 : and <code>map:new(())</code>.
 :)
declare %a:since("marklogic", "5.0") function map:map() as map:map external;
(:~
 : Returns a map that contains the entries from the XML serialized map data.
 :
 : This function returns a map with the entries in the <code>$map</code> element.
 :)
declare %a:since("marklogic", "5.0") function map:map($map as element(map:map)) as map:map external;
(:~
 : Returns a map that contains no entries.
 :
 : This function returns a map which contains no entries.
 :
 : <h1>Notes</h1>
 :
 : The function call <code>map:new()</code> produces the same result as the
 : expression <code>map:map(<map xmlns="http://marklogic.com/xdmp/map"/>)</code>
 : and <code>map:new(())</code>.
 :)
declare %a:since("marklogic", "7.0") function map:new() as map:map external;
(:~
 : Returns a map that combines the entries from a number of existing maps.
 :
 : This function returns a map that is formed by combining the contents of the
 : maps supplied in the <code>$maps</code> argument.
 :
 : The supplied maps are combined as follows:
 : <ol>
 :     <li>There is one entry in the returned map for each distinct key present
 :         in the union of the input maps, where two keys are distinct if they
 :         are not the same key.</li>
 :     <li>If duplicate keys are present, all but the last of a set of duplicates
 :         are ignored, where the ordering is based on the order of maps in the
 :         <code>$maps</code> argument.</li>
 : </ol>
 :)
declare %a:since("marklogic", "7.0") function map:new($maps as map:map*) as map:map external;
(:~
 : Updates the contents of the supplied map with an additional entry, which
 : replaces any existing entry for the same key.
 :
 : This function modifies the supplied <code>$map</code>, with a new entry whose
 : key is <code>$key</code> and whose associated value is <code>$value</code>.
 :
 : If the <code>$map</code> contains an entry whose key is the same key as
 : <code>$key</code>, that entry is replaced with the new value.
 :
 : If the <code>$value</code> is <code>()</code> then the key is removed from
 : the <code>$map</code>. This is equivalent to calling
 : <code>map:delete($map, $key)</code>.
 :)
declare %a:since("marklogic", "5.0") function map:put($map as map:map, $key as xs:string, $value as item()*) as empty-sequence() external;
(:~
 : Configure the supplied map to be passed by reference or value in JavaScript.
 :
 : If <code>$byref</code> is <code>true()</code> then the <code>$map</code> is
 : passed by reference when it is passed to JavaScript.
 :
 : If <code>$byref</code> is <code>false()</code> then the <code>$map</code> is
 : passed by value when it is passed to JavaScript. This is the default behaviour
 : for the <code>$map</code>.
 :)
declare %a:since("marklogic", "9.0") function map:set-javascript-by-ref($map as map:map, $byref as xs:boolean) as empty-sequence() external;
(:~
 : Updates and returns the supplied map with an additional entry, which
 : replaces any existing entry for the same key.
 :
 : This function modifies the supplied <code>$map</code>, with a new entry whose
 : key is <code>$key</code> and whose associated value is <code>$value</code>.
 :
 : If the <code>$map</code> contains an entry whose key is the same key as
 : <code>$key</code>, that entry is replaced with the new value.
 :
 : If the <code>$value</code> is <code>()</code> then the key is removed from
 : the <code>$map</code>. This is equivalent to calling
 : <code>map:delete($map, $key)</code>.
 :
 : The updated <code>$map</code> is returned from the <code>map:with</code>
 : function call.
 :
 : <h1>Notes</h1>
 :
 : The <code>map:with</code> function calls can be chained when using the
 : arrow operator syntax. For example:
 :
 : <pre><code>map:new()
 :    => map:with("Su", "Sunday")
 :    => map:with("Mo", "Monday")
 :    => map:with("Tu", "Tuesday")
 :    => map:with("We", "Wednesday")
 :    => map:with("Th", "Thursday")
 :    => map:with("Fr", "Friday")
 :    => map:with("Sa", "Saturday")</code></pre>
 :
 : The <code>map:with</code> function is otherwise equivalent to using the
 : <code>map:put</code> function.
 :)
declare %a:since("marklogic", "9.0") function map:with($map as map:map, $key as xs:string, $value as item()*) as map:map external;
