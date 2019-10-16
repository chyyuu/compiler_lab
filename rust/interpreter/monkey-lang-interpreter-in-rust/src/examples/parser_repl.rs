extern crate monkey;

use std::io;
use monkey::lexer::*;
use monkey::parser::*;

fn start<R: io::BufRead, W: io::Write>(mut reader: R, mut writer: W) -> io::Result<()> {

    loop {
        writer.write(b"> ");
        writer.flush();
        let mut line = String::new();
        reader.read_line(&mut line)?;

        let l = Lexer::new(&line);

//        println!("======== Lexer Output ===========");
//        for t in l {
//            println!("{:?}",t);
//        }

        let mut p = Parser::new(l);
        let prog = p.parse_program().unwrap();

        println!("======== Parser Output ===========");
        println!("{}",prog.to_string());

        for s in prog.statements {
            println!("{:?}",s);
        }
    }
    Ok(())
}

fn main() -> io::Result<()> {
    println!("Welcome to the Monkey Parser REPL!");
    let input = io::stdin();
    let output = io::stdout();
    let result = start(input.lock(), output.lock());
    result
}