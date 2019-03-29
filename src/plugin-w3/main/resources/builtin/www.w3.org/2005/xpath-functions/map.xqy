xquery version "3.1";
(:~
 : XPath and XQuery Functions and Operators: Functions that Operate on Maps
 :
 : @see https://www.w3.org/TR/2017/REC-xpath-functions-31-20170321/
 :
 : This software includes material copied from or derived from the XPath and
 : XQuery Functions and Operators 3.1 specifications. Copyright © 2017 W3C®
 : (MIT, ERCIM, Keio, Beihang).
 :)
module namespace map = "http://www.w3.org/2005/xpath-functions/map";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare option o:requires "xpath-functions/3.1";

(:~
 : Tests whether a supplied map contains an entry for a given key.
 :
 : This function returns true if the map supplied as <code>$map</code> contains
 : an entry with the same key as the supplied value of <code>$key</code>;
 : otherwise it returns false.
 :)
declare %a:since("xpath-functions", "3.1-20170321") function map:contains($map as map(*), $key as xs:anyAtomicType) as xs:boolean external;
(:~
 : Returns a map that contains a single entry (a key-value pair).
 :
 : This function returns a map which contains a single entry. The key of the
 : entry in the new map is <code>$key</code>, and its associated value is
 : <code>$value</code>.
 :
 : <h1>Notes</h1>
 :
 : The function call <code>map:entry(K, V)</code> produces the same result as
 : the expression <code>map{K : V}</code>.
 :
 : This function is intended primarily for use in conjunction with the function
 : <code>map:merge</code>. For example, a map containing seven entries may be
 : constructed like this:
 :
 : <pre><code>map:merge((
 :    map:entry("Su", "Sunday"),
 :    map:entry("Mo", "Monday"),
 :    map:entry("Tu", "Tuesday"),
 :    map:entry("We", "Wednesday"),
 :    map:entry("Th", "Thursday"),
 :    map:entry("Fr", "Friday"),
 :    map:entry("Sa", "Saturday")
 :    ))</code></pre>
 :
 : This function can be used to construct a map with a variable number of entries,
 : for example:
 :
 : <pre><code>map:merge(for $b in //book return map:entry($b/isbn, $b))</code></pre>
 :)
declare %a:since("xpath-functions", "3.1-20170321") function map:entry($key as xs:anyAtomicType, $value as item()*) as map(*) external;
(:~
 : Searches the supplied input sequence and any contained maps and arrays for a
 : map entry with the supplied key, and returns the corresponding values.
 :
 : This function searches the sequence supplied as <code>$input</code> looking
 : for map entries whose key is the same key as <code>$key</code>. The associated
 : value in any such map entry (each being in general a sequence) is returned as
 : a member of the result array.
 :
 : The search processes the <code>$input</code> sequence using the following
 : recursively-defined rules (any equivalent algorithm may be used provided it
 : delivers the same result, respecting those rules that constrain the order
 : of the result):
 :
 : <ol>
 :    <li>To process a sequence, process each of its items in order.</li>
 :    <li>To process an item that is an array, process each of the array's
 :        members in order (each member is, in general, a sequence).</li>
 :    <li>To process an item that is a map, then for each key-value entry
 :        (K, V) in the map (in implementation-dependent order) perform
 :        both of the following steps, in order:
 :        <ol type="a">
 :           <li>If K is the same key as <code>$key</code>, then add V as
 :               a new member to the end of the result array.</li>
 :           <li>Process V (which is, in general, a sequence).</li>
 :        </ol>
 :    </li>
 :    <li>To process an item that is neither a map nor an array, do nothing.
 :        (Such items are ignored).</li>
 : </ol>
 :
 : <h1>Notes</h1>
 :
 : If <code>$input</code> is an empty sequence, map, or array, or if the requested
 : <code>$key</code> is not found, the result will be a zero-length array.
 :)
declare %a:since("xpath-functions", "3.1-20170321") function map:find($input as item()*, $key as xs:anyAtomicType) as array(*) external;
(:~
 : Applies a supplied function to every entry in a map, returning the
 : concatenation of the results.
 :
 : This function takes any map as its <code>$map</code> argument and applies
 : the supplied function to each entry in the map, in implementation-dependent
 : order; the result is the sequence obtained by concatenating the results of
 : these function calls.
 :
 : The function is non-deterministic with respect to ordering. This means that
 : two calls with the same arguments are not guaranteed to process the map
 : entries in the same order.
 :
 : The function supplied as <code>$action</code> takes two arguments. It is
 : called supplying the key of the map entry as the first argument, and the
 : associated value as the second argument.
 :)
declare %a:since("xpath-functions", "3.1-20170321") function map:for-each($map as map(*), $action as function(xs:anyAtomicType, item()*) as item()*) as item()* external;
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
 : Invoking the map as a function item has the same effect as calling
 : <code>math:get</code>: that is, when <code>$map</code> is a map, the
 : expression <code>$map($K)</code> is equivalent to <code>map:get($map, $K)</code>.
 : Similarly, the expression
 : <code>map:get(map:get(map:get($map, 'employee'), 'name'), 'first')</code> can
 : be written as <code>$map('employee')('name')('first')</code>.
 :)
declare %a:since("xpath-functions", "3.1-20170321") function map:get($map as map(*), $key as xs:anyAtomicType) as item()* external;
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
declare %a:since("xpath-functions", "3.1-20170321") function map:keys($map as map(*)) as xs:anyAtomicType* external;
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
 :     <li>If there are duplicate keys, that is, if two or more maps contain
 :         entries having the same key, all but the first of a set of duplicates
 :         are ignored, where the ordering is based on the order of maps in the
 :         <code>$maps</code> argument.</li>
 : </ol>
 :
 : This is equivalent to the function call
 : <code>map:merge($maps, map { duplicates: "use-first" })</code>.
 :)
declare %a:since("xpath-functions", "3.1-20170321") function map:merge($maps as map(*)*) as map(*) external;
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
 :     <li>If there are duplicate keys, that is, if two or more maps contain
 :         entries having the same key, then the way this is handled is
 :         controlled by the <code>$options</code> argument.</li>
 : </ol>
 :
 : The definitive specification is as follows.
 :
 : <ol>
 :    <li>The effect of calling the single-argument function is the same as the
 :        effect of calling the two-argument function with an empty map as the
 :        value of <code>$options</code>.</li>
 :    <li>The <code>$options</code> argument can be used to control the way in
 :        which duplicate keys are handled. The option parameter conventions
 :        apply.</li>
 :    <li>The entries that may appear in the <code>$options</code> map are as follows:
 :        <table>
 :            <thead><tr><th>Key</th><th>Value</th><th>Meaning</th></tr></thead>
 :            <tbody><tr>
 :                <td><code>duplicates</code></td>
 :                <td colspan="2">
 :                    Determines the policy for handling duplicate keys:
 :                    specifically, the action to be taken if two maps in the input
 :                    sequence <code>$maps</code> contain entries with key values
 :                    K1 and K2 where K1 and K2 are the same key.
 :                    <ul>
 :                        <li><strong>Type:</strong> <code>xs:string</code></li>
 :                        <li><strong>Default:</strong> <code>use-first</code></li>
 :                    </ul>
 :                </td>
 :            </tr><tr>
 :                <td></td>
 :                <td><code>reject</code></td>
 :                <td>An error is raised [err:FOJS0003] if duplicate keys are
 :                    encountered.</td>
 :            </tr><tr>
 :                <td></td>
 :                <td><code>use-first</code></td>
 :                <td>If duplicate keys are present, all but the first of a set
 :                    of duplicates are ignored, where the ordering is based on
 :                    the order of maps in the <code>$maps</code> argument.</td>
 :            </tr><tr>
 :                <td></td>
 :                <td><code>use-last</code></td>
 :                <td>If duplicate keys are present, all but the last of a set
 :                    of duplicates are ignored, where the ordering is based on
 :                    the order of maps in the <code>$maps</code> argument.</td>
 :            </tr><tr>
 :                <td></td>
 :                <td><code>use-any</code></td>
 :                <td>If duplicate keys are present, all but one of a set of
 :                    duplicates are ignored, and it is implementation-dependent
 :                    which one is retained.</td>
 :            </tr><tr>
 :                <td></td>
 :                <td><code>combine</code></td>
 :                <td>If duplicate keys are present, the result map includes an
 :                    entry for the key whose associated value is the sequence
 :                    concatenation of all the values associated with the key,
 :                    retaining order based on the order of maps in the
 :                    <code>$maps</code> argument. The key value in the result
 :                    map that corresponds to such a set of duplicates must be
 :                    the same key as each of the duplicates, but it is otherwise
 :                    unconstrained: for example if the duplicate keys are
 :                    <code>xs:byte(1)</code> and <code>xs:short(1)</code>, the
 :                    key in the result could legitimately be
 :                    <code>xs:long(1)</code>.</td>
 :            </tr></tbody>
 :        </table>
 :    </li>
 : </ol>
 :)
declare %a:since("xpath-functions", "3.1-20170321") function map:merge($maps as map(*)*, $options as map(*)) as map(*) external;
(:~
 : Returns a map containing all the contents of the supplied map, but with an
 : additional entry, which replaces any existing entry for the same key.
 :
 : This function returns a map that contains all entries from the supplied
 : <code>$map</code>, with the exception of any entry whose key is the same
 : key as <code>$key</code>, together with a new entry whose key is
 : <code>$key</code> and whose associated value is <code>$value</code>.
 :
 : The effect of the function call <code>map:put($MAP, $KEY, $VALUE)</code> is
 : equivalent to the result of the following steps:
 :
 : <ol>
 :    <li>
 :       <p><code>let $MAP2 := map:remove($MAP, $KEY)</code></p>
 :       <p>This returns a map in which all entries with the same key as
 :          <code>$KEY</code> have been removed.</p>
 :    </li>
 :    <li>Construct and return a map containing:
 :        <ol type="a">
 :           <li>All the entries (key/value pairs) in <code>$MAP2</code>, and</li>
 :           <li>The entry <code>map:entry($KEY, $VALUE)</code></li>
 :        </ol>
 :    </li>
 : </ol>
 :
 : <h1>Notes</h1>
 :
 : There is no requirement that the type of <code>$key</code> and <code>$value</code>
 : be consistent with the types of any existing keys and values in the supplied map.
 :)
declare %a:since("xpath-functions", "3.1-20170321") function map:put($map as map(*), $key as xs:anyAtomicType, $value as item()*) as map(*) external;
(:~
 : Returns a map containing all the entries from a supplied map, except those
 : having a specified key.
 :
 : This function removes the entry from <code>$map</code> whose key is the same
 : key as <code>$key</code>.
 :
 : No failure occurs if an item in <code>$keys</code> does not correspond to any
 : entry in <code>$map</code>; that key value is simply ignored.
 :)
declare %a:since("xpath-functions", "3.1-20170321") function map:remove($map as map(*), $keys as xs:anyAtomicType*) as map(*) external;
(:~
 : Returns the number of entries in the supplied map.
 :
 : This function takes any map as its <code>$map</code> argument and returns
 : the number of entries that are present in the map.
 :)
declare %a:since("xpath-functions", "3.1-20170321") function map:size($map as map(*)) as xs:integer external;
