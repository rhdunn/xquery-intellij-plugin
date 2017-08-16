xquery version "1.0-ml";
(:~
 : MarkLogic semantics functions
 :
 : @see https://docs.marklogic.com/xdmp/extension
 : @see https://docs.marklogic.com/sem/semantic-functions
 :)
module namespace sem = "http://marklogic.com/xdmp/semantics";

import module namespace a = "http://reecedunn.co.uk/xquery/annotations" at "res://reecedunn.co.uk/xquery/annotations.xqy";

declare %a:since("marklogic", "7.0") function sem:binding() as sem:binding external;
declare %a:since("marklogic", "7.0") function sem:binding($map as element(json:object)) as sem:binding external;
declare %a:since("marklogic", "8.0") function sem:bnode() as sem:blank external;
declare %a:since("marklogic", "8.0") function sem:bnode($value as xs:anyAtomicType) as sem:blank external;
declare %a:since("marklogic", "7.0") %a:variadic("item()*") function sem:coalesce($parameter as item()*) as item()* external;
declare %a:since("marklogic", "7.0") function sem:datatype($value as xs:anyAtomicType) as sem:iri external;
declare %a:since("marklogic", "8.0") function sem:default-graph-iri() as sem:iri external;
declare %a:since("marklogic", "8.0") function sem:graph-add-permissions($graph as sem:iri, $permissions as element(sec:permission)*) as empty-sequence() external;
declare %a:since("marklogic", "8.0") function sem:graph-get-permissions($graph as sem:iri) as element(sec:permission)* external;
declare %a:since("marklogic", "8.0") function sem:graph-get-permissions($graph as sem:iri, $format as xs:string) as element(sec:permission)* external;
declare %a:since("marklogic", "8.0") function sem:graph-remove-permissions($graph as sem:iri, $permissions as element(sec:permission)*) as empty-sequence() external;
declare %a:since("marklogic", "8.0") function sem:graph-set-permissions($graph as sem:iri, $permissions as element(sec:permission)*) as empty-sequence() external;
declare %a:since("marklogic", "7.0") function sem:if($condition as xs:boolean, $then as item()*, $else as item()*) as item()* external;
declare %a:since("marklogic", "8.0") function sem:in-memory-store($dataset as sem:triple*) as sem:store external;
declare %a:since("marklogic", "7.0") function sem:invalid($string as xs:string, $datatype as sem:iri) as sem:invalid external;
declare %a:since("marklogic", "7.0") function sem:invalid-datatype($val as sem:invalid) as sem:iri external;
declare %a:since("marklogic", "7.0") function sem:isBlank($value as xs:anyAtomicType) as xs:boolean external;
declare %a:since("marklogic", "7.0") function sem:isIRI($value as xs:anyAtomicType) as xs:boolean external;
declare %a:since("marklogic", "7.0") function sem:isLiteral($value as xs:anyAtomicType) as xs:boolean external;
declare %a:since("marklogic", "8.0") function sem:isNumeric($value as xs:anyAtomicType) as xs:boolean external;
declare %a:since("marklogic", "7.0") function sem:lang($value as xs:anyAtomicType) as xs:string external;
declare %a:since("marklogic", "7.0") function sem:langMatches($lang-tag as xs:string, $lang-range as xs:string) as xs:boolean external;
declare %a:since("marklogic", "7.0") function sem:random() as xs:double external;
declare %a:since("marklogic", "8.0") function sem:resolve-iri($relative as xs:string) as sem:iri external;
declare %a:since("marklogic", "8.0") function sem:resolve-iri($relative as xs:string, $base as xs:string) as sem:iri external;
declare %a:since("marklogic", "8.0") function sem:ruleset-store($locations as xs:string*) as sem:store external;
declare %a:since("marklogic", "8.0") function sem:ruleset-store($locations as xs:string*, $store as sem:store*) as sem:store external;
declare %a:since("marklogic", "8.0") function sem:ruleset-store($locations as xs:string*, $store as sem:store*, $options as xs:string*) as sem:store external;
declare %a:since("marklogic", "7.0") function sem:sameTerm($a as xs:anyAtomicType, $b as xs:anyAtomicType) as xs:boolean external;
declare %a:since("marklogic", "7.0") function sem:sparql($sparql as xs:string) as item()* external;
declare %a:since("marklogic", "7.0") function sem:sparql($sparql as xs:string, $bindings as map:map?) as item()* external;
declare %a:since("marklogic", "7.0") function sem:sparql($sparql as xs:string, $bindings as map:map?, $options as xs:string*) as item()* external;
declare %a:since("marklogic", "7.0") function sem:sparql($sparql as xs:string, $bindings as map:map?, $options as xs:string*, $query_or_store (: as [7.0]cts:query? [8.0]sem:store* :)) as item()* external;
declare %a:since("marklogic", "7.0") %a:until("marklogic", "8.0") function sem:sparql($sparql as xs:string, $bindings as map:map?, $options as xs:string*, $query as cts:query?, $forest-ids as xs:unsignedLong*) as item()* external;
declare %a:since("marklogic", "9.0") function sem:sparql-plan($sparql as xs:string) as element() external;
declare %a:since("marklogic", "9.0") function sem:sparql-plan($sparql as xs:string, $bindingNames as xs:string*) as element() external;
declare %a:since("marklogic", "9.0") function sem:sparql-plan($sparql as xs:string, $bindingNames as xs:string*, $options as xs:string*) as element() external;
declare %a:since("marklogic", "7.0") %a:until("marklogic", "8.0") function sem:sparql-triples($sparql as xs:string, $dataset as sem:triple*) as item()* external;
declare %a:since("marklogic", "7.0") %a:until("marklogic", "8.0") function sem:sparql-triples($sparql as xs:string, $dataset as sem:triple*, $bindings as map:map?) as item()* external;
declare %a:since("marklogic", "7.0") %a:until("marklogic", "8.0") function sem:sparql-triples($sparql as xs:string, $dataset as sem:triple*, $bindings as map:map?, $options as xs:string*) as item()* external;
declare %a:since("marklogic", "8.0") function sem:sparql-update($sparql as xs:string) as empty-sequence() external;
declare %a:since("marklogic", "8.0") function sem:sparql-update($sparql as xs:string, $bindings as map:map?) as empty-sequence() external;
declare %a:since("marklogic", "8.0") function sem:sparql-update($sparql as xs:string, $bindings as map:map?, $options as xs:string*) as empty-sequence() external;
declare %a:since("marklogic", "8.0") function sem:sparql-update($sparql as xs:string, $bindings as map:map?, $options as xs:string*, $store as sem:store*) as empty-sequence() external;
declare %a:since("marklogic", "8.0") function sem:sparql-update($sparql as xs:string, $bindings as map:map?, $options as xs:string*, $store as sem:store*, $default-permissions as element(sec:permission)*) as empty-sequence() external;
declare %a:since("marklogic", "8.0") function sem:store() as sem:store external;
declare %a:since("marklogic", "8.0") function sem:store($options as xs:string*) as sem:store external;
declare %a:since("marklogic", "8.0") function sem:store($options as xs:string*, $query as cts:query?) as sem:store external;
declare %a:since("marklogic", "7.0") function sem:timezone-string($value as xs:dateTime) as xs:string external;
declare %a:since("marklogic", "7.0") function sem:triple($subject_or_node as item()) as sem:triple external;
declare %a:since("marklogic", "7.0") function sem:triple($subject_or_node as item(), $predicate as xs:anyAtomicType) as sem:triple external;
declare %a:since("marklogic", "7.0") function sem:triple($subject_or_node as item(), $predicate as xs:anyAtomicType, $object as xs:anyAtomicType) as sem:triple external;
declare %a:since("marklogic", "7.0") function sem:triple($subject_or_node as item(), $predicate as xs:anyAtomicType, $object as xs:anyAtomicType, $graph as sem:iri?) as sem:triple external;
declare %a:since("marklogic", "7.0") function sem:triple-graph($triple as sem:triple) as sem:iri? external;
declare %a:since("marklogic", "7.0") function sem:triple-object($triple as sem:triple) as xs:anyAtomicType external;
declare %a:since("marklogic", "7.0") function sem:triple-predicate($triple as sem:triple) as xs:anyAtomicType external;
declare %a:since("marklogic", "7.0") function sem:triple-subject($triple as sem:triple) as xs:anyAtomicType external;
declare %a:since("marklogic", "7.0") function sem:typed-literal($value as xs:string, $datatype as sem:iri) as xs:anyAtomicType external;
declare %a:since("marklogic", "7.0") function sem:unknown($string as xs:string, $datatype as sem:iri) as sem:unknown external;
declare %a:since("marklogic", "7.0") function sem:unknown-datatype($val as sem:unknown) as sem:iri external;
declare %a:since("marklogic", "7.0") function sem:uuid() as sem:iri external;
declare %a:since("marklogic", "7.0") function sem:uuid-string() as xs:string external;