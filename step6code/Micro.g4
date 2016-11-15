grammar Micro;

/* Program */
@parser::header{
  import java.lang.*;
  import java.util.*;
}
@members {
      public SymbolTableTree tree = new SymbolTableTree();
      public int block_number = 0;
      public java.util.LinkedList<Integer> exit_stack = new java.util.LinkedList<Integer>();
      public IRList ir_list = new IRList();  
      public AbstractSyntaxTree abs;
      public int old_node_index = 0;
      public IRNode new_node, extra_node;
      public java.util.LinkedList<Integer> old_node_stack = new java.util.LinkedList<Integer>();
      public String tempString = IRNode.getTempPrefix();
      public int links;
      public Function function;
}
program: 'PROGRAM' id 
         'BEGIN' pgm_body 
         'END' 
         ;
         
id: IDENTIFIER;
pgm_body: 
  decl {ir_list.addAll(ir_list.callMainList());} // CALL MAIN (push + jsr)
  func_declarations; 

decl: string_decl{links++;} decl | var_decl decl | ; //empty

/* Global String Declaration */

string_decl: 'STRING' id ':=' str ';' 
{
  Symbol symbol = new Symbol($id.text, "STRING", $str.text);
  ir_list.addLast(new IRNode("STR", $id.text, null, $str.text));
  tree.current_scope.add_symbol(symbol);      
}
;

str: STRINGLITERAL;

/* Variable Declaration */

var_decl: var_type id_list ';'
{
  String[] strList = $id_list.text.split(",");
  for (String id : strList){
    links++;
    Symbol symbol = new Symbol(id, $var_type.text, "0");
    if (tree.isRoot()){ir_list.addLast(new IRNode("var", null, null, id));}
    else{function.addLocal(symbol);}
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
  function.addParameter($id.text, $var_type.text);
}
;
param_decl_tail: ',' param_decl param_decl_tail | ; // empty

/* Function Declarations */

func_declarations: func_decl func_declarations | ; // empty

func_decl: 
  'FUNCTION' any_type id 
  {
    tree.enterScope($id.text, 0);
    function = new Function();
    tree.current_scope.add_function(function);
    ir_list.addLast(IRNode.getLabelNode($id.text));
  }
  '(' 
  param_decl_list{function.addParameterValues();}
  ')' 
  'BEGIN' 
  func_body 
  'END'
  {
    if (!ir_list.getLast().opcode.equals("RET")){
      ir_list.addLast(IRNode.getReturnNode());
    }
    tree.exitScope();
  }
;

func_body: {links = 0;}
  decl{
    // TODO: INCLUDE TEMPS AND STATEMENT decls in # of links!
    ir_list.addLast(new IRNode("LINK", null, null, Integer.toString(links)));

  }
  stmt_list
;

/* Statement List */

stmt_list: stmt stmt_list| ; // empty
stmt   : base_stmt | if_stmt | do_while_stmt;
base_stmt : assign_stmt | read_stmt | write_stmt | return_stmt;

/* Basic Statements */

assign_stmt: assign_expr ';';
// TODO: In ABS.end(): check if symbol is global or should be a function reg, THEN assign
// TODO: Will a PARAM ever be on LHS??!?!?!
assign_expr: id {abs = new AbstractSyntaxTree(tree.current_scope.getSymbol($id.text), function.reg_count, tree.current_scope);}
':=' expr {abs.end();ir_list.addAll(abs.ir_list); function.reg_count = abs.getTempCount();}
;
// TODO: ADD PARAMETER WRITES!
read_stmt: 'READ' '(' id_list ')' ';'
{
  String[] idList = $id_list.text.split(",");
  String opcode = null; 
  for (String id : idList){
    // Change res if it's a local
    String res = id; 
    Symbol symbol = function.getLocal(id);
    if (symbol==null){symbol = tree.current_scope.getSymbol(id);}
    else{res = symbol.value;} 

    if (symbol.type.equals("INT")){opcode = "READI";}
    else if (symbol.type.equals("FLOAT")){opcode = "READF";}
    IRNode ir_node = new IRNode(opcode, null, null, res);
    ir_list.addLast(ir_node); 
  }
}
;
write_stmt: 'WRITE' '(' id_list ')' ';'
{
  String[] idList = $id_list.text.split(",");
  String opcode = null;
  for (String id : idList){    
    // Change res if it's a local
    String res = id; 
    Symbol symbol = function.getLocal(id);
    if (symbol==null){symbol = tree.current_scope.getSymbol(id);}
    else{res = symbol.value;} 

    if (symbol.type.equals("INT")){opcode = "WRITEI";}
    else if (symbol.type.equals("FLOAT")){opcode = "WRITEF";}
    else if (symbol.type.equals("STRING")){opcode = "WRITES";}
    IRNode ir_node = new IRNode(opcode, null, null, res);
    ir_list.addLast(ir_node); 
  }
}
;
return_stmt : 
  'RETURN' 
  expr{
    ir_list.addLast(IRNode.getReturnNode());
  }
 ';'
;

/* Expressions */

expr: expr_prefix factor;
expr_prefix: expr_prefix factor addop | ; // empty
factor: factor_prefix postfix_expr;
factor_prefix: factor_prefix postfix_expr mulop | ; //empty
postfix_expr: primary | call_expr; // TODO: old_abs and new abs since we need to make one in the middle of another for funcCall

call_expr: {ir_list.addLast(IRNode.getPushNode());} // push return value 
  id 
  '(' 
  expr_list
  ')' {ir_list.addAll(ir_list.callFunction($id.text));}
;
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
          exit_stack.push(tree.getCurrentExit());
        } 
        '(' cond ')' {ir_list.addLast(new_node); old_node_index = ir_list.size()-1; old_node_stack.push(old_node_index);}
        decl 
        stmt_list {ir_list.addLast(IRNode.getJumpNode(exit_stack.getFirst()));}
        else_part 
        'ENDIF' {
            ir_list.addLast(IRNode.getLabelNode(exit_stack.pop()));
            old_node_stack.pop(); // Pop due to nested if statements!
          	tree.exitScope();
          }
;

else_part: 
    'ELSIF' {
              tree.enterScope("BLOCK", ++block_number);
              // we guessed in the COND segment that the label was ENDIF, need to replace with (and add) the new ELSIF entrance label
              ir_list.replace(old_node_stack.pop(), "label"+ tree.getCurrentEntrance()); 
              ir_list.addLast(IRNode.getLabelNode(tree.getCurrentEntrance()));
    } 
    '(' cond ')' {
      ir_list.addLast(new_node); old_node_index = ir_list.size()-1; old_node_stack.push(old_node_index);
    }
    
    decl 
    stmt_list{
      ir_list.addLast(IRNode.getJumpNode(exit_stack.getFirst()));
      tree.exitScope();
    }
    else_part 
    |  
; //empty

/* cond creates a "new_node" IRNode with the correct Comparison type: assumes the jump label is top of the exit_stack stack */

cond: 
    {abs = new AbstractSyntaxTree(function.reg_count, tree.current_scope);}      
    expr 
      {new_node = new IRNode(null, null, null, null);
       abs.setType($expr.text); abs.end(); ir_list.addAll(abs.ir_list); function.reg_count = abs.getTempCount();
       new_node.op1 = abs.root.value;} 
    
    compop {new_node.opcode+=abs.type;}
      {abs = new AbstractSyntaxTree(function.reg_count, tree.current_scope);}
    expr
      {abs.setType($expr.text); abs.end();ir_list.addAll(abs.ir_list); function.reg_count = abs.getTempCount();
       new_node.op2 = abs.root.value; 
       new_node.result = "label"+ exit_stack.getFirst();
      }  
    | 
    'TRUE' {
            ir_list.addLast(new IRNode("STOREI", "0", null, tempString + (++function.reg_count)));
            ir_list.addLast(new IRNode("STOREI", "1", null, tempString + (++function.reg_count))); 
            new_node = new IRNode("EQI", tempString + function.reg_count, tempString + (function.reg_count-1), "label"+ exit_stack.getFirst() );
          }
    | 
    'FALSE'{
            ir_list.addLast(new IRNode("STOREI", "1", null, tempString + (++function.reg_count)));
            ir_list.addLast(new IRNode("STOREI", "1", null, tempString + (++function.reg_count))); 
            new_node = new IRNode("EQI", tempString + function.reg_count, tempString + (function.reg_count-1), "label"+ exit_stack.getFirst());
          }
;

compop: 
  '<'  {new_node.opcode = "GE";} | 
  '>'  {new_node.opcode = "LE";} | 
  '='  {new_node.opcode = "NE";} | 
  '!=' {new_node.opcode = "EQ";} |
  '<=' {new_node.opcode = "GT";} |
  '>=' {new_node.opcode = "LT";}
;

do_while_stmt: 'DO' 
  {
    tree.enterScope("BLOCK", ++block_number);
    ir_list.addLast(IRNode.getLabelNode(tree.getCurrentEntrance()));
    exit_stack.push(tree.getCurrentExit());
  }
  decl stmt_list 'WHILE' '(' cond ')' ';' 
  {
    ir_list.addLast(new_node);
    ir_list.addLast(IRNode.getJumpNode(tree.getCurrentEntrance()));
    ir_list.addLast(IRNode.getLabelNode(exit_stack.pop()));
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
