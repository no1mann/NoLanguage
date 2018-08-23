# No - Programming Language # 
No is a simple, general-purpose programming language based around Objective-C. No is explicit, statically typed, and executed in a virtual machine written in Java.

## Features
- Includes Tokenizer, Parser, and Executor
- Variables include integer and boolean
- Full order-of-operation integer operations and boolean operators
- Statements include if-else, while, and print
- Parsing, executing and compilation error handling

## Internal Implementation
The internal implementation of No code is stored in an abstact syntax tree. The main node of the parsed code is the main function. It stores a list of pointers for each statement that needs to be executed; each one of these statements is called a branch. The branches are executed in order from left to right based on the order of the source code. More complicated branches, such as if or while statements, have sub branches with more statements. The No compiler handles these more complicated statements using recursion. The current iteration of code allows for functions to easily be implemented in the future with little to no changes.

Variables are stored in a HashMap data structure in the Java virtual environment. This is where No differs from Objective-C, which uses a stack for variable tracking. With a HashMap, sharing variable names within different scopes requires more complicated execution code which is currently not written.

## Example Code
The code below generates all prime numbers from 1 to 100. 
```
int max;
int i;
int tracking;
bool found;

max = 100;

i = 2;
while(i <= max){
	if((i%2!=0 && i%5!=0) || i<7){
		tracking = 2;
		found = true;
		while(tracking <= (i/2)){
			if(i % tracking == 0){
				found = false;
			}
			tracking = tracking + 1;
		}
		if(found){
			print(i);
		}
	}
	i = i + 1;
}
```

## Expressions
Boolean Operators:
- '||' - or
- '&&' - and
- '==' - equal
- '!=' - not equal
- '\>' - greater than
- '\>=' - greater than or equal to
- '\<' - less than
- '\<=' - less than or equal to

Integer Operators:
- '\+' - addition
- '\-' - subtraction
- '\*' - multiplication
- '\/' - division
- '\^' - exponent
- '\%' - modulus

## Statements
Declares an integer or boolean variable:
```
int myInt;
bool myBoolean;
```

Assigns an expression to the associated variable:
```
myInt = 5+(5*3);
myBoolean = true && false;
```

If statement:
```
if(expression){
    true statements
}
else{
    false statements
}
```

While statement:
```
while(expression){
    statements
}
```

Print statement:
```
print(expression);
```
