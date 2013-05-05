package pl.caltha.labs.ldapfilters;

import java.util.LinkedList;
import java.util.Queue;
import java.io.IOException;
import java.io.StringReader;

@SuppressWarnings("unused")

%%
%class FilterParser
%integer
%unicode
%char
%states YYCOMPOSITE, YYSIMPLE, YYDONE
%states YYATTRNAME, YYATTRTYPE, YYOPER, YYPRES, YYVALUE
%{
    private String attrName;
    
    private AttributeType attrType;
    
    private Operator operator;
    
    private String value;

    private final LinkedList<Integer> stack = new LinkedList<Integer>();

    private CompoundFilter comp = new Sentinel();

	private final LinkedList<CompoundFilter> compStack = new LinkedList<CompoundFilter>();
	
	private static class Sentinel extends CompoundFilter {
	    public <V> V accept(FilterVisitor<V> visitor, V data) {
	    	return data;
	    }
	}
	
	private CompoundFilter booleanOperator(char c) {
	    switch(c) {
	       case '&':
	          return new AndFilter();
	       case '|':
	          return new OrFilter();
	       case '!':
	          return new NotFilter();
	       default:
	          throw new ParseException("invalid boolean operator at " + (yychar + 1));
	    }
	}
	
	private Filter result() {
	    if(!(comp instanceof Sentinel))
	        throw new IllegalStateException("unbalanced expression");
	    return comp.getTerms().get(0);
	}
	
	public static Filter parse(String input) throws ParseException {
	    FilterParser parser = new FilterParser(new StringReader(input));
	    try {
	        int state;
	        do {
	           state = parser.yylex();
	        } while(state != YYEOF);
	    } catch(IOException e) {
	    	throw new RuntimeException("Unexpected IOException from StringReader", e);
	    }
	    return parser.result();
	}
%}
%%

<YYINITIAL, YYCOMPOSITE> {
    "(&" | 
    "(|" | 
    "(!" {
        compStack.add(0, comp);
        comp = booleanOperator(yycharat(1));
        stack.add(0, yystate());
        yybegin(YYCOMPOSITE);
        return yystate();
    }
  
    "(" {
        stack.add(0, yystate());
        attrType = AttributeType.STRING;
        yybegin(YYSIMPLE);
        return yystate();
     }
}

<YYCOMPOSITE, YYVALUE, YYPRES> {
    ")" {
        if(stack.size() == 0)  
            throw new ParseException("too many closing parens at position " + yychar);
        
        if(yystate() == YYVALUE || yystate() == YYPRES) {
           comp.addTerm(SimpleFilter.newFilter(attrName, attrType, null, operator, value));
        } else {
           CompoundFilter prev = compStack.remove();
           prev.addTerm(comp);
           comp = prev;
        }
        
        if(stack.size() == 1)
          yybegin(YYDONE);
        else
          yybegin(stack.remove());
        return yystate(); 
    }
}

<YYSIMPLE> {
	[^=~()<>:]+ { 
		attrName = yytext().trim();
		yybegin(YYATTRNAME);
		return yystate(); 
	}
}

<YYATTRNAME> {
    ":" [^=~():]+ {
		attrType = AttributeType.parse(yytext().trim().substring(1), yychar + 1);
		yybegin(YYATTRTYPE);
		return yystate();
	}
}

<YYATTRNAME, YYATTRTYPE> {
	"=" |
	"~=" |
	">=" |
	"<=" {
		yybegin(YYOPER);
		operator = Operator.parse(yytext(), yychar);
		return yystate();
	}		
	"=*)" {
	    yypushback(1);
		yybegin(YYPRES);
		operator = Operator.parse(yytext(), yychar);
		return yystate();
	}
}

<YYOPER> {
    ( [^\\()] | "\\\\" | "\\(" | "\\)" )* {
      yybegin(YYVALUE);
      value = yytext();
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