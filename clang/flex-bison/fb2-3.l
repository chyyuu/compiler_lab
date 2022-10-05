/* 包含文件的框架 */

%option noyywrap
%x IFILE

%{

/* 包含文件的堆栈 */
struct bufstack {
    struct bufstack *prev; /* 上一个文件信息 */
    YY_BUFFER_STATE bs; /* 保存的缓冲区 */
    int lineno; /* 保存的行号 */
    char *filename; /* 文件名 */
    FILE *f; /* 当前文件 */
} *curbs = 0;

char *curfilename;

int newfile(char *fn);
int popfile(void);

int grades = 1;
char format[20] = "%4d %s";

#define SHOW_INDENT

%}

%%
 /* 匹配#include语句直到引号或者< */
^"#"[ \t]*include[ \t]*[\"<]            { BEGIN IFILE; }

 /* 处理文件名直到结束引号、>或者行结束符 */
<IFILE>[^ \t\n\">]+                     {
                                            {
                                                int c;
                                                while((c=input()) && c != '\n');
                                            }
										#ifdef SHOW_INDENT
                                            sprintf(format, "%%%dd #include <%%s>\n", grades*4);
                                            fprintf(yyout, format, yylineno, yytext);

                                            grades++;
                                            sprintf(format, "%%%dd %%s", grades*4);
										#endif
                                            yylineno++;
                                            if(!newfile(yytext))
                                                yyterminate(); /* no such file */
                                            BEGIN INITIAL;
                                        }

 /* 处理IFILE状态中错误输入的情况 */
<IFILE>.|\n                             {
										#ifdef SHOW_INDENT
                                            grades--;
                                            sprintf(format, "%%%dd %%s", grades*4);
										#endif
                                            fprintf(stderr, "%4d bad include line\n", yylineno);
                                            yyterminate();
                                        }

 /* 文件结束时弹出文件堆栈，如果是最外层文件就结束 */
<<EOF>>                                 {
										#ifdef SHOW_INDENT
                                            grades--;
                                            sprintf(format, "%%%dd %%s", grades*4);
										#endif
                                            if(!popfile())
                                                yyterminate();
                                        }

 /* 在每一行的开始打印出行号 并且每遇到一个\n时就把行号加1 */
^.                                      { fprintf(yyout, format, yylineno, yytext); }
^\n                                     { fprintf(yyout, format, yylineno, yytext);yylineno++; } //
\n                                      { ECHO; yylineno++; }
.                                       { ECHO; }

%%

int main(int argc, char **argv) {
    if(argc < 2) {
        fprintf(stderr, "need filename\n");
        return 1;
    }

    if(newfile(argv[1])) {
        yylex();
    }

    return 0;
}

int newfile(char *fn) {
    FILE *f = fopen(fn, "r");
    struct bufstack *bs = malloc(sizeof(struct bufstack));

    /* 如果文件打开失败或者没有足够空间时就退出 */
    if(!f) {
        perror(fn);
        return 0;
    }
    if(!bs) {
        perror("malloc");
        return 1;
    }

    /* 记住当前状态 */
    if(curbs) {
        curbs->lineno = yylineno;
    }

    bs->prev = curbs;

    /* 建立当前文件信息 */
    bs->bs = yy_create_buffer(f, YY_BUF_SIZE);
    bs->f = f;
    bs->filename = fn;

    yy_switch_to_buffer(bs->bs);

    curbs = bs;
    yylineno = 1;
    curfilename = fn;

    return 1;
}

int popfile(void) {
    struct bufstack *bs = curbs;
    struct bufstack *prevbs;

    if(!bs) {
        return 0;
    }

    /* 删除当前文件信息 */
    fclose(bs->f);
    yy_delete_buffer(bs->bs);

    /* 切换回上一个文件 */
    prevbs = bs->prev;
    free(bs);

    if(!prevbs) {
        return 0;
    }

    yy_switch_to_buffer(prevbs->bs);

    curbs = prevbs;
    yylineno = curbs->lineno;
    curfilename = curbs->filename;

    return 1;
}
