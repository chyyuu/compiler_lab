use std::fs::File;
use std::io::prelude::*;
use std::env;
use std::path::Path;
use std::process;

use pl0::scanner::Scanner;
use pl0::parser::Parser;
//use pl0::interp::Interp;
use pl0::irgen::IRGen;
use pl0::codegen::CodeGen;
use pl0::vm::VM;

fn main() {
    let args: Vec<String> = env::args().collect();
    if args.len() != 2 {
        println!("usage: pl0 <file>");
        process::exit(64);
    } else {
        run_file(&args[1])
    }
}

fn run_file(file: &str) {
    let path = Path::new(file);
    let mut file = File::open(&path)
        .expect("Failed to open file");

    let mut source = String::new();
    file.read_to_string(&mut source)
        .expect("Failed to read file");

    let source: Vec<char> = source.chars().collect();
    run(source);
}

fn run(source: Vec<char>) {
    let mut scanner = Scanner::new(source);
    scanner.scan_tokens();

    let mut parser = Parser::new(scanner.tokens);
    let program = parser.parse().unwrap_or_else(|err| {
        eprintln!("error: {}", err);
        process::exit(1);
    });

    //let mut interp = Interp::new();
    //interp.eval(program);

    let mut irgen = IRGen::new();
    irgen.gen(program);
    //for c in irgen.code.iter() {
    //    println!("{:?}", c);
    //}

    let mut ir = irgen.code.clone();
    let mut codegen = CodeGen::new();
    codegen.gen(&mut ir);

    let mut vm = VM::new();
    vm.load(&codegen.output);
    vm.run();
}
