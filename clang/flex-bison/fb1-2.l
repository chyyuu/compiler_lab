/* 英式英语 -> 美工英语 */
%option noyywrap

%%
"colour" { printf("color"); }
"flavour" { printf("flavor"); }
"clever" { printf("smart"); }
"smart" { printf("elegant"); }
"conservative" { printf("liberal"); }
. { printf("%s", yytext); }
%%

int main(int argc, char **argv) {
	return yylex();
}

