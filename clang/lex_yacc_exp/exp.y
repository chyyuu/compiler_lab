/* 用YACC实现的一个简单的计算器 */
%{
#include <stdio.h>
%}
/* 终结符 */
%token INTEGER
/* 优先级和结合性 */
%left '+'
%left '*'
%%
input : /* empty string */
        | input line
        ;
line  : '\n'
        | exp '\n' { printf ("\t%d\n", $1); }
        | error '\n'
                ;
exp   : INTEGER { $$ = $1; }
        | exp '+' exp { $$ = $1 + $3; }
        | exp '*' exp { $$ = $1 * $3; }
        | '(' exp ')' { $$ = $2; } ;
%%

/* 用户子程序 */
void main (void) {
  yyparse ();
}

//int yylex() {
/* 自行编写或从 Lex 得到, 随后介绍 Lex和YACC的联用,需删去这里的 yylex()定义 */
//}
void yyerror (char *s) {
  printf ("%s\n", s);
}

