package pl.caltha.labs.ldapfilters;

import java.util.Queue;
import java.util.LinkedList;

@SuppressWarnings("unused")

%%
%class Lexer
%integer
%unicode
%char
%states YYCOMPOSITE, YYSIMPLE, YYDONE
%states YYATTR, YYOPER, YYPRES, YYVALUE
%{
	private final Queue<Integer> stack = new LinkedList<Integer>();
%}
%%

<YYINITIAL, YYCOMPOSITE> {
    "(&" | 
    "(|" | 
    "(!" {
        yybegin(YYCOMPOSITE);
        stack.offer(yystate());
        return yystate();
    }
  
    "(" {
        yybegin(YYSIMPLE);
        stack.offer(yystate());
        return yystate();
     }
}

<YYCOMPOSITE, YYVALUE, YYPRES> {
    ")" {
        if(stack.size() == 0)  
            throw new ParseException("too many closing parens at position " + yychar);        
        if(stack.size() == 1)
            yybegin(YYDONE);        
        else
            yybegin(stack.remove());
        return yystate(); 
    }
}

<YYSIMPLE> {
	[:whiteSpace:]* [^=><~()]+ [:whiteSpace:]* { 
		yybegin(YYATTR); 
		return yystate(); 
	}
}

<YYATTR> {
	"=" |
	"~=" |
	">=" |
	"<=" {
		yybegin(YYOPER);
		return yystate();
	}		
	"=*)" {
	    yypushback(1);
		yybegin(YYPRES);
		return yystate();
	}
}

<YYOPER> {
    ( [^\\()] | "\\\\" | "\\(" | "\\)" )* {
      yybegin(YYVALUE);
      return yystate();
    }
}

<YYDONE> {
    . | \n {
        throw new ParseException("Illegal character " + yytext() + " at position " + yychar );
    }
}

/* error fallback */
. | \n { 
    throw new ParseException("Illegal character " + yytext() + " at position " + yychar ); 
}