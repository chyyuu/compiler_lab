### 词法描述
in syntax/parser_ll.rs
```
[lexical]
'void' = 'Void'
'int' = 'Int'
'bool' = 'Bool'
'string' = 'String'
'new' = 'New'
'null' = 'Null'
'true' = 'True'
'false' = 'False'
'class' = 'Class'
'extends' = 'Extends'
'this' = 'This'
'while' = 'While'
'for' = 'For'
'if' = 'If'
'else' = 'Else'
'return' = 'Return'
'break' = 'Break'
'Print' = 'Print'
'ReadInteger' = 'ReadInteger'
'ReadLine' = 'ReadLine'
'static' = 'Static'
'instanceof' = 'InstanceOf'
'abstract' = 'Abstract'
'var' = 'Var'
'fun' = 'Fun'
'=>' = 'Arrow'
'<=' = 'Le'
'>=' = 'Ge'
'==' = 'Eq'
'!=' = 'Ne'
'&&' = 'And'
'\|\|' = 'Or'
'\+' = 'Add'
'-' = 'Sub'
'\*' = 'Mul'
'/' = 'Div'
'%' = 'Mod'
'=' = 'Assign'
'<' = 'Lt'
'>' = 'Gt'
'\.' = 'Dot'
',' = 'Comma'
';' = 'Semi' # short for semicolon
'!' = 'Not'
'\(' = 'LPar' # short for parenthesis
'\)' = 'RPar'
'\[' = 'LBrk' # short for bracket
'\]' = 'RBrk'
'\{' = 'LBrc' # short for brace
'\}' = 'RBrc'
':' = 'Colon'
# line break in a StringLit will be reported by parser's semantic act
'"[^"\\]*(\\.[^"\\]*)*"' = 'StringLit'
'"[^"\\]*(\\.[^"\\]*)*' = 'UntermString'
'//[^\n]*' = '_Eps'
'\s+' = '_Eps'
'\d+|(0x[0-9a-fA-F]+)' = 'IntLit'
'[A-Za-z]\w*' = 'Id'
'.' = '_Err'
"##)]
```

### 语法描述

in  syntax/parser.rs   90 Productions

```
Program -> ClassList
ClassList -> ClassList ClassDef
ClassList -> ClassDef
ClassDef -> Class Id MaybeExtends LBrc FieldList RBrc
ClassDef -> Abstract Class Id MaybeExtends LBrc FieldList RBrc
MaybeExtends -> Extends Id
MaybeExtends ->
FieldList -> FieldList VarDef Semi
FieldList -> FieldList FuncDef
FieldList ->
FuncDef -> Abstract Type Id LPar VarDefListOrEmpty RPar Semi
FuncDef -> Static Type Id LPar VarDefListOrEmpty RPar Block
FuncDef -> Type Id LPar VarDefListOrEmpty RPar Block
VarDef -> Type Id
VarDefListOrEmpty -> VarDefList
VarDefListOrEmpty ->
VarDefList -> VarDefList Comma VarDef
VarDefList -> VarDef
Block -> LBrc StmtList RBrc
StmtList -> StmtList Stmt
StmtList ->
Stmt -> Simple Semi
Stmt -> If LPar Expr RPar Stmt MaybeElse
Stmt -> While LPar Expr RPar Stmt
Stmt -> For LPar Simple Semi Expr Semi Simple RPar Stmt
Stmt -> Return Expr Semi
Stmt -> Return Semi
Stmt -> Print LPar ExprList RPar Semi
Stmt -> Break Semi
Stmt -> Block
MaybeElse -> Else Stmt
MaybeElse ->
Simple -> LValue Assign Expr
Simple -> VarDef // the VarDef without init
Simple -> Type Id Assign Expr // the VarDef with init
Simple -> Var Id Assign Expr // the VarDef with init
Simple -> Expr
Simple ->
Expr -> LValue
Expr -> Expr LPar ExprListOrEmpty RPar
Expr -> IntLit
Expr -> True
Expr -> False
Expr -> StringLit
Expr -> Null
Expr -> LPar Expr RPar
Expr -> Expr Add Expr
Expr -> Expr Sub Expr
Expr -> Expr Mul Expr
Expr -> Expr Div Expr
Expr -> Expr Mod Expr
Expr -> Expr Eq Expr
Expr -> Expr Ne Expr
Expr -> Expr Lt Expr
Expr -> Expr Le Expr
Expr -> Expr Ge Expr
Expr -> Expr Gt Expr
Expr -> Expr And Expr
Expr -> Expr Or Expr
Expr -> ReadInteger LPar RPar
Expr -> ReadLine LPar RPar
Expr -> This
Expr -> New Id LPar RPar
Expr -> New Type LBrk Expr RBrk
Expr -> InstanceOf LPar Expr Comma Id RPar
Expr -> LPar Class Id RPar Expr
Expr -> Sub Expr
Expr -> Not Expr
Expr -> Fun LPar VarDefListOrEmpty RPar Arrow Expr
Expr -> Fun LPar VarDefListOrEmpty RPar Block
ExprList -> ExprList Comma Expr
ExprList -> Expr
ExprListOrEmpty -> ExprList
ExprListOrEmpty ->
MaybeOwner -> Expr Dot
MaybeOwner ->
VarSel -> MaybeOwner Id
LValue -> VarSel
LValue -> Expr LBrk Expr RBrk
Type -> Int
Type -> Bool
Type -> Void
Type -> StringExprList
Type -> Class Id
Type -> Type LBrk RBrk
Type -> Type LPar TypeListOrEmpty RPar
TypeList -> TypeList Comma Type
TypeList -> Type
TypeListOrEmpty -> TypeList
TypeListOrEmpty ->
```



in  syntax/parser_ll.rs ONLY used in PA1B  125 Productions

```
Program -> ClassList

ClassList -> ClassDef ClassList
ClassList ->

ClassDef -> Class Id MaybeExtends LBrc FieldList RBrc
ClassDef -> Abstract Class Id MaybeExtends LBrc FieldList RBrc

MaybeExtends -> Extends Id
MaybeExtends ->

FieldList -> FieldDef FieldList
FieldList ->

FieldDef -> Static Type Id LPar VarDefListOrEmpty RPar Block
FieldDef -> Abstract Type Id LPar VarDefListOrEmpty RPar Semi
FieldDef -> Type Id FuncOrVar

FuncOrVar -> LPar VarDefListOrEmpty RPar Block
FuncOrVar -> Semi

VarDefListOrEmpty -> VarDefList
VarDefListOrEmpty ->

Type -> SimpleType ArrayOrLambda

SimpleType -> Int
SimpleType -> Bool
SimpleType -> Void
SimpleType -> String
SimpleType -> Class Id

ArrayOrLambda -> LBrk RBrk ArrayOrLambda
ArrayOrLambda -> LPar TypeListOrEmpty RPar ArrayOrLambda
ArrayOrLambda ->

VarDefList -> VarDef VarDefListRem

VarDefListRem -> Comma VarDef VarDefListRem
VarDefListRem ->

ExprListOrEmpty -> ExprList      //for Term8
ExprListOrEmpty ->

ExprList -> Expr ExprListRem

ExprListRem -> Comma Expr ExprListRem
ExprListRem ->

TypeListOrEmpty -> TypeList    //for NewArrayRem  AND  ArrayOrLambda
TypeListOrEmpty ->

TypeList -> Type TypeListRem

TypeListRem -> Comma Type TypeListRem
TypeListRem ->

VarDef -> Type Id

Block -> LBrc StmtList RBrc  // for LambdaBody AND Stmt

StmtList -> Stmt StmtList
StmtList ->

Stmt -> Simple Semi
Stmt -> If LPar Expr RPar Stmt MaybeElse
Stmt -> While LPar Expr RPar Stmt
Stmt -> For LPar Simple Semi Expr Semi Simple RPar Stmt
Stmt -> Return MaybeExpr Semi
Stmt -> Print LPar ExprList RPar Semi
Stmt -> Break Semi
Stmt -> Block

Simple -> Expr MaybeAssign
Simple -> Type Id MaybeAssign
Simple -> Var Id Assign Expr
Simple ->

MaybeAssign -> Assign Expr
MaybeAssign ->

Blocked -> Stmt

MaybeElse -> Else Blocked
MaybeElse ->

MaybeExpr -> Expr
MaybeExpr ->

Op1 -> Or
Op2 -> And
Op3 -> Eq
Op3 -> Ne
Op4 -> Lt
Op4 -> Le
Op4 -> Ge
Op4 -> Gt
Op5 -> Add
Op5 -> Sub
Op6 -> Mul
Op6 -> Div
Op6 -> Mod
Op7 -> Sub
Op7 -> Not

LambdaBody -> Arrow Expr
LambdaBody -> Block

Expr -> Expr1
Expr -> Fun LPar VarDefListOrEmpty RPar LambdaBody

Expr1 -> Expr2 Term1
Expr2 -> Expr3 Term2
Expr3 -> Expr4 Term3
Expr4 -> Expr5 Term4
Expr5 -> Expr6 Term5
Expr6 -> Expr7 Term6
Expr7 -> Op7 Expr7 // not, neg
Expr7 -> LPar ParenOrCast
Expr7 -> Expr8
Expr8 -> Expr9 Term8
Expr9 -> IntLit
Expr9 -> True
Expr9 -> False
Expr9 -> StringLit
Expr9 -> Null
Expr9 -> ReadInteger LPar RPar
Expr9 -> ReadLine LPar RPar
Expr9 -> This
Expr9 -> InstanceOf LPar Expr Comma Id RPar
Expr9 -> Id
Expr9 -> New NewClassOrArray

Term1 -> Op1 Expr2 Term1 // or
Term1 ->
Term2 -> Op2 Expr3 Term2 // and
Term2 ->
Term3 -> Op3 Expr4 Term3 // eq, ne
Term3 ->
Term4 -> Op4 Expr5 Term4 // lt, le, ge, gt
Term4 ->
Term5 -> Op5 Expr6 Term5 // add sub
Term5 ->
Term6 -> Op6 Expr7 Term6 // mul, div, mod
Term6 ->
Term8 -> LBrk Expr RBrk Term8
Term8 -> Dot Id Term8
Term8 -> LPar ExprListOrEmpty RPar Term8
Term8 ->

NewClassOrArray -> Id LPar RPar
NewClassOrArray -> SimpleType NewArrayRem

ParenOrCast -> Expr RPar
ParenOrCast -> Class Id RPar Expr9

NewArrayRem -> LBrk AfterLBrk
NewArrayRem -> LPar TypeListOrEmpty RPar NewArrayRem

AfterLBrk -> Expr RBrk
AfterLBrk -> RBrk NewArrayRem
```

### 程序结构（syntax/src/ast.rs）

```mermaid
graph TD
A[Program] -->|...|B(ClassDef)
A --> C(Class_Main)
B -->E(FieldDef_FunDef:FunDef)
B -->F(FieldDef_VarDef:VarDef)
E -->|...|G(param:VarDef)
E --> H(ret:SynTy)
E --> I(body:Block)
I -->|...|J(Stmt:StmtKind)
J -->Assign
J -->LocalVarDef
J -->ExprEval
J -->Skip
J -->If
J -->While
J -->For
J -->Return
J -->Print
J -->Break
J -->Block
```
### 表达式（syntax/src/ast.rs）

```mermaid
graph TD
A[Expression:ExprKind]-->VarSel
A-->IndexSel
A-->IntLit
A-->BoolLit
A-->StringLit
A-->NullLit
A-->Call
A-->Unary
A-->Binary
A-->This
A-->ReadInt
A-->ReadLine
A-->NewClass
A-->NewArray
A-->ClassCast
A-->Lambda
A-->ClassTest?

```

### 执行流程

在parser.rs中添加`#[expand]`

```rust
#[expand]
#[lex(r##"
priority = []
```

就可以在编译时生成扩展出来的代码，分析代码，可以得到LL(1)parser的大致执行过程：

```mermaid
graph LR
A[test_all]-->B[test_one_caught]
B-->C[test_one]
C-->D[run]
D-->F[compile]
F-->G[parser_ll::work]
G-->|expand|H[parser.parse]
H-->I[parser._parse]
I-->|expand|J[parser.act]
```

另外，通过 

```
cargo doc
```

可以得到项目的函数文档。

### QA

#### 为何syntax/src/ast.rs中的`struct ASTAlloc`中只有`class, func,var,program`，没有`expr`等？

#### 对decaf spec词法的理解问题？
```
syntax/parser_ll.rs [lexical]部分
# line break in a StringLit will be reported by parser's semantic act
'"[^"\\]*(\\.[^"\\]*)*"' = 'StringLit'   //???
'"[^"\\]*(\\.[^"\\]*)*' = 'UntermString' //???
'//[^\n]*' = '_Eps'
'\s+' = '_Eps'   //all space ??? 
'\d+|(0x[0-9a-fA-F]+)' = 'IntLit'
'[A-Za-z]\w*' = 'Id'
'.' = '_Err' //??? 与 '\.' = 'Dot'的区别是啥？
```

ANSWER：

`'"[^"\\]*(\\.[^"\\]*)*"' = 'StringLit' `定义就是匹配一般的字符串常量的正则表达式，可参考https://stackoverflow.com/questions/37032620/regex-for-matching-a-string-literal-in-java 中对它的解释。

`'"[^"\\]*(\\.[^"\\]*)*' = 'UntermString' `定义则比较奇怪，可以看出它比第一个少了末尾的一个引号。是因为decaf语言要求检查这种语法错误：不闭合的字符串，即直到程序的末尾也没有出现末尾的引号。

#### blocked的语法规则有必要吗？

```
Blocked -> Stmt
MaybeElse -> Else Blocked
```

#### 语法规则为何由op1-7的划分？

为了表现出优先级