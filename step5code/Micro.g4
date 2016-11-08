grammar Micro;

/* Program */
@members {

      public SymbolTableTree tree = new SymbolTableTree();
      public int block_number = 0;
      public java.util.LinkedList<Integer> if_label = new java.util.LinkedList<Integer>();
      public IRList ir_list = new IRList();  
      public AbstractSyntaxTree abs;
      public int reg_number = 0;
      public IRNode new_node, extra_node;
      int old_node_index;
      public String tempString = IRNode.getTempPrefix();
}
program: 'PROGRAM' id 
         'BEGIN' pgm_body 
         'END' {
                 //tree.print();
                 //ir_list.print();
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
    ir_list.addLast(new IRNode("var", null, null, id));
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
assign_expr: id {abs = new AbstractSyntaxTree(tree.current_scope.getSymbol($id.text), reg_number, tree.current_scope);}
':=' expr {abs.end();ir_list.addAll(abs.ir_list); reg_number = abs.getTempCount();}
;

read_stmt: 'READ' '(' id_list ')' ';'
{
  String[] idList = $id_list.text.split(",");
  String opcode = null;
  for (String id : idList){
    Symbol symbol = tree.current_scope.getSymbol(id);
    if (symbol.type.equals("INT")){opcode = "READI";}
    else if (symbol.type.equals("FLOAT")){opcode = "READF";}
    IRNode ir_node = new IRNode(opcode, null, null, id);
    ir_list.addLast(ir_node); 
  }
}
;
write_stmt: 'WRITE' '(' id_list ')' ';'
{
  String[] idList = $id_list.text.split(",");
  String opcode = null;
  for (String id : idList){
    Symbol symbol = tree.current_scope.getSymbol(id);
    if (symbol.type.equals("INT")){opcode = "WRITEI";}
    else if (symbol.type.equals("FLOAT")){opcode = "WRITEF";}
    else if (symbol.type.equals("STRING")){opcode = "WRITES";}
    IRNode ir_node = new IRNode(opcode, null, null, id);
    ir_list.addLast(ir_node); 
  }
}
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

primary: '('   {abs.open_expr();}
  expr ')'     {abs.close_expr();} | 
  id           {abs.add_operand($id.text);}| 
  INTLITERAL   {abs.add_operand($INTLITERAL.text);}| 
  FLOATLITERAL {abs.add_operand($FLOATLITERAL.text);}
  ;
addop: '+'{abs.add_operator("+");} | 
       '-'{abs.add_operator("-");}
       ;
mulop: '*'{abs.add_operator("*");} | 
       '/'{abs.add_operator("/");}
       ;

/* Complex Statements and Condition */ 

if_stmt: 'IF' 
        {
          tree.enterScope("BLOCK", ++block_number);
          //ir_list.addLast(new IRNode("LABEL", null, null, "label"+(2*tree.current_scope.block_number)));
          if_label.push(2*tree.current_scope.block_number + 1);
        } 
        '(' cond ')' {ir_list.addLast(new_node); old_node_index = ir_list.size()-1;}
        decl 
        stmt_list {ir_list.addLast(new IRNode("JUMP",null,null,"label"+if_label.getFirst()));}
        else_part 
        'ENDIF' {
          	ir_list.addLast(new IRNode("LABEL",null,null, "label"+if_label.pop()));
          	tree.exitScope();
          }
;

else_part: 
    'ELSIF' {
              tree.enterScope("BLOCK", ++block_number);
              ir_list.replace(old_node_index, "label"+ (2*tree.current_scope.block_number));
              ir_list.addLast(new IRNode("LABEL", null, null, "label" + (2*tree.current_scope.block_number) ));
    } 
    '(' cond ')' {
      ir_list.addLast(new_node); old_node_index = ir_list.size()-1;
    }
    
    decl 
    stmt_list{ir_list.addLast(new IRNode("JUMP",null,null,"label"+if_label.getFirst()));} 
    {
      tree.exitScope();
    }
    else_part |  
; //empty

cond: 
    {abs = new AbstractSyntaxTree(reg_number, tree.current_scope);}      
    expr 
      {new_node = new IRNode(null, null, null, null);
       abs.setType($expr.text); abs.end(); ir_list.addAll(abs.ir_list); reg_number = abs.getTempCount();
       new_node.op1 = abs.root.value;} 
    
    compop {new_node.opcode+=abs.type;}
      {abs = new AbstractSyntaxTree(reg_number, tree.current_scope);}
    expr
      {abs.setType($expr.text); abs.end();ir_list.addAll(abs.ir_list); reg_number = abs.getTempCount();
       new_node.op2 = abs.root.value; 
       new_node.result = "label"+ if_label.getFirst();
      }  
    | 
    'TRUE' {
            ir_list.addLast(new IRNode("STOREI", "0", null, tempString + (++reg_number)));
            ir_list.addLast(new IRNode("STOREI", "1", null, tempString + (++reg_number))); 
            new_node = new IRNode("EQI", tempString + reg_number, tempString + (reg_number-1), "label"+ if_label.getFirst() );
          }
    | 
    'FALSE'{
            ir_list.addLast(new IRNode("STOREI", "1", null, tempString + (++reg_number)));
            ir_list.addLast(new IRNode("STOREI", "1", null, tempString + (++reg_number))); 
            new_node = new IRNode("EQI", tempString + reg_number, tempString + (reg_number-1), "label"+ if_label.getFirst());
          }
;

compop: 
  '<'  {new_node.opcode = "GE";}  | 
  '>'  {new_node.opcode = "LE";} | 
  '='  {new_node.opcode = "NE";} | 
  '!=' {new_node.opcode = "EQ";} |
  '<=' {new_node.opcode = "GT";} |
  '>=' {new_node.opcode = "LT";}
;

do_while_stmt: 'DO' 
{
  tree.enterScope("BLOCK", ++block_number);
  ir_list.addLast(new IRNode("LABEL", null, null, "label"+(2*tree.current_scope.block_number)));
  if_label.push(2*tree.current_scope.block_number+1);
}
decl stmt_list 'WHILE' '(' cond ')' ';' 
{
  ir_list.addLast(new_node);
  ir_list.addLast(new IRNode("JUMP", null, null, "label"+2*tree.current_scope.block_number));
  ir_list.addLast(new IRNode("LABEL", null, null, "label"+if_label.pop()));
  tree.exitScope();  
};

/* ----------------------   LEXER RULES! ---------------------- */
KEYWORD
       : 'PROGRAM'| 'BEGIN'| 'END'| 'FUNCTION'| 'READ'| 'WRITE'| 'IF'
       | 'ELSIF'| 'ENDIF'| 'DO'| 'WHILE'| 'CONTINUE'| 'BREAK'| 'RETURN'
       | 'INT'| 'VOID'| 'STRING'| 'FLOAT'| 'TRUE'| 'FALSE'
         ;
IDENTIFIER: [A-Za-z][A-Za-z0-9]*;
INTLITERAL: [0-9]+; 
FLOATLITERAL: [0-9]*'.'[0-9]+; 
STRINGLITERAL : '"'(~'"')*'"';      
COMMENT: ('--'(~'\n')*'\n') -> skip; 
WS: (' ' | '\t' | '\n' | '\r' )+ -> skip;
OPERATOR
      : ':='| '+' | '-' | '*' | '/' | '=' | '!=' | '<' | '>' | '(' 
      | ')' | ';' | ',' | '<=' | '>=' 
      ; 
