xquery version "3.1";
(:~
 : XPath and XQuery Functions and Operators
 :
 : @see https://www.w3.org/TR/1999/REC-xpath-19991116/#corelib
 :
 : @see http://www.w3.org/TR/1999/REC-xslt-19991116
 : @see http://www.w3.org/TR/2007/REC-xslt20-20070123/
 : @see https://www.w3.org/TR/2017/REC-xslt-30-20170608/
 :
 : @see https://www.w3.org/TR/2003/WD-xpath-functions-20030502/
 : @see https://www.w3.org/TR/2011/WD-xpath-functions-30-20111213/
 : @see https://qt4cg.org/branch/master/xpath-functions-40/Overview.html
 :
 : @see https://www.w3.org/TR/2007/REC-xpath-functions-20070123/
 : @see https://www.w3.org/TR/2010/REC-xpath-functions-20101214/
 : @see https://www.w3.org/TR/2014/REC-xpath-functions-30-20140408/
 : @see https://www.w3.org/TR/201/REC-xpath-functions-31-20170321/
 :
 : @see https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#id-func-put
 :
 : This software includes material copied from or derived from the XPath and
 : XQuery Functions and Operators 1.0 to 3.1 specifications. Copyright ©
 : 2007-2017 W3C® (MIT, ERCIM, Keio, Beihang).
 :)
module namespace fn = "http://www.w3.org/2005/xpath-functions";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare option o:requires "xpath-functions/1.0";

declare %a:since("xpath-functions", "4.0-20210113") item-type rng as record(
            number  as xs:double,
            next    as (function() as record(number, next, permute, *)),
            permute as (function(item()*) as item()*),
            *
        );

declare %a:since("xpath-functions", "1.0-20070123") function fn:QName($paramURI as xs:string?, $paramQName as xs:string) as xs:QName external;
declare %a:since("xpath-functions", "1.0-20070123") function fn:abs($arg as xs:numeric?) as xs:numeric? external;
declare %a:restrict-until("$name", "xslt", "4.0-20210113", "xs:string")
        %a:since("xslt", "3.0-20170608") function fn:accumulator-after($name as union(xs:QName, xs:string)) as item()* external;
declare %a:restrict-until("$name", "xslt", "4.0-20210113", "xs:string")
        %a:since("xslt", "3.0-20170608") function fn:accumulator-before($name as union(xs:QName, xs:string)) as item()* external;
declare %a:since("xpath-functions", "1.0-20030502") function fn:adjust-date-to-timezone($arg as xs:date?) as xs:date? external;
declare %a:since("xpath-functions", "1.0-20030502") function fn:adjust-date-to-timezone($arg as xs:date?, $timezone as xs:dayTimeDuration?) as xs:date? external;
declare %a:since("xpath-functions", "1.0-20030502") function fn:adjust-dateTime-to-timezone($arg as xs:dateTime?) as xs:dateTime? external;
declare %a:since("xpath-functions", "1.0-20030502") function fn:adjust-dateTime-to-timezone($arg as xs:dateTime?, $timezone as xs:dayTimeDuration?) as xs:dateTime? external;
declare %a:since("xpath-functions", "1.0-20030502") function fn:adjust-time-to-timezone($arg as xs:time?) as xs:time? external;
declare %a:since("xpath-functions", "1.0-20030502") function fn:adjust-time-to-timezone($arg as xs:time?, $timezone as xs:dayTimeDuration?) as xs:time? external;
declare %a:since("xpath-functions", "4.0-20210113") function fn:all($input as item()*, $predicate as function(item()) as xs:boolean) as xs:integer* external;
declare %a:since("xpath-functions", "3.0-20140408") function fn:analyze-string($input as xs:string?, $pattern as xs:string) as element(fn:analyze-string-result) external;
declare %a:since("xpath-functions", "3.0-20140408") function fn:analyze-string($input as xs:string?, $pattern as xs:string, $flags as xs:string) as element(fn:analyze-string-result) external;
declare %a:since("xpath-functions", "3.1-20170321") function fn:apply($function as function(*), $array as array(*)) as item()* external;
declare %a:since("xpath-functions", "3.0-20140408") function fn:available-environment-variables() as xs:string* external;
declare %a:since("xslt", "3.0-20170608") function fn:available-system-properties() as xs:QName* external;
declare %a:since("xpath-functions", "1.0-20030502") function fn:avg($arg as xs:anyAtomicType*) as xs:anyAtomicType? external;
declare %a:since("xpath-functions", "1.0-20030502") function fn:base-uri() as xs:anyURI? external;
declare %a:since("xpath-functions", "1.0-20030502") function fn:base-uri($arg as node()?) as xs:anyURI? external;
declare %a:since("xpath", "1.0-19991116")
        %a:since("xpath-functions", "1.0-20070123") function fn:boolean($arg as item()*) as xs:boolean external;
declare %a:since("xpath", "1.0-19991116")
        %a:since("xpath-functions", "1.0-20070123") function fn:ceiling($arg as xs:numeric?) as xs:numeric? external;
declare %a:since("xpath-functions", "4.0-20210113") function fn:characters($value as xs:string?) as xs:string* external;
declare %a:since("xpath-functions", "1.0-20070123") function fn:codepoint-equal($comparand1 as xs:string?, $comparand2 as xs:string?) as xs:boolean? external;
declare %a:since("xpath-functions", "1.0-20030502") function fn:codepoints-to-string($arg as xs:integer*) as xs:string external;
declare %a:since("xslt", "3.0-20170608")
        %a:since("xpath-functions", "3.1-20170321") function fn:collation-key($key as xs:string) as xs:base64Binary external;
declare %a:since("xslt", "3.0-20170608")
        %a:since("xpath-functions", "3.1-20170321") function fn:collation-key($key as xs:string, $collation as xs:string) as xs:base64Binary external;
declare %a:since("xpath-functions", "1.0-20070123") function fn:collection() as node()* external;
declare %a:since("xpath-functions", "1.0-20030502") function fn:collection($arg as xs:string?) as node()* external;
declare %a:since("xpath-functions", "1.0-20030502") function fn:compare($comparand1 as xs:string?, $comparand2 as xs:string?) as xs:integer? external;
declare %a:since("xpath-functions", "1.0-20030502") function fn:compare($comparand1 as xs:string?, $comparand2 as xs:string?, $collation as xs:string) as xs:integer? external;
declare %a:since("xpath-functions", "1.0-20030502")
        %a:until("xpath-functions", "1.0-20070123")
        %a:since("basex", "6.0")
        %a:since("marklogic", "5.0") function fn:concat() as xs:string external;
declare %a:since("xpath-functions", "1.0-20030502")
        %a:until("xpath-functions", "1.0-20070123")
        %a:since("basex", "6.0")
        %a:since("marklogic", "5.0") function fn:concat($arg1 as xs:anyAtomicType?) as xs:string external;
declare %a:since("xpath", "1.0-19991116")
        %a:since("xpath-functions", "1.0-20070123") function fn:concat($arg1 as xs:anyAtomicType?, $arg2 as xs:anyAtomicType?, $args as xs:anyAtomicType?...) as xs:string external;
declare %a:since("xpath", "1.0-19991116")
        %a:since("xpath-functions", "1.0-20070123") function fn:contains($arg1 as xs:string?, $arg2 as xs:string?) as xs:boolean external;
declare %a:since("xpath-functions", "1.0-20030502") function fn:contains($arg1 as xs:string?, $arg2 as xs:string?, $collation as xs:string) as xs:boolean external;
declare %a:since("xpath-functions", "3.1-20170321") function fn:contains-token($input as xs:string*, $token as xs:string) as xs:boolean external;
declare %a:since("xpath-functions", "3.1-20170321") function fn:contains-token($input as xs:string*, $token as xs:string, $collation as xs:string) as xs:boolean external;
declare %a:since("xpath-functions", "1.0-20030502")
        %a:until("xpath-functions", "1.0-20070123") function fn:context-item() as item()? external;
declare %a:since("xslt", "3.0-20170608") function fn:copy-of() as item() external;
declare %a:since("xslt", "3.0-20170608") function fn:copy-of($input as item()*) as item()* external;
declare %a:since("xpath", "1.0-19991116")
        %a:since("xpath-functions", "1.0-20070123") function fn:count($arg as item()*) as xs:integer external;
declare %a:since("xslt", "1.0-19991116") function fn:current() as item() external;
declare %a:since("xpath-functions", "1.0-20030502") function fn:current-date() as xs:date external;
declare %a:since("xpath-functions", "1.0-20030502") function fn:current-dateTime() as xs:dateTime external;
declare %a:since("xslt", "2.0-20070123") function fn:current-group() as item()* external;
declare %a:since("xslt", "2.0-20070123") function fn:current-grouping-key() as xs:anyAtomicType* external;
declare %a:since("xslt", "3.0-20170608") function fn:current-merge-group() as item()* external;
declare %a:since("xslt", "3.0-20170608") function fn:current-merge-group($source as xs:string) as item()* external;
declare %a:since("xslt", "3.0-20170608") function fn:current-merge-key() as xs:anyAtomicType* external;
declare %a:since("xslt", "3.0-20170608") function fn:current-output-uri() as xs:anyURI? external;
declare %a:since("xpath-functions", "1.0-20030502") function fn:current-time() as xs:time external;
declare %a:since("xslt", "3.0-20170608") function fn:current-output-uri() as xs:anyURI? external;
declare %a:since("xpath-functions", "3.0-20140408") function fn:data() as xs:anyAtomicType* external;
declare %a:since("xpath-functions", "1.0-20030502") function fn:data($arg as item()*) as xs:anyAtomicType* external;
declare %a:since("xpath-functions", "1.0-20070123") function fn:dateTime($arg1 as xs:date?, $arg2 as xs:time?) as xs:dateTime? external;
declare %a:since("xpath-functions", "1.0-20070123") function fn:day-from-date($arg as xs:date?) as xs:integer? external;
declare %a:since("xpath-functions", "1.0-20070123") function fn:day-from-dateTime($arg as xs:dateTime?) as xs:integer? external;
declare %a:since("xpath-functions", "1.0-20070123") function fn:days-from-duration($arg as xs:duration?) as xs:integer? external;
declare %a:since("xpath-functions", "1.0-20030502") function fn:deep-equal($parameter1 as item()*, $parameter2 as item()*) as xs:boolean external;
declare %a:since("xpath-functions", "1.0-20030502") function fn:deep-equal($parameter1 as item()*, $parameter2 as item()*, $collation as string) as xs:boolean external;
declare %a:since("xpath-functions", "1.0-20030502") function fn:default-collation() as xs:string external;
declare %a:since("xpath-functions", "3.1-20170321") function fn:default-language() as xs:language external;
declare %a:since("xpath-functions", "4.0-20210113") function fn:differences($input1 as item(), $input2 as item()) as map(*)* external;
declare %a:since("xpath-functions", "4.0-20210113") function fn:differences($input1 as item(), $input2 as item(), $options as map(*)) as map(*)* external;
declare %a:since("xpath-functions", "4.0-20210113") function fn:differences($input1 as item(), $input2 as item(), $options as map(*), $collation as xs:string) as map(*)* external;
declare %a:since("xpath-functions", "1.0-20030502")
        %a:until("xpath-functions", "1.0-20070123") function fn:distinct-nodes($arg as node()*) as node()* external;
declare %a:since("xpath-functions", "1.0-20030502") function fn:distinct-values($arg as xs:anyAtomicType*) as xs:anyAtomicType* external;
declare %a:since("xpath-functions", "1.0-20030502") function fn:distinct-values($arg as xs:anyAtomicType*, $collation as xs:string) as xs:anyAtomicType* external;
declare %a:since("marklogic", "5.0") function fn:doc() as document-node()* external;
declare %a:since("xpath-functions", "1.0-20030502")
        %a:restrict-since("$uri", "marklogic", "5.0", "xs:string*")
        %a:restrict-since("$uri", "xpath-functions", "1.0-20070123", "xs:string?")
        %a:restrict-since("return", "marklogic", "5.0", "document-node()*")
        %a:restrict-since("return", "xpath-functions", "1.0-20070123", "document-node()?") function fn:doc($uri as xs:string*) as document-node()* external;
declare %a:since("xpath-functions", "1.0-20070123") function fn:doc-available($uri as xs:string?) as xs:boolean external;
declare %a:since("xslt", "1.0-19991116") function fn:document($uri-sequence as item()*) as node()* external;
declare %a:since("xslt", "1.0-19991116") function fn:document($uri-sequence as item()*, $base-node as node()) as node()* external;
declare %a:since("xpath-functions", "3.0-20140408") function fn:document-uri() as xs:anyURI? external;
declare %a:since("xpath-functions", "1.0-20030502") function fn:document-uri($arg as node()?) as xs:anyURI? external;
declare %a:restrict-until("$name", "xslt", "4.0-20210113", "xs:string")
        %a:since("xslt", "1.0-19991116") function fn:element-available($name as union(xs:QName, xs:string)) as xs:boolean external;
declare %a:since("xpath-functions", "1.0-20101214") function fn:element-with-id($arg as xs:string*) as element()* external;
declare %a:since("xpath-functions", "1.0-20101214") function fn:element-with-id($arg as xs:string*, $node as node()) as element()* external;
declare %a:since("xpath-functions", "1.0-20030502") function fn:empty($arg as item()*) as xs:boolean external;
declare %a:since("xpath-functions", "1.0-20070123") function fn:encode-for-uri($uri-part as xs:string?) as xs:string external;
declare %a:since("xpath-functions", "1.0-20030502") function fn:ends-with($arg1 as xs:string?, $arg2 as xs:string?) as xs:boolean external;
declare %a:since("xpath-functions", "1.0-20030502") function fn:ends-with($arg1 as xs:string?, $arg2 as xs:string?, $collation as xs:string) as xs:boolean external;
declare %a:since("xpath-functions", "3.0-20140408") function fn:environment-variable($name as xs:string) as xs:string? external;
declare %a:since("xpath-functions", "1.0-20030502") function fn:error() as none external;
declare %a:restrict-until("$error", "xpath-functions", "1.0-20070123", "item()?")
        %a:restrict-since("$error", "xpath-functions", "1.0-20070123", "xs:QName")
        %a:since("xpath-functions", "1.0-20030502") function fn:error($error as (xs:QName|item()?)) as none external;
declare %a:since("xpath-functions", "1.0-20070123") function fn:error($error as xs:QName?, $description as xs:string) as none external;
declare %a:since("xpath-functions", "1.0-20070123") function fn:error($error as xs:QName?, $description as xs:string, $error-object as item()*) as none external;
declare %a:since("xpath-functions", "1.0-20070123") function fn:escape-html-uri($uri as xs:string?) as xs:string external;
declare %a:since("xpath-functions", "1.0-20030502")
        %a:until("xpath-functions", "1.0-20070123", "fn:encode-for-uri#1", "fn:iri-to-uri#1", "fn:escape-html-uri#1") function fn:escape-uri($uri as xs:string?, $escape-reserved as xs:boolean) as xs:string external;
declare %a:since("xpath-functions", "1.0-20030502") function fn:exactly-one($arg as item()*) as item() external;
declare %a:since("xpath-functions", "1.0-20030502") function fn:exists($arg as item()*) as xs:boolean external;
declare %a:since("xpath-functions", "1.0-20030502")
        %a:until("xpath-functions", "1.0-20070123") function fn:expanded-QName($paramURI as xs:string, $paramLocal as xs:string) as xs:QName external;
declare %a:since("xpath", "1.0-19991116")
        %a:since("xpath-functions", "1.0-20070123") function fn:false() as xs:boolean external;
declare %a:since("xpath-functions", "3.0-20140408") function fn:filter($seq as item()*, $f as function(item()) as xs:boolean) as item()* external;
declare %a:since("xpath", "1.0-19991116")
        %a:since("xpath-functions", "1.0-20070123") function fn:floor($arg as xs:numeric?) as xs:numeric? external;
declare %a:since("xpath-functions", "3.0-20140408") function fn:fold-left($seq as item()*, $zero as item()*, $f as function(item()*, item()) as item()*) as item()* external;
declare %a:since("xpath-functions", "3.0-20140408") function fn:fold-right($seq as item()*, $zero as item()*, $f as function(item()*, item()) as item()*) as item()* external;
declare %a:since("xpath-functions", "3.0-20140408") function fn:for-each($seq as item()*, $f as function(item()) as item()*) as item()* external;
declare %a:since("xpath-functions", "3.0-20140408") function fn:for-each-pair($seq1 as item()*, $seq2 as item()*, $f as function(item(), item()) as item()*) as item()* external;
declare %a:since("xslt", "2.0-20070123")
        %a:since("xpath-functions", "3.0-20140408") function fn:format-date($value as xs:date?, $picture as xs:string) as xs:string? external;
declare %a:since("xslt", "2.0-20070123")
        %a:since("xpath-functions", "3.0-20140408") function fn:format-date($value as xs:date?, $picture as xs:string, $language as xs:string?, $calendar as xs:string?, $place as xs:string?) as xs:string? external;
declare %a:since("xslt", "2.0-20070123")
        %a:since("xpath-functions", "3.0-20140408") function fn:format-dateTime($value as xs:dateTime?, $picture as xs:string) as xs:string? external;
declare %a:since("xslt", "2.0-20070123")
        %a:since("xpath-functions", "3.0-20140408") function fn:format-dateTime($value as xs:dateTime?, $picture as xs:string, $language as xs:string?, $calendar as xs:string?, $place as xs:string?) as xs:string? external;
declare %a:since("xpath-functions", "3.0-20140408") function fn:format-integer($value as xs:integer?, $picture as xs:string) as xs:string external;
declare %a:since("xpath-functions", "3.0-20140408") function fn:format-integer($value as xs:integer?, $picture as xs:string, $lang as xs:string?) as xs:string external;
declare %a:since("xslt", "1.0-19991116")
        %a:since("xpath-functions", "3.0-20140408") function fn:format-number($value as xs:numeric?, $picture as xs:string) as xs:string external;
declare %a:restrict-until("$decimal-format-name", "xpath-functions", "4.0-20210113", "xs:string?")
        %a:since("xslt", "1.0-19991116")
        %a:since("xpath-functions", "3.0-20140408") function fn:format-number($value as xs:numeric?, $picture as xs:string, $decimal-format-name as union(xs:string, xs:QName)?) as xs:string external;
declare %a:since("xslt", "2.0-20070123")
        %a:since("xpath-functions", "3.0-20140408") function fn:format-time($value as xs:time?, $picture as xs:string) as xs:string? external;
declare %a:since("xslt", "2.0-20070123")
        %a:since("xpath-functions", "3.0-20140408") function fn:format-time($value as xs:time?, $picture as xs:string, $language as xs:string?, $calendar as xs:string?, $place as xs:string?) as xs:string? external;
declare %a:since("xpath-functions", "3.0-20140408") function fn:function-arity($func as function(*)) as xs:integer external;
declare %a:restrict-until("$name", "xslt", "4.0-20210113", "xs:string")
        %a:since("xslt", "1.0-19991116") function fn:function-available($name as union(xs:QName, xs:string)) as xs:boolean external;
declare %a:restrict-until("$name", "xslt", "4.0-20210113", "xs:string")
        %a:since("xslt", "2.0-20070123") function fn:function-available($name as union(xs:QName, xs:string), $arity as xs:integer) as xs:boolean external;
declare %a:since("xpath-functions", "3.0-20140408") function fn:function-lookup($name as xs:QName, $arity as xs:integer) as function(*)? external;
declare %a:since("xpath-functions", "3.0-20140408") function fn:function-name($func as function(*)) as xs:QName? external;
declare %a:since("xslt", "1.0-19991116")
        %a:since("xpath-functions", "3.0-20140408") function fn:generate-id() as xs:string external;
declare %a:since("xslt", "1.0-19991116")
        %a:since("xpath-functions", "3.0-20140408") function fn:generate-id($arg as node()?) as xs:string external;
declare %a:since("xpath-functions", "1.0-20030502")
        %a:until("xpath-functions", "1.0-20070123", "fn:day-from-date#1") function fn:get-day-from-date($arg as xs:date?) as xs:integer? external;
declare %a:since("xpath-functions", "1.0-20030502")
        %a:until("xpath-functions", "1.0-20070123", "fn:day-from-dateTime#1") function fn:get-day-from-dateTime($arg as xs:dateTime?) as xs:integer? external;
declare %a:since("xpath-functions", "1.0-20030502")
        %a:until("xpath-functions", "1.0-20070123", "fn:days-from-duration#1") function fn:get-days-from-dayTimeDuration($arg as xs:dayTimeDuration?) as xs:integer? external;
declare %a:since("xpath-functions", "1.0-20030502")
        %a:until("xpath-functions", "1.0-20070123", "fn:hours-from-dateTime#1") function fn:get-hours-from-dateTime($arg as xs:dateTime?) as xs:integer? external;
declare %a:since("xpath-functions", "1.0-20030502")
        %a:until("xpath-functions", "1.0-20070123", "fn:hours-from-duration#1") function fn:get-hours-from-dayTimeDuration($arg as xs:dayTimeDuration?) as xs:integer? external;
declare %a:since("xpath-functions", "1.0-20030502")
        %a:until("xpath-functions", "1.0-20070123", "fn:hours-from-time#1") function fn:get-hours-from-time($arg as xs:time?) as xs:integer? external;
declare %a:since("xpath-functions", "1.0-20030502")
        %a:until("xpath-functions", "1.0-20070123") function fn:get-in-scope-namespaces($element as element()) as xs:string* external;
declare %a:since("xpath-functions", "1.0-20030502")
        %a:until("xpath-functions", "1.0-20070123", "fn:local-name-from-QName#1") function fn:get-local-name-from-QName($arg as xs:QName?) as xs:string? external;
declare %a:since("xpath-functions", "1.0-20030502")
        %a:until("xpath-functions", "1.0-20070123", "fn:minutes-from-dateTime#1") function fn:get-minutes-from-dateTime($arg as xs:dateTime?) as xs:integer? external;
declare %a:since("xpath-functions", "1.0-20030502")
        %a:until("xpath-functions", "1.0-20070123", "fn:minutes-from-duration#1") function fn:get-minutes-from-dayTimeDuration($arg as xs:dayTimeDuration?) as xs:integer? external;
declare %a:since("xpath-functions", "1.0-20030502")
        %a:until("xpath-functions", "1.0-20070123", "fn:minutes-from-time#1") function fn:get-minutes-from-time($arg as xs:time?) as xs:integer? external;
declare %a:since("xpath-functions", "1.0-20030502")
        %a:until("xpath-functions", "1.0-20070123", "fn:month-from-date#1") function fn:get-month-from-date($arg as xs:date?) as xs:integer? external;
declare %a:since("xpath-functions", "1.0-20030502")
        %a:until("xpath-functions", "1.0-20070123", "fn:month-from-dateTime#1") function fn:get-month-from-dateTime($arg as xs:dateTime?) as xs:integer? external;
declare %a:since("xpath-functions", "1.0-20030502")
        %a:until("xpath-functions", "1.0-20070123", "fn:months-from-duration#1") function fn:get-months-from-yearMonthDuration($arg as xs:yearMonthDuration?) as xs:integer? external;
declare %a:since("xpath-functions", "1.0-20030502")
        %a:until("xpath-functions", "1.0-20070123") function fn:get-namespace-from-QName($arg as xs:QName?) as xs:string? external;
declare %a:since("xpath-functions", "1.0-20030502")
        %a:until("xpath-functions", "1.0-20070123") function fn:get-namespace-uri-for-prefix($element as element(), $prefix as xs:string) as xs:string? external;
declare %a:since("xpath-functions", "1.0-20030502")
        %a:until("xpath-functions", "1.0-20070123", "fn:seconds-from-dateTime#1") function fn:get-seconds-from-dateTime($arg as xs:dateTime?) as xs:decimal? external;
declare %a:since("xpath-functions", "1.0-20030502")
        %a:until("xpath-functions", "1.0-20070123", "fn:seconds-from-duration#1") function fn:get-seconds-from-dayTimeDuration($arg as xs:dayTimeDuration?) as xs:decimal? external;
declare %a:since("xpath-functions", "1.0-20030502")
        %a:until("xpath-functions", "1.0-20070123", "fn:seconds-from-time#1") function fn:get-seconds-from-time($arg as xs:time?) as xs:decimal? external;
declare %a:since("xpath-functions", "1.0-20030502")
        %a:until("xpath-functions", "1.0-20070123", "fn:timezone-from-date#1") function fn:get-timezone-from-date($arg as xs:date?) as xs:dayTimeDuration? external;
declare %a:since("xpath-functions", "1.0-20030502")
        %a:until("xpath-functions", "1.0-20070123", "fn:timezone-from-dateTime#1") function fn:get-timezone-from-dateTime($arg as xs:dateTime?) as xs:dayTimeDuration? external;
declare %a:since("xpath-functions", "1.0-20030502")
        %a:until("xpath-functions", "1.0-20070123", "fn:timezone-from-time#1") function fn:get-timezone-from-time($arg as xs:time?) as xs:dayTimeDuration? external;
declare %a:since("xpath-functions", "1.0-20030502")
        %a:until("xpath-functions", "1.0-20070123", "fn:year-from-date#1") function fn:get-year-from-date($arg as xs:date?) as xs:integer? external;
declare %a:since("xpath-functions", "1.0-20030502")
        %a:until("xpath-functions", "1.0-20070123", "fn:year-from-dateTime#1") function fn:get-year-from-dateTime($arg as xs:dateTime?) as xs:integer? external;
declare %a:since("xpath-functions", "1.0-20030502")
        %a:until("xpath-functions", "1.0-20070123", "fn:years-from-duration#1") function fn:get-years-from-yearMonthDuration($arg as xs:yearMonthDuration?) as xs:integer? external;
declare %a:since("xpath-functions", "3.0-20140408") function fn:has-children() as xs:boolean external;
declare %a:since("xpath-functions", "3.0-20140408") function fn:has-children($node as node()?) as xs:boolean external;
declare %a:since("xpath-functions", "3.0-20140408") function fn:head($arg as item()*) as item()? external;
declare %a:since("xpath-functions", "4.0-20210113") function fn:highest($input as item()*) as item()* external;
declare %a:since("xpath-functions", "4.0-20210113") function fn:highest($input as item()*, $collation as xs:string?) as item()* external;
declare %a:since("xpath-functions", "4.0-20210113") function fn:highest($input as item()*, $collation as xs:string?, $key as function(item()) as xs:anyAtomicType*) as item()* external;
declare %a:since("xpath-functions", "1.0-20070123") function fn:hours-from-dateTime($arg as xs:dateTime?) as xs:integer? external;
declare %a:since("xpath-functions", "1.0-20070123") function fn:hours-from-duration($arg as xs:duration?) as xs:integer? external;
declare %a:since("xpath-functions", "1.0-20070123") function fn:hours-from-time($arg as xs:time?) as xs:integer? external;
declare %a:since("xpath", "1.0-19991116")
        %a:since("xpath-functions", "1.0-20070123") function fn:id($arg as xs:string*) as element()* external;
declare %a:since("xpath-functions", "1.0-20070123") function fn:id($arg as xs:string*, $node as node()) as element()* external;
declare %a:since("xpath-functions", "4.0-20210113") function fn:identity($input as item()*) as item()* external;
declare %a:since("xpath-functions", "1.0-20030502") function fn:idref($arg as xs:string*) as node()* external;
declare %a:since("xpath-functions", "1.0-20070123") function fn:idref($arg as xs:string*, $node as node()) as node()* external;
declare %a:since("xpath-functions", "1.0-20030502") function fn:implicit-timezone() as xs:dayTimeDuration external;
declare %a:since("xpath-functions", "4.0-20210113") function fn:in-scope-namespaces($element as element()) as map(union(xs:NCName, enum("")), xs:anyURI) external;
declare %a:since("xpath-functions", "1.0-20070123") function fn:in-scope-prefixes($element as element()) as xs:string* external;
declare %a:since("xpath-functions", "1.0-20030502") function fn:index-of($seqParam as xs:anyAtomicType*, $srchParam as xs:anyAtomicType) as xs:integer* external;
declare %a:since("xpath-functions", "1.0-20030502") function fn:index-of($seqParam as xs:anyAtomicType*, $srchParam as xs:anyAtomicType, $collation as xs:string) as xs:integer* external;
declare %a:since("xpath-functions", "4.0-20210113") function fn:index-where($input as item()*, $predicate as function(item()) as xs:boolean) as xs:integer* external;
declare %a:since("xpath-functions", "3.0-20140408") function fn:innermost($nodes as node()*) as node()* external;
declare %a:since("xpath-functions", "1.0-20030502")
        %a:until("xpath-functions", "1.0-20070123") function fn:input() as node()? external;
declare %a:since("xpath-functions", "1.0-20030502") function fn:insert-before($target as item()*, $position as xs:integer, $inserts as item()*) as item()* external;
declare %a:since("xpath-functions", "1.0-20070123") function fn:iri-to-uri($iri as xs:string?) as xs:string external;
declare %a:since("xpath-functions", "4.0-20210113") function fn:is-NaN($value as xs:anyAtomicType) as xs:boolean external;
declare %a:since("xpath-functions", "1.0-20030502")
        %a:until("xpath-functions", "1.0-20070123") function fn:item-at($seqParam as item()*, $posParam as xs:integer) as item()? external;
declare %a:since("xpath-functions", "4.0-20210113") function fn:json($input as item()*) as xs:string external;
declare %a:since("xpath-functions", "4.0-20210113") function fn:json($input as xs:string?, $options as map(*)) as xs:string external;
declare %a:since("xpath-functions", "3.1-20170321") function fn:json-doc($href as xs:string?) as item()? external;
declare %a:since("xpath-functions", "3.1-20170321") function fn:json-doc($href as xs:string?, $options as map(*)) as item()? external;
declare %a:since("xslt", "3.0-20170608")
        %a:since("xpath-functions", "3.1-20170321") function fn:json-to-xml($json-text as xs:string?) as document-node()? external;
declare %a:since("xslt", "3.0-20170608")
        %a:since("xpath-functions", "3.1-20170321") function fn:json-to-xml($json-text as xs:string?, $options as map(*)) as document-node()? external;
declare %a:restrict-until("$key-name", "xslt", "4.0-20210113", "xs:string")
        %a:since("xslt", "1.0-19991116") function fn:key($key-name as union(xs:string, xs:QName), $key-value as xs:anyAtomicType*) as node()* external;
declare %a:restrict-until("$key-name", "xslt", "4.0-20210113", "xs:string")
        %a:since("xslt", "2.0-20070123") function fn:key($key-name as union(xs:string, xs:QName), $key-value as xs:anyAtomicType*, $top as node()) as node()* external;
declare %a:since("xpath", "1.0-19991116")
        %a:since("xpath-functions", "1.0-20070123") function fn:lang($testlang as xs:string?) as xs:boolean external;
declare %a:since("xpath-functions", "1.0-20070123") function fn:lang($testlang as xs:string?, $node as node()) as xs:boolean external;
declare %a:since("xpath", "1.0-19991116")
        %a:since("xpath-functions", "1.0-20070123") function fn:last() as xs:integer external;
declare %a:since("xpath-functions", "3.1-20170321") function fn:load-xquery-module($module-uri as xs:string) as map(*) external;
declare %a:since("xpath-functions", "3.1-20170321") function fn:load-xquery-module($module-uri as xs:string, $options as map(*)) as map(*) external;
declare %a:since("xpath", "1.0-19991116")
        %a:since("xpath-functions", "1.0-20070123") function fn:local-name() as xs:string external;
declare %a:since("xpath", "1.0-19991116")
        %a:since("xpath-functions", "1.0-20070123") function fn:local-name($arg as node()?) as xs:string external;
declare %a:since("xpath-functions", "1.0-20070123") function fn:local-name-from-QName($arg as xs:QName?) as xs:NCName? external;
declare %a:since("xpath-functions", "1.0-20030502") function fn:lower-case($arg as xs:string?) as xs:string external;
declare %a:since("xpath-functions", "4.0-20210113") function fn:lowest($input as item()*) as item()* external;
declare %a:since("xpath-functions", "4.0-20210113") function fn:lowest($input as item()*, $collation as xs:string?) as item()* external;
declare %a:since("xpath-functions", "4.0-20210113") function fn:lowest($input as item()*, $collation as xs:string?, $key as function(item()) as xs:anyAtomicType*) as item()* external;
declare %a:since("xpath-functions", "3.0-20111213")
        %a:until("xpath-functions", "3.0-20130108", "fn:for-each#2") function fn:map($f as function(item()) as item()*, $seq as item()*) as item()* external;
declare %a:since("xpath-functions", "3.0-20111213")
        %a:until("xpath-functions", "3.0-20130108", "fn:for-each-pair#3") function fn:map-pairs($seq1 as item()*, $seq2 as item()*, $f as function(item(), item()) as item()*) as item()* external;
declare %a:since("xpath-functions", "1.0-20030502") function fn:matches($input as xs:string?, $pattern as xs:string) as xs:boolean external;
declare %a:since("xpath-functions", "1.0-20030502") function fn:matches($input as xs:string?, $pattern as xs:string, $flags as xs:string) as xs:boolean external;
declare %a:since("xpath-functions", "1.0-20030502") function fn:max($arg as xs:anyAtomicType*) as xs:anyAtomicType? external;
declare %a:since("xpath-functions", "1.0-20030502") function fn:max($arg as xs:anyAtomicType*, $collation as string) as xs:anyAtomicType? external;
declare %a:since("xpath-functions", "1.0-20030502") function fn:min($arg as xs:anyAtomicType*) as xs:anyAtomicType? external;
declare %a:since("xpath-functions", "1.0-20030502") function fn:min($arg as xs:anyAtomicType*, $collation as string) as xs:anyAtomicType? external;
declare %a:since("xpath-functions", "1.0-20070123") function fn:minutes-from-dateTime($arg as xs:dateTime?) as xs:integer? external;
declare %a:since("xpath-functions", "1.0-20070123") function fn:minutes-from-duration($arg as xs:duration?) as xs:integer? external;
declare %a:since("xpath-functions", "1.0-20070123") function fn:minutes-from-time($arg as xs:time?) as xs:integer? external;
declare %a:since("xpath-functions", "1.0-20070123") function fn:month-from-date($arg as xs:date?) as xs:integer? external;
declare %a:since("xpath-functions", "1.0-20070123") function fn:month-from-dateTime($arg as xs:dateTime?) as xs:integer? external;
declare %a:since("xpath-functions", "1.0-20070123") function fn:months-from-duration($arg as xs:duration?) as xs:integer? external;
declare %a:since("xpath", "1.0-19991116")
        %a:since("xpath-functions", "1.0-20070123") function fn:name() as xs:string external;
declare %a:since("xpath", "1.0-19991116")
        %a:since("xpath-functions", "1.0-20070123") function fn:name($arg as node()?) as xs:string external;
declare %a:since("xpath", "1.0-19991116")
        %a:since("xpath-functions", "1.0-20070123") function fn:namespace-uri() as xs:anyURI external;
declare %a:since("xpath", "1.0-19991116")
        %a:since("xpath-functions", "1.0-20070123") function fn:namespace-uri($arg as node()?) as xs:anyURI external;
declare %a:restrict-until("$prefix", "xpath-functions", "4.0-20210113", "xs:string?")
        %a:since("xpath-functions", "1.0-20070123") function fn:namespace-uri-for-prefix($prefix as union(xs:NCName, enum(""))?, $element as element()) as xs:anyURI? external;
declare %a:since("xpath-functions", "1.0-20070123") function fn:namespace-uri-from-QName($arg as xs:QName?) as xs:anyURI? external;
declare %a:since("xpath-functions", "3.0-20140408") function fn:nilled() as xs:boolean external;
declare %a:since("xpath-functions", "1.0-20070123") function fn:nilled($arg as node()?) as xs:boolean? external;
declare %a:since("xpath-functions", "1.0-20030502")
        %a:until("xpath-functions", "1.0-20070123", "xdmp:node-kind#1") function fn:node-kind($arg as node()?) as xs:string external;
declare %a:since("xpath-functions", "3.0-20140408") function fn:node-name() as xs:QName? external;
declare %a:since("xpath-functions", "1.0-20030502") function fn:node-name($arg as node()?) as xs:QName? external;
declare %a:since("xpath", "1.0-19991116")
        %a:since("xpath-functions", "1.0-20070123") function fn:normalize-space() as xs:string external;
declare %a:since("xpath", "1.0-19991116")
        %a:since("xpath-functions", "1.0-20070123") function fn:normalize-space($arg as xs:string?) as xs:string external;
declare %a:since("xpath-functions", "1.0-20030502") function fn:normalize-unicode($arg as xs:string?) as xs:string external;
declare %a:since("xpath-functions", "1.0-20030502") function fn:normalize-unicode($arg as xs:string?, $normalizationForm as xs:string) as xs:string external;
declare %a:since("xpath", "1.0-19991116")
        %a:since("xpath-functions", "1.0-20070123") function fn:not($arg as item()*) as xs:boolean external;
declare %a:since("xpath", "1.0-19991116")
        %a:since("xpath-functions", "1.0-20070123") function fn:number() as xs:double external;
declare %a:since("xpath", "1.0-19991116")
        %a:since("xpath-functions", "1.0-20070123") function fn:number($arg as xs:anyAtomicType?) as xs:double external;
declare %a:since("xpath-functions", "1.0-20030502") function fn:one-or-more($arg as item()*) as item()+ external;
declare %a:since("xpath-functions", "3.0-20140408") function fn:outermost($nodes as node()*) as node()* external;
declare %a:since("xpath-functions", "3.1-20170321") function fn:parse-ietf-date($value as xs:string?) as xs:dateTime? external;
declare %a:since("xpath-functions", "3.1-20170321") function fn:parse-json($json-text as xs:string?) as item()? external;
declare %a:since("xpath-functions", "3.1-20170321") function fn:parse-json($json-text as xs:string?, $options as map(*)) as item()? external;
declare %a:since("xpath-functions", "3.0-20140408") function fn:parse-xml($arg as xs:string?) as document-node(element(*))? external;
declare %a:since("xpath-functions", "3.0-20140408") function fn:parse-xml-fragment($arg as xs:string?) as document-node()? external;
declare %a:since("xpath-functions", "3.0-20140408") function fn:path() as xs:string? external;
declare %a:since("xpath-functions", "3.0-20140408") function fn:path($arg as node()?) as xs:string? external;
declare %a:since("xpath", "1.0-19991116")
        %a:since("xpath-functions", "1.0-20070123") function fn:position() as xs:integer external;
declare %a:since("xpath-functions", "1.0-20070123") function fn:prefix-from-QName($arg as xs:QName?) as xs:NCName? external;
declare %a:since("xpath-update", "1.0-20110317") %updating function fn:put($node as node(), $uri as xs:string) as empty-sequence() external;
declare %a:since("xpath-functions", "3.1-20170321") function fn:random-number-generator() as rng external;
declare %a:since("xpath-functions", "3.1-20170321") function fn:random-number-generator($seed as xs:anyAtomicType?) as rng external;
declare %a:since("xpath-functions", "4.0-20210113") function fn:range-from($input as item()*, $start as (function(item()) as xs:boolean)) as item()* external;
declare %a:since("xpath-functions", "4.0-20210113") function fn:range-to($input as item()*, $start as (function(item()) as xs:boolean)) as item()* external;
declare %a:since("xslt", "2.0-20070123") function fn:regex-group($group-number as xs:integer) as xs:string external;
declare %a:since("xpath-functions", "1.0-20030502") function fn:remove($target as item()*, $position as xs:integer) as item()* external;
declare %a:since("xpath-functions", "1.0-20030502") function fn:replace($input as xs:string?, $pattern as xs:string, $replacement as xs:string) as xs:string external;
declare %a:since("xpath-functions", "1.0-20030502") function fn:replace($input as xs:string?, $pattern as xs:string, $replacement as xs:string, $flags as xs:string) as xs:string external;
declare %a:since("xpath-functions", "4.0-20210113") function fn:replace($value as xs:string?, $pattern as xs:string, $replacement as xs:string?, $flags as xs:string?, $action as (function(xs:string, xs:string*) as xs:string?)?) as xs:string external;
declare %a:since("xpath-functions", "4.0-20210113") function fn:replicate($input as item()*, $count as xs:nonNegativeInteger) as item()* external;
declare %a:since("xpath-functions", "1.0-20030502") function fn:resolve-QName($qname as xs:string?, $element as element()) as xs:QName? external;
declare %a:since("xpath-functions", "1.0-20030502") function fn:resolve-uri($relative as xs:string?) as xs:anyURI? external;
declare %a:since("xpath-functions", "1.0-20030502") function fn:resolve-uri($relative as xs:string?, $base as xs:string) as xs:anyURI? external;
declare %a:since("xpath-functions", "1.0-20070123") function fn:reverse($arg as item()*) as item()* external;
declare %a:since("xpath-functions", "1.0-20030502") function fn:root() as node() external;
declare %a:since("xpath-functions", "1.0-20030502") function fn:root($arg as node()?) as node()? external;
declare %a:since("xpath", "1.0-19991116")
        %a:since("xpath-functions", "1.0-20070123") function fn:round($arg as xs:numeric?) as xs:numeric? external;
declare %a:since("xpath-functions", "3.0-20140408") function fn:round($arg as xs:numeric?, $precision as xs:integer) as xs:numeric? external;
declare %a:since("xpath-functions", "1.0-20030502") function fn:round-half-to-even($arg as xs:numeric?) as xs:numeric? external;
declare %a:since("xpath-functions", "1.0-20030502") function fn:round-half-to-even($arg as xs:numeric?, $precision as xs:integer) as xs:numeric? external;
declare %a:since("xpath-functions", "1.0-20070123") function fn:seconds-from-dateTime($arg as xs:dateTime?) as xs:decimal? external;
declare %a:since("xpath-functions", "1.0-20070123") function fn:seconds-from-duration($arg as xs:duration?) as xs:decimal? external;
declare %a:since("xpath-functions", "1.0-20070123") function fn:seconds-from-time($arg as xs:time?) as xs:decimal? external;
declare %a:since("xpath-functions", "1.0-20030502")
        %a:until("xpath-functions", "1.0-20070123") function fn:sequence-node-identical($arg1 as node()*, $arg2 as node()*) as xs:boolean? external;
declare %a:since("xpath-functions", "3.0-20140408") function fn:serialize($arg as item()*) as xs:string external;
declare %a:since("xpath-functions", "3.0-20140408") function fn:serialize($arg as item()*, $params as item()?) as xs:string external;
declare %a:since("xpath-functions", "4.0-20210113") function fn:slice($input as item()*, $start as xs:integer?, $end as xs:integer?, $step as xs:integer?) as item()* external;
declare %a:since("xslt", "3.0-20170608") function fn:snapshot() as item() external;
declare %a:since("xslt", "3.0-20170608") function fn:snapshot($input as item()*) as item()* external;
declare %a:since("xpath-functions", "4.0-20210113") function fn:some($input as item()*, $predicate as function(item()) as xs:boolean) as xs:integer* external;
declare %a:since("xpath-functions", "3.1-20170321") function fn:sort($input as item()*) as item()* external;
declare %a:since("xpath-functions", "3.1-20170321") function fn:sort($input as item()*, $collation as xs:string?) as item()* external;
declare %a:since("xpath-functions", "3.1-20170321") function fn:sort($input as item()*, $collation as xs:string?, $key as function(item()) as xs:anyAtomicType*) as item()* external;
declare %a:since("xpath-functions", "4.0-20210113") function fn:stack-trace() as xs:string external;
declare %a:since("xpath", "1.0-19991116")
        %a:since("xpath-functions", "1.0-20070123") function fn:starts-with($arg1 as xs:string?, $arg2 as xs:string?) as xs:boolean external;
declare %a:since("xpath-functions", "1.0-20030502") function fn:starts-with($arg1 as xs:string?, $arg2 as xs:string?, $collation as xs:string) as xs:boolean external;
declare %a:since("xpath-functions", "1.0-20070123") function fn:static-base-uri() as xs:anyURI? external;
declare %a:since("xslt", "3.0-20170608") function fn:stream-available($uri as xs:string?) as xs:boolean external;
declare %a:since("xpath", "1.0-19991116")
        %a:since("xpath-functions", "1.0-20070123") function fn:string() as xs:string external;
declare %a:since("xpath", "1.0-19991116")
        %a:since("xpath-functions", "1.0-20070123") function fn:string($arg as item()?) as xs:string external;
declare %a:since("xpath-functions", "3.0-20140408") function fn:string-join($arg1 as xs:string*) as xs:string external;
declare %a:since("xpath-functions", "1.0-20030502") function fn:string-join($arg1 as xs:string*, $arg2 as xs:string) as xs:string external;
declare %a:since("xpath", "1.0-19991116")
        %a:since("xpath-functions", "1.0-20070123") function fn:string-length() as xs:integer external;
declare %a:since("xpath", "1.0-19991116")
        %a:since("xpath-functions", "1.0-20070123") function fn:string-length($arg as xs:string?) as xs:integer external;
declare %a:since("xpath-functions", "1.0-20030502")
        %a:until("xpath-functions", "1.0-20070123") function fn:string-pad($arg as xs:string?, $count as xs:integer) as xs:string? external;
declare %a:since("xpath-functions", "1.0-20030502") function fn:string-to-codepoints($arg as xs:string?) as xs:integer* external;
declare %a:since("xpath-functions", "1.0-20030502") function fn:subsequence($sourceSeq as item()*, $startingLoc as xs:double) as item()* external;
declare %a:since("xpath-functions", "1.0-20030502") function fn:subsequence($sourceSeq as item()*, $startingLoc as xs:double, $length as xs:double) as item()* external;
declare %a:since("xpath", "1.0-19991116")
        %a:since("xpath-functions", "1.0-20070123") function fn:substring($sourceString as xs:string?, $startingLoc as xs:double) as xs:string external;
declare %a:since("xpath", "1.0-19991116")
        %a:since("xpath-functions", "1.0-20070123") function fn:substring($sourceString as xs:string?, $startingLoc as xs:double, $length as xs:double) as xs:string external;
declare %a:since("xpath", "1.0-19991116")
        %a:since("xpath-functions", "1.0-20070123") function fn:substring-after($arg1 as xs:string?, $arg2 as xs:string?) as xs:string external;
declare %a:since("xpath-functions", "1.0-20030502") function fn:substring-after($arg1 as xs:string?, $arg2 as xs:string?, $collation as xs:string) as xs:string external;
declare %a:since("xpath", "1.0-19991116")
        %a:since("xpath-functions", "1.0-20070123") function fn:substring-before($arg1 as xs:string?, $arg2 as xs:string?) as xs:string external;
declare %a:since("xpath-functions", "1.0-20030502") function fn:substring-before($arg1 as xs:string?, $arg2 as xs:string?, $collation as xs:string) as xs:string external;
declare %a:since("xpath-functions", "1.0-20030502")
        %a:until("xpath-functions", "1.0-20070123") function fn:subtract-dateTimes-yielding-dayTimeDuration($arg1 as xs:dateTime, $arg2 as xs:dateTime) as xs:dayTimeDuration external;
declare %a:since("xpath-functions", "1.0-20030502")
        %a:until("xpath-functions", "1.0-20070123") function fn:subtract-dateTimes-yielding-yearMonthDuration($arg1 as xs:dateTime, $arg2 as xs:dateTime) as xs:yearMonthDuration external;
declare %a:since("xpath", "1.0-19991116")
        %a:since("xpath-functions", "1.0-20070123") function fn:sum($arg as xs:anyAtomicType*) as xs:anyAtomicType external;
declare %a:since("xpath-functions", "1.0-20070123") function fn:sum($arg as xs:anyAtomicType*, $zero as xs:anyAtomicType?) as xs:anyAtomicType? external;
declare %a:restrict-until("$name", "xslt", "4.0-20210113", "xs:string")
        %a:since("xslt", "1.0-19991116") function fn:system-property($name as union(xs:QName, xs:string)) as xs:string external;
declare %a:since("xpath-functions", "3.0-20140408") function fn:tail($arg as item()*) as item()? external;
declare %a:since("xpath-functions", "1.0-20070123") function fn:timezone-from-date($arg as xs:date?) as xs:dayTimeDuration? external;
declare %a:since("xpath-functions", "1.0-20070123") function fn:timezone-from-dateTime($arg as xs:dateTime?) as xs:dayTimeDuration? external;
declare %a:since("xpath-functions", "1.0-20070123") function fn:timezone-from-time($arg as xs:time?) as xs:dayTimeDuration? external;
declare %a:since("xpath-functions", "3.1-20170321") function fn:tokenize($input as xs:string?) as xs:string* external;
declare %a:since("xpath-functions", "1.0-20030502") function fn:tokenize($input as xs:string?, $pattern as xs:string) as xs:string* external;
declare %a:since("xpath-functions", "1.0-20030502") function fn:tokenize($input as xs:string?, $pattern as xs:string, $flags as xs:string) as xs:string* external;
declare %a:since("xpath-functions", "3.1-20170321") function fn:trace($value as item()*) as item()* external;
declare %a:since("xpath-functions", "1.0-20030502") function fn:trace($value as item()*, $label as xs:string) as item()* external;
declare %a:since("xpath-functions", "3.1-20170321") function fn:transform($options as map(*)) as map(*) external;
declare %a:since("xpath", "1.0-19991116")
        %a:since("xpath-functions", "1.0-20070123") function fn:translate($arg as xs:string?, $mapString as xs:string, $transString as xs:string) as xs:string external;
declare %a:since("xpath", "1.0-19991116")
        %a:since("xpath-functions", "1.0-20070123") function fn:true() as xs:boolean external;
declare %a:restrict-until("$name", "xslt", "4.0-20210113", "xs:string")
        %a:since("xslt", "2.0-20070123") function fn:type-available($name as union(xs:QName, xs:string)) as xs:boolean external;
declare %a:since("xpath-functions", "4.0-20210113") function fn:uniform($values as xs:anyAtomicType*) as xs:boolean external;
declare %a:since("xpath-functions", "4.0-20210113") function fn:uniform($values as xs:anyAtomicType*, $collation as xs:string) as xs:boolean external;
declare %a:since("xpath-functions", "4.0-20210113") function fn:unique($values as xs:anyAtomicType*) as xs:boolean external;
declare %a:since("xpath-functions", "4.0-20210113") function fn:unique($values as xs:anyAtomicType*, $collation as xs:string) as xs:boolean external;
declare %a:since("xpath-functions", "1.0-20030502") function fn:unordered($sourceSeq as item()*) as item()* external;
declare %a:since("xslt", "2.0-20070123") function fn:unparsed-entity-public-id($entity-name as xs:string) as xs:string external;
declare %a:since("xslt", "3.0-20170608") function fn:unparsed-entity-public-id($entity-name as xs:string, $doc as node()) as xs:string external;
declare %a:since("xslt", "1.0-19991116") function fn:unparsed-entity-uri($entity-name as xs:string) as xs:anyURI external;
declare %a:since("xslt", "3.0-20170608") function fn:unparsed-entity-uri($entity-name as xs:string, $doc as node()) as xs:anyURI external;
declare %a:since("xslt", "2.0-20070123")
        %a:since("xpath-functions", "3.0-20140408") function fn:unparsed-text($href as xs:string?) as xs:string? external;
declare %a:since("xslt", "2.0-20070123")
        %a:since("xpath-functions", "3.0-20140408") function fn:unparsed-text($href as xs:string?, $encoding as xs:string) as xs:string? external;
declare %a:since("xslt", "2.0-20070123")
        %a:since("xpath-functions", "3.0-20140408") function fn:unparsed-text-available($href as xs:string?) as xs:string? external;
declare %a:since("xslt", "2.0-20070123")
        %a:since("xpath-functions", "3.0-20140408") function fn:unparsed-text-available($href as xs:string?, $encoding as xs:string) as xs:string? external;
declare %a:since("xpath-functions", "3.0-20140408") function fn:unparsed-text-lines($href as xs:string?) as xs:string? external;
declare %a:since("xpath-functions", "3.0-20140408") function fn:unparsed-text-lines($href as xs:string?, $encoding as xs:string) as xs:string? external;
declare %a:since("xpath-functions", "1.0-20030502") function fn:upper-case($arg as xs:string?) as xs:string external;
declare %a:since("xpath-functions", "3.0-20140408") function fn:uri-collection() as xs:anyURI* external;
declare %a:since("xpath-functions", "3.0-20140408") function fn:uri-collection($arg as xs:string?) as xs:anyURI* external;
declare %a:since("xslt", "3.0-20170608")
        %a:since("xpath-functions", "3.1-20170321") function fn:xml-to-json($input as node()?) as xs:string? external;
declare %a:since("xslt", "3.0-20170608")
        %a:since("xpath-functions", "3.1-20170321") function fn:xml-to-json($input as node()?, $options as map(*)) as xs:string? external;
declare %a:since("xpath-functions", "1.0-20070123") function fn:year-from-date($arg as xs:date?) as xs:integer? external;
declare %a:since("xpath-functions", "1.0-20070123") function fn:year-from-dateTime($arg as xs:dateTime?) as xs:integer? external;
declare %a:since("xpath-functions", "1.0-20070123") function fn:years-from-duration($arg as xs:duration?) as xs:integer? external;
declare %a:since("xpath-functions", "1.0-20030502") function fn:zero-or-one($arg as item()*) as item()? external;
