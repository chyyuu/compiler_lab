#!/usr/bin/python
# -*- coding: utf-8 -*-  

import ply.lex as lex

tokens = ['NUMBER','TOKHEAT','STATE','TOKTARGET','TOKTEMPRATURE']

def t_NUMBER(t):
    r'[0-9]+'
    return t;

def t_TOKHEAT(t):
    r'heat'
    return t

def t_STATE(t):
    r'on|off'
    return t

def t_TOKTARGET(t):
    r'target'
    return t

def t_TOKTEMPRATURE(t):
    r'temprature'
    return t

# 不做处理的符号 空格与tab
t_ignore = " \t"

# 行号统计
def t_newline(t):
    r'\n+'
    t.lexer.lineno += t.value.count("\n")

# 出错处理
def t_error(t):
    print "Illegal character '%s'" % t.value[0]
    t.lexer.skip(1)

# Build the parser
lexer = lex.lex()

# # 测试数据
# s = '''
# heat on
# '''

# # Give the lexer some input
# lexer.input(s)

# while True:
#     tok = lexer.token()
#     if not tok: break
#     print '(',tok.type,','+str(tok.value)+')'
