/* 基于抽象语法树的计算器对应的C例程 */

#include <stdio.h>
#include <stdlib.h>
#include <stdarg.h>
#include "fb3-1.h"

struct ast *newast(int nodetype, struct ast *l, struct ast *r) {
	struct ast *a = malloc(sizeof(struct ast));

	if(!a) {
		yyerror("out of space");
		exit(0);
	}

	a->nodetype = nodetype;
	a->l = l;
	a->r = r;

	return a;
}

struct ast *newnum(double d) {
	struct numval *a = malloc(sizeof(struct numval));

	if(!a) {
		yyerror("out of space");
		exit(0);
	}

	a->nodetype = 'K';
	a->number = d;

	return (struct ast *)a;
}

#define dprintf

double eval(struct ast *a) {
	double l,r;
	double v = 0; // 子树的计算结果 

	switch(a->nodetype) {
		case 'K': v = ((struct numval *)a)->number;dprintf("%f\n", v); break;
		case '+': l=eval(a->l);r=eval(a->r);v = l + r;dprintf("%f+%f=%f\n", l, r, v); break;
		case '-': l=eval(a->l);r=eval(a->r);v = l - r;dprintf("%f-%f=%f\n", l, r, v); break;
		case '*': l=eval(a->l);r=eval(a->r);v = l * r;dprintf("%f*%f=%f\n", l, r, v); break;
		case '/': l=eval(a->l);r=eval(a->r);v = l / r;dprintf("%f/%f=%f\n", l, r, v); break;
		case '|': l=eval(a->l);v=l; if(v < 0) v = -v;dprintf("|%f|=%f\n", l, v); break;
		case 'M': l=eval(a->l);v=-l;dprintf("-%f=%f\n", l, v); break;
		default: printf("internal error: bad node %c\n", a->nodetype);
	}

	return v;
}

void treefree(struct ast *a) {
	switch(a->nodetype) {
		/* 两棵子树 */
		case '+':
		case '-':
		case '*':
		case '/':
			treefree(a->r);

		/* 一棵子树 */
		case '|':
		case 'M':
			treefree(a->l);

		case 'K':
			free(a);
			break;

		default:
			printf("internal error: bad node %c\n", a->nodetype);

	}
}

void yyerror(char *s, ...) {
	va_list ap;
	va_start(ap, s);

	fprintf(stderr, "%d: error: ", yylineno);
	vfprintf(stderr, s, ap);
	fprintf(stderr, "\n");
}

int main(int argc, char **argv) {
	printf("> ");

	return yyparse();
}
