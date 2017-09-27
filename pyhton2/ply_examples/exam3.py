#!/usr/bin/python
# -*- coding: utf-8 -*-  

import ply.yacc as yacc
from exam3lex import tokens

def p_commands(p):
    '''commands : empty
                            | commands command
    '''

def p_command(p):
    '''command : heatswitch
                            | targetset'''

def p_heatswitch(p):
    'heatswitch : TOKHEAT STATE'
    print "Heat turned on or off"

def p_targetset(p):
    'targetset : TOKTARGET TOKTEMPRATURE NUMBER'
    print "temprature set"

def p_empty(p):
    'empty :'
    pass

# Error rule for syntax errors
def p_error(p):
    print "Syntax error in input!"

# Build the parser
parser = yacc.yacc()
 
while True:
   try:
       s = raw_input('calc > ')
   except EOFError:
       break
   if not s: continue
   result = parser.parse(s)
   print result