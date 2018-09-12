/* Dylan Porter */
/* C++ Compiler */
/* ParserPhaseTwo.java */

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class ParserPhaseTwo {
	
	/*datatype values*/
	final static int INT = 101; //integer
	final static int INC = 102; //integer constant
	final static int INV = 103; //integer variable
	final static int CHAR = 107; //chars
	
	/*Compound Operator Values*/
	final static int NE = 200; //!=
	final static int LE = 201; //<=
	final static int GE = 202; //>=
	final static int EQ = 203; //==
	final static int OR = 204; //||
	final static int AND = 205; //&&
	final static int SHR = 206; //>>
	final static int SHL = 207; //<<
	final static int _INC = 208; //++
	final static int _DEC = 209; //--
	final static int SCOPE = 210; //::
	
	final static String[] usedOps = {"!=", "<=", ">=", "==", "||", "&&"};
	
	/*Types*/
	final static int RES = 111; //Reserved Words
	final static int OP = 112; //Operators
	final static int PUN = 113; //Punctuation
	
	String formatStr = "%-10s %-10s %-10s %-10s %-10s", targetFile;
	public static ArrayList<String> constantList;
	BufferedWriter symbol;
	
	public void initGenerateQuads(String targetFile) throws IOException {
		symbol = new BufferedWriter(new FileWriter(targetFile));
	}
	
	public void generateQuads(Scanner input, LinearProbingHashST st, ArrayList<String> constantList, String targetFile, boolean isCall, ParseTree parse) throws IOException {
		this.constantList = constantList;
		this.targetFile = targetFile;
		
		if(!isCall)
		parse = new ParseTree(symbol);

		int column1 = 0, column2 = 0;
		while(input.hasNextInt()) {
			
			/*Column1 and Column2 help us gather information in the ST*/
			column1 = input.nextInt(); column2 = input.nextInt();

			/*We need the left hand side variable in assignment*/
			/*Find the 'return' keyword in symbol table and compare to current token*/
			int value = st.get("return").getValue(), typeReturn = st.get("return").getType();

			/*
			/*Handle if and while statements*/
			int valueIf = st.get("if").getValue(), valueWhile = st.get("while").getValue();
			int numParen = 1;
			String boolExp = "";
			boolean isWhile = false;
			
			while(column1 == 111 && (column2 == valueIf || column2 == valueWhile)) {
				input = generateStatement(input, st, parse, column1, column2);
				column1 = this.column1; column2 = this.column2;

			}
			/*END IF and WHILE statements*/

			if(column1 == 111 && (column1 != typeReturn && column2 != value)) {
				System.out.println("ERROR: Assignment with a Reserved Word");
				System.exit(0);
			} else if(column1 == typeReturn && column2 == value) {
				column1 = input.nextInt(); column2 = input.nextInt();

				if(!(column1 == 101 && column2 == 0)) {
					System.out.println("ERROR: Missing '0' in Return Statement");
					System.exit(0);
				}
				column1 = input.nextInt(); column2 = input.nextInt();
				if((column1 == 113 && (char)column2 == ';')) {
					//symbol.close();
					return;
				} else {
					System.out.println("ERROR: Missing Semicolon in Return Statement");
					System.exit(0);
				}
				
			}
			
			String strVariable = st.getName(column1, column2);

			/*If the item exists in the symbol table*/
			if(!strVariable.equals("null") && !strVariable.toLowerCase().equals("cout") && !strVariable.toLowerCase().equals("cin")) {

				/*Check to see if the variable exists for left hand side*/
				if(!constantList.contains(strVariable)) {
					System.out.println("ERROR: Variable " + strVariable + " is undeclared!");
					System.exit(0);
				}
				
				int type = st.get(strVariable).getType(), elType = st.get(strVariable).getelType();
				
				if((type == 102 || type == 108) || (elType == 102 || elType == 108)) {
					System.out.println("ERROR: Assignment to constant variable " + strVariable);
					System.exit(0);
				}
				
				column1 = input.nextInt(); column2 = input.nextInt();
				/*If there is an equals sign after this variable*/
				
				if(column1 == 112 && (char)column2 == '=') {
					column1 = input.nextInt(); column2 = input.nextInt();
					
					/*Collect the total expression for parse tree*/
					String expression = "";
					boolean readNumeric = false;
					
					while(column2 != ';') {

						if(readNumeric) {
							if(!(column1 == 112 || column1 == 113)) {
								System.out.println("ERROR: Missing Operator in Statement");
								System.exit(0);
							}
						}
						
						/*If a regular constant*/
						if(column1 == 101) {
							expression += column2; readNumeric = true;
						} else if(column1 == 112 || column1 == 113) {
							/*If *,/,+,-*/
							expression += (char)column2; readNumeric = false;
						} else {
							/*AnotherVariable*/
							String tempVar = st.getName(column1, column2);
							LexObject lex = st.get(tempVar);
							if(!constantList.contains(tempVar)) {
								System.out.println("ERROR: Using Undeclared Variable " + tempVar + "!");
								System.exit(0);
							}
							if(lex.getType() == 102 || lex.getType() == 108) {
								expression += lex.getValue();
							} else {
								expression += tempVar;
							}
							readNumeric = true;
							if(column1 == 111) {
								System.out.println("Error: Missing Semicolon");
								System.exit(0);
							}
							if(!constantList.contains(tempVar)) {
								System.out.println(column1 + " " +  column2);
								System.out.println("ERROR: Undeclared variable " + tempVar + " used in assignment.");
								System.exit(0);
							}

						}
						
						String temp = expression, op = "";
						
						try {
							while(temp.contains("]")) { 
	
								op = temp.substring(temp.indexOf("]")+1, temp.indexOf("]")+2);
								if(!(op.equals("+") || op.equals("-") || op.equals("*") || op.equals("/") || op.equals("%") || op.equals(";") || op.equals("="))) {
									System.out.println("ERROR: Missing Operator in Assignment");
									System.exit(0);
								}
								temp = temp.replaceFirst("]", "");
							}
						} catch (java.lang.StringIndexOutOfBoundsException e) {}
						
						column1 = input.nextInt(); column2 = input.nextInt();
					}

					if(expression.contains("=")) {
						System.out.println("ERROR: Missing Semicolon");
						System.exit(0);
					}
					if(expression.contains("[")) {
						/*Check to see that we have assignments necessary in array*/
						
						parse.printArrayAssignment(expression, strVariable, false, st);
					} else {
						parse.genParseTree(expression);
						if(parse.temp > parse.maxTemp) { parse.maxTemp = parse.temp; }

						parse.printEquals(strVariable, expression, isCall);
					}
				/*Handle an array statement*/
				} else if(column1 == 113 && (char) column2 == '['){
					String expression = "";
					boolean readNumeric = false;
					while(!(column1 == 113 && (char)column2 == ';')) {

						if(readNumeric) {
							if(!(column1 == 112 || column1 == 113)) {
								System.out.println("ERROR: Missing Operator in Statement");
								System.exit(0);
							}
						}
						
						/*If a regular constant*/
						if(column1 == 101 || column1 == 107) {
							expression += column2; readNumeric = true;
						} else if(column1 == 112 || column1 == 113) {
							/*If *,/,+,-,[,]*/
							expression += (char)column2; readNumeric = false;

						} else {
							/*AnotherVariable*/
							String tempVar = st.getName(column1, column2);
							LexObject lex = st.get(tempVar);
							if(!constantList.contains(tempVar)) {
								System.out.println("ERROR: Using Undeclared Variable " + tempVar + "!");
								System.exit(0);
							}
							if(lex.getType() == 102 || lex.getType() == 108) {
								expression += lex.getValue();
							} else {
								expression += tempVar;
							}
						}
						column1 = input.nextInt(); column2 = input.nextInt();
					}

					
					
					String temp = expression, op = "";
					try {
						while(temp.contains("]")) { 

							op = temp.substring(temp.indexOf("]")+1, temp.indexOf("]")+2);
							if(!(op.equals("+") || op.equals("-") || op.equals("*") || op.equals("/") || op.equals("%") || op.equals(";") || op.equals("="))) {
								System.out.println("ERROR: Missing Operator in Assignment");
								System.exit(0);
							}
							temp = temp.replaceFirst("]", "");
						}
					} catch (java.lang.StringIndexOutOfBoundsException e) {}
					
					parse.printArrayStatement(strVariable + expression, strVariable);
				}
			/*This will handle the cout statements*/
			} else if(strVariable.toLowerCase().equals("cout") || strVariable.toLowerCase().equals("cin")) {

				String temp = strVariable.toLowerCase();
				
				column1 = input.nextInt(); column2 = input.nextInt();
				/*While we have need reached a semicolon*/
				
				String exp = "";
				while(true) {
					
					/*Check to see if we are at EOL*/

					if(column1 == 113 && (char) column2 == ';') {
						break;
					}
					
					/*If missing <<, through an error*/
					if(!((column1 == 112) && (column2 == 207 || column2 == 206)) && (column1 != 113 && column2 != 91)) {
						
						if(temp.equals("cin")) {
							System.out.println("Missing '>>' in Statement");
						} else {
							System.out.println("Missing '<<' in Statement");
						}
						System.exit(0);
					} 
					
					/*Get the identifier to print*/
					column1 = input.nextInt(); column2 = input.nextInt();

					/*Collect potential expression in print out*/
					
					exp = "";
					int loopCount = 0;
					boolean isPrevNum = false;
					while(((char) column2 != ';') && !(column1 == 112 && (column2 == 207 || column2 == 206))) {
						
						/*If a regular constant*/
						if(column1 == 101 || column1 == 107) {
							if(column2 != 32) {
								exp += column2;
							} else {exp += " ";}
						if(isPrevNum && loopCount != 0) {
							System.out.println("ERROR: Missing << or >>");
							System.exit(0);
						}
						isPrevNum = true;
						} else if(column1 == 112 || column1 == 113) {
							/*If *,/,+,-,[,]*/
							exp += (char)column2;
							isPrevNum = false;
							if((char)column2 == ']') {
								isPrevNum = true;
							}
						} else {
							/*AnotherVariable*/
							String tempVar = st.getName(column1, column2);
							LexObject lex = st.get(tempVar);
							if(!constantList.contains(tempVar) && !lex.getIfString() && !tempVar.equals("endl")) {
								System.out.println("ERROR: Using Undeclared Variable " + tempVar + "!");
								System.exit(0);
							}
							if(lex.getType() == 102 || lex.getType() == 108) {
								exp += lex.getValue();
							} else {
								exp += tempVar;
							}
							if(isPrevNum && loopCount != 0) {
								System.out.println("ERROR: Missing << or >>");
								System.exit(0);
							}
							isPrevNum = true;
						}
						
						column1 = input.nextInt(); column2 = input.nextInt();
						loopCount++;
						
					}

					if(exp.equals(" ")) {
						if(temp.equals("cin")) {
							writeToFile("cin", "\' \'", "", "", "");
						} else {
							writeToFile("cout", "\' \'", "", "", "");
						}
					} else if(exp.trim().equals("endl")) {
						if(temp.equals("cin")) {
							writeToFile("cin", "\\n", "", "", "");
						} else {
							writeToFile("cout", "\\n", "", "", "");
						}
					}
					else if(loopCount > 1 && !exp.contains("[")) { 
						parse.genParseTree(exp); 
						if(parse.temp > parse.maxTemp) { parse.maxTemp = parse.temp; }
						parse.printOutput();
					} else if(exp.contains("[")) {
						if(temp.equals("cin")) {
							parse.printArrayAssignment(exp,"cin",true, st);
						} else {
							parse.printArrayAssignment(exp,"",true, st);
						}
					} else {
						if(temp.equals("cin")) {
							writeToFile("cin", exp, "", "", "");
						} else {
							writeToFile("cout", exp, "", "", "");
						}
					}
				}
			}
		}

	}
	
	public void writeToFile(String index0, String index1, String index2, String index3, String index4) throws IOException {
		symbol.write(String.format(formatStr, index0, index1, index2, index3, index4));
		symbol.newLine();
	}
	
	public Scanner generateStatement(Scanner input, LinearProbingHashST st, ParseTree parse, int col1, int col2) throws IOException {

		int column1 = col1, column2 = col2;
		int valueIf = st.get("if").getValue(), valueWhile = st.get("while").getValue(), valueElse = st.get("else").getValue();
		int numParen = 1;
		String boolExp = "";
		boolean isWhile = false;
		
		if(col2 == valueWhile) { isWhile = true; }

		if(column1 == 111 && (column2 == valueIf || column2 == valueWhile)) {
			column1 = input.nextInt(); column2 = input.nextInt();
			column1 = input.nextInt(); column2 = input.nextInt();
			
			while(numParen != 0) {
				/*If a regular constant*/
				if(column1 == 101) {
					boolExp += column2;
				} else if(column1 == 112 || (column1 == 113 && (column2 != 91 && column2 != 93))) {
					/*If *,/,+,-*/
					if(column1 == 112 && (column2 < 200 || column2 > 205)) {
						boolExp += (char)column2;
					} else if(column1 == 112 && (column2 >= 200 && column2 <= 205)) {
						boolExp += usedOps[column2-200];
					}
					if(column1 == 112 && (char)column2 == '(') { numParen++; } 
					else if(column1 == 112 && (char)column2 == ')') {numParen--;}
				} else { 
					/*AnotherVariable*/
					String temp = st.getName(column1, column2);
					LexObject lex = st.get(temp);
					if(!constantList.contains(temp) && column1 != 113) {
						System.out.println("ERROR: Using Undeclared Variable " + temp + "!");
						System.exit(0);
					}
					if(lex.getType() == 102 || lex.getType() == 108) {
						boolExp += lex.getValue();
					} else if(column1 == 113) {
						if(column2 == 91) {
							boolExp += "[";
						} else {
							boolExp += "]";
						}
					} else {
						boolExp += temp;
					}
				}

				column1 = input.nextInt(); column2 = input.nextInt();
			}
		} else {
			return input;
		}		
		
		boolExp = boolExp.substring(0, boolExp.length()-1);
		
		if(!isWhile) {
			int sFalse = parse.incrementLabel();
			int sEnd = parse.incrementLabel();
			parse.genParseTree(boolExp);
			if(parse.temp > parse.maxTemp) { parse.maxTemp = parse.temp; }

			writeToFile("==", "_t_" + (parse.temp-1), "0", "goto", "L_" + sFalse);
			//System.out.println("==\t" + "_t_" + (parse.temp-1) + "\t" + "0\t" + "goto\tL_" + sFalse);
			
			//CODE TODO HERE//
			if(column1 == 111 && column2 != valueElse) {
				generateStatement(input, st, parse, column1, column2);
			} else {
				/*CODE for S1*/
				//System.out.println("S1CODE");
				input = printCode(input, column1, column2, st, parse);
				column1 = this.column1; column2 = this.column2;
				
				/*END CODE for S1*/
			}
			
			writeToFile("goto", "L_" + sEnd, "", "", "");
			//System.out.println("goto\tL_" + sEnd);
			
			writeToFile("L_" + sFalse,"", "", "", "");
			//System.out.println("L_" + sFalse);
			/*CODE for S2*/
			/*If the code fails, it is only an if statement*/
			try {
				column1 = input.nextInt(); column2 = input.nextInt();
				if(column1 == 111 && column2 == valueIf) {
					generateStatement(input, st, parse, column1, column2);
					writeToFile("L_" + sEnd,"", "", "", "");
					
				} else {
					//column1 = input.nextInt(); column2 = input.nextInt();
					//System.out.println("S2CODE");
					input = printCode(input, column1, column2, st, parse);
					column1 = this.column1; column2 = this.column2;
				
					/*END CODE for S2*/
					writeToFile("L_" + sEnd,"", "", "", "");
					//System.out.println("L_" + sEnd);
				}
			} catch(java.util.NoSuchElementException e) {
				writeToFile("L_" + sEnd,"", "", "", "");
				//System.out.println("L_" + sEnd);
			}
		/*If a While Statement*/
		} else {

			int sBegin = parse.incrementLabel();
			int sFalse = parse.incrementLabel();
			
			writeToFile("L_" + sBegin,"", "", "", "");
		    //System.out.println("L_" + sBegin);
		    //code for B;
			
		    //code for s1
			//System.out.println("S1CODE");
			
			parse.genParseTree(boolExp);
			if(parse.temp > parse.maxTemp) { parse.maxTemp = parse.temp; }
			writeToFile("==","_t_" + (parse.temp-1), "0", "goto", "L_" + sFalse);
			//System.out.println("==\t" + "_t_" + (parse.temp-1) + "\t" + "0\t" + "goto\tL_" + sFalse);
			
			if(column1 == 111 && column2 != valueElse) {
				generateStatement(input, st, parse, column1, column2);
			} else {
				input = printCode(input, column1, column2, st, parse);
				column1 = this.column1; column2 = this.column2;
			}
			/*END CODE for S1*/
			
			writeToFile("goto","L_" + sBegin, "", "", "");
			//System.out.println("goto\tL_" + sBegin);
			writeToFile("L_" + sFalse,"", "", "", "");
			//System.out.println("L_" + sFalse);
		}
		
		return input;
		
	}
	
	
	
	
	
	private int column1 = 0, column2 = 0;
	
	
public Scanner printCode(Scanner input, int column1, int column2, LinearProbingHashST st, ParseTree parse) throws IOException {
		
		int valueIf = st.get("if").getValue(), valueWhile = st.get("while").getValue(), valueElse = st.get("else").getValue();
		
		String quadString = "";
		if(column1 == 113 && (char)column2 == '{') {
			column1 = input.nextInt(); column2 = input.nextInt();
			
			if(column1 == 111 && (column2 == valueIf || column2 == valueWhile)) {
				generateStatement(input, st, parse, column1, column2);
				return input;
			}
			
			String exp = "";
			while(!(column1 == 113 && (char)column2 == '}')) {
				/*This takes care of statement * x, and then if or while statement*/
				if(column1 == 111 && (column2 == valueIf || column2 == valueWhile)) {
					generateQuads(new Scanner(quadString), st, this.constantList, this.targetFile, true, parse);
					input = generateStatement(input, st, parse, column1, column2);
					/*Temporary Provision*/
					String tempQuads = this.column1 + "\t" + this.column2 + "\n"; 
					Scanner tempInput = input; 

					column1 = tempInput.nextInt(); column2 = tempInput.nextInt(); 
					while(!(column1 == 113 && (char)column2 == '}')) {
						tempQuads += column1 + "\t" + column2 + "\n";
						column1 = tempInput.nextInt(); column2 = tempInput.nextInt(); 
					}
                    input = tempInput;

					generateQuads(new Scanner(tempQuads), st, this.constantList, this.targetFile, true, parse);
					input = generateStatement(input, st, parse, column1, column2);
					quadString = "";
					continue;
				}
				/*Need the code to call genQuad()*/
				quadString += column1 + "\t" + column2 + "\n";
				/*   ****************Collect Expression********************     */
				if(column1 == 101) {
					exp += column2;
				} else if(column1 == 112 || column1 == 113) {
					/*If *,/,+,-*/
					if(column1 == 112 && (column2 < 200 || column2 > 205)) {
						exp += (char)column2;
					} else if(column1 == 112 && (column2 >= 200 && column2 <= 205)) {
						exp += usedOps[column2-200];
					} else {
						exp += (char)column2;
					}

				} else {
					/*AnotherVariable*/
					String temp = st.getName(column1, column2);
					exp += temp;
				}
				
				column1 = input.nextInt(); column2 = input.nextInt();
			}

			column1 = input.nextInt(); column2 = input.nextInt();
			//System.out.println(exp);
			generateQuads(new Scanner(quadString), st, this.constantList, this.targetFile, true, parse);


		} else {
			String exp = "";
			while(!(column1 == 113 && (char)column2 == ';')) {
				quadString += column1 + "\t" + column2 + "\n";
				/*   ****************Collect Expression********************     */
				if(column1 == 101) {
					exp += column2;
				} else if(column1 == 112 || column1 == 113) {
					/*If *,/,+,-*/
					if(column1 == 112 && (column2 < 200 || column2 > 205)) {
						exp += (char)column2;
					} else if(column1 == 112 && (column2 >= 200 && column2 <= 205)) {
						exp += usedOps[column2-200];
					}

				} else {
					/*AnotherVariable*/
					String temp = st.getName(column1, column2);
					exp += temp;
				}
				column1 = input.nextInt(); column2 = input.nextInt();
			}
			//113 59
			quadString += "113 \t 59\n";
			//System.out.println(exp);
			generateQuads(new Scanner(quadString), st, this.constantList, this.targetFile, true, parse);

			column1 = input.nextInt(); column2 = input.nextInt();
		}
		this.column1 = column1; this.column2 = column2; 
		return input;
	}
}
