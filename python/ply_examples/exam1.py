#!/usr/bin/python
# -*- coding: utf-8 -*-  

import ply.lex as lex

tokens = ['START','STOP']

def t_START(t):
    r'start'
    print "start command received"
    return t

def t_STOP(t):
    r'stop'
    print "stop command received"
    return t

# 行号统计
def t_newline(t):
    r'\n+'
    t.lexer.lineno += t.value.count("\n")

# 出错处理
def t_error(t):
    print "Illegal character '%s'" % t.value[0]
    t.lexer.skip(1)


# Build the lexer
lexer = lex.lex()

# 测试数据
s = '''
stop and start
'''

# Give the lexer some input
lexer.input(s)

while True:
    tok = lexer.token()
    print tok
    if not tok: break
