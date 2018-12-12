xquery version "3.0";
(:~
 : eXist-db Security Manager module functions
 :
 : @see http://exist-db.org/exist/apps/fundocs/view.html?uri=http://exist-db.org/xquery/securitymanager&location=java:org.exist.xquery.functions.securitymanager.SecurityManagerModule&details=true
 :
 :)
module namespace sm = "http://exist-db.org/xquery/securitymanager";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare %a:since("exist", "4.4") function sm:add-group-ace($path as xs:anyURI, $group-name as xs:string, $allowed as xs:boolean, $mode as xs:string) as empty-sequence() external;
declare %a:since("exist", "4.4") function sm:add-group-manager($group as xs:string, $manager as xs:string+) as empty-sequence() external;
declare %a:since("exist", "4.4") function sm:add-group-member($group as xs:string, $member as xs:string+) as empty-sequence() external;
declare %a:since("exist", "4.4") function sm:add-user-ace($path as xs:anyURI, $user-name as xs:string, $allowed as xs:boolean, $mode as xs:string) as empty-sequence() external;
declare %a:since("exist", "4.4") function sm:chgrp($path as xs:anyURI, $group-name as xs:string) as empty-sequence() external;
declare %a:since("exist", "4.4") function sm:chmod($path as xs:anyURI, $mode as xs:string) as empty-sequence() external;
declare %a:since("exist", "4.4") function sm:chown($path as xs:anyURI, $owner as xs:string) as empty-sequence() external;
declare %a:since("exist", "4.4") function sm:chown($path as xs:anyURI, $owner as xs:string) as empty-sequence() external;
declare %a:since("exist", "4.4") function sm:create-account($username as xs:string, $password as xs:string, $primary-group as xs:string, $groups as xs:string*) as empty-sequence() external;
declare %a:since("exist", "4.4") function sm:create-account($username as xs:string, $password as xs:string, $primary-group as xs:string, $groups as xs:string*, $full-name as xs:string, $description as xs:string) as empty-sequence() external;
declare %a:since("exist", "4.4") function sm:create-account($username as xs:string, $password as xs:string, $groups as xs:string*) as empty-sequence() external;
declare %a:since("exist", "4.4") function sm:create-account($username as xs:string, $password as xs:string, $groups as xs:string*, $full-name as xs:string, $description as xs:string) as empty-sequence() external;
declare %a:since("exist", "4.4") function sm:create-group($group-name as xs:string) as item() external;
declare %a:since("exist", "4.4") function sm:create-group($group-name as xs:string, $description as xs:string) as item() external;
declare %a:since("exist", "4.4") function sm:create-group($group-name as xs:string, $managers as xs:string+, $description as xs:string) as item() external;
declare %a:since("exist", "4.4") function sm:delete-group($group-id as xs:string) as item() external;
declare %a:since("exist", "4.4") function sm:find-groups-by-groupname($starts-with as xs:string) as xs:string* external;
declare %a:since("exist", "4.4") function sm:find-groups-where-groupname-contains($fragment as xs:string) as xs:string* external;
declare %a:since("exist", "4.4") function sm:find-users-by-name($starts-with as xs:string) as xs:string* external;
declare %a:since("exist", "4.4") function sm:find-users-by-name-part($starts-with as xs:string) as xs:string* external;
declare %a:since("exist", "4.4") function sm:find-users-by-username($starts-with as xs:string) as xs:string* external;
declare %a:since("exist", "4.4") function sm:get-account-metadata($username as xs:string, $attribute as xs:anyURI) as xs:string? external;
declare %a:since("exist", "4.4") function sm:get-account-metadata-keys() as xs:anyURI* external;
declare %a:since("exist", "4.4") function sm:get-account-metadata-keys($username as xs:string) as xs:anyURI* external;
declare %a:since("exist", "4.4") function sm:get-group-managers($group as xs:string) as xs:string+ external;
declare %a:since("exist", "4.4") function sm:get-group-members($group as xs:string) as xs:string+ external;
declare %a:since("exist", "4.4") function sm:get-group-metadata($group-name as xs:string, $attribute as xs:anyURI) as xs:string? external;
declare %a:since("exist", "4.4") function sm:get-group-metadata-keys() as xs:anyURI* external;
declare %a:since("exist", "4.4") function sm:get-group-metadata-keys($group-name as xs:string) as xs:anyURI* external;
declare %a:since("exist", "4.4") function sm:get-groups() as xs:string* external;
declare %a:since("exist", "4.4") function sm:get-permissions($path as xs:anyURI) as document-node() external;
declare %a:since("exist", "4.4") function sm:get-umask($username as xs:string) as xs:int* external;
declare %a:since("exist", "4.4") function sm:get-user-groups($user as xs:string) as xs:string+ external;
declare %a:since("exist", "4.4") function sm:get-user-primary-group($user as xs:string) as xs:string external;
declare %a:since("exist", "4.4") function sm:group-exists($group as xs:string) as xs:boolean external;
declare %a:since("exist", "4.4") function sm:has-access($path as xs:anyURI, $mode as xs:string) as xs:boolean external;
declare %a:since("exist", "4.4") function sm:id() as document-node() external;
declare %a:since("exist", "4.4") function sm:insert-group-ace($path as xs:anyURI, $index as xs:int, $group-name as xs:string, $allowed as xs:boolean, $mode as xs:string) as empty-sequence() external;
declare %a:since("exist", "4.4") function sm:insert-user-ace($path as xs:anyURI, $index as xs:int, $user-name as xs:string, $allowed as xs:boolean, $mode as xs:string) as empty-sequence() external;
declare %a:since("exist", "4.4") function sm:is-account-enabled($username as xs:string) as xs:boolean external;
declare %a:since("exist", "4.4") function sm:is-authenticated() as xs:boolean external;
declare %a:since("exist", "4.4") function sm:is-dba($username as xs:string) as xs:boolean external;
declare %a:since("exist", "4.4") function sm:is-externally-authenticated() as xs:boolean external;
declare %a:since("exist", "4.4") function sm:list-groups() as xs:string* external;
declare %a:since("exist", "4.4") function sm:list-users() as xs:string+ external;
declare %a:since("exist", "4.4") function sm:mode-to-octal($mode as xs:string) as xs:string external;
declare %a:since("exist", "4.4") function sm:modify-ace($path as xs:anyURI, $index as xs:int, $allowed as xs:boolean, $mode as xs:string) as empty-sequence() external;
declare %a:since("exist", "4.4") function sm:octal-to-mode($octal as xs:string) as xs:string external;
declare %a:since("exist", "4.4") function sm:passwd($username as xs:string, $password as xs:string) as empty-sequence() external;
declare %a:since("exist", "4.4") function sm:passwd-hash($username as xs:string, $password-digest as xs:string) as empty-sequence() external;
declare %a:since("exist", "4.4") function sm:remove-account($username as xs:string) as empty-sequence() external;
declare %a:since("exist", "4.4") function sm:remove-ace($path as xs:anyURI, $index as xs:int) as empty-sequence() external;
declare %a:since("exist", "4.4") function sm:remove-group($group-name as xs:string) as item() external;
declare %a:since("exist", "4.4") function sm:remove-group-manager($group as xs:string, $manager as xs:string+) as empty-sequence() external;
declare %a:since("exist", "4.4") function sm:remove-group-manager($group as xs:string, $manager as xs:string+) as empty-sequence() external;
declare %a:since("exist", "4.4") function sm:set-account-enabled($username as xs:string, $enabled as xs:boolean) as empty-sequence() external;
declare %a:since("exist", "4.4") function sm:set-account-metadata($username as xs:string, $attribute as xs:anyURI, $value as xs:string) as empty-sequence() external;
declare %a:since("exist", "4.4") function sm:set-group-metadata($group-name as xs:string, $attribute as xs:anyURI, $value as xs:string) as empty-sequence() external;
declare %a:since("exist", "4.4") function sm:set-umask($username as xs:string, $umask as xs:int) as empty-sequence() external;
declare %a:since("exist", "4.4") function sm:set-user-primary-group($username as xs:string, $group as xs:string) as empty-sequence() external;
declare %a:since("exist", "4.4") function sm:user-exists($user as xs:string) as xs:boolean external;