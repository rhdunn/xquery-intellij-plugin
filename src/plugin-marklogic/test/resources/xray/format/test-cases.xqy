xquery version "1.0-ml";
module namespace test = "http://github.com/robwhitby/xray/test";

import module namespace assert = "http://github.com/robwhitby/xray/assertions" at "/xray/src/assertions.xqy";

declare %test:case function passing-test()
{
  assert:equal(1, 1)
};

declare %test:case function failing-test()
{
  assert:equal(1, 2)
};

declare %test:ignore function ignored-test()
{
  fn:error()
};

declare %test:case function exception()
{
  fn:error()
};
