/// https://mashplant.gitbook.io/decaf-doc/pa1a/lalr1-shi-yong-zhi-dao/yi-ge-wan-zheng-de-li-zi
use parser_macros::lalr1;
struct Parser; // 用户希望成为parser的struct

#[lalr1(Expr)]
#[verbose("verbose.txt")]
#[log_token]
#[log_reduce]
#[expand]
#[show_fsm("fsm.dot")]
#[show_dfa("dfa.dot")]
#[lex(r#"
# 描述终结符的优先级和结合性，越靠后优先级越高；结合性分为left，right和no_assoc
priority = [
  { assoc = 'left', terms = ['Add', 'Sub'] },
  { assoc = 'left', terms = ['Mul', 'Div', 'Mod'] },
  { assoc = 'no_assoc', terms = ['UMinus'] },
  { assoc = 'no_assoc', terms = ['RPar'] },
]

# 描述识别出终结符的正则表达式
[lexical]
'\(' = 'LPar'
'\)' = 'RPar'
'\+' = 'Add'
'-' = 'Sub'
'\*' = 'Mul'
'/' = 'Div'
'%' = 'Mod'
'\d+' = 'IntLit'
'\s+' = '_Eps'
"#)]

impl Parser {
  // 为了简单起见，这里都没有实现错误处理
  // 任何一个非终结符的类型必须在整个parser中是统一的，例如这里的Expr是i32类型
  // 任何一个终结符必须具有Token类型
  // 函数的名字其实是可以随便取的，最终的代码中并不会被保留，不过为了可读性还是最好符合本条规则的含义
  #[rule(Expr -> Expr Add Expr)]
  fn expr_add(l: i32, _op: Token, r: i32) -> i32 { l + r }
  #[rule(Expr -> Expr Sub Expr)]
  fn expr_sub(l: i32, _op: Token, r: i32) -> i32 { l - r }
  #[rule(Expr -> Expr Mul Expr)]
  fn expr_mul(l: i32, _op: Token, r: i32) -> i32 { l * r }
  #[rule(Expr -> Expr Div Expr)]
  fn expr_div(l: i32, _op: Token, r: i32) -> i32 { l / r }
  #[rule(Expr -> Expr Mod Expr)]
  fn expr_mod(l: i32, _op: Token, r: i32) -> i32 { l % r }
  #[rule(Expr -> Sub Expr)]
  #[prec(UMinus)] // 本条产生式与UMinus相同，比二元运算符都高
  fn expr_neg(_op: Token, r: i32) -> i32 { -r }
  #[rule(Expr -> LPar Expr RPar)]
  fn expr_paren(_l: Token, i: i32, _r: Token) -> i32 { i }
  #[rule(Expr -> IntLit)]
  fn expr_int(i: Token) -> i32 { std::str::from_utf8(i.piece).unwrap().parse().unwrap() }
}

fn main() {
  assert_eq!(Parser.parse(&mut Lexer::new(b"1 - 2 * (3 + 4 * 5 / 6) + -7 * -9 % 10")), Ok(-8));
}

