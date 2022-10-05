/* 基于抽象语法树的计算器 */

%{

#include <stdio.h>
#include <stdlib.h>
#include "fb3-1.h"

%}

%union {
	struct ast *a;
	double d;
}

%token <d> NUMBER
%token EOL

%type <a> exp factor term

%%

calclist: /* 空 */
 | calclist exp EOL {
	printf("= %4.4g\n", eval($2)); // 计算抽象语法树并打印结果
	treefree($2); // 释放抽象语法树
	printf("> ");
 }

 | calclist EOL {
	printf("> "); /* 空行或者注释 */
 }
;

exp: factor
 | exp '+' factor { $$ = newast('+', $1, $3); }
 | exp '-' factor { $$ = newast('-', $1, $3); }
;

factor: term
 | factor '*' term { $$ = newast('*', $1, $3); }
 | factor '/' term { $$ = newast('/', $1, $3); }
;

term: NUMBER { $$ = newnum($1); }
 | '|' exp '|' { $$ = newast('|', $2, NULL); }
 | '(' exp ')' { $$ = $2; }
 | '-' term { $$ = newast('M', $2, NULL); }
;

%%

