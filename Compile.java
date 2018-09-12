/*    Dylan Porter     */
/*    C++ Compiler     */
/*    Compile.java     */

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Compile {
	
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
	
	public void execute(String path, String code, ArrayList<String> constantList, LinearProbingHashST st, Parser parse) throws IOException {
		File file = new File(path);
		if(!file.exists()) { file.createNewFile(); }
		
		/*Proceed with Writing the Code*/
		BufferedWriter target = new BufferedWriter(new FileWriter(path));
		Scanner input = new Scanner(new File(code));
		
		target.write("#include <iostream>");
		target.newLine();
		target.write("using namespace std;");
		target.newLine(); target.newLine();
		
		/*Print out all temps*/
		ParseTree parseTemp = new ParseTree(null);
		if(parseTemp.maxTemp > 0) { target.write("int "); }
		if(parseTemp.maxTemp == 0) {
			target.write("int _t_0;");
		}
		for(int i = 0; i < parseTemp.maxTemp; i++) {
			if(i + 1 == parseTemp.maxTemp) {
				target.write("_t_" + i + ";");
			} else {
				target.write("_t_" + i + ", ");
			}
		}
		target.newLine();
		
		constantList.remove("main");
		/*Print out all variables*/
		for(String s : constantList) {
			//101 102 107 108
			int type = st.get(s).getType(), value = st.get(s).getValue();
			boolean isInitialized = st.get(s).getInitialized();
			if(type == 101) {
				target.write("int ");
				if(isInitialized) { target.write(s + " = " + value + ";"); } 
				else { target.write(s + ";"); }
			} else if(type == 102) {
				target.write("const int ");
				if(isInitialized) { target.write(s + " = " + value + ";"); } 
				else { target.write(s + ";"); }
			} else if(type == 107) {
				target.write("char ");
				if(isInitialized) { target.write(s + " = '" + (char)value + "';"); } 
				else { target.write(s + ";"); }
			} else if(type == 108) {
				target.write("const char ");
				if(isInitialized) { target.write(s + " = '" + (char)value + "';"); } 
				else { target.write(s + ";"); }
			} else if(type == 110){
				int elType = st.get(s).getelType(), size = st.get(s).getSize();
				if(elType == 101 || elType == 102) {
					target.write("int " + s + "[" + size + "];");
				} else {
					target.write("char " + s + "[" + size + "];");
				}
			}
			
			target.newLine();
		}
		
		target.write("int main() {"); target.newLine();
		
		while(input.hasNext()) {
			String command = input.next();
			String[] params = new String[5];
			
			if(command.equals("+")) {
				for(int i = 0; i < 3; i++) { params[i] = input.next(); }
				target.write("\t" + params[2] + " = " + params[0] + " + " + params[1] + ";");
				target.newLine();
			} else if(command.equals("-")) {
				int i = 0;
				Scanner temp = new Scanner(input.nextLine());
				while(temp.hasNext()) { params[i++] = temp.next(); }
				if(i == 2) {
					target.write("\t" + params[1] + " = -" + params[0] + ";");
				} else {
					target.write("\t" + params[2] + " = " + params[0] + " - " + params[1] + ";");
				}
				target.newLine();
			} else if(command.equals("/")) {
				for(int i = 0; i < 3; i++) { params[i] = input.next(); }
				target.write("\t" + params[2] + " = " + params[0] + " / " + params[1] + ";");
				target.newLine();
			} else if(command.equals("*")) {
				for(int i = 0; i < 3; i++) { params[i] = input.next(); }
				target.write("\t" + params[2] + " = " + params[0] + " * " + params[1] + ";");
				target.newLine();
			} else if(command.equals("%")) {
				for(int i = 0; i < 3; i++) { params[i] = input.next(); }
				target.write("\t" + params[2] + " = " + params[0] + " % " + params[1] + ";");
				target.newLine();
			} else if(command.equals("<=")) {
				for(int i = 0; i < 4; i++) { params[i] = input.next(); }
				target.write("\t" + "if(" + params[0] + " <= " + params[1] + ") {goto " + params[3] + ";}");
				target.newLine();
			} else if(command.equals(">=")) {
				for(int i = 0; i < 4; i++) { params[i] = input.next(); }
				target.write("\t" + "if(" + params[0] + " >= " + params[1] + ") {goto " + params[3] + ";}");
				target.newLine();
			} else if(command.equals("<")) {
				for(int i = 0; i < 4; i++) { params[i] = input.next(); }
				target.write("\t" + "if(" + params[0] + " < " + params[1] + ") {goto " + params[3] + ";}");
				target.newLine();
			} else if(command.equals(">")) {
				for(int i = 0; i < 4; i++) { params[i] = input.next(); }
				target.write("\t" + "if(" + params[0] + " > " + params[1] + ") {goto " + params[3] + ";}");
				target.newLine();
			} else if(command.equals("==")) {
				for(int i = 0; i < 4; i++) { params[i] = input.next(); }
				target.write("\t" + "if(" + params[0] + " == " + params[1] + ") {goto " + params[3] + ";}");
				target.newLine();
			} else if(command.equals("!=")) {
				for(int i = 0; i < 4; i++) { params[i] = input.next(); }
				target.write("\t" + "if(" + params[0] + " != " + params[1] + ") {goto " + params[3] + ";}");
				target.newLine();
			} else if(command.equals("&")) {
				for(int i = 0; i < 3; i++) { params[i] = input.next(); }
				target.write("\t" + params[2] + " = " + params[0] + " & " + params[1] + ";");
				target.newLine();
			} else if(command.equals("|")) {
				for(int i = 0; i < 3; i++) { params[i] = input.next(); }
				target.write("\t" + params[2] + " = " + params[0] + " | " + params[1] + ";");
				target.newLine();
			} else if(command.equals("^")) {
				for(int i = 0; i < 3; i++) { params[i] = input.next(); }
				target.write("\t" + params[2] + " = " + params[0] + " ^ " + params[1] + ";");
				target.newLine();
			} else if(command.equals("goto")) {
				for(int i = 0; i < 1; i++) { params[i] = input.next(); }
				target.write("\tgoto " + params[0] + ";");
				target.newLine();
			} else if(command.equals("=")) {
				for(int i = 0; i < 2; i++) { params[i] = input.next(); }
				target.write("\t" + params[1] + " = " + params[0] + ";");
				target.newLine();
			} else if(command.equals("cout")) {
				
				String sequence = input.nextLine().trim();
				if(sequence.equals("'")) {
					target.write("\tcout << '" + " " + "';");
				} else if(sequence.equals("\\n")) {
					target.write("\tcout << endl;");
				} else {
					try {
						if(st.get(sequence).getIfString()) {
							target.write("\tcout << \"" + sequence + "\";");
						} else {
							target.write("\tcout << " + sequence + ";");
						}
					} catch (java.lang.NullPointerException e){
						if(!sequence.contains("_t_") && !sequence.equals("' '"))
						try {
							target.write("\tcout << '" + (char)Integer.parseInt(sequence) + "';");
						} catch(java.lang.NumberFormatException ex) {
							target.write("\tcout << \"" + sequence + " \";");
						}
						else if(sequence.equals("' '"))
						target.write("\tcout << ' ';");
						else 
						target.write("\tcout << " + sequence + ";");

					}
				}
				target.newLine();
			} else if(command.equals("[]=")) {
				for(int i = 0; i < 3; i++) { params[i] = input.next(); }
				target.write("\t" + params[2] + "[" + params[1] + "]" + " = " + params[0] + ";");
				target.newLine();
			} else if(command.equals("=[]")) {
				for(int i = 0; i < 3; i++) { params[i] = input.next(); }
				target.write("\t" + params[2] + " = " +  params[0] + "[" + params[1] + "];");
				target.newLine();
			} else if(command.contains("L_")){
				target.write("\t" + command + ":");
				target.newLine();
			}
		}
		
		target.write("\treturn 0;");
		target.newLine();
		target.write("}");
		target.close();
	}
	
}
