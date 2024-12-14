import java_cup.runtime.*;

%%
%cup
%class Lexer
%public
%unicode
%line
%char
%column
%full

%{
StringBuffer string = new StringBuffer();

private Symbol symbol(int type) {
    return new Symbol(type, yyline, yycolumn);
}
private Symbol symbol(int type, Object value) {
    return new Symbol(type, yyline, yycolumn, value);
}

}%

NUM = [0-9]+

%%

<YYINITIAL> {NUM} {return symbol(sym.NUMBER, Integer.parseInt(yytext()));}