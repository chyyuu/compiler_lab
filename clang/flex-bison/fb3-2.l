/* 识别计算器的记号 */
%option noyywrap nodefault yylineno

%{

#include "fb3-2.h"
#include "fb3-2.yy.h"

%}

/* 浮点数指数部分 */
EXP ([Ee][-+]?[0-9]+)

%%

 /* 单一字符操作符 */
"+" |
"-" |
"*" |
"/" |
"=" |
"|" |
"," |
";" |
"(" |
")" |
"{" |
"}" { return yytext[0]; }

 /* 比较操作符，所有返回值都是CMP记号 */
">" { yylval.fn = 1; return CMP; }
"<" { yylval.fn = 2; return CMP; }
"<>" { yylval.fn = 3; return CMP; }
"==" { yylval.fn = 4; return CMP; }
">=" { yylval.fn = 5; return CMP; }
"<=" { yylval.fn = 6; return CMP; }
 
 /* 关键字 */
"if" { return IF; }
"else" { return ELSE; }
"while" { return WHILE; }
"func" { return FUNC; }
"ret" { return RET; }

 /* 内置函数 */
"sqrt" { yylval.fn = B_sqrt; return CALL; }
"exp" { yylval.fn = B_exp; return CALL; }
"log" { yylval.fn = B_log; return CALL; }
"print" { yylval.fn = B_print; return CALL; }

 /* 名字 */
[a-zA-Z][a-zA-Z0-9]* { yylval.s = lookup(yytext); return NAME; }
"."?[0-9]+{EXP}? { yylval.d = atof(yytext); return NUMBER; }

"//".*
[ \t\n]
. { yyerror("Mystery character %c\n", *yytext); }

%%
