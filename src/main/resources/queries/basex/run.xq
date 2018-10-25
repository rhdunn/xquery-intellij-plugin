(:~ Run a query on a BaseX XQuery processor.
 :
 : @param $query The query script to run.
 : @param $context The context item to bind to $query.
 :
 : This script has additional logic to map the semantics of the BaseX API to
 : the semantics of the API implemented in the xquery-intellij-plugin to support
 : the Run/Debug Configurations of the IntelliJ IDEs. Specifically:
 :
 : 1.  If there is an error, the BaseX API throws an exception. This catches
 :     the error and returns an element(err:error) object.
 :)
xquery version "3.0";
declare namespace o = "http://reecedunn.co.uk/xquery/options";
declare option o:implementation "basec/6.0";

declare variable $query as xs:string external;
declare variable $context as item()* external := ();

declare function local:error(
    $err:code as xs:QName,
    $err:description as xs:string?,
    $err:value as item()*,
    $err:module as xs:string?,
    $err:line-number as xs:integer?,
    $err:column-number as xs:integer,
    $err:additional
) {
    <err:error xmlns:dbg="http://reecedunn.co.uk/xquery/debug">
        <err:code>{$err:code}</err:code>
        <err:description>{$err:description}</err:description>
        <err:value count="{count($err:value)}"></err:value>
    </err:error>
};

try {
    let $bindings := map { "": $context }
    return ("query", xquery:eval($query, $bindings))
} catch * {
    "error", local:error($err:code, $err:description, $err:value, $err:module, $err:line-number, $err:column-number, $err:additional)
}
