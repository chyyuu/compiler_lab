%{
#include <stdio.h>
%}
%%
[a-z]  printf("%c",yytext[0]+'A'-'a');
%%

