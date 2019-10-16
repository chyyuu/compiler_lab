# pa1a实验记录

## 理解代码框架

### 目录结构

- common：提供一些通用的配置和工具
- syntax：提供ast各个节点的定义，也提供了类型的定义(也许放在这里不太合适，这样做是为了避免crate间循环引用)
- typeck：执行语义分析，类型检查
- tac：提供tac的定义和相关数据结构
- tacgen：将ast翻译为tac
- tacopt：将tac划分为流图，并执行一些基于数据流的优化
- codegen：实现编译器后端，目前实现了mips代码的生成
- print：提供各种结果或中间结果(如ast，tac，asm)的输出
- driver：将各个crate的内容整合起来，组成一个可执行文件并执行测试

### step 1: support **var**

1.  添加token

```rust
// /syntax/src/parser.rs
#[lex(r##"
...
[lexical]
...
'var' = 'Var'
...
```

执行测试，产生如下错误：

```shell
oto@X280:~/thecode/decaf-rs$ cargo run --bin test
   Compiling syntax v0.1.0 (/home/oto/thecode/decaf-rs/syntax)
warning: Conflict at prod `MaybeElse -> Else Blocked` and `MaybeElse ->`, both's PS contains term `Else`.

error[E0599]: no variant or associated item named `Var` found for type `ty::SynTyKind<'_>` in the current scope
  --> syntax/src/parser.rs:46:1
   |
46 | #[lalr1(Program)]
   | ^^^^^^^^^^^^^^^^^ variant or associated item not found in `ty::SynTyKind<'_>`
   | 
  ::: syntax/src/ty.rs:6:1
   |
6  | pub enum SynTyKind<'a> {
   | ---------------------- variant or associated item `Var` not found here

error: aborting due to previous error

For more information about this error, try `rustc --explain E0599`.
error: Could not compile `syntax`.


```

根据错误信息，可以看出有了'Var'这个token后，decaf-rs需要对其进行解析，这是就要用到它自动生成的语法类型的种类SynTyKind Var了。但我们还没定义，所以出错。

### step 2:添加Var类型定义

```
// /syntax/src/ty.rs
pub enum SynTyKind<'a> {
  ...
  Var,
}
```

执行测试，产生如下错误：

```shell
oto@X280:~/thecode/decaf-rs$ cargo run --bin test
   Compiling syntax v0.1.0 (/home/oto/thecode/decaf-rs/syntax)
warning: Conflict at prod `MaybeElse -> Else Blocked` and `MaybeElse ->`, both's PS contains term `Else`.

   Compiling tacgen v0.1.0 (/home/oto/thecode/decaf-rs/tacgen)
   Compiling typeck v0.1.0 (/home/oto/thecode/decaf-rs/typeck)
   Compiling print v0.1.0 (/home/oto/thecode/decaf-rs/print)
error[E0004]: non-exhaustive patterns: `&Var` not covered
  --> typeck/src/lib.rs:44:22
   |
44 |     let kind = match &s.kind {
   |                      ^^^^^^^ pattern `&Var` not covered
   |
   = help: ensure that all possible cases are being handled, possibly by adding wildcards or more match arms

error: aborting due to previous error

For more information about this err对or, try `rustc --explain E0004`.
error: Could not compile `typeck`.
warning: build failed, waiting for other jobs to finish...
error: build failed

```
根据错误信息，可以看出在`typeck/src/lib.rs`中缺少了对Var这样的语法类型种类进行的处理。

### step 3: 添加对Var类型的处理

```
// /typeck/src/lib.rs
impl<'a> TypeCk<'a> {
  pub fn ty(&mut self, s: &SynTy<'a>, is_arr: bool) -> Ty<'a> {
    let kind = match &s.kind {
      SynTyKind::Int => TyKind::Int,
      ...
      SynTyKind::Var => TyKind::Error,
      ...
    };
    match kind {
      TyKind::Error => Ty::error(),
      TyKind::Void if s.arr != 0 => self.errors.issue(s.loc, VoidArrayElement),
      _ => Ty { arr: s.arr + (is_arr as u32), kind }
    }
  }
}
```

语法分析阶段只能得出`SynTy`的结果，为了得到`Ty`则必须经过pa2的语义分析，但现在还没做，所以添加一行代码`SynTyKind::Var => TyKind::Error,`。pa1中把它留在了这里，但是没有用到，目前可以当它不存在。

执行测试，产生如下错误：

```shell
oto@X280:~/thecode/decaf-rs$ cargo run --bin test
   Compiling syntax v0.1.0 (/home/oto/thecode/decaf-rs/syntax)
warning: Conflict at prod `MaybeElse -> Else Blocked` and `MaybeElse ->`, both's PS contains term `Else`.

   Compiling tacgen v0.1.0 (/home/oto/thecode/decaf-rs/tacgen)
   Compiling typeck v0.1.0 (/home/oto/thecode/decaf-rs/typeck)
   Compiling print v0.1.0 (/home/oto/thecode/decaf-rs/print)
   Compiling driver v0.1.0 (/home/oto/thecode/decaf-rs/driver)
    Finished dev [unoptimized + debuginfo] target(s) in 3.99s
     Running `target/debug/test`
......
testcase/S1/test6.decaf: Pass
testcase/S1/var1.decaf: Fail: first different line on 1
your line: "*** Error at (5,9): syntax error" (testcase/S1/out/var1.result:1)
ans  line: "TopLevel @ (1,1)" (testcase/S1/result/var1.result:1)
testcase/S1/var2.decaf: Fail: first different line on 1
your line: "*** Error at (5,9): syntax error" (testcase/S1/out/var2.result:1)
ans  line: "*** Error at (11,14): syntax error" (testcase/S1/result/var2.result:1)
testcase/S1/var6.decaf: Fail: first different line on 1
your line: "*** Error at (3,9): syntax error" (testcase/S1/out/var6.result:1)
ans  line: "TopLevel @ (1,1)" (testcase/S1/result/var6.result:1)

```

可以运行了！但没有能够正确解析var相关的decaf文件。看来是到了语法分析的层面了。

添加对Var类型的语法处理

```
  // /syntax/src/parser.rs
  
  #[lalr1(Program)]
  #[lex(r##"
  ....
  "##)]
  impl<'p> Parser<'p> {
  #[rule(Program -> ClassList)]
  fn program(&self, class: Vec<&'p ClassDef<'p>>) -> &'p Program<'p> {
    self.alloc.program.alloc(Program { class, main: dft(), scope: dft() })
  }
  ......
  #[rule(Simple -> Var Id Assign Expr)] // the VarDef with init
  fn simple_var(&self, v: Token, name: Token, a: Token, init: Expr<'p>) -> Stmt<'p> {
    let loc = name.loc();
    let syn_ty = SynTy { loc: v.loc(), arr: 0, kind: SynTyKind::Var };
    mk_stmt(loc, (&*self.alloc.var.alloc(VarDef { loc, name: name.str(), syn_ty, init: Some((a.loc(), init)), ty: dft(), owner: dft() })).into())
  }
 }
```

这里可以看到，这里添加了一个产生式`#[rule(Simple -> Var Id Assign Expr)] `。这是基于[decaf规范有关var的语法描述](https://decaf-project.gitbook.io/decaf-2019/new-features#yu-fa)和如下的例子构造的：

```shell
//语法
simpleStmt ::= 'var' id '=' expr
//例子             
var x = 1;
var s = "123";
```

查看`/syntax/src/parser.rs`中的代码，有一个产生式`[rule(Simple -> Type Id Assign Expr)] `与我们要做的很类似。

```
  // /syntax/src/parser.rs
  
  #[rule(Simple -> Type Id Assign Expr)] // the VarDef with init
  fn simple_var_def_init(&self, syn_ty: SynTy<'p>, name: Token, a: Token, init: Expr<'p>) -> Stmt<'p> {
    let loc = name.loc();
    mk_stmt(loc, (&*self.alloc.var.alloc(VarDef { loc, name: name.str(), syn_ty, init: Some((a.loc(), init)), ty: dft(), owner: dft() })).into())
  }
```

照着它的写法试试，添加产生式和对应的扩展代码，即在parser最后添加：

```
  // /syntax/src/parser.rs
    #[rule(Simple -> Var Id Assign Expr)] // the VarDef with init
  fn simple_var(&self, v: Token, name: Token, a: Token, init: Expr<'p>) -> Stmt<'p> {
    let loc = name.loc();
    let syn_ty = SynTy { loc: v.loc(), arr: 0, kind: SynTyKind::Var };
    mk_stmt(loc, (&*self.alloc.var.alloc(VarDef { loc, name: name.str(), syn_ty, init: Some((a.loc(), init)), ty: dft(), owner: dft() })).into())
  }
```

与之前的不同，是创建了一个`syn_ty`的Var语法类型。





## QA

### 如何理解代码`pub(crate) ...` 

```
//syntax/src/lib.rs
pub(crate) fn dft<T: Default>() -> T { T::default() }
```



