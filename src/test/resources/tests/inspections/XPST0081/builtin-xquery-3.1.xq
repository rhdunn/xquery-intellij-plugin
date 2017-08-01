xquery version "3.1";
declare variable $local:x as xs:boolean := fn:true();
declare variable $local:y := <a xml:id="1" xsi:a="http://www.example.com/test"/>;
declare variable $local:z := math:sin(map:size(map {}) div array:size(array {}));
()