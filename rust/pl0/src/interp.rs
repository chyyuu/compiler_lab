use std::collections::HashMap;
use std::ops::Neg;
use std::process;
use crate::ast::{Block, Expr, Literal, Type};

#[derive(Clone, Debug)]
pub enum EnvVal {
    Number(i32),
    ProcVal(Block),
}

#[derive(Default)]
pub struct Interp {
    pub env: HashMap<String, EnvVal>
}

impl Interp {
    pub fn new() -> Interp {
        Interp {
            env: HashMap::new()
        }
    }

    pub fn eval(&mut self, program: Block) {
        match program {
            Block::Program(p) => self.eval(*p),
            Block::Block(consts, vars, procs, stmts) => {
                self.extend_env_consts(*consts);
                self.extend_env_vars(*vars);
                self.extend_env_procs(procs);
                self.eval(*stmts);
            },
            Block::Begin(stmts) => {
                for stmt in stmts {
                    self.eval(stmt);
                }
            },
            Block::If(expr, block) => {
                let val = self.eval_expr(expr);
                if val > 0 {
                    self.eval(*block);
                }
            },
            Block::Assign(var, expr) => {
                let val = self.eval_expr(expr);
                if let Expr::Var(s) = var {
                    self.env.insert(s, EnvVal::Number(val));
                }
            },
            Block::WriteLn(expr) => {
                println!("{}", self.eval_expr(expr));
            },
            Block::While(expr, stmt) => {
                loop {
                    let val = self.eval_expr(expr.clone());
                    if val < 1 {
                        break;
                    }

                    self.eval(*stmt.clone());
                }
            },
            Block::Call(expr) => {
                if let Expr::Var(v) = expr {
                    let procval = self.env.get(&v);
                    if procval.is_none() {
                        eprintln!("function {} not defined", v);
                        process::exit(1);
                    }
                    let procval = procval.unwrap().clone();
                    if let EnvVal::ProcVal(p) = procval {
                        self.eval(p.to_owned());
                    } else {
                        eprintln!("{} is not a function", v);
                        process::exit(1);
                    }
                }
            },
            _ => (),
        }
    }

    fn eval_expr(&mut self, expr: Expr) -> i32 {
        match expr {
            Expr::Literal(l) => {
                let Literal::Number(n) = l;
                n
            },
            Expr::Var(v) => {
                let val = self.env.get(&v);
                if val.is_none() {
                    eprintln!("variable {} not declared", v);
                    process::exit(1);
                }

                if let EnvVal::Number(n) = val.unwrap() {
                    return *n;
                } else {
                    return 0;
                }
            },
            Expr::PrefixExpr(prefix, expr) => {
                if prefix.is_some() {
                    let prefix = prefix.unwrap();
                    match prefix {
                        Type::Minus => self.eval_expr(*expr).neg(),
                        Type::Plus => self.eval_expr(*expr),
                        _ => self.eval_expr(*expr),
                    }
                } else {
                    self.eval_expr(*expr)
                }
            },
            Expr::Expr(left, sign, right) => {
                match sign {
                    Type::Plus => self.eval_expr(*left) + self.eval_expr(*right),
                    Type::Minus => self.eval_expr(*left) - self.eval_expr(*right),
                    Type::Star => self.eval_expr(*left) * self.eval_expr(*right),
                    Type::Slash => self.eval_expr(*left) / self.eval_expr(*right),
                    Type::Greater => {
                        let left = self.eval_expr(*left);
                        let right = self.eval_expr(*right);
                        if left > right {
                            1
                        } else {
                            0
                        }
                    },
                    Type::GreaterEqual => {
                        let left = self.eval_expr(*left);
                        let right = self.eval_expr(*right);
                        if left >= right {
                            1
                        } else {
                            0
                        }
                    },
                    Type::Less => {
                        let left = self.eval_expr(*left);
                        let right = self.eval_expr(*right);
                        if left < right {
                            1
                        } else {
                            0
                        }
                    },
                    Type::LessEqual => {
                        let left = self.eval_expr(*left);
                        let right = self.eval_expr(*right);
                        if left <= right {
                            1
                        } else {
                            0
                        }
                    },
                    Type::Equal => {
                        let left = self.eval_expr(*left);
                        let right = self.eval_expr(*right);
                        if left == right {
                            1
                        } else {
                            0
                        }
                    },
                    Type::Hash => {
                        let left = self.eval_expr(*left);
                        let right = self.eval_expr(*right);
                        if left != right {
                            1
                        } else {
                            0
                        }
                    },
                    _ => 0
                }
            },
            Expr::OddExpr(expr) => {
                let val = self.eval_expr(*expr);
                if val % 2 == 1 {
                    1
                } else {
                    0
                }
            },
            Expr::Group(expr) => self.eval_expr(*expr),
        }
    }

    fn extend_env_consts(&mut self, block: Block) {
        if let Block::ConstDecs(cds) = block {
            for cd in cds {
                if let Block::Const(Expr::Var(s),
                                    Expr::Literal(l)) = cd {
                    let Literal::Number(n) = l;
                    self.env.insert(s, EnvVal::Number(n));
                }
            }
        }
    }

    fn extend_env_vars(&mut self, block: Block) {
        if let Block::VarDecs(vds) = block {
            for v in vds {
                if let Expr::Var(s) = v {
                    self.env.insert(s, EnvVal::Number(0));
                }
            }
        }
    }

    fn extend_env_procs(&mut self, block: Vec<Block>) {
        for b in block {
            if let Block::Procedure(name, body) = b {
                if let Expr::Var(v) = name {
                    let procval = EnvVal::ProcVal(*body);
                    self.env.insert(v, procval);
                }
            }
        }
    }
}
