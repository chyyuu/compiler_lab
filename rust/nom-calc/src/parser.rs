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
    IResult,
};


fn parse_identifier(input: &str) -> IResult<&str, &str> {
    alpha1(input)
}


#[cfg(test)]
mod tests {
    #[test]
    fn it_works() {
        assert_eq!(2 + 2, 4);
    }
}