package proyecto1;
import java_cup.runtime.*;

%%

%cupsym cup
%class Lexer                               
%unicode
%cup
%line
%column
%public

%{
    private Symbol symbol(int type) {
        return new Symbol(type, yyline, yycolumn);
    }

    private Symbol symbol(int type, Object value) {
        return new Symbol(type, yyline, yycolumn, value);
    }
%}

/* Definiciones de patrones */
NUMBER =-?(0|[1-9][0-9]*)
ID =_\D[a-zA-Z0-9_]+_
FLOAT=[+-]?([0-9]*[.])?[0-9]+
BOOL=true|false
CHAR='(.){1}'
STRING="(.)+"

SPACE=[ \t\r\n]+
MULTILINE_COMMENT=\_([^]|\n)*_\/
ONELINE_COMMENT=\#.*

%%

{SPACE} {/* No hace nada */}
{MULTILINE_COMMENT} {/* No hace nada */}
{ONELINE_COMMENT} {/* No hace nada */}

{NUMBER} {return symbol(sym.INTEGER_VAL, Integer.parseInt(yytext()));}
{FLOAT} {return symbol(sym.FLOAT_VAL, Double.parseDouble(yytext()));}
{BOOL} {return symbol(sym.BOOL_VAL, Boolean.parseBoolean(yytext()));}
{CHAR} {return symbol(sym.CHAR_VAL, yytext().charAt(1));} // Extrae el caracter entre las comillas.
{STRING} {return symbol(sym.STRING_VAL, yytext().substring(1, yytext().length() - 1));} // Remueve las comillas.


"," {return symbol(sym.COMMA, ",");}

"rodolfo" {return symbol(sym.INTEGER, "rodolfo");}
"bromista" {return symbol(sym.FLOAT, "bromista");}
"trueno" {return symbol(sym.BOOLEAN, "trueno");}
"cupido" {return symbol(sym.CHAR, "cupido");}
"cometa" {return symbol(sym.STRING, "cometa");}

"abreempaque" {return symbol(sym.LEFT_BRACE, "abreempaque");}
"cierraempaque" {return symbol(sym.RIGHT_BRACE, "cierraempaque");}

"entrega" {return symbol(sym.ASSIGNMENT, "entrega");}

"abreregalo" {return symbol(sym.LEFT_PARENTHESIS, "abreregalo");}
"cierraregalo" {return symbol(sym.RIGHT_PARENTHESIS, "cierraregalo");}

"navidad" {return symbol(sym.ADD, "navidad");}
"intercambio" {return symbol(sym.SUB, "intercambio");}
"reyes" {return symbol(sym.DIV, "reyes");}
"nochebuena" {return symbol(sym.MUL, "nochebuena");}
"magos" {return symbol(sym.MOD, "magos");}
"adviento" {return symbol(sym.TO_THE_POWER, "adviento");}

"quien" {return symbol(sym.INCREMENT, "quien");}
"grinch" {return symbol(sym.DECREMENT, "grinch");}

"snowball" {return symbol(sym.LESS_THAN, "snowball");}
"evergreen" {return symbol(sym.LESS_OR_EQUAL_THAN, "evergreen");}
"minstix" {return symbol(sym.GREATER_THAN, "minstix");}
"upatree" {return symbol(sym.GREATER_OR_EQUAL_THAN, "upatree");}
"mary" {return symbol(sym.EQUAL_TO, "mary");}
"openslae" {return symbol(sym.NOT_EQUAL_TO, "openslae");}

"melchor" {return symbol(sym.AND, "melchor");}
"gaspar" {return symbol(sym.OR, "gaspar");}
"baltazar" {return symbol(sym.NOT, "baltazar");}

"finregalo" {return symbol(sym.SEMICOLON, "finregalo");}

"elfo" {return symbol(sym.IF, "elfo");}
"hada" {return symbol(sym.ELSE, "hada");}
"envuelve" {return symbol(sym.WHILE, "envuelve");}
"duende" {return symbol(sym.FOR, "duende");}
"varios" {return symbol(sym.SWITCH, "varios");}
"historia" {return symbol(sym.CASE, "historia");}
"ultimo" {return symbol(sym.DEFAULT, "ultimo");}
"corta" {return symbol(sym.BREAK, "corta");}
"envia" {return symbol(sym.RETURN, "envia");}
"sigue" {return symbol(sym.COLON, "sigue");}

"narra" {return symbol(sym.PRINT, "narra");}
"read" {return symbol(sym.INPUT, "read");}

"_verano_" {return symbol(sym.MAIN, "_verano_");}

{ID} {return symbol(sym.ID, yytext());}

/* Cualquier otro caracter no reconocido */
.                         { System.out.println("Caracter no reconocido: " + yytext() + " en linea: " + yyline); }