xquery version "1.0-ml";
module namespace test = "http://github.com/robwhitby/xray/test";

import module namespace assert = "http://github.com/robwhitby/xray/assertions" at "/xray/src/assertions.xqy";

declare %test:case function compare-1-xml()
{
  let $actual := <a lorem="ipsum"/>
  let $expected := <b lorem="ipsum"/>
  return assert:equal($actual, $expected)
};
