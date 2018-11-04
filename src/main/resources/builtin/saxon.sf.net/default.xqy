xquery version "1.0";
(:~
 : Saxon extension functions
 : @see http://www.saxonica.com/html/documentation/functions/saxon
 :
 :)
module namespace saxon = "http://saxon.sf.net/";

declare namespace jt = "http://saxon.sf.net/java-type";
declare namespace xsl = "http://www.w3.org/1999/XSL/Transform";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare %a:since("saxon/pe", "9.2") %a:since("saxon/ee", "9.2") function saxon:adjust-to-civil-time($in as xs:dateTime, $tz as xs:string) as xs:dateTime? external; (: reqs PE or EE :)
declare %a:since("saxon/pe", "9.8") function saxon:array-member($value as item()*) as jt:com.saxonica.functions.extfn.ArrayMemberValue external;
declare %a:since("saxon/pe", "8.1") %a:since("saxon/ee", "8.1") function saxon:base64Binary-to-octets($input as xs:base64Binary) as xs:integer* external;
declare %a:since("saxon/pe", "8.5") %a:since("saxon/ee", "8.5") function saxon:base64Binary-to-string($input as xs:base64Binary?, $encoding as xs:string) as xs:string external;
declare %a:since("saxon/pe", "9.1") %a:since("saxon/ee", "9.1") function saxon:column-number() as xs:integer external;
declare %a:since("saxon/pe", "9.1") %a:since("saxon/ee", "9.1") function saxon:column-number($node as node()) as xs:integer external;
declare %a:since("saxon/pe", "9.0") %a:since("saxon/ee", "9.0") function saxon:compile-query($query as xs:string) as jt:net.sf.saxon.query.XQueryExpression external;
declare %a:since("saxon/pe", "8.5") %a:since("saxon/ee", "8.5") function saxon:compile-stylesheet($stylesheet as document-node()) as jt.net.sf.saxon.PreparedStylesheet external;
declare %a:since("saxon/pe", "9.3") %a:since("saxon/ee", "9.3") function saxon:current-mode-name() as xs:QName? external; (: @TODO the documentation claims that this was designed for XSLT :)
declare %a:since("saxon/pe", "8.0") %a:since("saxon/ee", "8.0") function saxon:decimal-divide($arg1 as xs:decimal?, $arg2 as xs:decimal, $precision as xs:integer) as xs:decimal? external;
declare %a:since("saxon/pe", "8.6.1") %a:since("saxon/ee", "8.6.1") function saxon:deep-equal($arg1 as item()*, $arg2 as item()*, $collation as xs:string?, $flags as xs:string) as xs:boolean external;
declare %a:since("saxon/pe", "8.0") %a:since("saxon/ee", "8.0") function saxon:discard-document($doc as document-node()) as document-node() external;
declare %a:since("saxon/pe", "7.2") %a:since("saxon/ee", "7.2") function saxon:eval($stored-expression as jt:net.sf.saxon.functions.Evaluate-PreparedExpression, $param1 as item()*, $param2 as item()*, $param3 as item()*) as xs:double external;
declare %a:since("saxon/pe", "7.2") %a:since("saxon/ee", "7.2") function saxon:evaluate($xpath as xs:string, $param1 as item()*, $param2 as item()*, $param3 as item()*) as item()* external;
declare %a:since("saxon/pe", "8.2") %a:since("saxon/ee", "8.2") function saxon:evaluate-node($node as node()) as item()* external;
declare %a:since("saxon/pe", "7.2") %a:since("saxon/ee", "7.2") function saxon:expression($string as xs:string) as jt:net.sf.saxon.functions.Evaluate-PreparedExpression external;
declare %a:since("saxon/pe", "7.2") %a:since("saxon/ee", "7.2") function saxon:expression($string as xs:string, $ns as element()) as jt:net.sf.saxon.functions.Evaluate-PreparedExpression external;
declare %a:since("saxon/pe", "8.0") %a:since("saxon/ee", "8.0") function saxon:get-pseudo-attribute($att as xs:string) as xs:string? external;
declare %a:since("saxon/pe", "8.0") %a:since("saxon/ee", "8.0") function saxon:has-same-nodes($arg1 as node()*, $arg2 as node()*) as xs:boolean external;
declare %a:since("saxon/pe", "8.1") %a:since("saxon/ee", "8.1") function saxon:hexBinary-to-octets($input as xs:hexBinary) as xs:integer* external;
declare %a:since("saxon/pe", "8.5") %a:since("saxon/ee", "8.5") function saxon:hexBinary-to-string($input as xs:hexBinary, $encoding as xs:string) as xs:string external;
declare %a:since("saxon/pe", "8.1") %a:since("saxon/ee", "8.1") function saxon:highest($input as item()*) as item()* external;
declare %a:since("saxon/pe", "8.1") %a:since("saxon/ee", "8.1") function saxon:highest($input as item()*, $key as function(*) (: $key [8.1] as jt:net.sf.saxon.functions.Evaluate-PreparedExpression [9.2] as function() [9.5] as function(*) :)) as item()* external;
declare %a:since("saxon/pe", "9.1") %a:since("saxon/ee", "9.1") function saxon:in-summer-time($date as xs:dateTime, $region as xs:string) as xs:boolean external;
declare %a:since("saxon/pe", "8.3") %a:since("saxon/ee", "8.3") %a:until("saxon/pe", "9.4") %a:until("saxon/ee", "9.4") function saxon:index($sequence as item()*, $expression as jt:net.sf.saxon.functions.Evaluate-PreparedExpression, $collation as xs:string) as jt:com.saxonica.expr.IndexedSequence external;
declare %a:since("saxon/pe", "9.5") %a:since("saxon/ee", "9.5") function saxon:index($sequence as item()*, $function as function(item()) as xs:anyAtomicType* (: [9.5] as xs:integer  :)) as map(*) (: [8.3] as jt:com.saxonica.expr.IndexedSequence [9.5] as xs:double [9.7] as map(*) :) external;
declare %a:since("saxon/ee", "9.7") function saxon:is-defaulted($node as node()) as xs:boolean external;
declare %a:since("saxon/pe", "8.0") %a:since("saxon/ee", "8.0") function saxon:is-whole-number($arg as xs:numeric?) as xs:boolean external;
declare %a:since("saxon/pe", "9.0") %a:since("saxon/ee", "9.0") function saxon:last-modified($uri as xs:string? (: [9.0] () as document-uri($node)) [9.2] $uri as xs:string? :)) as xs:dateTime? external;
declare %a:since("saxon/pe", "9.0") %a:since("saxon/ee", "9.0") %a:until("saxon/pe", "9.1") %a:until("saxon/ee", "9.1") function saxon:last-modified($node as node()) as xs:dateTime? external;
declare %a:since("saxon/pe", "8.0") %a:since("saxon/ee", "8.0") function saxon:leading($input as item()*) as item() external;
declare %a:since("saxon/pe", "8.0") %a:since("saxon/ee", "8.0") function saxon:leading($input as item()*, $test as function(*) (: [8.0] as jt:net.sf.saxon.functions.Evaluate-PreparedExpression [9.5] as function(*) :)) as item() external;
declare %a:since("saxon/pe", "8.1") %a:since("saxon/ee", "8.1") function saxon:line-number() as xs:integer external;
declare %a:since("saxon/pe", "8.1") %a:since("saxon/ee", "8.1") function saxon:line-number($node as node()) as xs:integer external;
declare %a:since("saxon/pe", "8.0") %a:since("saxon/ee", "8.1") function saxon:lowest($input as item()*) as item()* external;
declare %a:since("saxon/pe", "8.0") %a:since("saxon/ee", "8.1") function saxon:lowest($input as item()*, $key as function(*) (: $key [8.0] as jt:net.sf.saxon.functions.Evaluate-PreparedExpression [9.2] as function() [9.5] as function(*) :)) as item()* external;
declare %a:since("saxon/pe", "8.1") %a:since("saxon/ee", "8.1") %a:deprecated("saxon/pe", "9.2") %a:deprecated("saxon/ee", "9.2") function saxon:namespace-node($prefix as xs:string, $uri as xs:string) as namespace-node() external;
declare %a:since("saxon/pe", "8.1") %a:since("saxon/ee", "8.1") function saxon:octets-to-base64Binary($octets as xs:integer*) as xs:base64Binary external;
declare %a:since("saxon/pe", "8.1") %a:since("saxon/ee", "8.1") function saxon:octets-to-hexBinary($octets as xs:integer*) as xs:hexBinary external;
declare %a:since("saxon/pe", "7.1") %a:since("saxon/ee", "7.1") %a:deprecated("saxon/pe", "9.6", "fn:parse-xml") %a:deprecated("saxon/ee", "9.6", "fn:parse-xml") function saxon:parse($xml as xs:string) as document-node() external;
declare %a:since("saxon/pe", "9.2") %a:since("saxon/ee", "9.2") function saxon:parse-html($html as xs:string) as document-node() external;
declare %a:since("saxon/pe", "8.0") %a:since("saxon/ee", "8.0") %a:deprecated("saxon/pe", "9.6", "fn:parse-xml") %a:deprecated("saxon/ee", "9.6", "fn:path") function saxon:path() as xs:string external;
declare %a:since("saxon/pe", "8.0") %a:since("saxon/ee", "8.0") %a:deprecated("saxon/pe", "9.6", "fn:parse-xml") %a:deprecated("saxon/ee", "9.6", "fn:path") function saxon:path($node as node()) as xs:string external;
declare %a:since("saxon/pe", "9.1") %a:since("saxon/ee", "9.1") function saxon:print-stack() as xs:string external;
declare %a:since("saxon/pe", "9.1") %a:since("saxon/ee", "9.1") function saxon:query($name as jt:net.sf.saxon.query.XQueryExpression?) as item()* external;
declare %a:since("saxon/pe", "9.1") %a:since("saxon/ee", "9.1") function saxon:query($name as jt:net.sf.saxon.query.XQueryExpression?, $context-item as item()?) as item()* external;
declare %a:since("saxon/pe", "9.1") %a:since("saxon/ee", "9.1") function saxon:query($name as jt:net.sf.saxon.query.XQueryExpression?, $context-item as item()?, $params as node()*) as item()* external;
declare %a:since("saxon/pe", "9.7") %a:since("saxon/ee", "9.7") function saxon:read-binary-resource($uri as xs:string) as xs:base64Binary external;
declare %a:since("saxon/ee", "9.5") function saxon:schema() as function(*)? external;
declare %a:since("saxon/ee", "9.5") function saxon:schema($kind as xs:string, $name as xs:QName) as function(*)? external;
declare %a:since("saxon/pe", "9.5") %a:since("saxon/ee", "9.5") function saxon:send-mail($emailConfig as map(*), $subject as xs:string, $content as item()) as xs:boolean external;
declare %a:since("saxon/pe", "9.5") %a:since("saxon/ee", "9.5") function saxon:send-mail($emailConfig as map(*), $subject as xs:string, $content as item(), $attachment as item()*) as xs:boolean external;
declare %a:since("saxon/pe", "7.1") %a:since("saxon/ee", "7.1") %a:deprecated("saxon/pe", "9.6", "fn:serialize") %a:deprecated("saxon/ee", "9.6", "fn:serialize") function saxon:serialize($node as node(), $format as xs:string) as xs:string external;
declare %a:since("saxon/pe", "7.1") %a:since("saxon/ee", "7,1") %a:deprecated("saxon/pe", "9.6", "fn:serialize") %a:deprecated("saxon/ee", "9.6", "fn:serialize") function saxon:serialize($node as node(), $format as element(xsl:output)) as xs:string external;