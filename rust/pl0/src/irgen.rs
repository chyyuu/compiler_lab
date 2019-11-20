use std::collections::HashMap;
use crate::ast::{Block, Expr, Literal, Type};
use crate::ir::{IR, Label, Line};

#[derive(Default)]
pub struct IRGen {
    pub symbol_table: HashMap<String, Label>,
    pub const_table: HashMap<String, i32>,
    pub code: Vec<Line>,
    sym: u32,
    label: u32,
}

impl IRGen {
    pub fn new() -> IRGen {
        IRGen {
            symbol_table: HashMap::new(),
            const_table: HashMap::new(),
            code: Vec::new(),
            sym: 0,
            label: 0,
        }
    }

    fn make_symbol(&mut self) -> Label {
        let s = format!("sym{}", self.sym);
        self.sym += 1;
        s
    }

    fn make_label(&mut self) -> Label {
        let s = format!("l{}", self.label);
        self.label += 1;
        s
    }

    pub fn gen(&mut self, program: Block) {
        match program {
            Block::Program(p) => {
                self.gen(*p);
                self.code.push(Line::new(None, IR::HALT));
                self.remove_noops();
            },
            Block::Block(consts, vars, procs, stmts) => {
                self.gen_consts(*consts);
                self.gen_vars(*vars);
                self.gen_procs(procs);
                self.gen(*stmts);
            },
            Block::Begin(stmts) => {
                for stmt in stmts {
                    self.gen(stmt);
                }
            },
            Block::If(expr, block) => {
                let label = self.make_label();
                self.gen_expr(expr);
                self.code.push(Line::new(None, IR::JMZ(label.clone())));
                self.gen(*block);
                self.code.push(Line::new(Some(label.clone()), IR::NOOP));
            },
            Block::Assign(var, expr) => {
                self.gen_expr(expr);
                if let Expr::Var(s) = var {
                    let sym = self.symbol_table.get(&s);
                    if let Some(s) = sym {
                        self.code.push(Line::new(None, IR::STORE(s.to_string())))
                    }
                }
            },
            Block::WriteLn(expr) => {
                self.gen_expr(expr);
                self.code.push(Line::new(None, IR::WRITE));
            },
            Block::While(expr, stmt) => {
                let back = self.make_label();
                let forward = self.make_label();

                self.code.push(Line::new(Some(back.to_owned()), IR::NOOP));
                self.gen_expr(expr);
                self.code.push(Line::new(None, IR::JMZ(forward.to_owned())));
                self.gen(*stmt);
                self.code.push(Line::new(None, IR::JMP(back.to_owned())));
                self.code.push(Line::new(Some(forward.to_owned()), IR::NOOP));
            },
            Block::Call(expr) => {
                if let Expr::Var(v) = expr {
                    let sym = self.symbol_table.get(&v);
                    if let Some(s) = sym {
                        self.code.push(
                            Line::new(None, IR::CALL(s.to_string()))
                        );
                    }
                }
            },
            _ => (),
        }
    }

    fn gen_expr(&mut self, expr: Expr) {
        match expr {
            Expr::Literal(l) => {
                let Literal::Number(n) = l;
                self.code.push(Line::new(None, IR::LOADC(n)));
            },
            Expr::Var(v) => {
                let sym = self.symbol_table.get(&v);
                if let Some(s) = sym {
                    self.code.push(Line::new(None, IR::LOAD(s.to_string())));
                } else {
                    let sym = self.const_table.get(&v);
                    if let Some(n) = sym {
                        self.code.push(Line::new(None, IR::LOADC(*n)));
                    }
                }
            },
            Expr::PrefixExpr(prefix, expr) => {
                if prefix.is_some() {
                    let prefix = prefix.unwrap();
                    match prefix {
                        Type::Minus => {
                            self.gen_expr(*expr);
                            self.code.push(Line::new(None, IR::LOADC(-1)));
                            self.code.push(Line::new(None, IR::MUL));
                        },
                        Type::Plus => {
                            self.gen_expr(*expr);
                            self.code.push(Line::new(None, IR::LOADC(1)));
                            self.code.push(Line::new(None, IR::MUL));
                        },
                        _ => self.gen_expr(*expr),
                    }
                } else {
                    self.gen_expr(*expr)
                }
            },
            Expr::Expr(left, sign, right) => {
                match sign {
                    Type::Plus => {
                        self.gen_expr(*left);
                        self.gen_expr(*right);
                        self.code.push(Line::new(None, IR::ADD));
                    },
                    Type::Minus => {
                        self.gen_expr(*left);
                        self.gen_expr(*right);
                        self.code.push(Line::new(None, IR::SUB));
                    },
                    Type::Star => {
                        self.gen_expr(*left);
                        self.gen_expr(*right);
                        self.code.push(Line::new(None, IR::MUL));
                    },
                    Type::Slash => {
                        self.gen_expr(*left);
                        self.gen_expr(*right);
                        self.code.push(Line::new(None, IR::DIV));
                    },
                    Type::Greater => {
                        self.gen_expr(*left);
                        self.gen_expr(*right);
                        self.code.push(Line::new(None, IR::GT));
                    },
                    Type::GreaterEqual => {
                        self.gen_expr(*left);
                        self.gen_expr(*right);
                        self.code.push(Line::new(None, IR::GTE));
                    },
                    Type::Less => {
                        self.gen_expr(*left);
                        self.gen_expr(*right);
                        self.code.push(Line::new(None, IR::LT));
                    },
                    Type::LessEqual => {
                        self.gen_expr(*left);
                        self.gen_expr(*right);
                        self.code.push(Line::new(None, IR::LTE));
                    },
                    Type::Equal => {
                        self.gen_expr(*left);
                        self.gen_expr(*right);
                        self.code.push(Line::new(None, IR::EQ));
                    },
                    Type::Hash => {
                        self.gen_expr(*left);
                        self.gen_expr(*right);
                        self.code.push(Line::new(None, IR::NEQ));
                    },
                    _ => (),
                }
            },
            Expr::OddExpr(expr) => {
                self.gen_expr(*expr);
                self.code.push(Line::new(None, IR::ODD));
            },
            Expr::Group(expr) => self.gen_expr(*expr),
        }
    }

    fn gen_consts(&mut self, block: Block) {
        if let Block::ConstDecs(cds) = block {
            for cd in cds {
                if let Block::Const(Expr::Var(s),
                                    Expr::Literal(l)) = cd {
                    let Literal::Number(n) = l;
                    self.const_table.insert(s, n);
                }
            }
        }
    }

    fn gen_vars(&mut self, block: Block) {
        if let Block::VarDecs(vds) = block {
            for v in vds {
                if let Expr::Var(s) = v {
                    let sym = self.make_symbol();
                    self.symbol_table.insert(s, sym.clone());
                    self.code.push(Line::new(Some(sym.clone()), IR::DEC(0)));
                }
            }
        }
    }

    fn gen_procs(&mut self, block: Vec<Block>) {
        for b in block {
            if let Block::Procedure(name, body) = b {
                if let Expr::Var(v) = name {
                    let sym = self.make_symbol();
                    self.symbol_table.insert(v, sym.clone());
                    self.code.push(Line::new(Some(sym.clone()), IR::StartFunc));
                    self.gen(*body);
                    self.code.push(Line::new(None, IR::RET));
                }
            }
        }
    }

    /*
     * Remove NOOPS that exist just to hold labels by passing the label forward.
     */
    fn remove_noops(&mut self) {
        let mut i = 0;
        while i < self.code.len() {
            let current = self.code[i].clone();
            if let Line { inst: IR::NOOP, label: l } = current {
                if (i + 1) < self.code.len() {
                    let mut next = &mut self.code[i + 1];
                    next.label = l.clone();
                    self.code.remove(i);
                }
            }

            i += 1;
        }
    }
}
