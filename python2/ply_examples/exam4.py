#!/usr/bin/python
# -*- coding: utf-8 -*-  
# exam4.py

import ply.lex as lex
import ply.yacc as yacc

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

def p_commands(p):
    '''commands : empty
                            | commands command
    '''

def p_command(p):
    '''command : heat_switch
                            | target_set'''

def p_heatswitch(p):
    'heat_switch : TOKHEAT STATE'
    print "Heat turned " + p[2]

def p_targetset(p):
    'target_set : TOKTARGET TOKTEMPRATURE NUMBER'
    print "temprature set " + p[3]

def p_empty(p):
    'empty :'
    pass

# Error rule for syntax errors
def p_error(p):
    print "Syntax error in input!"


# Build the lexer
lexer = lex.lex()

# Build the parser
parser = yacc.yacc()
 
while True:
   try:
       s = raw_input('input > ')
   except EOFError:
       break
   if not s: continue
   result = parser.parse(s)