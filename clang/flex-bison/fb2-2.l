/* 读取一些文件 */
%option noyywrap

%{
	int chars = 0;
	int words = 0;
	int lines = 0;

	int totchars = 0;
	int totwords = 0;
	int totlines = 0;
%}


%%

[^ \t\n\r\f\v]+    { words++; chars += strlen(yytext); }
\n                 { chars++; lines++; }
.                  { chars++; }

%%

int main(int argc, char **argv) {
	int i;

	if(argc < 2) {
		yylex();

		printf("lines=%d, words=%d, chars=%d\n", lines, words, chars);
	}

	for(i = 1; i < argc; i++) {
		FILE *f = fopen(argv[i], "r");
		if(!f) {
			continue;
		}

		yyrestart(f);
		yylex();
		fclose(f);

		printf("file=%s, lines=%d, words=%d, chars=%d\n", argv[i], lines, words, chars);

		totchars += chars; chars = 0;
		totwords += words; words = 0;
		totlines += lines; lines = 0;
	}

	if(argc > 2) {
		printf("totlines=%d, totwords=%d, totchars=%d\n", totlines, totwords, totchars);
	}

	return 0;
}
