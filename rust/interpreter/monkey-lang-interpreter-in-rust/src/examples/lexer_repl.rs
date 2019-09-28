extern crate monkey;

use std::io;
use monkey::lexer::*;

fn start<R: io::BufRead, W: io::Write>(mut reader: R, mut writer: W) -> io::Result<()> {

    loop {
        writer.write(b"> ");
        writer.flush();
        let mut line = String::new();
        reader.read_line(&mut line)?;

        let l = Lexer::new(&line);
        for t in l {
            println!("{:?}",t);
        }
    }
    Ok(())
}

fn main() -> io::Result<()> {
    println!("Welcome to the Monkey Lexer REPL!");
    let input = io::stdin();
    let output = io::stdout();
    let result = start(input.lock(), output.lock());
    result
}