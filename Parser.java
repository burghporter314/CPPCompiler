/* Dylan Porter */
/* C++ Compiler */
/* Parser.java */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Parser {
	
	/*datatype values*/
	final static int INT = 101; //integer
	final static int INC = 102; //integer constant
	final static int INV = 103; //integer variable
	final static int CHAR = 107; //chars
	final static int CHARC = 108; //char constant
	final static int ARRAY = 110; // Arrays
	
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
	
	/*Types*/
	final static int RES = 111; //Reserved Words
	final static int OP = 112; //Operators
	final static int PUN = 113; //Punctuation
	
	ParserPhaseTwo parse;
	final static String[] keyWords = {"alignas", "alignof", "and", "and_eq", "asm", "auto", "bitand", "bitor", 
			"bool", "break", "case", "catch", "char", "char16_t", "char32_t", "class", 
			"compl", "concept", "const", "const_cast", "constexpr", "continue", "decltype", 
			"default", "define", "defined", "delete", "do", "double", "dynamic_cast", 
			"elif", "else", "endif", "enum", "error", "explicit", "export", "extern", 
			"false", "float", "for", "friend", "goto", "if", "ifdef", "ifndef", "include", 
			"inline", "int", "line", "long", "mutable", "namespace", "new", "noexcept", 
			"not", "not_eq", "nullptr", "operator", "or", "or_eq", "pragma", "private", 
			"protected", "public", "register", "reinterpret_cast", "requires", "return", 
			"short", "signed", "sizeof", "static", "static_assert", "static_cast", "struct", 
			"switch", "template", "this", "thread_local", "throw", "true", "try", "typedef", 
			"typeid", "typename", "undef", "union", "unsigned", "using", "virtual", "void", 
			"volatile", "wchar_t", "while", "xor", "xor_eq"};
	
	public LinearProbingHashST read(LinearProbingHashST st, String fileName, String quadsFile) throws IOException {
		
		parse = new ParserPhaseTwo();
		
		int countLeftBracket = 0, countRightBracket = 0;
		ArrayList<String> constantList = new ArrayList<String>();

		Scanner input = new Scanner(new File(fileName));

		input = new Scanner(new File(fileName));
		
		/* Verify the necessary prerequisite statements exist */
		
		int[] sequence = {112, 111, 112, -1, 112, 111, 111, -1, 113};
		String[] words = {"#", "include", "<", "", ">", "using", "namespace", "std", ";"};
		for(int a = 0; a < 9; a++) {
			int next = input.nextInt();
			if(next == 111 && sequence[a] == 111) {
				if(!(st.get(words[a]).getValue() == input.nextInt())) {
					System.out.println("Error: Check include and using statements");
					System.exit(0);
				}
			} else if(next == 112 && sequence[a] == 112) {
				if(!((char)input.nextInt() == words[a].charAt(0))) {
					System.out.println("Error: Check include and using statements");
					System.exit(0);
				}
			} else if(next == 113 && sequence[a] == 113) {
				if(!((char)input.nextInt() == words[a].charAt(0))) {
					System.out.println("Error: Check include and using statements");
					System.exit(0);
				}
			} else if(sequence[a] == -1){ input.nextInt(); } 
			else {
				System.out.println("Error: Check include and using statements");
				System.exit(0);
			}
		}
		
		int column1 = 0, column2 = 0;
		
		/*Loop through all cases of output to check for problems*/
		while(input.hasNextInt()) {
			
			column1 = input.nextInt();
			column2 = input.nextInt();
			
			/*if const, there must be a following int or char*/
			
			if(verify("const", st, column1, column2, 111)) {
				
				column1 = input.nextInt(); column2 = input.nextInt();
				
				if(verify("int", st, column1, column2, 111) || verify("char", st, column1, column2, 111)) {
					
					/*Determine if char constant, or int constant*/
					int typeValue = 108;
					if(verify("int", st, column1, column2, 111)) { typeValue = 102; }
					
					/* Make sure that a reserved word is not the variable name */
					column1 = input.nextInt(); column2 = input.nextInt();
					
					/*If column1 is 111-113, the variable name is not valid*/
					if(column1 == 111 || column1 == 112 || column1 == 113) {
						System.out.println("ERROR: Assignment to a Reserved Word!");
						System.exit(0);
					/*The variable name is valid, continue to check the statement*/
					} else {
						/*While is needed because we can have an indefinite amount of variables*/
						while(true) {
							/*Retrieves String associated with hash, position*/
							String constantTemp = st.getName(column1, column2);
							
							/*Check to see if the variable has been declared previously in program*/
							if(constantList.contains(constantTemp)) {
								System.out.println("ERROR: Declared '" + constantTemp + "' more than once!");
								System.exit(0);
							/*Variable has not been declared, so proceed*/
							} else {
								constantList.add(constantTemp);
								column1 = input.nextInt(); column2 = input.nextInt();
								
								if(!input.hasNextLine()) { System.out.println("Error: Expecting item but file ended"); System.exit(0); }
								
								/*113 59 = ';' 113 44 = ',' and 112 61 = '='*/
								if((column1 == 112 && column2 == 61)) {
									
									/*Skip ahead to verify that there is a continuation or a terminal semicolon*/
									input.nextLine(); 
									column1 = input.nextInt(); column2 = input.nextInt();
									
									if(column1 == 113 && column2 == 44) {System.out.println("ERROR: need initialization after =");System.exit(0);}
									
									/*Get the Value of this Variable to Store*/
									int value = column2;
									if(column1 != 107 && column1 != 101) {
										String str = st.get(st.getName(column1, column2)).getName();
										if(!constantList.contains(str)) {System.out.println("ERROR: Assignment to Uninitialized Variable!"); System.exit(0);}
										value = st.get(st.getName(column1, column2)).getValue();
									}
									
									input.nextLine();
									if(!input.hasNextInt()) { System.out.println("Error: Missing Semicolon!"); System.exit(0); }
									column1 = input.nextInt(); column2 = input.nextInt();
									
									/*We've encountered a semicolon, so break the loop*/
									if(column1 == 113 && column2 == 59) { 
										st.get(constantTemp).setType(typeValue);
										st.get(constantTemp).setValue(value);
										st.get(constantTemp).setInitialized(true);
										break; 
									}
									/*We've encountered a comma, so proceed*/
									else if(column1 == 113 && column2 == 44) { 
										st.get(constantTemp).setType(typeValue);
										st.get(constantTemp).setValue(value);
										st.get(constantTemp).setInitialized(true);
										column1 = input.nextInt(); column2 = input.nextInt(); 
									}
									
									/*We are missing an item necessary to proceed*/
									else {System.out.println("Error: missing comma or semicolon in declaration!"); System.exit(0);}
								} 
								else if(column1 == 113 && column2 == 91) {
									//Array Detected
									column1 = input.nextInt(); column2 = input.nextInt();
									
									/*If the identifier is only 1 letter, or an Integer*/
									String result = "";
									
									int capacity = column2;
									if(column1 != 107 && column1 != 101 && column1 != 113) {
										String str = st.get(st.getName(column1, column2)).getName();
										if(!constantList.contains(str)) {System.out.println("ERROR: Assignment to Uninitialized Variable!"); System.exit(0);}
										capacity = st.get(st.getName(column1, column2)).getValue();
									}

									if(!(column1 == 113 && column2 == 93)) {
										column1 = input.nextInt(); column2 = input.nextInt();
										
										/*If we have an ending bracket*/
										if(column1 == 113 && column2 == 93) {
											column1 = input.nextInt(); column2 = input.nextInt();
											
											/*If an Equal Sign is Detected*/
											if(column1 == 112 && column2 == 61) {
												column1 = input.nextInt(); column2 = input.nextInt();
												
												/*Array is Declared without a Left Bracket*/
												if(!(column1 == 113 && column2 == 123)) {
													System.out.println("Missing Left Bracket in Array");
													System.exit(0);
												}
												
												/*Loop through all the elements of the Array*/
												while(true) {
													result = "";
													column1 = input.nextInt(); column2 = input.nextInt();
													
													/*Make sure that the index of this array contains the correct Type*/
													if(column1 != 101) {result = st.getName(column1, column2);}
													if(!(result.length() == 1 || column1 == 101)) {
														System.out.println("Error: Invalid Type Assigned to Array");
														System.exit(0);
													}
													
													/*Check for a comma or semicolon, if none, through error*/
													column1 = input.nextInt(); column2 = input.nextInt();
													if(column1 == 113 && column2 == 125) {
														
														//Found the end of array statement
														st.get(constantTemp).setType(ARRAY);
														st.get(constantTemp).setSize(capacity);
														st.get(constantTemp).setelType(typeValue);
														column1 = input.nextInt(); column2 = input.nextInt();
														break;
													/*The user did not put in a necessary comma or semicolon*/
													} else if(!(column1 == 113 && column2 == 44)) {
														System.out.println("Missing Comma or Right Bracket in Array Declaration!");
														System.exit(0);
													}
												}
												if(column1 == 113 && column2 == 59) {break;}
												else if(!(column1 == 113 && column2 == 44)) {
													System.out.println("ERROR: Missing Semicolon");
													System.exit(0);
												}
												column1 = input.nextInt(); column2 = input.nextInt();
											} else {
												/*You are allowed to not declare an array in a non const variable*/
												System.out.println("You must initialize const array!");
												System.exit(0);
											}
										} else {
											System.out.println("Invalid Item in Array Brackets!");
											System.exit(0);
										}
									} else {
										System.out.println("Array is Uninitialized!");
										System.exit(0);
									}
	
							    }
								else {
									/*Throw error if no = sign with const declaration*/
									System.out.println("Error: Need = with a Constant Declaration");
									System.exit(0);
								}
							} 
						}
					}
				} else {
					System.out.println("ERROR: Must have int or char after const!");
				}
			} 
			else if(verify("int", st, column1, column2, 111) || verify("char", st, column1, column2, 111)) {

				int typeValue = 107;
				if(verify("int", st, column1, column2, 111)) { typeValue = 101; }
				
				/* Make sure that a reserved word is not the variable name */
				column1 = input.nextInt(); column2 = input.nextInt();
				
				/*If column1 is 111-113, the variable name is not valid*/
				if(column1 == 111 || column1 == 112 || column1 == 113) {
					System.out.println("ERROR: Assignment to a Reserved Word!");
					System.exit(0);
				/*The variable name is valid*/
				} else {

					/*While is needed because we can have an indefinite amount of variables*/
					while(true) {
						/*Retrieves String associated with hash, position*/
						String constantTemp = st.getName(column1, column2);

						/*Check to see if the variable has been declared previously in program*/
						if(constantList.contains(constantTemp)) {
							System.out.println("ERROR: Declared '" + constantTemp + "' more than once!");
							System.exit(0);
						/*Variable has not been declared, so proceed*/
						} else {
							/*Set type of the variable (Suggested fix from Dr. Drozdek)*/
							st.get(constantTemp).setType(typeValue);
							
							constantList.add(constantTemp);
							column1 = input.nextInt(); column2 = input.nextInt();

							if(!input.hasNextLine()) { System.out.println("Error: Expecting item but file ended"); System.exit(0); }
							
							
							/*113 59 = ';' 113 44 = ',' and 112 61 = '='*/
							
							if((column1 == 112 && column2 == 61)) {
								//Equals Detected
								/*Skip ahead to verify that there is a continuation or a terminal semicolon*/
								input.nextLine(); 
								column1 = input.nextInt(); column2 = input.nextInt();
								if(column1 == 113 && column2 == 44) {System.out.println("ERROR: need initialization after =");System.exit(0);}
								
								/*Need to store updated value*/
								int value = column2;
								if(column1 != 107 && column1 != 101) {
									String str = st.get(st.getName(column1, column2)).getName();
									if(!constantList.contains(str)) {System.out.println("ERROR: Assignment to Uninitialized Variable!"); System.exit(0);}
									value = st.get(st.getName(column1, column2)).getValue();
								}
								
								input.nextLine();
								if(!input.hasNextInt()) {System.out.println("Error: Missing Semicolon"); System.exit(0);}
								column1 = input.nextInt(); column2 = input.nextInt();
								
								/*If there is a semicolon, break the loop*/
								if(column1 == 113 && column2 == 59) { 
									st.get(constantTemp).setType(typeValue);
									st.get(constantTemp).setValue(value);
									st.get(constantTemp).setInitialized(true);
									st.get(constantTemp).setValue(value);
									st.get(constantTemp).setInitialized(true);
									break; 
								}
								
								/*If there is a comma, continue the loop*/
								else if(column1 == 113 && column2 == 44) {
									st.get(constantTemp).setType(typeValue);
									st.get(constantTemp).setValue(value);
									st.get(constantTemp).setInitialized(true);
									column1 = input.nextInt(); column2 = input.nextInt(); 
								}
								
								/*otherwise, we are missing an item in the declaration*/
								else {System.out.println("Error: Missing comma or semicolon in declaration!"); System.exit(0);}

							} else if(column1 == 113 && column2 == 44) {
								// Comma Detected, continue the loop
								column1 = input.nextInt(); column2 = input.nextInt();
							} else if(column1 == 113 && column2 == 59) {
								//Semicolon EOL detected, stop the loop
								break;
							} else if(column1 == 113 && column2 == 91) {

								//Array Detected
								column1 = input.nextInt(); column2 = input.nextInt();
								
								/*Requested per Drozdek's Comments*/
								st.get(constantTemp).setType(110);
								st.get(constantTemp).setelType(typeValue);
								st.get(constantTemp).setSize(column2);
								
								/*If the identifier is only 1 letter, or an Integer*/
								String result = "";
								int capacity = column2;
								if(column1 != 107 && column1 != 101 && column1 != 113) {
									String str = st.get(st.getName(column1, column2)).getName();
									if(!constantList.contains(str)) {System.out.println("ERROR: Assignment to Uninitialized Variable!"); System.exit(0);}
									capacity = st.get(st.getName(column1, column2)).getValue();
								}

								if(!(column1 == 113 && column2 == 93)) {
									column1 = input.nextInt(); column2 = input.nextInt();
									
									/*If we have an ending bracket*/
									if(column1 == 113 && column2 == 93) {
										column1 = input.nextInt(); column2 = input.nextInt();
										
										/*If an Equal Sign is Detected*/
										if(column1 == 112 && column2 == 61) {
											column1 = input.nextInt(); column2 = input.nextInt();
											
											/*Array is Declared without a Left Bracket*/
											if(!(column1 == 113 && column2 == 123)) {
												System.out.println("Missing Left Bracket in Array");
												System.exit(0);
											}
											
											/*Loop through all the elements of the Array*/
											while(true) {
												result = "";
												column1 = input.nextInt(); column2 = input.nextInt();
												
												/*Make sure that the index of this array contains the correct Type*/
												if(column1 != 101) {result = st.getName(column1, column2);}
												if(!(result.length() == 1 || column1 == 101)) {
													System.out.println("Error: Invalid Type Assigned to Array");
													System.exit(0);
												}
												
												/*Check for a comma or semicolon, if none, through error*/
												column1 = input.nextInt(); column2 = input.nextInt();
												if(column1 == 113 && column2 == 125) {
													//Found the end of array statement
													st.get(constantTemp).setType(ARRAY);
													st.get(constantTemp).setSize(capacity);
													st.get(constantTemp).setelType(typeValue);
													column1 = input.nextInt(); column2 = input.nextInt();
													break;
												/*The user did not put in a necessary comma or semicolon*/
												} else if(!(column1 == 113 && column2 == 44)) {
													System.out.println("Missing Comma or Right Bracket in Array Declaration!");
													System.exit(0);
												}
											}
											if(column1 == 113 && column2 == 59) {break;}
											else if(!(column1 == 113 && column2 == 44)) {
												System.out.println("ERROR: Missing Semicolon");
												System.exit(0);
											}
											column1 = input.nextInt(); column2 = input.nextInt();
										} else {
											/*You are allowed to not declare an array in a non const variable*/
											try {
												column1 = input.nextInt(); column2 = input.nextInt();
											} catch (java.util.NoSuchElementException e) {break;}
										}
									} else {
										System.out.println("Invalid Item in Array Brackets!");
										System.exit(0);
									}
								} else {
									System.out.println("Invalid Type in Brackets for Array!");
									System.exit(0);
								}

						    } else {

								//Fatal Error Occurred
						    	
						    	if(column1 == 112 && column2 == 40) {

						    		input.nextLine();
									column1 = input.nextInt(); column2 = input.nextInt();
									if(column1 == 112 && column2 == 41) {
							    		input.nextLine();
										column1 = input.nextInt(); column2 = input.nextInt();
										if(column1 == 113 && column2 == 123) {
											countLeftBracket++;
											/*Found Main, Initiate Second Phase of Parser*/
											parse.initGenerateQuads(quadsFile);
											ParseTree parse2 = null;
											parse.generateQuads(input, st, constantList, quadsFile, false, parse2);
											parse.symbol.close();
											break;
										} else {
											System.out.println("ERROR: Missing { in main");
										}
									} else {
										System.out.println("ERROR: System does not support method parameters!");
									}
						    	} else {
									System.out.println("Error: Invalid Variable Declaration");
									System.exit(0);
						    	}
							}
						} 
					}
				}
			} 
			else if(column1 == 113) {
				if(column2 == 123) {countLeftBracket++;}
				else if(column2 == 125) {countRightBracket++;}
			} else {
				/*
				/*Return Statement for Main
				if(column1 == 111) {
					if(st.get("return").getValue() == column2) {
						input.nextLine(); 
						column1 = input.nextInt(); column2 = input.nextInt();
					}
				}
				
				/*Store the String resultant from column1 and column2 for Error Checking
				String temp = st.getName(column1, column2);
				
				/*Data Type Checking
				if(column1 == 111) {
					if(!(verify("int", st, column1, column2, 111) || verify("char", st, column1, column2, 111))) {
						System.out.println("Invalid Data Type!");
						System.exit(0);
					}
				} else {
					/*Check to see if Variable is initialized
					column1 = input.nextInt(); column2 = input.nextInt();
					if(column1 == 112 && column2 == 61 && !constantList.contains(temp)) {
						System.out.println("ERROR: Initialization without Declaration!");
						System.exit(0);
					/*Potential Assignment Statement to an already Existing Variable
					} else if(constantList.contains(temp)) {

						int type = st.get(temp).getType();
						/*Non-Constant Integer / Char
						if(type == 101 || type == 107) {
							if(column1 == 112 && column2 == 61) {
								column1 = input.nextInt(); column2 = input.nextInt();
								String hash = st.getName(column1, column2);
								/*Check if variable = variable
								if(!hash.equals("null")) {
									st.get(temp).setValue(st.get(hash).getValue());
								} else {
									st.get(temp).setValue(column2);
								}
								column1 = input.nextInt(); column2 = input.nextInt();
								if(!(column1 == 113 && column2 == 59)) {
									System.out.println("ERROR: Missing Semicolon");
									System.exit(0);
								}
							} else {
								System.out.println("ERROR: Missing '='");
								System.exit(0);
							}
						/*Constant char or int
						} else if(type == 102 || type == 108) {
							System.out.println("ERROR: Cannot reinitialize const variable");
						/*Array
						} else if(type == 110) {
							/*Iterate Through
							int increment = 1;
							column1 = input.nextInt(); column2 = input.nextInt();
							
							String hash = st.getName(column1, column2);

							/*Check if variable = variable
							if(!hash.equals("null")) {
								/*int[] a = char[] b;
								if(st.get(temp).getelType() != st.get(hash).getelType()) {
									System.out.println("ERROR: Assigning array to array of different type");
									System.exit(0);
								}
								st.get(temp).setSize(st.get(hash).getSize());
								column1 = input.nextInt(); column2 = input.nextInt();
								if(!(column1 == 113 && column2 == 59)) {
									System.out.println("ERROR: Missing Semicolon");
									System.exit(0);
								}
							/*If variable = {1,2,4,etc}
							} else {
							
								if(column1 == 113 && column2 == 123) {
									while(true) {
										column1 = input.nextInt(); column2 = input.nextInt();
										if(column1 == 101 || column1 == 107) {
											column1 = input.nextInt(); column2 = input.nextInt();
											if(column1 == 113 && column2 == 44) {
												increment++;
											} else if(column1 == 113 && column2 == 125) {
												column1 = input.nextInt(); column2 = input.nextInt();
												if(!(column1 == 113 && column2 == 59)) {
													System.out.println("ERROR: Missing Semicolon");
													System.exit(0);
												} else {
													st.get(temp).setSize(increment);
													break;
												}
											} else {
												System.out.println("ERROR: Assignment");
												System.exit(0);
											}
										} else {
											System.out.println("ERROR: Assignment");
											System.exit(0);
										}
									}
								} else {
									System.out.println("ERROR: Missing Left Bracket");
									System.exit(0);
								}
							}
						} else {
							System.out.println("Unknown error in assignment.");
						}
					}
				}*/
			}
		}

		
		/*Check to see if there are missing brackets*/
		if(countLeftBracket < countRightBracket) {System.out.println("ERROR: Missing Left Bracket(s)"); System.exit(0);}
		else if(countLeftBracket > countRightBracket) {System.out.println("ERROR: Missing Right Bracket(s)"); System.exit(0);}
		
		input.close();
		/*
		LexObject[] hashVals = st.getVals();
		for(int i = 0; i < hashVals.length; i++) {
			if(hashVals[i] != null) {
				LexObject temp = hashVals[i];
				System.out.println(i + ": ");
				while((temp = temp.next) != null) {
					String formatStr = "%-20s %-15s %-15s %-15s %-15s";
					System.out.println(String.format(formatStr, temp.getName(), temp.getType(), temp.getValue(), temp.getSize(), temp.getelType()));
				}
			}
		}*/
		
		return st;
	}
	
	public boolean verify(String target, LinearProbingHashST st, int list, int list2, int value) {
		return(list == value && st.get(target).getValue() == list2);
	}
}