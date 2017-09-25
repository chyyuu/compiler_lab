lex toupper.l
gcc -o toupper lex.yy.c -ll
./toupper < test.txt

