# Example
convert a Regular Expression to NFA(Nondeterministic Finite Automata) to DFA(Deterministic Finite Automata) 

## pre-requirement
rust toolchain (nightly OR 1.39+) is installed.

## usage
tested on ubuntu 19.10 x86-64

```
# run the example with help
cargo run -- -h

# run the example for RE to NFA
cargo run -- --nfa nfa.dot "(1|0)*1*0*10(1|0)*"

# show the figure of NFA
xdot nfa.dot

# run the example for RE to NFA to raw DFA
cargo run -- --raw_dfa raw_dfa.dot "(1|0)*1*0*10(1|0)*"

# show the figure of raw DFA
xdot raw_dfa.dot

# run the example for RE to NFA to raw DFA to small DFA
cargo run -- --dfa dfa.dot "(1|0)*1*0*10(1|0)*"

# show the figure of small DFA
xdot dfa.dot
```
