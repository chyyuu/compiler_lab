use re2dfa::*;
use clap::{App, Arg};
use std::{io, fs, process};

fn main() -> io::Result<()> {
  let m = App::new("show_fa")
    .arg(Arg::with_name("input").required(true))
    .arg(Arg::with_name("nfa").long("nfa").takes_value(true))
    .arg(Arg::with_name("raw_dfa").long("raw_dfa").takes_value(true).help("show the dfa directly converted from nfa"))
    .arg(Arg::with_name("dfa").long("dfa").takes_value(true).help("show the minimized dfa"))
    .get_matches();
  let input = m.value_of("input").unwrap();
  let re = parse(input).unwrap_or_else(|e| {
    eprintln!("Invalid regex `{}`: {}", input, e);
    process::exit(1);
  });
  let nfa = Nfa::from_re(&re);
  if let Some(path) = m.value_of("nfa") { fs::write(path, nfa.print_dot())?; }
  let dfa = Dfa::from_nfa(&nfa, 0);
  if let Some(path) = m.value_of("raw_dfa") { fs::write(path, dfa.print_dot())?; }
  let dfa = dfa.minimize();
  if let Some(path) = m.value_of("dfa") { fs::write(path, dfa.print_dot())?; }
  Ok(())
}
