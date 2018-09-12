/* Dylan Porter */
/* C++ Compiler */
/* LexicalAnalyzer.java */

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


public class LexicalAnalyzer {

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
	
	/*Types*/
	final static int RES = 111; //Reserved Words
	final static int OP = 112; //Operators
	final static int PUN = 113; //Punctuation
	
	final static String[] keyWords = {"alignas", "alignof", "and", "and_eq", "asm", "auto", "bitand", "bitor", 
			"bool", "break", "case", "catch", "char", "char16_t", "char32_t", "class", 
			"compl", "concept", "const", "const_cast", "constexpr", "continue", "decltype", 
			"default", "define", "defined", "delete", "do", "double", "dynamic_cast", 
			"elif", "else", "endif", "enum", "error", "explicit", "export", "extern", 
			"false", "float", "for", "friend", "goto", "if", "ifdef", "ifndef", "include", 
			"inline", "int", "line", "long", "mutable", "namespace", "new", "noexcept", 
			"not", "not_eq", "null", "nullptr", "operator", "or", "or_eq", "pragma", "private", 
			"protected", "public", "register", "reinterpret_cast", "requires", "return", 
			"short", "signed", "sizeof", "static", "static_assert", "static_cast", "struct", 
			"switch", "template", "this", "thread_local", "throw", "true", "try", "typedef", 
			"typeid", "typename", "undef", "union", "unsigned", "using", "virtual", "void", 
			"volatile", "wchar_t", "while", "xor", "xor_eq"};


	static LinearProbingHashST read(String fileName, String outputFileName) throws IOException {
		
		LinearProbingHashST<String, LexObject> st = new LinearProbingHashST<>(100);
		
		for(String s : keyWords) {
			st.put(new LexObject(s, 111));
		}
		
		FileInputStream is = new FileInputStream(fileName);
		BufferedWriter output = new BufferedWriter(new FileWriter(outputFileName));

		ArrayList<Character> chars = new ArrayList<Character>();
		int x;
		while((x = is.read()) != -1) { chars.add((char)x);}
		
		for(int i = 0; i < chars.size(); i++) {
			/*----------------------------------COMMENTS-------------------------------------------*/
			if(chars.get(i) == '/') {
				if(i < chars.size() - 1) {
					if(chars.get(i+1) == '/') { 
						while(chars.get(++i) != '\r') {if(i == chars.size()) {System.exit(0);}}
					} else if(chars.get(i+1) == '*') { 
						while(!(chars.get(i) == '*' && chars.get(i+1) == '/')) {
							i++;
							if(i == chars.size()) { System.out.println("ERROR: Unclosed Comment"); System.exit(0); }
						}
						i++;
					} else {
						output.write(OP + "\t" + (int)'/'); output.newLine();
					}
				} else {
					output.write(OP + "\t" + (int)'/'); output.newLine();
				}

			/*----------------------------------IDENTIFIERS-------------------------------------------*/
			} else if((Character.isLetter(chars.get(i)) || chars.get(i) == '_') && i < chars.size()-1) {
				String sequence = Character.toString(chars.get(i));
				while(++i < chars.size() && (Character.isLetterOrDigit(chars.get(i)) || chars.get(i) == '_')) {
					sequence += Character.toString(chars.get(i));
				}

				LexObject temp;
				if(sequence.matches("^-?\\d+$")) {
					output.write(INT + "\t" + Integer.parseInt(sequence)); output.newLine();
				}
				if(st.get(sequence) == null) {
					st.put(new LexObject(sequence, st.getHash(sequence)));
				}
				temp = st.get(sequence);
				output.write(temp.getType() + "\t" + temp.getValue()); output.newLine();
				i--;
			/*----------------------------------NUMBERS-------------------------------------------*/
			} else if((Character.isDigit(chars.get(i))) && i < chars.size()-1) {
				String sequence = Character.toString(chars.get(i));
				while(++i < chars.size() && Character.isDigit(chars.get(i))) {
					sequence += Character.toString(chars.get(i));
				}
				output.write(INT + "\t" + sequence);
				output.newLine();
				i--;
			/*----------------------------------DOUBLE QUOTES-------------------------------------------*/
			} else if(chars.get(i) == '"' && i < chars.size()-1) {
				//MAYBE ADD SYMBOL?
				String sequence = "";
				while(++i < chars.size() && chars.get(i) != '\n') {
					if(chars.get(i) == '"') {break;}
					sequence += Character.toString(chars.get(i));
				}
				if(i == chars.size() || chars.get(i) == '\n') {
					System.out.println("ERROR: Unterminated Double Quote");
					System.exit(0);
				}
				LexObject temp;
				if(st.get(sequence) == null) {
					st.put(new LexObject(sequence, st.getHash(sequence), true));
				}
				temp = st.get(sequence);
				output.write(temp.getType() + "\t" + temp.getValue()); output.newLine();
			/*----------------------------------SINGLE QUOTES-------------------------------------------*/
			} else if(chars.get(i) == '\'' && i < chars.size()-1) {
				//MAYBE ADD SYMBOL?
				String sequence = "";
				while(++i < chars.size() && chars.get(i) != '\n') {
					if(chars.get(i) == '\'') {break;}
					sequence += Character.toString(chars.get(i));
				}
				if(i == chars.size() || chars.get(i) == '\n') {
					System.out.println("ERROR: Unterminated Single Quote");
					System.exit(0);
				}
				LexObject temp;
				if(st.get(sequence) == null) {
					//st.put(new LexObject(sequence, st.getHash(sequence)));
				}
				temp = st.get(sequence);
				output.write(CHAR + "\t" + (int)sequence.charAt(0)); output.newLine();
			/*----------------------------------! or !=-------------------------------------------*/
			} else if(chars.get(i) == '!') {
				if(i < chars.size() - 1) {
					if(chars.get(i+1) == '=') {
						output.write(OP + "\t" + NE); output.newLine();
						i++;
					} else {
						output.write(OP + "\t" + (int)'!'); output.newLine();
					}
				} else {
					output.write(OP + "\t" + (int)'!'); output.newLine();
				}
			/*----------------------------------#-------------------------------------------*/
			} else if(chars.get(i) == '#') {
				output.write(OP + "\t" + (int)'#'); output.newLine();
			/*----------------------------------%-------------------------------------------*/
			} else if(chars.get(i) == '%') {
				output.write(OP + "\t" + (int)'%'); output.newLine();
			/*----------------------------------& or &&-------------------------------------------*/
			} else if(chars.get(i) == '&') {
				if(i < chars.size() - 1) {
					if(chars.get(i+1) == '&') {
						output.write(OP + "\t" + AND); output.newLine();
						i++;
					} else {
						output.write(OP + "\t" + (int)'&'); output.newLine();
					}
				} else {
					output.write(OP + "\t" + (int)'&'); output.newLine();
				}
			/*----------------------------------: or ::-------------------------------------------*/
			} else if(chars.get(i) == ':') {
				if(i < chars.size() - 1) {
					if(chars.get(i+1) == ':') {
						output.write(OP + "\t" + SCOPE); output.newLine();
						i++;
					} else {
						output.write(OP + "\t" + (int)':'); output.newLine();
					}
				} else {
					output.write(OP + "\t" + (int)':'); output.newLine();
				}
			/*----------------------------------(-------------------------------------------*/
			} else if(chars.get(i) == '(') {
				output.write(OP + "\t" + (int)'('); output.newLine();
			/*----------------------------------)-------------------------------------------*/
			} else if(chars.get(i) == ')') {
				output.write(OP + "\t" + (int)')'); output.newLine();
			/*----------------------------------*-------------------------------------------*/
			} else if(chars.get(i) == '*') {
				output.write(OP + "\t" + (int)'*'); output.newLine();
			/*----------------------------------+ or ++-------------------------------------------*/
			} else if(chars.get(i) == '+') {
				if(i < chars.size() - 1) {
					if(chars.get(i+1) == '+') {
						output.write(OP + "\t" + _INC); output.newLine();
						i++;
					} else {
						output.write(OP + "\t" + (int)'+'); output.newLine();
					}
				} else {
					output.write(OP + "\t" + (int)'+'); output.newLine();
				}
			/*---------------------------------- - or -- -------------------------------------------*/
			} else if(chars.get(i) == '-') {
				if(i < chars.size() - 1) {
					if(chars.get(i+1) == '-') {
						output.write(OP + "\t" + _DEC); output.newLine();
						i++;
					} else {
						output.write(OP + "\t" + (int)'-'); output.newLine();
					}
				} else {
					output.write(OP + "\t" + (int)'-'); output.newLine();
				}
			/*---------------------------------- . -------------------------------------------*/
			} else if(chars.get(i) == '.') {
				output.write(PUN + "\t" + (int)'.'); output.newLine();
			/*---------------------------------- / -------------------------------------------*/
			} else if(chars.get(i) == '/') {
				output.write(OP + "\t" + (int)'/'); output.newLine();
			/*---------------------------------- { -------------------------------------------*/
			} else if(chars.get(i) == '{') {
				output.write(PUN + "\t" + (int)'{'); output.newLine();
			/*---------------------------------- } -------------------------------------------*/
			} else if(chars.get(i) == '}') {
				output.write(PUN + "\t" + (int)'}'); output.newLine();
			/*---------------------------------- | or || -------------------------------------------*/
			} else if(chars.get(i) == '|') {
				if(i < chars.size() - 1) {
					if(chars.get(i+1) == '|') {
						output.write(OP + "\t" + OR); output.newLine();
						i++;
					} else {
						output.write(OP + "\t" + (int)'|'); output.newLine();
					}
				} else {
					output.write(OP + "\t" + (int)'|'); output.newLine();
				}
			/*---------------------------------- ; -------------------------------------------*/
			} else if(chars.get(i) == ';') {
				output.write(PUN + "\t" + (int)';'); output.newLine();
			/*---------------------------------- | -------------------------------------------*/
			} else if(chars.get(i) == '}') {
				output.write(PUN + "\t" + (int)'}'); output.newLine();
			/*---------------------------------- < <= or << -------------------------------------------*/
			} else if(chars.get(i) == '<') {
				if(i < chars.size() - 1) {
					if(chars.get(i+1) == '=') {
						output.write(OP + "\t" + LE); output.newLine();
						i++;
					} else if(chars.get(i+1) == '<') {
						output.write(OP + "\t" + SHL); output.newLine();
						i++;
					} else {
						output.write(OP + "\t" + (int)'<'); output.newLine();
					}
				} else {
					output.write(OP + "\t" + (int)'<'); output.newLine();
				}
			/*---------------------------------- > >= or >> -------------------------------------------*/
			} else if(chars.get(i) == '>') {
				if(i < chars.size() - 1) {
					if(chars.get(i+1) == '=') {
						output.write(OP + "\t" + GE); output.newLine();
						i++;
					} else if(chars.get(i+1) == '>') {
						output.write(OP + "\t" + SHR); output.newLine();
						i++;
					} else {
						output.write(OP + "\t" + (int)'>'); output.newLine();
					}
				} else {
					output.write(OP + "\t" + (int)'>'); output.newLine();
				}
			/*---------------------------------- = or == -------------------------------------------*/
			} else if(chars.get(i) == '=') {
				
				if(i < chars.size() - 1) {
					if(chars.get(i+1) == '=') {
						output.write(OP + "\t" + EQ); output.newLine();
						if(chars.get(i+1) != ' ') {
							i++;
						}
					} else {
						output.write(OP + "\t" + (int)'='); output.newLine();
						if(chars.get(i+1) == ' ') {
							i++;
						}
					}
				} else {
					output.write(OP + "\t" + (int)'='); output.newLine();
				}
				if((Character.isDigit(chars.get(i)) || chars.get(i).equals('{'))) { i--; }
			/*---------------------------------- [ -------------------------------------------*/
			} else if(chars.get(i) == '[') {
				output.write(PUN + "\t" + (int)'['); output.newLine();
			/*---------------------------------- ] -------------------------------------------*/
			} else if(chars.get(i) == ']') {
				output.write(PUN + "\t" + (int)']'); output.newLine();
			/*---------------------------------- , -------------------------------------------*/
			} else if(chars.get(i) == ',') {
				output.write(PUN + "\t" + (int)','); output.newLine();
			} else if(chars.get(i) != '\n' && chars.get(i) != '\r' && chars.get(i) != ' ') {
				System.out.println("ERROR: Illegal Character Detected " + (int)chars.get(i));
				System.exit(0);
			}
		}
		is.close();
		output.close();
		
		return st;
	}
}
