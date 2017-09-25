package Example;

import java_cup.runtime.SymbolFactory;
%%
%cup
%class Scanner
%{
	public Scanner(java.io.InputStream r, SymbolFactory sf){
		this(r);
		this.sf=sf;
	}
	private SymbolFactory sf;
%}
%eofval{
    return sf.newSymbol("EOF",sym.EOF);
%eofval}

%%
";" { return sf.newSymbol("Semicolon",sym.SEMI); }
"+" { return sf.newSymbol("Plus",sym.PLUS); }
"*" { return sf.newSymbol("Times",sym.TIMES); }
"(" { return sf.newSymbol("Left Bracket",sym.LPAREN); }
")" { return sf.newSymbol("Right Bracket",sym.RPAREN); }
[0-9]+ { return sf.newSymbol("Integral Number",sym.NUMBER, new Integer(yytext())); }
[ \t\r\n\f] { /* ignore white space. */ }
. { System.err.println("Illegal character: "+yytext()); }
