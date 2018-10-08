//! This crate is specifically used for one thing: turning expressions inside of a string
//! into a value. This crate acts as a scientific calculator, and includes several functions.
//! 
//! If you need a portion of the calculator changed or removed, please fork it, and make your
//! changes. We encourage others to change RSC to their liking. You do not need to attribute
//! anything to us. This is MIT licensed software.

pub mod lexer;
pub mod parser;
pub mod computer;

pub enum EvalError {
    ParserError(parser::ParserError),
    LexerError(lexer::LexerError),
}

/// Turn an expression inside a string into a number.
/// If you are looking for more control, you may want to use
/// the `lexer`, `parser`, and `computer` modules individually.
pub fn eval(input: &str) -> Result<f64, EvalError> {
    match lexer::tokenize(input) {
        Ok(tokens) => match parser::parse(&tokens) {
            Ok(ast) => Ok(computer::compute(&ast)),
            Err(parser_err) => Err(EvalError::ParserError(parser_err)),
        }
        Err(lexer_err) => Err(EvalError::LexerError(lexer_err)),
    }
}
