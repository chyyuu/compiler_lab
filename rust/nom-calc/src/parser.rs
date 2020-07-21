extern crate nom;
use nom::{
    branch::alt,
    bytes::complete::tag,
    bytes::complete::take_while,
    character::complete::{alpha1, char},
    combinator::map,
    multi::many0,
    number::complete::double,
    sequence::{delimited, preceded, tuple},
    Err, error::ErrorKind, Needed,
    IResult,
};

#[derive(Debug, PartialEq)]
pub enum ParsedFactor<'a> {
    Literal(f64),
    Identifier(&'a str),
    SubExpression(Box<ParsedExpr<'a>>),
}

#[derive(Debug, PartialEq, Clone, Copy)]
pub enum TermOperator {
    Multiply,
    Divide,
}

#[derive(Debug, PartialEq, Clone, Copy)]
pub enum ExprOperator {
    Add,
    Subtract,
}

pub type ParsedTerm<'a> = (ParsedFactor<'a>, Vec<(TermOperator, ParsedFactor<'a>)>);

pub type ParsedExpr<'a> = (ParsedTerm<'a>, Vec<(ExprOperator, ParsedTerm<'a>)>);

fn parse_factor(input: &str) -> IResult<&str, ParsedFactor> {
    preceded(
        skip_spaces,
        alt((
            map(parse_identifier, ParsedFactor::Identifier),
            map(double, ParsedFactor::Literal),
            map(parse_subexpr, |expr| {
                ParsedFactor::SubExpression(Box::new(expr))
            }),
        )),
    )(input)
}

fn parse_term(input: &str) -> IResult<&str, ParsedTerm> {
    tuple((
        parse_factor,
        many0(tuple((
            preceded(
                skip_spaces,
                alt((
                    map(char('*'), |_| TermOperator::Multiply),
                    map(char('/'), |_| TermOperator::Divide),
                )),
            ),
            parse_factor,
        ))),
    ))(input)
}

fn parse_expr(input: &str) -> IResult<&str, ParsedExpr> {
    tuple((
        parse_term,
        many0(tuple((
            preceded(
                skip_spaces,
                alt((
                    map(char('+'), |_| ExprOperator::Add),
                    map(char('-'), |_| ExprOperator::Subtract),
                )),
            ),
            parse_term,
        ))),
    ))(input)
}


fn parse_subexpr(input: &str) -> IResult<&str, ParsedExpr> {
    delimited(
        preceded(skip_spaces, char('(')),
        parse_expr,
        preceded(skip_spaces, char(')')),
    )(input)
}


fn skip_spaces(input: &str) -> IResult<&str, &str> {
    let chars = " \t\r\n";
    take_while(move |ch| chars.contains(ch))(input)
}

fn parse_identifier(input: &str) -> IResult<&str, &str> {
    alpha1(input)
}


#[cfg(test)]
mod tests {
    use  super::*;
    #[test]
    fn test_parse_ident() {
        assert_eq!(parse_identifier("aB1c"), Ok(("1c", "aB")));
        assert_eq!(parse_identifier("1c"), Err(Err::Error(("1c", ErrorKind::Alpha))));
        assert_eq!(parse_identifier(""), Err(Err::Error(("", ErrorKind::Alpha))));
    }
    #[test]
    fn test_parse_factor() {
        assert_eq!(parse_factor("aB"), Ok(("",ParsedFactor::Identifier("aB"))));
        assert_eq!(parse_factor("6.8"), Ok(("",ParsedFactor::Literal(6.8))));
        //assert_eq!(parse_factor("a/b"), Ok(("",ParsedFactor::SubExpression(Box(6)))));
        //assert_eq!(parse_factor(" a * b"), Ok(("",ParsedFactor::Literal(6.8))));
    }
}