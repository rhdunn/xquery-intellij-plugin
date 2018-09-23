xquery version "3.0";

(:~
: User: bridger
: Date: 9/22/18
: Time: 3:39 PM
: To change this template use File | Settings | File Templates.
:)

module namespace crypto = "http://expath.org/ns/crypto";

import module namespace a = "http://reecedunn.co.uk/xquery/annotations" at "res://reecedunn.co.uk/xquery/annotations.xqy";

declare %a:since("basex", "7.0") function crypto:hmac($message as xs:string, $key as xs:anyAtomicType, $algorithm as xs:string) as xs:base64Binary (: $key as [7.0]xs:string [8.6]xs:anyAtomicType :) external;
declare %a:since("basex", "7.0") function crypto:hmac($message as xs:string, $key as xs:anyAtomicType, $algorithm as xs:string, $encoding as xs:string) as xs:base64Binary (: $key as [7.0]xs:string [8.6]xs:anyAtomicType :) external;
