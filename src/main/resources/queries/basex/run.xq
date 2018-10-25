(:~ Run a query on a BaseX XQuery processor.
 :
 : @param $query The query script to run.
 : @param $context The context item to bind to $query.
 :
 : This script has additional logic to map the semantics of the BaseX API to
 : the semantics of the API implemented in the xquery-intellij-plugin to support
 : the Run/Debug Configurations of the IntelliJ IDEs. Specifically:
 :
 :     (no differences)
 :)
xquery version "3.0";
declare namespace o = "http://reecedunn.co.uk/xquery/options";
declare option o:implementation "basec/6.0";

declare variable $query as xs:string external;
declare variable $context as item()* external := ();

let $bindings := map { "": $context }
return xquery:eval($query, $bindings)
