/* 识别出用于计算器的记号并把它们输出 */
%option noyywrap

%{
	enum yytokentype {
		NUMBER = 258,
		ADD = '+',
		SUB = '-',
		MUL = '*',
		DIV = '/',
		ABS = '|',
		EOL = '\n'
	};

	int yylval;
%}

%%
"+"        { return ADD; }
"-"        { return SUB; }
"*"        { return MUL; }
"/"        { return DIV; }
"|"        { return ABS; }
[0-9]+     { yylval = atoi(yytext); return NUMBER; }
\n         { return EOL; }
[ \t]      { /* 忽略空白字符 */ }
.          { printf("\nMystery character %c\n", *yytext); }
%%

int main(int argc, char **argv) {
	int tok;

	while(tok = yylex()) {
		if(tok == NUMBER) {
			printf("%d", yylval);
		} else {
			printf("%c", tok);
		}
	}

	return 0;
}
