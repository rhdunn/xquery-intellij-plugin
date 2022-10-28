xquery version "3.1";
(:~
 : BaseX Store Module functions
 :
 : @see https://docs.basex.org/wiki/Store_Module
 :)
module namespace store = "http://basex.org/modules/store";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare option o:requires "basex/10.0";

declare %a:since("basex", "10.0") function store:get($key as xs:string) as item()* external;
declare %a:since("basex", "10.0") function store:put($key as xs:string, $value as item()*) as empty-sequence() external;
declare %a:since("basex", "10.0") function store:get-or-put($key as xs:string, $put as function() as item()*) as item()* external;
declare %a:since("basex", "10.0") function store:remove($key as xs:string) as empty-sequence() external;
declare %a:since("basex", "10.0") function store:keys() as xs:string* external;
declare %a:since("basex", "10.0") function store:clear() as empty-sequence() external;
declare %a:since("basex", "10.0") function store:read() as empty-sequence() external;
declare %a:since("basex", "10.0") function store:read($name as xs:string) as empty-sequence() external;
declare %a:since("basex", "10.0") function store:write() as empty-sequence() external;
declare %a:since("basex", "10.0") function store:write($name as xs:string) as empty-sequence() external;
declare %a:since("basex", "10.0") function store:list() as xs:string* external;
declare %a:since("basex", "10.0") function store:delete($name as xs:string) as empty-sequence() external;