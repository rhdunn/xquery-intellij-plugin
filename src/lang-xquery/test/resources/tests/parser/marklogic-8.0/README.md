# MarkLogic 8.0 Vendor Extension Test Files

This is an extensive set of tests for an XQuery parser that cover both
valid and invalid MarkLogic 8.0 vendor extension constructs. The text files
are the expected AST generated for the corresponding XQuery file by the parser
in this project.

These tests only cover the different sequences of tokens, not the lexical
spaces those tokens belong to. The lexical space variations are handled by
the Lexer tests.

These tests only cover the new and modified constructs in the MarkLogic 8.0
vendor extensions. For complete coverage, the MarkLogic 6.0 vendor extension,
XQuery 1.0 and XQuery 3.0 tests need to be used as well.

## License

Copyright (C) 2016 Reece H. Dunn

All the files in this directory are licensed under the Apache 2.0 license.
