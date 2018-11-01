(: Return the version string for eXist-db.
 :)
xquery version "3.0";
declare namespace o = "http://reecedunn.co.uk/xquery/options";
declare option o:implementation "exist-db/3.0";

system:get-version()
