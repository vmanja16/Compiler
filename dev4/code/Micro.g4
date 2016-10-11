grammar Micro;
           
/* Program */
@members {
      public SymbolTableTree tree = new SymbolTableTree();
      public int block_number = 0;
      public IRList ir_list = new IRList();  
}
program: 'PROGRAM' id 
         'BEGIN' pgm_body 
         'END' {
                 //tree.print();
                 ir_list.print();
               }
         ;
         
id: IDENTIFIER;

pgm_body: decl func_declarations; 

decl: string_decl decl | var_decl decl | ; //empty


/* Global String Declaration */

string_decl: 'STRING' id ':=' str ';' 
{
  Symbol symbol = new Symbol($id.text, "STRING", $str.text);
  tree.current_scope.add_symbol(symbol);      
}
;

str: STRINGLITERAL;


/* Variable Declaration */

var_decl: var_type id_list ';'
{
  String[] strList = $id_list.text.split(",");
  for (String id : strList){
    Symbol symbol = new Symbol(id, $var_type.text, "0");
    tree.current_scope.add_symbol(symbol);
  }
}
;

var_type: 'FLOAT' | 'INT';
any_type: var_type | 'VOID';
id_list: id id_tail;
id_tail: ',' id id_tail | ; // empty


/* Function Parameter List */

param_decl_list: param_decl param_decl_tail | ; // empty

param_decl: var_type id {
  Symbol symbol = new Symbol($id.text, $var_type.text, "0");
  tree.current_scope.add_symbol(symbol);
}
;
param_decl_tail: ',' param_decl param_decl_tail | ; // empty


/* Function Declarations */

func_declarations: func_decl func_declarations | ; // empty

func_decl: 'FUNCTION' any_type id 
{
  tree.enterScope($id.text, 0);
}
'(' param_decl_list ')' 'BEGIN' func_body 
'END'
{
  tree.exitScope();
}
;

func_body: decl stmt_list;

/* Statement List */

stmt_list: stmt stmt_list| ; // empty
stmt   : base_stmt | if_stmt | do_while_stmt;
base_stmt : assign_stmt | read_stmt | write_stmt | return_stmt;

/* Basic Statements */

assign_stmt: assign_expr ';';
assign_expr: id ':=' expr;
read_stmt: 'READ' '(' id_list ')' ';';
write_stmt: 'WRITE' '(' id_list
{
  String[] idList = $id_list.text.split(",");
  String opcode = null;
  for (String id : idList){
    Symbol symbol = tree.current_scope.getSymbol(id);
    if (symbol.type.equals("INT")){opcode = "WRITEI";}
    else if (symbol.type.equals("FLOAT")){opcode = "WRITEF";}
    IRNode ir_node = new IRNode(opcode, null, null, id);
    ir_list.addLast(ir_node); 
  }
}
 ')' ';'
;
return_stmt : 'RETURN' expr ';';

/* Expressions */

expr: expr_prefix factor;
expr_prefix: expr_prefix factor addop | ; // empty
factor: factor_prefix postfix_expr;
factor_prefix: factor_prefix postfix_expr mulop | ; //empty
postfix_expr: primary | call_expr;
call_expr: id '(' expr_list ')';
expr_list: expr expr_list_tail | ; // empty
expr_list_tail: ',' expr expr_list_tail | ; // empty
primary: '(' expr ')' | id | INTLITERAL | FLOATLITERAL;
addop: '+' | '-';
mulop: '*' | '/';

/* Complex Statements and Condition */ 

if_stmt: 'IF' 
{
  tree.enterScope("BLOCK", ++block_number);
} 
'(' cond ')' decl stmt_list else_part 'ENDIF' {tree.exitScope();};

else_part: 'ELSIF' '(' cond ')' {
  tree.enterScope("BLOCK", ++block_number);
}

decl stmt_list 
{
  tree.exitScope();
}
else_part | ; //empty

cond: expr compop expr | 'TRUE' | 'FALSE';
compop: '<' | '>' | '=' | '!=' | '<=' | '>=';

do_while_stmt: 'DO' 
{
  tree.enterScope("BLOCK", ++block_number);
}
decl stmt_list 'WHILE' '(' cond ')' ';' 
{
  tree.exitScope();
};

/* ----------------------   LEXER RULES! ---------------------- */

KEYWORD
       : 'PROGRAM'| 'BEGIN'| 'END'| 'FUNCTION'| 'READ'| 'WRITE'| 'IF'
       | 'ELSIF'| 'ENDIF'| 'DO'| 'WHILE'| 'CONTINUE'| 'BREAK'| 'RETURN'
       | 'INT'| 'VOID'| 'STRING'| 'FLOAT'| 'TRUE'| 'FALSE'
         ;

IDENTIFIER
      : [A-Za-z][A-Za-z0-9]*;


INTLITERAL
      : [0-9]+; 

FLOATLITERAL
      : [0-9]*'.'[0-9]+; 
      
STRINGLITERAL 
      : '"'(~'"')*'"';      

COMMENT
      : ('--'(~'\n')*'\n') -> skip; 

WS
      : (' ' | '\t' | '\n' | '\r' )+ -> skip;

OPERATOR
      : ':='| '+' | '-' | '*' | '/' | '=' | '!=' | '<' | '>' | '(' 
      | ')' | ';' | ',' | '<=' | '>=' 
      ; 
