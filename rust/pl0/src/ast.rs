#[derive(Clone, Copy, Debug, PartialEq)]
pub enum Type {
    Bang,                   // !   X
    Begin,     // reserved
    Call,      // reserved
    Comma,                  // ,   X
    Const,     // reserved
    Do,        // reserved
    Dot,                    // .   X
    End,       // reserved
    Equal,                  // =   X
    Greater,                // >   X
    GreaterEqual,           // >=  X
    Hash,                   // #   X
    Identifier,
    If,        // reserved
    LeftParen,              // (   X
    Less,                   // <   X
    LessEqual,              // <=  X
    Minus,                  // -   X
    Number,
    Odd,       // reserved
    Plus,                   // +   X
    Procedure, // reserved
    Question,               // ?   X
    RightParen,             // )   X
    ColonEqual,             // :=  X
    Semicolon,              // ;   X
    Slash,                  // /
    Star,                   // *
    Then,      // reserved
    Var,       // reserved
    While,     // reserved

    EOF,
}

#[derive(Clone, Debug)]
pub enum Literal {
    Number(i32)
}

#[derive(Clone, Debug)]
pub struct Token {
    pub r#type: Type,
    pub lexeme: String,
    pub literal: Option<Literal>,
    pub line: u32
}

impl Token {
    pub fn new(r#type: Type,
           lexeme: String,
           literal: Option<Literal>,
           line: u32) -> Token {
        Token {
            r#type, lexeme, literal, line
        }
    }
}

#[derive(Clone, Debug)]
pub enum Expr {
    Expr(Box<Expr>, Type, Box<Expr>),
    Literal(Literal),
    OddExpr(Box<Expr>),
    PrefixExpr(Option<Type>, Box<Expr>),
    Var(String),
    Group(Box<Expr>),
}

#[derive(Clone, Debug)]
pub enum Block {
    Assign(Expr, Expr),
    Begin(Vec<Block>),
    Block(Box<Block>, Box<Block>, Vec<Block>, Box<Block>),
    Call(Expr),
    Const(Expr, Expr),
    ConstDecs(Vec<Block>),
    If(Expr, Box<Block>),
    Procedure(Expr, Box<Block>),
    Program(Box<Block>),
    VarDecs(Vec<Expr>),
    While(Expr, Box<Block>),
    WriteLn(Expr),
}
