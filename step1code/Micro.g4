grammar Micro;

KEYWORD
       : 'PROGRAM'
       | 'BEGIN'
	   | 'END'
	   | 'FUNCTION'
	   | 'READ'
	   | 'WRITE'
	   | 'IF'
	   | 'ELSIF'
	   | 'ENDIF'
	   | 'DO'
	   | 'WHILE'
	   | 'CONTINUE'
	   | 'BREAK'
	   | 'RETURN'
	   | 'INT'
	   | 'VOID'
	   | 'STRING'
	   | 'FLOAT'
	   | 'TRUE'
	   | 'FALSE'
	   ;
// Program

program
       : 'PROGRAM' id 'BEGIN' pgm_body 'END';
	   
id 
       : IDENTIFIER;

pgm_body
       : decl func_declarations; 

decl   
       : string_decl decl
	   | var_decl
	   | 
	   ;

// Global String Declaration

string_decl
       : STRING id ':=' str ';';

str
       : STRINGLITERAL;

// Variable Declaration

var_decl
       : var_type id_list ';';

var_type
       : 'FLOAT' 
	   | 'INT';

any_type
       : var_type 
	   | 'VOID';

id_list
       : id id_tail;

id_tail
       : ',' id id_tail 
	   | ; // empty

// Function Parameter List

param_decl_list
	  : param_decl param_decl_tail 
	  | ; // empty

param_decl
      : var_type id;

param_decl_tail:
	  : ',' param_decl param_decl_tail
	  | ; // empty



OPERATOR: ':='
        | '+' 
		| '-' 
		| '*' 
		| '/' 
		| '=' 
		| '!=' 
		| '<' 
		| '>' 
		| '(' 
		| ')' 
		| ';' 
		| ',' 
		| '<=' 
		| '>=' 
		; 
           
