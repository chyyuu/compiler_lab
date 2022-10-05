/* 基于抽象语法树的计算器 */

%{

#include <stdio.h>
#include <stdlib.h>
#include "fb3-2.h"

%}

%union {
	struct ast *a;
	double d;
	struct symbol *s; /* 指定符号 */
	struct symlist *sl;
	int fn; /* 指定函数 */
}

/* 声明记号 */
%token <d> NUMBER
%token <s> NAME
%token <fn> CALL

%token IF ELSE WHILE FUNC RET

%nonassoc <fn> CMP

%right '='
%left '+' '-'
%left '*' '/'
%nonassoc UMINUS

%type <a> exp stmt list explist funcstmt funclist
%type <sl> symlist

%start calclist

%%

stmt: IF '(' exp ')' '{' list '}' { $$ = newflow('I', $3, $6, NULL); }
 | IF '(' exp ')' '{' list '}' ELSE '{' list '}' { $$ = newflow('I', $3, $6, $10); }
 | WHILE '(' exp ')' '{' list '}' { $$ = newflow('W', $3, $6, NULL); }
 | exp
;

list: /* 空 */ { $$ = NULL; }
 | stmt ';' list {
		if($3 == NULL)
			$$ = $1;
		else
			$$ = newast('L', $1, $3);
	}
;

exp: exp CMP exp { $$ = newcmp($2, $1, $3); }
 | exp '+' exp { $$ = newast('+', $1, $3); }
 | exp '-' exp { $$ = newast('-', $1, $3); }
 | exp '*' exp { $$ = newast('*', $1, $3); }
 | exp '/' exp { $$ = newast('/', $1, $3); }
 | '|' exp '|' { $$ = newast('|', $2, NULL); }
 | '(' exp ')' { $$ = $2; }
 | '-' exp %prec UMINUS { $$ = newast('M', $2, NULL); }
 | NUMBER { $$ = newnum($1); }
 | NAME { $$ = newref($1); }
 | NAME '=' exp { $$ = newasgn($1, $3); }
 | CALL '(' explist ')' { $$ = newfunc($1, $3); }
 | NAME '(' explist ')' { $$ = newcall($1, $3); }
;

explist: exp
 | exp ',' explist { $$ = newast('L', $1, $3); }
;

symlist: NAME { $$ = newsymlist($1, NULL); }
 | NAME ',' symlist { $$ = newsymlist($1,$3); }
;

funcstmt: IF '(' exp ')' '{' funclist '}' { $$ = newflow('I', $3, $6, NULL); }
 | IF '(' exp ')' '{' funclist '}' ELSE '{' funclist '}' { $$ = newflow('I', $3, $6, $10); }
 | WHILE '(' exp ')' '{' funclist '}' { $$ = newflow('W', $3, $6, NULL); }
 | RET exp { $$ = newast('R', $2, NULL); }
 | exp
;

funclist: /* 空 */ { $$ = NULL; }
 | funcstmt ';' funclist {
		if($3 == NULL)
			$$ = $1;
		else
			$$ = newast('L', $1, $3);
	}
;

calclist: /* 空 */
 | calclist stmt ';' { printf("= %4.4g\n> ", eval($2));treefree($2); }
 | calclist FUNC NAME '(' symlist ')' '{' funclist '}' { dodef($3, $5, $8);printf("Defined %s\n> ", $3->name); }
 | calclist error ';' { yyerrok; printf("> "); }
;

%%
