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
%states YYSIMPLE, YYCOMPOSITE, YYNESTED, YYDONE
%states YYATTRNAME, YYTYPEDATTR, YYATTRTYPE, YYATTRELEMTYPE, YYOPER, YYPRES, YYVALUE
%states YYREQUIREMENTS, YYREQUIREMENT, YYREQUIREMENT_DIRECTIVE, YYDIRECTIVE_NAME, YYDIRECTIVE_VALUE, YYDIRECTIVE_CLOSE_FILTER
%ctorarg int allowedNestingDepth
%init{
	this.allowedNestingDepth = allowedNestingDepth;
%init}
%{
    private String attrName;
    
    private AttributeType attrType;

    private AttributeType attrElemType; 
    
    private Operator operator;
    
    private String value;

    private final LinkedList<Integer> stack = new LinkedList<Integer>();

    private CompoundFilter comp = new Sentinel();

	private final LinkedList<CompoundFilter> compStack = new LinkedList<CompoundFilter>();
	
	private final int allowedNestingDepth;
	
	private int nestingDepth = 0;
	
	private String directiveName;
	
	private String directiveValue;
	
	private Requirement requirement;
	
	protected LinkedList<Requirement> requirements;
	
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
	
	protected void parseAll() throws ParseException {
	    try {
	        int state;
	        do {
	           state = yylex();
	        } while(state != YYEOF);
	    } catch(IOException e) {
	    	throw new RuntimeException("Unexpected IOException from StringReader", e);
	    }
	}
	
	public static Filter parse(String input) throws ParseException {
		return parse(input, 0);
	}
	
	public static Filter parse(String input, int allowedNestingDepth) throws ParseException {
	    FilterParser parser = new FilterParser(new StringReader(input), allowedNestingDepth);
	    parser.parseAll();
	    return parser.result();
	}
%}

ws = [ \n\t\f\r\n]*
extended = [a-zA-Z0-9._-]+

%%

<YYINITIAL, YYCOMPOSITE, YYNESTED> {
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
        attrElemType = null;
        yybegin(YYSIMPLE);
        return yystate();
     }
}

<YYCOMPOSITE, YYNESTED, YYVALUE, YYPRES> {
    ")" {
        if(stack.size() == 0)  
            throw new ParseException("too many closing parens at position " + yychar);
        
        if(yystate() == YYVALUE || yystate() == YYPRES) {
            try {
	            Filter term = SimpleFilter.newFilter(attrName, attrType, attrElemType, operator, value);
	            comp.addTerm(term);
	        } catch(Exception e) {    
	            throw new ParseException(e.getMessage() + " at position " + yychar);
	        }
        } else {
           CompoundFilter prev = compStack.remove();
           prev.addTerm(comp);
           comp = prev;
        }
        
        if(stack.size() == 1)
            yybegin(YYDONE);
        else
            yybegin(stack.remove());
            
        if(yystate() == YYNESTED)
        	nestingDepth--;
            
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
    ":" {
        yybegin(YYTYPEDATTR);
    }
}
 
<YYTYPEDATTR> {
    "List<" {
        attrType = AttributeType.LIST;
		yybegin(YYATTRELEMTYPE);
		return yystate();
    }	   

	[^=~<>():]+ {
		String typeRepr = yytext().trim();
		attrType = AttributeType.parse(typeRepr, yychar);
		yybegin(YYATTRTYPE);
		return yystate();
	}
}

<YYATTRELEMTYPE> {
    [^=~<>():]+ ">" {
        String typeRepr = yytext().trim();
        attrElemType = AttributeType.parse(typeRepr.substring(0, typeRepr.length() - 1), yychar);
        yybegin(YYATTRTYPE);
	    return yystate();
    }
}

<YYATTRNAME, YYATTRTYPE> {
	"=" | "~=" | ">=" | "<=" {
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
    "(" {
        if(operator != Operator.EQUAL) 
        	throw new ParseException("Illegal operator preceding nested filter at position " + yychar);
        nestingDepth++;
        if(nestingDepth > allowedNestingDepth)
        	throw new ParseException("Allowed nesting depth of " + allowedNestingDepth + " exceeded at position " + yychar); 		
        yypushback(1);
    	compStack.add(0, comp);
        comp = new NestedFilter(attrName);
        yybegin(YYNESTED);
        return yystate();
    }

    ( [^\\()] | "\\\\" | "\\(" | "\\)" )* {
        yybegin(YYVALUE);
        value = yytext();
        return yystate();
    }
}

<YYREQUIREMENTS> {
    {ws} [a-zA-Z0-9._-]+ {ws} {
  	    requirement = new Requirement(yytext().trim());
    	requirements.add(requirement);
  	    yybegin(YYREQUIREMENT);
  	    return yystate();
    }
}

<YYREQUIREMENT> {
    ";" {ws} {  		
  		yybegin(YYREQUIREMENT_DIRECTIVE);
  		return(yystate());
    }
    "," {ws} {
    	yybegin(YYREQUIREMENTS);
    	return(yystate());
    }
    <<EOF>> {
    	yybegin(YYDONE);
    	return yystate();
    }
}

<YYREQUIREMENT_DIRECTIVE> {
    "filter" {ws} ":=" {ws} "\"" {
    	compStack.add(0, comp);
        comp = new NestedFilter(requirement.getNamespace());
        stack.add(0, YYDIRECTIVE_CLOSE_FILTER);
        yybegin(YYNESTED);
        return yystate();
    }

    {extended} {ws} ":=" {
        directiveName = yytext().substring(0, yytext().length() - 2).trim();
        yybegin(YYDIRECTIVE_NAME);
        return yystate();
    }
}

<YYNESTED> {
	"\"" {
		if(stack.size() > 0 && stack.get(0) == YYDIRECTIVE_CLOSE_FILTER) {
		    yypushback(1);
		    yybegin(stack.remove());
		}
	}
}

<YYDIRECTIVE_CLOSE_FILTER> {
    "\"" {
        requirement.setFilter((NestedFilter)comp);
        yybegin(YYDIRECTIVE_VALUE);
    }
}

<YYDIRECTIVE_NAME> {
    [^,;]+  {
    	requirement.addDirective(directiveName, yytext().trim());
    	yybegin(YYDIRECTIVE_VALUE);
    	return yystate();
    }
}

<YYDIRECTIVE_VALUE> {
	";" {ws} {
	    yybegin(YYREQUIREMENT_DIRECTIVE);
	}
	"," {ws} {
		yypushback(yylength());
		yybegin(YYREQUIREMENT);
	}
	<<EOF>> {
		yybegin(YYDONE);
    	return yystate();
	}
}


<YYDONE> {
    . | \n {
        throw new ParseException("Illegal character " + yytext() + " at position " + yychar );
    }
}

<<EOF>> {
    if(yystate() != YYDONE)
		throw new ParseException("Unexpected end of input");
    return YYEOF;		
}

/* error fallback */
. | \n { 
    throw new ParseException("Illegal character " + yytext() + " at position " + yychar ); 
}
