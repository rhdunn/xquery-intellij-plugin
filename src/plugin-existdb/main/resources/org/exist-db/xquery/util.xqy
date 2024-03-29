xquery version "3.0";
(:~
 : eXist-db utility module functions
 :
 : @see http://exist-db.org/exist/apps/fundocs/view.html?uri=http://exist-db.org/xquery/util&location=java:org.exist.xquery.functions.util.UtilModule&details=true
 :)
module namespace util = "http://exist-db.org/xquery/util";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare %a:since("exist", "4.4") function util:absolute-resource-id($node-or-path as item()) as xs:integer? external;
declare %a:since("exist", "4.4") function util:base-to-integer($number as item(), $base as xs:integer) as xs:integer external;
declare %a:since("exist", "4.4") function util:base64-decode($string as xs:string?) as xs:string? external;
declare %a:since("exist", "4.4") function util:base64-encode($string as xs:string?) as xs:string? external;
declare %a:since("exist", "5.3") function util:base64-encode-url-safe($string as xs:string?) as xs:string? external;
declare %a:since("exist", "4.4") %a:deprecated("exist", "5.3") function util:base64-encode($string as xs:string?, $trim as xs:boolean) as xs:string? external;
declare %a:since("exist", "4.4") function util:binary-doc($binary-resource as xs:string?) as xs:base64Binary? external;
declare %a:since("exist", "4.4") function util:binary-doc-available($binary-resource as xs:string?) as xs:boolean external;
declare %a:since("exist", "4.6") function util:binary-doc-content-digest($binary-resource as xs:string?, $algorithm as xs:string) as xs:hexBinary? external;
declare %a:since("exist", "4.4") function util:binary-to-string($binary-resource as xs:base64Binary?) as xs:string? external;
declare %a:since("exist", "4.4") function util:binary-to-string($binary-resource as xs:base64Binary?, $encoding as xs:string) as xs:string? external;
declare %a:since("exist", "4.4") function util:call($function-reference as function(*), $parameters as item()*) as item()* external;
declare %a:since("exist", "4.4") %a:deprecated("exist", "4.4") function util:catch($java-classnames as xs:string+, $try-code-blocks as item()*, $catch-code-blocks as item()*) as item()* external;
declare %a:since("exist", "4.4") function util:collations() as xs:string* external;
declare %a:since("exist", "4.4") function util:collection-name($node-or-path-string as item()?) as xs:string? external;
declare %a:since("exist", "4.4") function util:compile($expression as xs:string) as xs:string external;
declare %a:since("exist", "4.4") function util:compile($expression as xs:string, $module-load-path as xs:string) as xs:string external;
declare %a:since("exist", "4.4") function util:compile-query($expression as xs:string, $module-load-path as xs:string?) as element() external;
declare %a:since("exist", "4.4") function util:declare-namespace($prefix as xs:string, $namespace-uri as xs:anyURI) as item() external;
declare %a:since("exist", "4.4") function util:declare-option($name as xs:string, $option as xs:string) as item() external;
declare %a:since("exist", "4.4") function util:declared-variables($namespace-uri as xs:string) as xs:string+ external;
declare %a:since("exist", "4.4") function util:deep-copy($item as item()?) as item()? external;
declare %a:since("exist", "4.4") %a:deprecated("exist", "4.4") %a:see-also("exist", "4.4", "inspect:inspect-function") function util:describe-function($function-name as xs:QName) as node() external;
declare %a:since("exist", "4.4") function util:disable-profiling() as item() external;
declare %a:since("exist", "4.4") function util:doctype($doctype as xs:string+) as node()* external;
declare %a:since("exist", "4.4") function util:document-id($node-or-path as item()) as xs:int? external;
declare %a:since("exist", "4.4") function util:document-name($node-or-path as item()) as xs:string? external;
declare %a:since("exist", "4.4") function util:enable-profiling($verbosity as xs:int) as item() external;
declare %a:since("exist", "4.4") function util:eval($expression as item()) as node()* external;
declare %a:since("exist", "4.4") function util:eval($expression as item(), $cache-flag as xs:boolean) as node()* external;
declare %a:since("exist", "4.4") function util:eval($expression as item(), $cache-flag as xs:boolean, $external-variable as xs:anyType*) as node()* external;
declare %a:since("exist", "5.3") function util:eval($expression as item(), $cache-flag as xs:boolean, $external-variable as xs:anyType*, $pass as xs:boolean) as node()* external;
declare %a:since("exist", "4.6") function util:eval-and-serialize($expression as item(), $default-serialization-params as item()?) as node()* external;
declare %a:since("exist", "4.6") function util:eval-and-serialize($expression as item(), $default-serialization-params as item()?, $starting-loc as xs:double) as node()* external;
declare %a:since("exist", "4.6") function util:eval-and-serialize($expression as item(), $default-serialization-params as item()?, $starting-loc as xs:double, $length as xs:double) as node()* external;
declare %a:since("exist", "4.4") function util:eval-inline($inline-context as item()*, $expression as item()) as item()* external;
declare %a:since("exist", "4.4") function util:eval-inline($inline-context as item()*, $expression as item(), $cache-flag as xs:boolean) as item()* external;
declare %a:since("exist", "4.4") function util:eval-with-context($expression as item(), $context as node()?, $cache-flag as xs:boolean) as node()* external;
declare %a:since("exist", "4.4") function util:eval-with-context($expression as item(), $context as node()?, $cache-flag as xs:boolean, $eval-context-item as item()?) as node()* external;
declare %a:since("exist", "4.4") function util:exclusive-lock($nodes as node()*, $expression as item()*) as item()* external;
declare %a:since("exist", "4.4") function util:expand($node as node()*) as node()* external;
declare %a:since("exist", "4.4") function util:expand($node as node()*, $serialization-parameters as xs:string) as node()* external;
declare %a:since("exist", "4.4") function util:function($name as xs:QName, $arity as xs:integer) as function(*) external;
declare %a:since("exist", "4.4") function util:get-fragment-between($beginning-node as node()?, $ending-node as node()?, $make-fragment as xs:boolean?, $display-root-namespace as xs:boolean?) as xs:string external;
declare %a:since("exist", "4.4") %a:deprecated("exist", "4.4") %a:see-also("exist", "4.4", "inspect:inspect-module-uri") function util:get-module-description($namespace-uri as xs:string) as xs:string external;
declare %a:since("exist", "4.4") function util:get-module-info() as element() external;
declare %a:since("exist", "4.4") %a:deprecated("exist", "4.4") %a:see-also("exist", "4.4", "inspect:inspect-module-uri") function util:get-module-info($namespace-uri as xs:string) as element() external;
declare %a:since("exist", "4.4") function util:get-option($name as xs:string) as xs:string? external;
declare %a:since("exist", "4.4") function util:get-resource-by-absolute-id($absolute-id as xs:integer) as item()? external;
declare %a:since("exist", "4.4") function util:get-sequence-type($sequence-type as xs:anyType*) as xs:string external;
declare %a:since("exist", "4.4") function util:hash($message as item(), $algorithm as xs:string) as xs:string external;
declare %a:since("exist", "4.4") function util:hash($message as item(), $algorithm as xs:string, $base64flag as xs:boolean) as xs:string external;
declare %a:since("exist", "4.4") function util:import-module($module-uri as xs:anyURI, $prefix as xs:string, $location as xs:anyURI) as item() external;
declare %a:since("exist", "4.4") function util:index-key-documents($nodes as node()*, $value as xs:anyAtomicType) as xs:integer? external;
declare %a:since("exist", "4.4") function util:index-key-documents($nodes as node()*, $value as xs:anyAtomicType, $index as xs:string) as xs:integer? external;
declare %a:since("exist", "4.4") function util:index-key-occurrences($nodes as node()*, $value as xs:anyAtomicType) as xs:integer? external;
declare %a:since("exist", "4.4") function util:index-key-occurrences($nodes as node()*, $value as xs:anyAtomicType, $index as xs:string) as xs:integer? external;
declare %a:since("exist", "4.4") function util:index-keys($node-set as node()*, $start-value as xs:anyAtomicType?, $function-reference as function(*), $max-number-returned as xs:int?) as item()* external;
declare %a:since("exist", "4.4") function util:index-keys($node-set as node()*, $start-value as xs:anyAtomicType?, $function-reference as function(*), $max-number-returned as xs:int?, $index as xs:string) as item()* external;
declare %a:since("exist", "4.4") function util:index-keys-by-qname($qname as xs:QName*, $start-value as xs:anyAtomicType?, $function-reference as function(*), $max-number-returned as xs:int?, $index as xs:string) as item()* external;
declare %a:since("exist", "4.4") function util:index-type($set-of-nodes as node()*) as xs:string? external;
declare %a:since("exist", "4.4") function util:inspect-function($function as function(*)) as node() external;
declare %a:since("exist", "4.4") function util:int-to-octal($int as xs:int) as xs:string external;
declare %a:since("exist", "4.4") function util:integer-to-base($number as xs:integer, $base as xs:integer) as xs:string external;
declare %a:since("exist", "4.4") function util:is-binary-doc($binary-resource as xs:string?) as xs:boolean external;
declare %a:since("exist", "4.4") function util:is-module-mapped($namespace-uri as xs:string) as xs:boolean external;
declare %a:since("exist", "4.4") function util:is-module-registered($namespace-uri as xs:string) as xs:boolean external;
declare %a:since("exist", "4.4") function util:line-number() as xs:integer external;
declare %a:since("exist", "4.4") %a:deprecated("exist", "4.4") %a:see-also("exist", "4.4", "inspect:module-functions") function util:list-functions() as function(*)* external;
declare %a:since("exist", "4.4") %a:deprecated("exist", "4.4") %a:see-also("exist", "4.4", "inspect:module-functions") function util:list-functions($namespace-uri as xs:string) as function(*)* external;
declare %a:since("exist", "4.4") function util:log($priority as xs:string, $message as item()*) as item() external;
declare %a:since("exist", "4.4") function util:log-app($priority as xs:string, $logger-name as xs:string, $message as item()*) as item() external;
declare %a:since("exist", "4.4") function util:log-system-err($message as item()*) as item() external;
declare %a:since("exist", "4.4") function util:log-system-out($message as item()*) as item() external;
declare %a:since("exist", "4.4") function util:map-module($namespace-uri as xs:string, $location-uri as xs:string) as item() external;
declare %a:since("exist", "4.4") function util:mapped-modules() as xs:string+ external;
declare %a:since("exist", "4.4") function util:node-by-id($document as node(), $node-id as xs:string) as node() external;
declare %a:since("exist", "4.4") function util:node-id($node as node()) as xs:string external;
declare %a:since("exist", "4.4") function util:node-xpath($node as node()) as xs:string? external;
declare %a:since("exist", "4.4") function util:octal-to-int($octal as xs:string) as xs:int external;
declare %a:since("exist", "4.4") function util:parse($to-be-parsed as xs:string?) as document-node()? external;
declare %a:since("exist", "4.4") function util:parse-html($to-be-parsed as xs:string?) as document-node()? external;
declare %a:since("exist", "4.4") function util:qname-index-lookup($qname as xs:QName, $comparison-value as xs:anyAtomicType) as node()* external;
(:~
 : Returns a random number between 0e0 and 1e0.
 :
 : This function returns a random number such that
 : <code>0e0 <= math-ext:random() < 1e0</code>.
 :)
declare %a:since("exist", "4.4") function util:random() as xs:double external;
declare %a:since("exist", "4.4") function util:random($max as xs:integer) as xs:integer external;
declare %a:since("exist", "4.4") function util:random-ulong() as xs:unsignedLong external;
declare %a:since("exist", "4.4") function util:registered-functions() as xs:string+ external;
declare %a:since("exist", "4.4") function util:registered-functions($namespace-uri as xs:string) as xs:string+ external;
declare %a:since("exist", "4.4") function util:registered-modules() as xs:string+ external;
declare %a:since("exist", "4.4") %a:deprecated("exist", "4.4") %a:see-also("xpath-functions", "3.0-20140408", "fn:serialize") function util:serialize($node-set as node()*, $parameters as item()*) as xs:string? external;
declare %a:since("exist", "4.4") %a:deprecated("exist", "4.4") %a:see-also("xpath-functions", "3.0-20140408", "fn:serialize") function util:serialize($a as node()*, $b as xs:string, $c as xs:string*) as xs:boolean? external;
declare %a:since("exist", "4.4") function util:shared-lock($nodes as node()*, $expression as item()*) as item()* external;
declare %a:since("exist", "4.4") function util:string-to-binary($encoded-string as xs:string?) as xs:base64Binary? external;
declare %a:since("exist", "4.4") function util:string-to-binary($encoded-string as xs:string?, $encoding as xs:string) as xs:base64Binary? external;
declare %a:since("exist", "4.4") function util:system-date() as xs:date external;
declare %a:since("exist", "4.4") function util:system-dateTime() as xs:dateTime external;
declare %a:since("exist", "4.4") function util:system-property($property-name as xs:string) as xs:string? external;
declare %a:since("exist", "4.4") function util:system-time() as xs:time external;
declare %a:since("exist", "4.4") function util:unescape-uri($escaped-string as xs:string, $encoding as xs:string) as xs:string external;
declare %a:since("exist", "4.4") function util:unmap-module($namespace-uri as xs:string) as item() external;
declare %a:since("exist", "4.4") function util:uuid() as xs:string external;
declare %a:since("exist", "4.4") function util:uuid($name as item()) as xs:string external;
declare %a:since("exist", "4.4") function util:wait($interval as xs:integer) as item() external;