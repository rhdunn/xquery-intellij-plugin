# XPath and XQuery Grammar

This plugin implements the XQuery 3.1 language specification, and associated
W3C and vendor extensions in a unified grammar. This file documents the changes
to the grammar from what is provided in the various specifications.

-----

- [Quantified Expressions](#quantified-expressions)

## Quantified Expressions

    QuantifiedExpr        := ("some" | "every") QuantifiedExprBinding ("," QuantifiedExprBinding)* "satisfies" ExprSingle
    QuantifiedExprBinding := "$" VarName TypeDeclaration? "in" ExprSingle

The `QuantifiedExprBinding` production is not used in the XPath and XQuery
grammar. Instead, the production is inlined in the `QuantifiedExpr` production.
This is split out in the plugin to mirror the change made to the `ForClause`
and `LetClause` productions with the addition of the `ForBinding` and
`LetBinding` productions.

This change was made to make it easier to implement the variable declaration
logic, as each `QuantifiedExprBinding` is a single variable declaration.
