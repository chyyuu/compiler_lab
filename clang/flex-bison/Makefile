CC=gcc

BIN_DIR = ./bin
OUT_DIR = ./out

CFLAGS = -O3 -I. -I$(OUT_DIR)
LFLAGS = -lm

EXE_FILES = \
	$(BIN_DIR)/fb1-1.exe \
	$(BIN_DIR)/fb1-2.exe \
	$(BIN_DIR)/fb1-3.exe \
	$(BIN_DIR)/fb1-4.exe \
	$(BIN_DIR)/fb2-2.exe \
	$(BIN_DIR)/fb2-3.exe \
	$(BIN_DIR)/fb2-4.exe \
	$(BIN_DIR)/fb3-1.exe \
	$(BIN_DIR)/fb3-2.exe

all: force $(EXE_FILES)

force: $(BIN_DIR) $(OUT_DIR)

.PHONY:

$(BIN_DIR):
	@mkdir $@

$(OUT_DIR):
	@mkdir $@

test: all
	@echo $(EXE_FILES) | sed 's|$(BIN_DIR)/||g' | sed 's|.exe||g' | xargs make 

$(BIN_DIR)/fb1-1.exe: $(OUT_DIR)/fb1-1.lex
	@echo -e "\E[34mbuild fb1-1 Unix的wc程序"
	@tput sgr0
	@$(CC) -o $@ $? $(LFLAGS)

fb1-1: $(BIN_DIR)/fb1-1.exe
	@echo -e "\E[34mtest "$@
	@tput sgr0
	$? < $@.l

$(BIN_DIR)/fb1-2.exe: $(OUT_DIR)/fb1-2.lex
	@echo -e "\E[34mbuild fb1-2 英式英语 -> 美工英语"
	@tput sgr0
	@$(CC) -o $@ $? $(LFLAGS)

fb1-2: $(BIN_DIR)/fb1-2.exe
	@echo -e "\E[34mtest "$@
	@tput sgr0
	$? < $@.l

$(BIN_DIR)/fb1-3.exe: $(OUT_DIR)/fb1-3.lex
	@echo -e "\E[34mbuild fb1-3 识别出用于计算器的记号并把它们输出"
	@tput sgr0
	@$(CC) -o $@ $? $(LFLAGS)

fb1-3: $(BIN_DIR)/fb1-3.exe
	@echo -e "\E[34mtest "$@
	@tput sgr0
	$? < $@.exp

$(BIN_DIR)/fb1-4.exe: $(OUT_DIR)/fb1-4.lex
	@echo -e "\E[34mbuild fb1-4 识别出用于计算器的记号并把它们输出"
	@tput sgr0
	@$(CC) -o $@ $? $(LFLAGS)

fb1-4: $(BIN_DIR)/fb1-4.exe
	@echo -e "\E[34mtest "$@
	@tput sgr0
	$? < $@.exp

$(BIN_DIR)/fb2-2.exe: $(OUT_DIR)/fb2-2.lex
	@echo -e "\E[34mbuild fb2-2 读取一些文件"
	@tput sgr0
	@$(CC) -o $@ $? $(LFLAGS)

fb2-2: $(BIN_DIR)/fb2-2.exe
	@echo -e "\E[34mtest "$@
	@tput sgr0
	@echo
	@echo a file test:
	@echo ------------------------------------------------
	@$? $@.l
	@echo
	@echo two files test:
	@echo ------------------------------------------------
	@$? $@.l Makefile

$(BIN_DIR)/fb2-3.exe: $(OUT_DIR)/fb2-3.lex
	@echo -e "\E[34mbuild fb2-3 包含文件的框架"
	@tput sgr0
	@$(CC) -o $@ $? $(LFLAGS)

fb2-3: $(BIN_DIR)/fb2-3.exe
	@echo -e "\E[34mtest "$@
	@tput sgr0
	$? $@.exp

$(BIN_DIR)/fb2-4.exe: $(OUT_DIR)/fb2-4.lex
	@echo -e "\E[34mbuild fb2-4 文本重要语汇索引"
	@tput sgr0
	@$(CC) -o $@ $? $(LFLAGS)

fb2-4: $(BIN_DIR)/fb2-4.exe
	@echo -e "\E[34mtest "$@
	@tput sgr0
	$? $@.l

$(BIN_DIR)/fb3-1.exe: $(OUT_DIR)/fb3-1.yy $(OUT_DIR)/fb3-1.lex $(OUT_DIR)/fb3-1.o
	@echo -e "\E[34mbuild fb3-1 基于抽象语法树的计算器"
	@tput sgr0
	@$(CC) -o $@ $? $(LFLAGS)

fb3-1: $(BIN_DIR)/fb3-1.exe
	@echo -e "\E[34mtest "$@
	@tput sgr0
	$? < fb3-1.exp

$(BIN_DIR)/fb3-2.exe: $(OUT_DIR)/fb3-2.yy $(OUT_DIR)/fb3-2.lex $(OUT_DIR)/fb3-2func.o
	@echo -e "\E[34mbuild fb3-2 基于抽象语法树的编程计算器"
	@tput sgr0
	@$(CC) -o $@ $? $(LFLAGS)

fb3-2: $(BIN_DIR)/fb3-2.exe
	@echo -e "\E[34mtest "$@
	@tput sgr0
	$? < fb3-2.exp

$(OUT_DIR)/%.lex: %.l
	@echo -e "\E[32m"$?"\E[m"
	@tput sgr0
	@flex -o$@.c $?
	@$(CC) $(CFLAGS) -S $@.c -o $@.s
	@$(CC) $(CFLAGS) -E $@.c -o $@.e
	@$(CC) $(CFLAGS) -c $@.c -o $@

$(OUT_DIR)/%.yy: %.y
	@echo -e "\E[32m"$?"\E[m"
	@tput sgr0
	@bison -v -d -y -o$@.c $?
	@$(CC) $(CFLAGS) -S $@.c -o $@.s
	@$(CC) $(CFLAGS) -E $@.c -o $@.e
	@$(CC) $(CFLAGS) -c $@.c -o $@

$(OUT_DIR)/%.o: %.c
	@echo -e "\E[32m"$?"\E[m"
	@tput sgr0
	@$(CC) $(CFLAGS) -S $? -o $(@:.o=.s)
	@$(CC) $(CFLAGS) -E $? -o $(@:.o=.e)
	@$(CC) $(CFLAGS) -c $? -o $@

clean:
	@echo -e "\E[33m"$@"\E[m"
	@tput sgr0
	@rm -rf $(BIN_DIR) $(OUT_DIR)
