(:~ Run a query on a BaseX XQuery processor.
 :
 : @param $query The query script to run.
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

xquery:eval($query, ())
