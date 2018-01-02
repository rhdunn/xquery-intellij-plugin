# XPath and XQuery Grammar

This plugin implements the XQuery 3.1 language specification, and associated
W3C and vendor extensions in a unified grammar. This file documents the changes
to the grammar from what is provided in the various specifications.

-----

- [Quantified Expressions](#quantified-expressions)
- [Cast Expressions](#cast-expressions)

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

## Typeswitch Expressions

    TypeswitchExpr    ::=  "typeswitch" "(" Expr ")" CaseClause+ DefaultCaseClause
    DefaultCaseClause ::=  "default" ("$" VarName)? "return" ExprSingle

The default case expression is factored out here into a separate grammar
production, similar to the `CaseClause` expression.

This change was made to make it easier to implement the variable declaration
logic, as each `CaseClause` can expose a variable bound to the typeswitch
expression that is valid for the scope of the case's return expression.

## Cast Expressions

    CastExpr                 := ArrowTransformUpdateExpr ( "cast" "as" SingleType )?
    ArrowTransformUpdateExpr := ArrowExpr | TransformWithExpr | UpdateExpr

The XQuery 3.1 and Update Facility 3.0 specifications both define constructs for use
between `CastExpr` and `UnaryExpr`. This supports either construct, but does not allow
both constructs to be mixed in the same expression, unless parentheses are used. It
also supports the BaseX `UpdateExpr` constructs here, using the same logic.

This differs from the
[proposed official resolution](https://www.w3.org/Bugs/Public/show_bug.cgi?id=30015)
which has not been standardized. Instead, this change supports any expression
that is valid in the different specifications, but not expressions that mix them
in ways not convered by those expressions.

__NOTE:__ This plugin does not model the `ArrowTransformUpdateExpr` construct
in the AST.
