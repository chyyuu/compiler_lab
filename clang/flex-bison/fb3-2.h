/* 计算器的声明部分 */

/* 与词法分析器的接口 */
extern int yylineno; /* 来自词法分析器 */
void yyerror(char *s, ...);

/* 符号表 */
struct symbol { /* 变量名 */
	char *name;
	double value;
	struct ast *func; /* 函数体 */
	struct symlist *syms;
};

/* 固定大小的简单的符号表 */
#define NHASH 9997
struct symbol symtab[NHASH];

struct symbol *lookup(char*);

/* 符号列表，作为参数列表 */
struct symlist {
	struct symbol *sym;
	struct symlist *next;
};

struct symlist *newsymlist(struct symbol *sym, struct symlist *next);
void symlistfree(struct symlist *sl);

/*
 * 节点类型
 * + - * / |
 * 0-7 比较操作符，位编码：04 等于，02 小于，01大于
 * M 单目负号
 * L 表达式或者语句列表
 * I IF 语句
 * W WHILE 语句
 * N 符号引用
 * = 赋值
 * S 符号列表
 * F 内置函数调用
 * C 用户函数调用
 */

enum bifs { /* 内置函数 */
	B_sqrt = 1,
	B_exp,
	B_log,
	B_print
};

/* 抽象语法树节点 */
/* 所有节点都有公共的初始nodetype */

struct ast {
	int nodetype;
	struct ast *l;
	struct ast *r;
};

struct fncall { /* 内置函数 */
	int nodetype; /* 类型 F */
	struct ast *l;
	enum bifs functype;
};

struct ufncall { /* 用户自定义函数 */
	int nodetype; /* 类型 C */
	struct ast *l;
	struct symbol *s;
};

struct flow {
	int nodetype; /* 类型 I 或者 W */
	struct ast *cond; /* 条件 */
	struct ast *tl; /* then 分支或者 do 语句 */
	struct ast *el; /* 可选的 else 分支 */
};

struct numval {
	int nodetype; /* 类型 K */
	double number;
};

struct symref {
	int nodetype; /* 类型 N */
	struct symbol *s;
};

struct symasgn {
	int nodetype; /* 类型 = */
	struct symbol *s;
	struct ast *v; /* 值 */
};

/* 构造抽象语法树 */
struct ast *newast(int nodetype, struct ast *l, struct ast *r);
struct ast *newcmp(int cmptype, struct ast *l, struct ast *r);
struct ast *newfunc(int functype, struct ast *l);
struct ast *newcall(struct symbol *s, struct ast *l);
struct ast *newref(struct symbol *s);
struct ast *newasgn(struct symbol *s, struct ast *v);
struct ast *newnum(double d);
struct ast *newflow(int nodetype, struct ast *cond, struct ast *tl, struct ast *tr);

/* 定义函数 */
void dodef(struct symbol *name, struct symlist *syms, struct ast *stmts);

/* 计算抽象语法树 */
double eval(struct ast *);

/* 删除和释放抽象语法树 */
void treefree(struct ast *);
