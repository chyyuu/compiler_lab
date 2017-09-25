%{
#include "y.tab.h"
%}
%%
0|[1-9][0-9]* { yylval = atoi(yytext); return INTEGER; }
[+*()\n]      { return yytext[0];}
.             { /*do nothing*/ }
%%

