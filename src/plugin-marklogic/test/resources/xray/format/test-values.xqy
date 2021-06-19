xquery version "1.0-ml";
module namespace test = "http://github.com/robwhitby/xray/test";

import module namespace assert = "http://github.com/robwhitby/xray/assertions" at "/xray/src/assertions.xqy";

declare %test:case function compare-1-element()
{
  let $actual := <a lorem="ipsum"/>
  let $expected := <b lorem="ipsum"/>
  return assert:equal($actual, $expected)
};

declare %test:case function compare-2-empty-sequence()
{
  assert:equal((), ()),
  assert:equal((), 1),
  assert:equal(2, ())
};

declare %test:case function compare-3-sequence()
{
  let $actual := (<a/>, <b/>, 3, 4, <c/>)
  let $expected := (<a/>, <d/>, 6, 8, <c/>)
  return assert:equal($actual, $expected)
};
