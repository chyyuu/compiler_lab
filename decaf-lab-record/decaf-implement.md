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



### QA

#### 为何syntax/src/ast.rs中的`struct ASTAlloc`中只有`class, func,var,program`，没有`expr`等？

