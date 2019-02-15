xquery version "3.0";
(:~
 : eXist-db expath repository module functions
 :
 : @see http://exist-db.org/exist/apps/fundocs/view.html?uri=http://exist-db.org/xquery/repo&location=java:org.exist.xquery.modules.expathrepo.ExpathPackageModule&details=true
 :)
module namespace repo = "http://exist-db.org/xquery/repo";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare %a:since("exist", "4.4") function repo:deploy($pkgName as xs:string) as element() external;
declare %a:since("exist", "4.4") function repo:deploy($pkgName as xs:string, $targetCollection as xs:string) as element() external;
declare %a:since("exist", "4.4") function repo:get-resource($pkgName as xs:string, $resource as xs:string) as xs:base64Binary? external;
declare %a:since("exist", "4.4") function repo:get-root() as xs:string external;
declare %a:since("exist", "4.4") function repo:install($pkgName as xs:string) as xs:boolean external;
declare %a:since("exist", "4.4") function repo:install-and-deploy($pkgName as xs:string, $publicRepoURL as xs:string) as element() external;
declare %a:since("exist", "4.4") function repo:install-and-deploy($pkgName as xs:string, $version as xs:string?, $publicRepoURL as xs:string) as element() external;
declare %a:since("exist", "4.4") function repo:install-and-deploy-from-db($path as xs:string) as element() external;
declare %a:since("exist", "4.4") function repo:install-and-deploy-from-db($path as xs:string, $publicRepoURL as xs:string) as element() external;
declare %a:since("exist", "4.4") function repo:install-from-db($path as xs:string) as xs:boolean external;
declare %a:since("exist", "4.4") function repo:list() as xs:string* external;
declare %a:since("exist", "4.4") function repo:remove($pkgName as xs:string) as xs:boolean external;
declare %a:since("exist", "4.4") function repo:undeploy($pkgName as xs:string) as element() external;