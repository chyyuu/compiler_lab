use std::collections::HashMap;

use crate::ast::{Literal, Token, Type};

pub struct Scanner {
    source: Vec<char>,
    pub tokens: Vec<Token>,
    reserved: HashMap<&'static str, Type>,
    start: usize,
    current: usize,
    line: u32,
}

impl Scanner {
    pub fn new(source: Vec<char>) -> Scanner {
        let mut reserved = HashMap::new();
        reserved.insert("begin", Type::Begin);
        reserved.insert("call", Type::Call);
        reserved.insert("const", Type::Const);
        reserved.insert("do", Type::Do);
        reserved.insert("end", Type::End);
        reserved.insert("if", Type::If);
        reserved.insert("odd", Type::Odd);
        reserved.insert("procedure", Type::Procedure);
        reserved.insert("then", Type::Then);
        reserved.insert("var", Type::Var);
        reserved.insert("while", Type::While);

        Scanner {
            source,
            tokens: Vec::new(),
            reserved,
            start: 0,
            current: 0,
            line: 1
        }
    }

    pub fn scan_tokens(&mut self) {
        while !self.is_at_end() {
            self.start = self.current;
            self.scan_token();
        }

        self.add_token(Type::EOF);
    }

    fn scan_token(&mut self) {
        let c: char = self.advance();
        match c {
            '(' => self.add_token(Type::LeftParen),
            ')' => self.add_token(Type::RightParen),
            ',' => self.add_token(Type::Comma),
            '.' => self.add_token(Type::Dot),
            '-' => self.add_token(Type::Minus),
            '+' => self.add_token(Type::Plus),
            ';' => self.add_token(Type::Semicolon),
            '*' => self.add_token(Type::Star),
            '#' => self.add_token(Type::Hash),
            '=' => self.add_token(Type::Equal),
            '?' => self.add_token(Type::Question),
            '!' => self.add_token(Type::Bang),
            '<' => {
                if self.match_char('=') {
                    self.add_token(Type::LessEqual);
                } else {
                    self.add_token(Type::Less);
                }
            },
            '>' => {
                if self.match_char('=') {
                    self.add_token(Type::GreaterEqual);
                } else {
                    self.add_token(Type::Greater);
                }
            },
            ':' => {
                if self.match_char('=') {
                    self.add_token(Type::ColonEqual);
                }
            },
            '/' => {
                if self.match_char('/') {
                    while (self.peek() != '\n') && !self.is_at_end() {
                        self.advance();
                    }
                } else {
                    self.add_token(Type::Slash);
                }
            },
            ' ' => return,
            '\t' => return,
            '\r' => return,
            '\n' => {
                self.line += 1;
                return;
            },
            _   => {
                if Scanner::is_digit(c) {
                    self.number();
                } else if Scanner::is_alpha(c) {
                    self.identifier();
                } else {
                    self.error("Unknown character");
                }
            },
        }
    }

    fn add_token(&mut self, token: Type) {
        self.add_literal_token(token, None);
    }

    fn add_literal_token(&mut self, token: Type, literal: Option<Literal>) {
        let lexeme = self.source[self.start..self.current].to_vec();
        let lexeme = lexeme.iter().collect();
        let token = Token::new(token, lexeme, literal, self.line);
        self.tokens.push(token);
    }

    fn advance(&mut self) -> char {
        self.current += 1;
        self.source[self.current - 1]
    }

    fn is_at_end(&self) -> bool {
        self.current >= self.source.len()
    }

    fn match_char(&mut self, expected: char) -> bool {
        if self.is_at_end() {
            return false;
        }

        if self.source[self.current] != expected {
            return false;
        }

        self.current += 1;
        true
    }

    fn peek(&mut self) -> char {
        if self.is_at_end() {
            return '\0';
        }

        self.source[self.current]
    }

    fn is_digit(c: char) -> bool {
        c >= '0' && c <= '9'
    }

    fn is_alpha(c: char) -> bool {
        (c >= 'a' && c <= 'z') ||
        (c >= 'A' && c <= 'Z') ||
        c == '_'
    }

    fn is_alpha_numeric(c: char) -> bool {
        Scanner::is_alpha(c) || Scanner::is_digit(c)
    }

    fn number(&mut self) {
        while Scanner::is_digit(self.peek()) {
            self.advance();
        }

        let slice: Vec<char> = self.source[self.start..self.current].to_vec();
        let slice: String = slice.iter().collect();
        let digit: i32 = match slice.parse() {
            Ok(d) => d,
            Err(_) => {
                self.error("Failed to parse digit");
                0
            }
        };
        self.add_literal_token(Type::Number, Some(Literal::Number(digit)));
    }

    fn identifier(&mut self) {
        while Scanner::is_alpha_numeric(self.peek()) {
            self.advance();
        }

        let slice: Vec<char> = self.source[self.start..self.current].to_vec();
        let slice: String = slice.iter().collect();

        match self.reserved.get(&slice.as_str()) {
            Some(&t) => self.add_token(t),
            None => self.add_token(Type::Identifier),
        }
    }

    fn error(&self, message: &str) {
        println!("Error at line {}, {}, character {}",
            self.line, message, self.source[self.current - 1]);
    }
}
