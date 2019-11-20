#  pl0 语言
https://github.com/cwalk/PL0-Compiler

## pl0 语言简介

PL 语言是PASCAL语言的一个子集，该语言不太大，但能充分展示高级语言的最基本成分。PL0具有子程序概念，包括过程说明和过程调用语句。在数据类型方面，PL0只包含唯一的整型，可以说明这种类型的常量和变量。运算符有+，-，*，/，=，<>，<，>，<=，>=，(，)。说明部分包括常量说明、变量说明和过程说明。

PL/0的程序结构比较完全，赋值语句作为基本结构，构造概念有

- 顺序执行、条件执行和重复执行，分别由begin/end,if then else和while do语句表示。
- PL0还具有子程序概念，包括过程说明和过程调用语句。
- 在数据类型方面，PL0只包含唯一的整型，可以说明这种类型的常量和变量。
- 运算符有+，-，*，/，=，<>，<，>，<=，>=，(，)。
- 说明部分包括常量说明、变量说明和过程说明。

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

### PL/0语言文法的EBNF表示

（From 北航软院的编译实践）

```
<程序> ::= <分程序>.

<分程序> ::= [<常量说明部分>][变量说明部分>]{<过程说明部分>}<语句>

<常量说明部分> ::= const<常量定义>{,<常量定义>};

<常量定义> ::= <标识符>=<无符号整数>

<无符号整数> ::= <数字>{<数字>}

<标识符> ::= <字母>{<字母>|<数字>}

<变量说明部分>::= var<标识符>{,<标识符>};

<过程说明部分> ::= <过程首部><分程序>；

<过程首部> ::= procedure<标识符>;

<语句> ::= <赋值语句>|<条件语句>|<当型循环语句>|<过程调用语句>|<读语句>|<写语句>|<复合语句>|<重复语句>|<空>

<赋值语句> ::= <标识符>:=<表达式>

<表达式> ::= [+|-]<项>{<加法运算符><项>}

<项> ::= <因子>{<乘法运算符><因子>}

<因子> ::= <标识符>|<无符号整数>|'('<表达式>')'

<加法运算符> ::= +|-

<乘法运算符> ::= *|/

<条件> ::= <表达式><关系运算符><表达式>|odd<表达式>

<关系运算符> ::= =|<>|<|<=|>|>=

<条件语句> ::= if<条件>then<语句>[else<语句>]

<当型循环语句> ::= while<条件>do<语句>

<过程调用语句> ::= call<标识符>

<复合语句> ::= begin<语句>{;<语句>}end

<重复语句> ::= repeat<语句>{;<语句>}until<条件>

<读语句> ::= read'('<标识符>{,<标识符>}')'

<写语句> ::= write'('<标识符>{,<标识符>}')'

<字母> ::= a|b|...|X|Y|Z

<数字> ::= 0|1|2|...|8|9
```

注意：

- 数据类型：无符号整数
- 标识符类型：简单变量(var)和常数(const)
- 数字位数：小于14位
- 标识符的有效长度：小于10位
- 过程嵌套：小于3层

## 语法错误处理

错误处理的原则： 尽可能准确指出错误位置和错误属性； 尽可能进行校正

### 短语层恢复技术

在进入某个语法单位时，调用TEST函数, 检查当前符号是否属于该语法单位的开始符号集合。在语法单位分析结束时，调用TEST函数, 检查当前符号是否属于调用该语法单位时应有的后跟符号集合。

TEST函数(s1 需要的符号集合, s2 不需要的符号集合, errcode 错误号)：查看当前符号是否不在需要的符号s1中，如果不在，就报错，且把s2集合补充到s1集合中，不停地获取符号，直到它跳出s1集合。

PL/0文法非终结符的开始符号集与后继符号集

| 非终结符 | FIRST(S)                                                     | FOLLOW(S)                 |
| -------- | ------------------------------------------------------------ | ------------------------- |
| 分程序   | const var procedure ident if call begin while read write repeat | . ;                       |
| 语句     | ident call begin if while read write until                   | . ; end                   |
| 条件     | odd + - ( ident number                                       | then do                   |
| 表达式   | = + - ( ident number                                         | . ; R end then do         |
| 项       | ident number (                                               | . ; R + - end then do     |
| 因子     | ident number (                                               | . ; R + - * / end then do |

###  PL/0语言的出错信息表

| 编号 | 出错原因                                               |
| ---- | ------------------------------------------------------ |
| 1    | 常数说明中的"="写成"∶="。                              |
| 2    | 常数说明中的"="后应是数字。                            |
| 3    | 常数说明中的标识符后应是"="。                          |
| 4    | const ,var, procedure后应为标识符。                    |
| 5    | 漏掉了'，'或'；'。                                     |
| 6    | 过程说明后的符号不正确(应是语句开始符，或过程定义符)。 |
| 7    | 应是语句开始符。                                       |
| 8    | 程序体内语句部分的后跟符不正确。                       |
| 9    | 程序结尾丢了句号'.'。                                  |
| 10   | 语句之间漏了'；'。                                     |
| 11   | 标识符未说明。                                         |
| 12   | 赋值语句中，赋值号左部标识符属性应是变量。             |
| 13   | 赋值语句左部标识符后应是赋值号'∶='。                   |
| 14   | call后应为标识符。                                     |
| 15   | call后标识符属性应为过程。                             |
| 16   | 条件语句中丢了'then'。                                 |
| 17   | 丢了'end"或'；'。                                      |
| 18   | while型循环语句中丢了'do'。                            |
| 19   | 语句后的符号不正确。                                   |
| 20   | 应为关系运算符。                                       |
| 21   | 表达式内标识符属性不能是过程。                         |
| 22   | 表达式中漏掉右括号')'。                                |
| 23   | 因子后的非法符号。                                     |
| 24   | 表达式的开始符不能是此符号。                           |
| 31   | 数越界。                                               |
| 32   | read语句括号中的标识符不是变量。                       |
| 33   | 格式错误，应为右括号                                   |
| 34   | 格式错误，应为左括号                                   |
| 35   | read()中的变量未声明                                   |
| 36   | 变量字符过长                                           |

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
4    MUL    (sp =sp – 1 and  stack[sp] = stack[sp] * stack[sp + 1])
5    DIV     (sp =sp – 1 and stack[sp] = stack[sp] / stack[sp + 1])
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

### P-Code语言

（From 北航软院的编译实践）

P-code 语言：一种栈式机的语言。此类栈式机没有累加器和通用寄存器，有一个栈式存储器，有四个控制寄存器（指令寄存器 I，指令地址寄存器 P，栈顶寄存器 T和基址寄存器 B），算术逻辑运算都在栈顶进行。

|  F   |  L   |  A   |
| :--: | :--: | :--: |
|      |      |      |

#### 指令格式

​       F ：操作码
​       L ：层次差（标识符引用层减去定义层）
​       A ：不同的指令含义不同

#### P-code 指令的含义

| 指令    | 具体含义                                        |
| ------- | :---------------------------------------------- |
| LIT 0,a | 取常量a放到数据栈栈顶                           |
| OPR 0,a | 执行运算，a表示执行何种运算(+ - * /)            |
| LOD l,a | 取变量放到数据栈栈顶(相对地址为a,层次差为l)     |
| STO l,a | 将数据栈栈顶内容存入变量(相对地址为a,层次差为l) |
| CAL l,a | 调用过程(入口指令地址为a,层次差为l)             |
| INT 0,a | 数据栈栈顶指针增加a                             |
| JMP 0,a | 无条件转移到指令地址a                           |
| JPC 0,a | 条件转移到指令地址a                             |



#### 代码生成与地址返填

对于if then [else],while do和repeat until语句，要生成跳转指令，故采用地址返填技术。

- if-then-else语句的目标代码生成模式：

  `if <condition> then <statement>[else]`
|  |             |
| ------ | ----------- |
|        | <condition> |
|        | JPC addr1   |
|        | <statement> |
| addr1: | [else]      |

- while-do语句的目标代码生成模式：
`while <condition> do <statement> `
|  |             |
| -------------------------------- | ----------- |
| addr2:                           | <condition> |
|                                  | JPC addr3   |
|                                  | <statement> |
|                                  | JPC addr2   |
| addr3:                           |             |

 

- repeat-until语句的目标代码生成模式：
`repeat <statement> until <condition>`
|   |             |
| ------------------------------------ | ----------- |
| addr4:                               | <statement> |
|                                      | <condition> |
|                                      | JPC addr4   |

 

#### OPR指令设计解释

| (1).OPR 0 0                                                  |
| ------------------------------------------------------------ |
| RETUEN(stack[sp + 1] ß base(L);sp ß bp - 1;bp ß stack[sp + 2];pc ß stack[sp + 3];) |
| (2).OPR 0 1                                                  |
| NEG(- stack[sp] )                                            |
| (3).OPR 0 2                                                  |
| ADD(sp ß sp – 1 ;stack[sp] ß stack[sp] + stack[sp + 1])      |
| (4).OPR 0 3                                                  |
| SUB(sp ß sp – 1 ;stack[sp] ßstack[sp] - stack[sp + 1])       |
| (5).OPR 0 4                                                  |
| MUL(sp ß sp – 1 ;stack[sp] ß stack[sp] * stack[sp + 1])      |
| (6).OPR 0 5                                                  |
| DIV(sp ß sp – 1 ;stack[sp] ß stack[sp] / stack[sp + 1])      |
| (7).OPR 0 6                                                  |
| ODD(stack[sp] ß stack % 2)                                   |
| (8).OPR 0 7                                                  |
| MOD(sp ß sp – 1 ;stack[sp] ß stack[sp] % stack[sp + 1])      |
| (9).OPR 0 8                                                  |
| EQL(sp ß sp – 1 ;stack[sp] ß stack[sp] == stack[sp + 1])     |
| (10).OPR 0 9                                                 |
| NEQ(sp ß sp – 1 ;stack[sp] ß stack[sp] != stack[sp + 1])     |
| (11).OPR 0 10                                                |
| LSS(sp ß sp – 1 ;stack[sp] ß stack[sp] < stack[sp + 1])      |
| (12).OPR 0 11                                                |
| GEQ(sp ß sp – 1 ;stack[sp] ß stack[sp] >= stack[sp + 1])     |
| (13).OPR 0 12                                                |
| GTR(sp ß sp – 1 ;stack[sp] ß stack[sp] > stack[sp + 1])      |
| (14).OPR 0 13                                                |
| LEQ(sp ß sp – 1 ;stack[sp] ß stack[sp] <= stack[sp + 1])     |
| (15).OPR 0 14                                                |
| print (stack[sp]);sp ß sp – 1;                               |
| (16).OPR 0 15                                                |
| print ('\n');                                                |
| (17).OPR 0 16                                                |
| scan(stack[sp]);sp ß sp + 1;                                 |
|                                                              |