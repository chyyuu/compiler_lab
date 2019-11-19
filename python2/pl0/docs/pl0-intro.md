#  pl0 语言
https://github.com/cwalk/PL0-Compiler

## pl0 语言简介

PL 语言是PASCAL语言的一个子集，该语言不太大，但能充分展示高级语言的最基本成分。PL0具有子程序概念，包括过程说明和过程调用语句。在数据类型方面，PL0只包含唯一的整型，可以说明这种类型的常量和变量。运算符有+，-，*，/，=，<>，<，>，<=，>=，(，)。说明部分包括常量说明、变量说明和过程说明。

## PL0语言的词法法描述

```
Reserved Words: const, var, procedure, call, begin, end, if, then, else, while, do, read, write.	
Special Symbols: ‘+’, ‘-‘, ‘*’, ‘/’, ‘(‘, ‘)’, ‘=’, ’,’ , ‘.’, ‘ <’, ‘>’,  ‘;’ , ’:’ .
Identifiers: identsym = letter (letter | digit)* 
Numbers: numbersym = (digit)+
Invisible Characters: tab, white spaces, newline
Comments denoted by: /* . . .   */
```



## PL0语言的语法描述

```
program ::= block "." . 
block ::= const-declaration  var-declaration proc-declaration statement.	
constdeclaration ::= [ “const” ident "=" number {"," ident "=" number} “;"].	
var-declaration  ::= [ "var" ident {"," ident} “;"].
proc-declaration::= {"procedure" ident ";" block ";" } statement .
statement   ::= [ ident ":=" expression
			| "call" ident
	      	| "begin" statement { ";" statement } "end" 
	      	| "if" condition "then" statement [“else" statement]
	     	| "while" condition "do" statement
			| “read” ident
			| “write” ident
	      	| e ] . 

condition ::= "odd" expression 
	  		| expression  rel-op  expression.
  
rel-op ::= "="|“<>"|"<"|"<="|">"|">=“.
expression ::= [ "+"|"-"] term { ("+"|"-") term}.
term ::= factor {("*"|"/") factor}. 
factor ::= ident | number | "(" expression ")“.
number ::= digit {digit}.
ident ::= letter {letter | digit}.
digit ;;= "0" | "1" | "2" | "3" | "4" | "5" | "6" | "7" | "8" | "9“.
letter ::= "a" | "b" | … | "y" | "z" | "A" | "B" | ... | "Y" | "Z".
```

Based on Wirth’s definition for EBNF we have the following rule:

- `[ ]` means an optional item.

- `{ }` means repeat 0 or more times.
- Terminal symbols are enclosed in quote marks.
- A period is used to indicate the end of the definition of a syntactic class.

## 虚拟机

### Instruction Set Architecture (ISA)

There are 13 arithmetic/logical operations that manipulate the data within stack. These operations are indicated by the OP component = 2 (OPR). When an OPR instruction is encountered, the M component of the instruction is used to select the particular arithmetic/logical operation to be executed (e.g. to multiply the two elements at the top of the stack, write the instruction “2 0 4”).

### ISA:
```
01   – 	LIT	0, M	Push constant value (literal) M onto the stack
02   – 	OPR	0, M	Operation to be performed on the data at the top of the stack
			(detailed in Appendix B)
03   – 	LOD	L, M	Load value to top of stack from the stack location at offset M from
			L lexicographical levels down
04   – 	STO	L, M	Store value at top of stack in the stack location at offset M from
L lexicographical levels down
05   – 	CAL	L, M	Call procedure at code index M (generates new Activation Record
and pc = M)
06   – 	INC	0, M	Allocate M locals (increment sp by M). First four are: 
			Functional value(FL), Static Link (SL), 
			Dynamic Link (DL), and Return Address (RA)
07   – 	JMP	0, M	Jump to instruction M
08   – 	JPC	0, M	Jump to instruction M if top stack element is 0
09   – 	SIO	0, 1	Write the top stack element to the screen
10   – 	SIO	0, 2	Read in input from the user and store it at the top of the stack
```



### ISA Pseudo Code

```
01 – LIT    0, M 	sp = sp + 1; 
		  	stack[sp] = M; 

02 – OPR   0, #   ( 0  ≤  #  ≤  13 )	
0    RET     (sp = bp -1 and pc = stack[sp + 4] and bp = stack[sp + 3])
1    NEG    (-stack[sp])
2    ADD    (sp = sp – 1 and  stack[sp] = stack[sp] + stack[sp + 1])
3    SUB    (sp = sp – 1 and  stack[sp] = stack[sp] - stack[sp + 1])
4    MUL    (sp sp – 1 and  stack[sp] = stack[sp] * stack[sp + 1])
5    DIV     (sp sp – 1 and stack[sp] = stack[sp] / stack[sp + 1])
6    ODD   (stack[sp] = stack[sp] mod 2) or ord(odd(stack[sp]))
7    MOD  (sp = sp – 1 and  stack[sp] = stack[sp] mod stack[sp + 1])
8    EQL   (sp = sp – 1 and  stack[sp]  = stack[sp] = = stack[sp + 1])
9    NEQ  (sp = sp – 1 and  stack[sp]  = stack[sp] != stack[sp + 1])
10  LSS   (sp = sp – 1 and  stack[sp] =  stack[sp]  <  stack[sp + 1])  
11  LEQ   (sp = sp – 1 and  stack[sp]  = stack[sp] <=  stack[sp + 1]) 
12  GTR  (sp = sp – 1 and  stack[sp]  = stack[sp] >  stack[sp + 1])
13  GEQ (sp = sp – 1 and  stack[sp]  = stack[sp] >= stack[sp + 1])

03 – LOD   L, M 	sp =  sp + 1; 
	stack[sp] = stack[ base(L, bp) + M];
04 – STO   L, M   	stack[ base(L, bp) + M] = stack[sp]; 
	sp = sp - 1;
05 - CAL   L, M  	
    stack[sp + 1]  = 0			    /* space to return value
    stack[sp + 2]  =  base(L, bp); 	/* static link (SL)
	stack[sp + 3]  = bp;			/* dynamic link (DL)
	stack[sp + 4]  = pc	 		    /* return address (RA) 
	bp = sp + 1;
	pc = M;

06 – INC    0, M  	sp = sp + M;
07 – JMP   0, M   	pc = M;
08 – JPC    0, M   	if stack[sp] == 0 then { pc = M; }； sp = sp - 1;
09 – SIO  0, 1    	print(stack[sp]); sp = sp – 1;
10 -  SIO  0, 2		sp = sp + 1; read(stack[sp]);
```


NOTE: The result of a logical operation such as (A > B) is defined as 1 if the condition was met and 0 otherwise.			