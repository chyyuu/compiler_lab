use parser_macros::ll1;
use hashbrown::{HashSet, HashMap};

struct Parser;

pub enum Op {
  Add,
  Sub,
  Mul,
  Div,
  Mod,
}

#[ll1(Expr)]
#[log_token]
#[log_reduce]
#[expand]
#[verbose("verbose.txt")]
#[show_dfa("dfa.dot")]
#[lex(r#"
priority = []

[lexical]
'\(' = 'LParen'
'\)' = 'RParen'
'\d+' = 'IntConst'
'\+' = 'Add'
'-' = 'Sub'
'\*' = 'Mul'
'/' = 'Div'
'%' = 'Mod'
'\d+' = 'IntConst'
'\s+' = '_Eps'
"#)]
impl Parser {
  #[rule(Expr -> Term1 Expr1)]
  fn r0(mut t: i32, remain: Vec<(Op, i32)>) -> i32 {
    for (op, i) in remain.into_iter().rev() {
      match op {
        Op::Add => t += i,
        Op::Sub => t -= i,
        _ => unreachable!(),
      }
    }
    t
  }

  #[rule(Expr1 -> Add Term1 Expr1)]
  fn r1(_op: Token<'_>, t: i32, mut remain: Vec<(Op, i32)>) -> Vec<(Op, i32)> {
    remain.push((Op::Add, t));
    remain
  }

  #[rule(Expr1 -> Sub Term1 Expr1)]
  fn r2(_op: Token<'_>, t: i32, mut remain: Vec<(Op, i32)>) -> Vec<(Op, i32)> {
    remain.push((Op::Sub, t));
    remain
  }

  #[rule(Expr1 ->)]
  fn r3() -> Vec<(Op, i32)> {
    vec![]
  }

  #[rule(Term1 -> Term2 Expr2)]
  fn r4(mut t: i32, remain: Vec<(Op, i32)>) -> i32 {
    for (op, i) in remain.into_iter().rev() {
      match op {
        Op::Mul => t *= i,
        Op::Div => t /= i,
        Op::Mod => t %= i,
        _ => unreachable!(),
      }
    }
    t
  }

  #[rule(Expr2 -> Mul Term2 Expr2)]
  fn r5(_op: Token<'_>, t: i32, mut remain: Vec<(Op, i32)>) -> Vec<(Op, i32)> {
    remain.push((Op::Mul, t));
    remain
  }

  #[rule(Expr2 -> Div Term2 Expr2)]
  fn r6(_op: Token<'_>, t: i32, mut remain: Vec<(Op, i32)>) -> Vec<(Op, i32)> {
    remain.push((Op::Div, t));
    remain
  }

  #[rule(Expr2 -> Mod Term2 Expr2)]
  fn r7(_op: Token<'_>, t: i32, mut remain: Vec<(Op, i32)>) -> Vec<(Op, i32)> {
    remain.push((Op::Mod, t));
    remain
  }

  #[rule(Expr2 ->)]
  fn r8() -> Vec<(Op, i32)> {
    vec![]
  }

  #[rule(Term2 -> IntConst)]
  fn r9(i: Token<'_>) -> i32 {
    let int = std::str::from_utf8(i.piece).unwrap().parse::<i32>().unwrap();
    int
  }

  #[rule(Term2 -> Sub Term2)]
  fn r10(i: Token<'_>, r: i32) -> i32 {
    -r
  }

  #[rule(Term2 -> LParen Expr RParen)]
  fn r11(_l: Token<'_>, x: i32, _r: Token<'_>) -> i32 {
    x
  }
}

impl Parser {
  fn error(&mut self) {
    println!("error")
  }

  // parse impl with some error recovering(not fully unimplemented yet)
  // will be called be the generated parse function
  fn _parse<'a>(&mut self, target: u32, lookahead: &mut Token<'a>, lexer: &mut Lexer<'a>, f: &HashSet<u32>) -> StackItem<'a> {
    let target = target as usize;
    let follow: &[HashSet<u32>] = &*FOLLOW;
    let table: &[HashMap<u32, (u32, Vec<u32>)>] = &*TABLE;
    let is_nt = |x: u32| x < NT_NUM;

    let mut end = f.clone();
    end.extend(follow[target].iter());
    match table[target].get(&(lookahead.ty as u32)) {
      None => {
        unimplemented!()
      }
      Some((act, rhs)) => {
        let value_stk = rhs.iter().map(|&x| {
          if is_nt(x) {
            self._parse(x, lookahead, lexer, &end)
          } else {
            if (lookahead.ty as u32) == x {
              let token = *lookahead;
              *lookahead = lexer.next();
              StackItem::_Token(token)
            } else {
              self.error();
              StackItem::_Fail
            }
          }
        }).collect::<Vec<_>>();
        self.act(*act, value_stk)
      }
    }

  }
}

fn main() {
  let mut p = Parser;
  assert_eq!(p.parse(&mut Lexer::new(b"1 - 2 * (3 + 4 * 5 / 6) + -7 * -9 % 10")), Some(-8));
}
