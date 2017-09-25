lex exp.l
yacc -d exp.y
gcc y.tab.c lex.yy.c -ly -ll -o exp
./exp
