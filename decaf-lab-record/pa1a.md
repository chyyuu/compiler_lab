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

## 实验总结
得到lch助教的帮助，按照倒序的过程来实现。在理解编译原理和大致处理过程的情况下，根据错误和已有实现代码，采用试错大法完成。

1. eca49d527e450c5ec1cee493335bbf0913a7c69a (HEAD -> pa1a, origin/pa1a) add Lambda process in impl Printable for Expr<'_>. fix some unimplemented funs in tacgen/lib.rs and typeck/type_pass.rs
2. c81a0c6319552d44015b772fa1a9e35962078a93 add SynTyKind::Func process in impl Printable for SynTy & impl<'a> TypeCk<'a>
3. 76d537618f6853b506bdf446e24ee664d9bd2356 add Fun type
4. 5f99fab0e582722766c8ebd4be584109d68db1c6 add token LPar, add type related syntax
5. 0348a2a6c23ea5b80156e8f14ce8465197589f85 add lambda tokens => fun
6. d26ad82ba77fd4cb878e3f170127211c6cd3d40d update AST printing info for 'ABSTRACT' class. Testcase 8 passed. Now all testcases for abstract are passed
7. e7a2e8e751ec6fb3dbe180380fbb6e651fc45413 update ast print "ABSTRACT" fun. testcase abs 4 passed
8. dbbfd733acee546554b3863632cf80a603269129 update usage for FuncDef::body : Option<Block>. Now testcase abs 1,3,5 passed, 4,8 failed
9. 8801b8c66eef14142c5f7489bcacfacd1a4ecb9a update FuncDef::body definition
10. bf294feba01274513f9dacbbadb9277f3686d2df add product for abstract FUN
11. 42d352e3dbe794b53d0d58b6d2804aa1138c691c Merge branch 'master' into pa1a
12. 3db883d4849e40f79b01a53f3c1dc38aacceba0c (master) add abstract class lexeme & syntax. NOTICE testcase/S1/abstract5.decaf
13. 95d61dbb5aadb04d25ad9ceaba4d4a3fac718f3c add abstract class lexeme & syntax
14. 080726d6dc7f4bff021f773eb89504e18b164fb8 merge master branch
15. 09fc710e5b13e653b1c169a031e387cd04abbdae pa1a var tests passed
16. d9b95a557ac88b623545e4bda5d14df1a157aa17 update .gitignore
17. 264b51ab47a106b6a266a4c3ac9f704e77a86c54 pa1a init ver

## 实验过程

### step 0: preparation

1. 在linux (ubuntu 19.10 x86-64)上，安装rust toolchain nightly (1.39.0-nightly)

```
curl --proto '=https' --tlsv1.2 -sSf https://sh.rustup.rs | sh
rustup default nightly
```

2. 安装clion IDE（对教育界的学生和老师免费，感谢！），并安装plugin rust, toml等
3. （可选）更新crates mirror(为了编译过程中的下载提速)
   - [crates in USTC mirror 方法](http://mirrors.ustc.edu.cn/help/crates.io-index.html)
   - [crates in rust.cc mirror 方法](https://github.com/rustcc/lernaean-deploy/)
4. git pull试验代码, decaf试验综述文档和试验指导文档(便于本地查阅)
```
git clone https://github.com/decaf-lang/decaf-rs.git
git clone https://github.com/decaf-lang/decaf-2019-project.git
git clone https://github.com/decaf-lang/decaf-book-spec.git
```
4. 阅读在线文档

- [decaf 试验综述文档](https://decaf-project.gitbook.io/decaf-2019/)
- [decaf试验指导文档](https://decaf-lang.gitbook.io/decaf-book/)

### step 1: 添加**var**的token和定义

根据[Var的描述](https://decaf-project.gitbook.io/decaf-2019/new-features#te-xing-er-ben-di-lei-xing-tui-dao)理解Var的表示`'var' id '=' expr`。重点是修改`parser.rs`中的lexer和parser。



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
```

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

​```shell
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

### step 2: 添加对Var类型的处理

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
  ......
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

此时的commit 09fc710e5b13e653b1c169a031e387cd04abbdae，Var相关的testcase通过！

### step 3: 添加`abstract`的token和 abstract class语法产生式

```
// syntax/src/parser.rs
......
[lexical]
......
'abstract' = 'Abstract'
......

impl<'p> Parser<'p> {
......
  #[rule(ClassDef -> Abstract Class Id MaybeExtends LBrc FieldList RBrc)]
  fn class_def_a(&self, _a: Token, c: Token, name: Token, parent: Option<&'p str>, _l: Token, field: Vec<FieldDef<'p>>, _r: Token) -> &'p ClassDef<'p> {
    self.alloc.class.alloc(ClassDef { loc: c.loc(), abst: true, name: name.str(), parent, field, parent_ref: dft(), scope: dft() })
  }
  
//syntax/src/ast.rs
pub struct ClassDef<'a> {
  ......
  pub abst: bool,   //区分abstract class or concrete class
```

此时`commit 42d352e3dbe794b53d0d58b6d2804aa1138c691c`

编译能通过，且可以运行！但所有的abs 相关testcase出错。挑一个abstract5.decaf看看：

```java
abstract class Abstract {
    int v;
    static void s() { }
    abstract int a();
    int m(); // error
}

class Main {
    static void main() {
        class Abstract a = new Abstract(); // pass PA1
    }
}
```

看到的运行报错信息是`*** Error at (1,1): syntax error`。

```
testcase/S1/abstract5.decaf: Fail: first different line on 1
your line: "*** Error at (4,5): syntax error" (testcase/S1/out/abstract5.result:1)
ans  line: "*** Error at (5,12): syntax error" (testcase/S1/result/abstract5.result:1)
```

这说明`abstract class`能正确处理了！但第4行的错，说明我们还没有处理`abstract fun`，接下来我们要做这方面的改进。

### step 4: 添加 abstract fun的语法产生式和对应处理

添加语法产生式

```
// syntax/src/parser.rs
......
impl<'p> Parser<'p> {
......
  #[rule(FuncDef -> Abstract Type Id LPar VarDefListOrEmpty RPar Semi)]
  fn func_def_a(&self, _a: Token, ret: SynTy<'p>, name: Token, _l: Token, param: Vec<&'p VarDef<'p>>, _r: Token, _s1: Token) -> &'p FuncDef<'p> {
    self.alloc.func.alloc(FuncDef { loc: name.loc(), name: name.str(), ret, param, static_: false, body: None, ret_param_ty: dft(), class: dft(), scope: dft() })
  }

```

由于abstract fun没有函数体，所以要修改abstract fun中block的类型，允许abstract fun函数体为空。

```
// syntax/src/ast.rs
pub struct FuncDef<'a> {
  ......
  pub body: Option<Block<'a>>,
```

为此还要进一步分布在各个文件中的对body的处理。

```
 print/src/scope.rs        |    2 +-
 tacgen/src/lib.rs         |    2 +-
 typeck/src/symbol_pass.rs |    2 +-
 typeck/src/type_pass.rs   |    9 +-
```

此时commit dbbfd733acee546554b3863632cf80a603269129 。运行代码，testcase abs 1,3,5 passed, 4,8 failed。

```
testcase/S1/abstract4.decaf: Fail: first different line on 8
your line: "                    foo" (testcase/S1/out/abstract4.result:8)
ans  line: "                    ABSTRACT" (testcase/S1/result/abstract4.result:8)
testcase/S1/abstract5.decaf: Pass
testcase/S1/abstract8.decaf: Fail: first different line on 4
your line: "            Abstract" (testcase/S1/out/abstract8.result:4)
ans  line: "            ABSTRACT" (testcase/S1/result/abstract8.result:4)

```

看出，是输出字符不匹配，修改相关文件`print\src\ast.rs`的相关函数的输出字符串部分，就可以解决了。

此时commit d26ad82b。运行代码，ast相关testcase都通过了。

### step 5:  添加lambda语法产生式和对应处理

看看[lambda的文法](https://decaf-project.gitbook.io/decaf-2019/new-features#wen-fa-2)，主要扩展了`expr`和`call`。所以，我们也要进行扩展。

添加 token `Arrow, LPar`

```
// syntax/src/parser.rs

#[lex(r##"
priority = [
  { assoc = 'right', terms = ['Arrow'] },
  ......
  { assoc = 'no_assoc', terms = ['LBrk', 'Dot'] },
  { assoc = 'left', terms = ['LPar'] },
......
[lexical]
......
'fun' = 'Fun'
'=>' = 'Arrow'
```

添加产生式

```
// syntax/src/parser.rs
  //函数调用
  #[rule(Expr -> Expr LPar ExprListOrEmpty RPar)]
  #[prec(LPar)]
  fn expr_call(func: Expr<'p>, l: Token, arg: Vec<Expr<'p>>, _r: Token) -> Expr<'p> {
    mk_expr(l.loc(), Call { func: Box::new(func), arg, func_ref: dft() }.into())
  }
  //函数体为表达式形式的lambda
  #[rule(Expr -> Fun LPar VarDefListOrEmpty RPar Arrow Expr)]
  fn expr_lambda_e(f: Token, _l: Token, param: Vec<&'p VarDef<'p>>, _r: Token, _a: Token, e: Expr<'p>) -> Expr<'p> {
    mk_expr(f.loc(), Lambda { param, body: Box::new(LambdaBody::Expr(e)), scope: dft() }.into())
  }
  //函数体为语句形式的lambda
  #[rule(Expr -> Fun LPar VarDefListOrEmpty RPar Block)]
  fn expr_lambda_b(f: Token, _l: Token, param: Vec<&'p VarDef<'p>>, _r: Token, b: Block<'p>) -> Expr<'p> {
    mk_expr(f.loc(), Lambda { param, body: Box::new(LambdaBody::Block(b)), scope: dft() }.into())
  }
```

对表达式的Type的产生式也进行了扩展，支持fun type的表示

```
  #[rule(Type -> Type LPar TypeListOrEmpty RPar)]
  fn type_func(ret: SynTy<'p>, _l: Token, mut param: Vec<SynTy<'p>>, _r: Token) -> SynTy<'p> {
    let loc = ret.loc;
    param.insert(0, ret);
    SynTy { loc, arr: 0, kind: SynTyKind::Func(param.into()) }
  }

  #[rule(TypeList -> TypeList Comma Type)]
  fn type_list(l: Vec<SynTy<'p>>, _c: Token, r: SynTy<'p>) -> Vec<SynTy<'p>> { l.pushed(r) }
  #[rule(TypeList -> Type)]
  fn type_list1(t: SynTy<'p>) -> Vec<SynTy<'p>> { vec![t] }

  #[rule(TypeListOrEmpty -> TypeList)]
  fn type_list_or_empty1(t: Vec<SynTy<'p>>) -> Vec<SynTy<'p>> { t }
  #[rule(TypeListOrEmpty ->)]
  fn type_list_or_empty0() -> Vec<SynTy<'p>> { vec![] }
```

做完词素和产生式定义后，还需添加lambda的函数表达式类型：

```
// syntax/src/ty.rs
pub enum SynTyKind<'a> {
  Int,
  ......
  // [0] = ret, [1..] = param
  Func(Box<[SynTy<'a>]>),
}
```

此时commit 76d537618f6853b506bdf446e24ee664d9bd2356 。有进步，但编译出错，可以看到大部分是由于修改了`enum SynTyKind`后，在某些地方进行`match`处理时没有对`Func`进行处理。修改相关文件，注意`print/src/ast.rs`中的输出处理。此时commit  eca49d527e450c5ec1cee493335bbf0913a7c69a。编译，lambda测试用例通过！

## QA

### 如何理解代码`pub(crate) ...` 

```
//syntax/src/lib.rs
pub(crate) fn dft<T: Default>() -> T { T::default() }
```

表示 此函数在此crate范围内是pub的。