#!/usr/bin/python
# -*- coding: utf-8 -*-  

import ply.lex as lex

tokens = ["WORD","FILENAME","QUOTE","OBRACE","EBRACE","SEMICOLON"]

def t_WORD(t):
    r'[a-zA-Z][a-zA-Z0-9-]*'
    print "WORD ",
    return t

def t_FILENAME(t):
    r'[a-zA-Z0-9/.-]+'
    print "FILENAME ",
    return t

def t_QUOTE(t):
    r'"'
    print "QUOTE ",
    return t

def t_OBRACE(t):
    r'{'
    print "OBRACE ",
    return t

def t_EBRACE(t):
    r'}'
    print "EBRACE ",
    return t

def t_SEMICOLON(t):
    r';'
    print "SEMICOLON ",
    return t


# 不做处理的符号 空格与tab
t_ignore = " \t"

# 行号统计
def t_newline(t):
    r'\n+'
    t.lexer.lineno += t.value.count("\n")
    print ''

# 出错处理
def t_error(t):
    print "Illegal character '%s'" % t.value[0]
    t.lexer.skip(1)

# Build the lexer
lexer = lex.lex()


file_object = open('test.conf')
s = file_object.read()
print s


# Give the lexer some input
lexer.input(s)

while True:
    tok = lexer.token()
    if not tok: break