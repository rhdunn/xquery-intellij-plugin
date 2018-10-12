xquery version "3.0";
(:~
: BaseX Jobs Module functions
:
: @see http://docs.basex.org/wiki/Jobs_Module
:)
module namespace jobs = "http://basex.org/modules/jobs";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";

declare %a:since("basex", "8.5") function jobs:current() as xs:string external;
declare %a:since("basex", "8.5") function jobs:list() as xs:string* external;
declare %a:since("basex", "8.5") function jobs:list-details() as element(job)* external;
declare %a:since("basex", "8.5") function jobs:list-details($id as xs:string) as element(job)* external;
declare %a:since("basex", "8.5") function jobs:finished($id as xs:string) as xs:boolean external;
declare %a:since("basex", "8.5") function jobs:services() as element(job)* external;
declare %a:since("basex", "8.5") function jobs:eval($query as xs:string) as xs:string external;
declare %a:since("basex", "8.5") function jobs:eval($query as xs:string, $bindings as map(*)?) as xs:string external;
declare %a:since("basex", "8.5") function jobs:eval($query as xs:string, $bindings as map(*)?, $options as map(*)?) as xs:string external;
declare %a:since("basex", "9.0") function jobs:invoke($uri as xs:string) as xs:string external;
declare %a:since("basex", "9.0") function jobs:invoke($uri as xs:string, $bindings as map(*)?) as xs:string external;
declare %a:since("basex", "9.0") function jobs:invoke($uri as xs:string, $bindings as map(*)?, $options as map(*)?) as xs:string external;
declare %a:since("basex", "8.5") function jobs:result($id as xs:string) as item()* external;
declare %a:since("basex", "8.5") function jobs:stop($id as xs:string) as empty-sequence() external;
declare %a:since("basex", "8.5") function jobs:wait($id as xs:string) as empty-sequence() external;
