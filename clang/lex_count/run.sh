echo "build lex.yy.c"
lex count.l
echo "build count app"
gcc -o count lex.yy.c -ll
echo "run count app"
./count < count.l

