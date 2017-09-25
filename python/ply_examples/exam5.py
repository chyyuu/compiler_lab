#!/usr/bin/python
# -*- coding: utf-8 -*-  
# exam5.py

import ply.lex as lex
import ply.yacc as yacc

reserved = {
   'zone' : 'ZONETOK',
   'file' : 'FILETOK',
   'else' : 'ELSE',
}

tokens = ['WORD','FILENAME','QUOTE','OBRACE','EBRACE','SEMICOLON'] + list(reserved.values())

t_FILENAME = r'[a-zA-Z0-9/.-]+'

t_QUOTE = r'"'

t_OBRACE = r'{'

t_EBRACE = r'}'

t_SEMICOLON =  r';'

def t_WORD(t):
    r'[a-zA-Z][a-zA-Z0-9]+'
    t.type = reserved.get(t.value,'WORD')
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
    if len(p) == 3:
        p[0] = p[2]

def p_command(p):
    'command : zone_set'
    p[0] = p[1]

def p_zoneset(p):
    'zone_set : ZONETOK quotename zonecontent'
    print "complete zone for",p[2],"found"
    p[0] = p[3]

def p_zonecontent(p):
    'zonecontent : OBRACE zonestatements EBRACE SEMICOLON'
    p[0] = p[2]

def p_quotename(p):
    'quotename : QUOTE FILENAME QUOTE'
    p[0] = p[2]

def p_zonestatements(p):
    '''zonestatements : empty
                                        | zonestatements zonestatement SEMICOLON
    '''
    if len(p) == 4:
        p[0] = p[2]


def p_zonestatement(p):
    '''zonestatement : statements
                                    | FILETOK quotename
    '''
    if p[1]=='file':
        p[0] = p[2]
        print "a zonefile name",p[2],"was encountered"

def p_block(p):
    'block : OBRACE zonestatements EBRACE SEMICOLON'

def p_statements(p):
    '''statements : empty
                            | statements statement 
    '''

def p_statement(p):
    '''statement : WORD
                            | block
                            | quotename'''

# Error rule for syntax errors
def p_error(p):
    print "Syntax error in input!"

def p_empty(p):
    'empty :'
    pass

# Build the lexer
lexer = lex.lex()

# Build the parser
parser = yacc.yacc()

file_object = open('test2.conf')
s = file_object.read()
print s

# lexer.input(s)

# while True:
#     tok = lexer.token()
#     if not tok: break
#     print tok

result = parser.parse(s)
print result
