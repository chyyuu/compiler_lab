//! For making notable symbols and words out of text.

#[derive(Debug, Copy, Clone, PartialEq, Eq)]
pub enum Operator {
    Plus,
    Minus,
    Star,
    Slash,
    Percent,
    Caret,
    LParen,
    RParen,
}
use self::Operator::*;

#[derive(Debug, Copy, Clone, PartialEq, Eq)]
pub enum Function {
    // Single-argument functions
    Sqrt,
    Sin,
    Cos,
    Tan,
    Log,
}
use self::Function::*;

#[derive(Debug, Copy, Clone, PartialEq, Eq)]
pub enum Constant {
    Pi,
    E
}
use self::Constant::*;

#[derive(Debug, Copy, Clone, PartialEq)]
pub enum Token {
    Number(f64),
    Operator(Operator),
    Function(Function),
    Constant(Constant),
}

#[derive(Debug, Clone, PartialEq)]
pub enum LexerError {
    InvalidCharacter(char),
    InvalidNumber(String),
    InvalidIdentifier(String),
}

/// Turn a string into a vector of tokens.
pub fn tokenize(input: &str) -> Result<Vec<Token>, LexerError> {
    let mut tokens = Vec::<Token>::new();

    let chars: Vec<char> = input.chars().collect();

    let mut i = 0usize;
    while i < chars.len() {
        match chars[i] {
            '+' => tokens.push(Token::Operator(Plus)),
            '-' => tokens.push(Token::Operator(Minus)),
            '*' | '•' | '×' => tokens.push(Token::Operator(Star)),
            '/' | '÷' => tokens.push(Token::Operator(Slash)),
            '%' => tokens.push(Token::Operator(Percent)),
            '^' => tokens.push(Token::Operator(Caret)),
            '(' => tokens.push(Token::Operator(LParen)),
            ')' => tokens.push(Token::Operator(RParen)),
            '√' => tokens.push(Token::Function(Sqrt)),
            'π' => tokens.push(Token::Constant(Pi)),
            c => {
                if c.is_whitespace() {
                    i += 1;
                    continue;
                } else if c.is_digit(10) || c == '.' {
                    let mut number_string = c.to_string(); // Like creating a new string and pushing the character.
                    
                    i += 1;
                    while i < chars.len() && (chars[i].is_digit(10) || chars[i] == '.') {
                        number_string.push(chars[i]);
                        i += 1;
                    }

                    match number_string.parse::<f64>() {
                        Ok(num) => tokens.push(Token::Number(num)),
                        _ => return Err(LexerError::InvalidNumber(number_string)),
                    }

                    continue; // We i += 1 at end of latest while.
                } else if c.is_alphabetic() {
                    let mut full_identifier = c.to_string();

                    i += 1; // Step over first character of identifier.
                    // While we're still reading alphabetical characters.
                    while i < chars.len() && chars[i].is_alphabetic() {
                        full_identifier.push(chars[i]);
                        i += 1;
                    }

                    match &full_identifier.to_lowercase()[..] {
                        // Constants
                        "pi" => tokens.push(Token::Constant(Pi)),
                        "e" => tokens.push(Token::Constant(E)),

                        // Functions
                        "sqrt" => tokens.push(Token::Function(Sqrt)),
                        "sin" => tokens.push(Token::Function(Sin)),
                        "cos" => tokens.push(Token::Function(Cos)),
                        "tan" => tokens.push(Token::Function(Tan)),
                        "log" => tokens.push(Token::Function(Log)),
                        _ => return Err(LexerError::InvalidIdentifier(full_identifier)),
                    }

		            continue;
                } else {
                    return Err(LexerError::InvalidCharacter(c));
                }
            }
        }
        i += 1;
    }
    
    Ok(tokens)
}
