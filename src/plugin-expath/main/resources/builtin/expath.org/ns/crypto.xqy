xquery version "3.0";
(:~
 : Cryptographic Module (EXPath Candidate Module 14 February 2015)
 :
 : @see https://web.archive.org/web/20170227073403/http://expath.org/spec/crypto/20150214
 : @see https://web.archive.org/web/20170227144046/http://expath.org/spec/crypto/20110810
 : @see http://docs.basex.org/wiki/Crypto_Module
 :)
module namespace crypto = "http://expath.org/ns/crypto";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare option o:requires "expath-crypto/1.0-20110810";

declare option o:implements-module "basex/7.0 as expath-crypto/1.0-20110810";

declare %a:restrict-since("$key", "expath-crypto", "1.0-20110810", "xs:string")
        %a:restrict-until("$key", "basex", "8.6", "xs:string")
        %a:restrict-since("$key", "basex", "8.6", "(xs:string|xs:hexBinary|xs:base64Binary)")
        %a:since("expath-crypto", "1.0-20110810") function crypto:hmac($message as xs:string, $key as xs:anyAtomicType, $algorithm as xs:string) as xs:base64Binary external;
declare %a:restrict-since("$key", "expath-crypto", "1.0-20110810", "xs:string")
        %a:restrict-until("$key", "basex", "8.6", "xs:string")
        %a:restrict-since("$key", "basex", "8.6", "(xs:string|xs:hexBinary|xs:base64Binary)")
        %a:since("expath-crypto", "1.0-20110810") function crypto:hmac($message as xs:string, $key as xs:anyAtomicType, $algorithm as xs:string, $encoding as xs:string) as xs:base64Binary external;
declare %a:since("expath-crypto", "1.0-20110810") function crypto:encrypt($input as xs:string, $encoding as xs:string, $key as xs:string, $algorithm as xs:string) as xs:string external;
declare %a:since("expath-crypto", "1.0-20110810") function crypto:decrypt($input as xs:string, $type as xs:string, $key as xs:string, $algorithm as xs:string) as xs:string external;
declare %a:since("expath-crypto", "1.0-20110810") function crypto:generate-signature($input as node(), $canonicalization as xs:string, $digest as xs:string, $signature as xs:string, $prefix as xs:string, $type as xs:string) as node() external;
declare %a:since("expath-crypto", "1.0-20110810") function crypto:generate-signature($input as node(), $canonicalization as xs:string, $digest as xs:string, $signature as xs:string, $prefix as xs:string, $type as xs:string, $xpath as xs:string, $certificate as node()) as node() external;
declare %a:since("expath-crypto", "1.0-20110810") function crypto:generate-signature($input as node(), $canonicalization as xs:string, $digest as xs:string, $signature as xs:string, $prefix as xs:string, $type as xs:string, $xpath-or-certificate as (xs:string|node())) as node() external;
declare %a:since("expath-crypto", "1.0-20110810") function crypto:validate-signature($input-doc as node()) as xs:boolean external;