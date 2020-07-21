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


fn parse_identifier(input: &str) -> IResult<&str, &str> {
    alpha1(input)
}


#[cfg(test)]
mod tests {
    use  super::*;
    #[test]
    fn parse_ident() {
        assert_eq!(parse_identifier("aB1c"), Ok(("1c", "aB")));
        assert_eq!(parse_identifier("1c"), Err(Err::Error(("1c", ErrorKind::Alpha))));
        assert_eq!(parse_identifier(""), Err(Err::Error(("", ErrorKind::Alpha))));
    }
}