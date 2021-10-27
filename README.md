# compiler_lab

All programs have been tested on ubuntu 17.04 64 bit, and  some programs have been tested on windows 10 64bit. 


## lex & yacc
1. [count line num using lex&clang](clang/lex_count/)
1. [upper chars using lex&clang](clang/lex_toupper/)
1. [calculator using lex&yacc&clang](clang/yacc_lex_exp/)
1. [calculator using jlex&cup&java](clang/yacc_lex_exp/)
1. [calculator using ply&python](python2/ply_calc/)
1. [some simple examples using ply&python](python2/ply_examples/)
1. [calculator using rust](rust/calculator/)
1. [lexers using rust/nom](rust/lexer/)
1. [Solving LL(1) First/Follow/PS Sets](clang/LL-1-Parsing-Table-Calculator/)
## some automaton, turing machine codes

For understanding some concepts and internals of "Formal Languages and Automata"

### automaton

From https://github.com/cforth/toys/tree/master/Automaton

[书籍《计算的本质：深入剖析程序和计算机》](http://www.ituring.com.cn/book/1098)中第三章以及第四章的自动机实现。  
1. [section 3.1 确定性有限自动机（Deterministic Finite Automaton，DFA）](python3/automaton/DFA.py)
1. [section 3.2 非确定性有限自动机（Nondeterministic Finite Automata，NFA）](python3/automaton/NFA.py)  
1. [section 3.3 正则表达式的实现](automaton/Pattern.py)  
1. [section 3.4 NFA与DFA的等价性](automaton/NFASimulation.py)  
1. [section 4.1 确定性下推自动机（Deterministic PushDown Automaton，DPDA）](python3/automaton/DPDA.py)  
1. [section 4.2 非确定性下推自动机（Nondeterministic Pushdown Automaton，NPDA）](python3/automaton/NPDA.py)  
1. [section 4.3.1 词法分析（Lexical Analyzer）](python3/automaton/LexicalAnalyzer.py)  
1. [section 4.3.2 语法分析（Grammar Analyzer）](python3/automaton/GrammarAnalyzer.py)
1. [minimised DFA from DFA from NFA from Regular_Expression](python2/minidfa_dfa_nfa_regex)

#### automata in rust
1. [show nfa/dfa](rust/automata)

### turing machine

From https://github.com/cforth/toys/tree/master/Turing

[《计算的本质：深入剖析程序和计算机》](http://www.ituring.com.cn/book/1098)中第五章的图灵机实现。   
1. [section 5.1 确定型图灵机（Deterministic Turing Machine，DTM）](python3/turing_machine/DTM.py)

## compiler, interpreter, simulator

1. [Very basic IDE and vizualization/simulation of Mini-C compiler using kotlin](kotlin/minic-edu-app/)
1. [BASIC language & interpreter using ply &python](python2/ply_BASIC_lang)
1. [simple stack machine using python](python2/simple_stack_machine)

### little toy in rust
1. [interpreter in rust](rust/interpreter)
1. [compiler in rust](rust/compiler)
