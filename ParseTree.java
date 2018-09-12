/* Dylan Porter */
/* C++ Compiler */
/* ParseTree.java */

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Scanner;


public class ParseTree {
	
	BufferedWriter s;
	
	public ParseTree(BufferedWriter s) { this.s = s; }
	
	Scanner input;
	int temp = 0, label = 0;
	public static int maxTemp = 0;
	
	String formatStr = "%-10s %-10s %-10s %-10s %-10s";
	
	public void printArrayStatement(String statement, String varName) throws IOException {

		if(!checkParenthesis(statement, '(', ')')) {
			System.out.println("ERROR: Missing '(' or ')'");
			System.exit(0);
		} else if(!checkParenthesis(statement, '[', ']')) {
			System.out.println("ERROR: Missing '[' or ']'");
			System.exit(0);
		}
		
		/*s is left hand side, r is right hand side*/
		statement = statement.replaceAll("\\s", "");
		String s = statement.substring(0, statement.indexOf("="));
		String r = statement.substring(statement.indexOf("=")+1);
		
		/*This will serve as the final position for the left hand side -- the highest temp*/
		int lastLeft = 0;
		
		/*While there are still arrays in the string*/
		while(s.contains("[")) {
			/*exp is the expression with the []*/
			String exp = "";
			int currentIndex = 0;
			
			/*Loop through the entire string, find last occurring []*/
			for(int i = 0; i < s.length(); i++) {
				/*If '[', reset exp and collect the new (and higher precedence) expression*/
				if(s.charAt(i) == '[') {exp = ""; currentIndex = i;}
				
				/*We've encountered the end of our expression*/
				else if(s.charAt(i) != ']') { exp += s.charAt(i); }
				else {
					String var = s.substring(currentIndex-2, currentIndex);
					String tempS = s;

					s = s.substring(0,currentIndex-2) + String.valueOf(genParseTree(exp)) + s.substring(i+1);

					if(exp.length() <= 2) {
						
						writeToFile("=", exp, "", "_t_"+(temp), "");
						s = tempS.substring(0, currentIndex-2) + "_t_"+(temp+1) + tempS.substring(i+1);
						
						if(!(r.contains("+") || r.contains("-") || r.contains("*") || r.contains("/") || r.contains("%") || r.contains("("))) {
							writeToFile("[]=", r, "_t_"+(temp),varName, "");							
							return;
						}
						if(s.contains("[")) {
							writeToFile("=[]", var, "_t_"+(temp),"_t_" + ++temp, "");							
							temp++;
						} 
					} else {
						//PROBLEM
						writeToFile( "=", "_t_" + (temp-1), "", "_t_"+(temp), "");							
						//temp++;
					}
					if(temp > maxTemp) { this.maxTemp = temp; }
					break;
				}
			}
		}
		lastLeft = temp;
		
		/*Handle the right side*/
		
		while(r.contains("[")) {
			String exp = "", term = "";
			int currentIndex = 0;
			for(int i = 0; i < r.length(); i++) {

				if(r.charAt(i) == '[') {exp = ""; currentIndex = i;}
				else if(r.charAt(i) != ']') { exp += r.charAt(i); }
				else {
					
					term = r.substring(currentIndex-2, currentIndex);
					if(!(exp.contains("+") || exp.contains("-") || exp.contains("*") || exp.contains("/") || exp.contains("%") || exp.contains("("))) {
						temp++;
						writeToFile("=", exp, "", "_t_"+ (temp), "");							
						r = r.substring(0,currentIndex-2) + "_t_"+ temp + r.substring(i+1);
						
						writeToFile("=[]", term, "_t_"+temp++, "_t_"+ (temp), "");							
						r = r.replaceFirst("_t_"+(temp-1), "_t_"+(temp));
					} else {
						temp++;
						r = r.substring(0,currentIndex-2) + String.valueOf(genParseTree(exp)) + r.substring(i+1);
						temp--;
						writeToFile("=", "_t_"+(temp), "", "_t_"+ (++temp), "");							
						writeToFile("=[]", term, "_t_"+(temp),"_t_"+ (++temp), "");							
					}
					
					break;
				}
			}
			
		}
		/***********************/

		temp++;
		genParseTree(r);
		writeToFile("[]=", "_t_"+(temp-1), "_t_"+(lastLeft),varName, "");	
		if(temp > maxTemp) { this.maxTemp = temp; }
		this.temp = 0;

	}
	
	public void printArrayAssignment(String r, String varName, boolean isPrint, LinearProbingHashST st) throws IOException {

		if(!checkParenthesis(r, '(', ')')) {
			System.out.println("ERROR: Missing '(' or ')'");
			System.exit(0);
		} else if(!checkParenthesis(r, '[', ']')) {
			System.out.println("ERROR: Missing '[' or ']'");
			System.exit(0);
		}
		
		/*Handle the right side*/

		while(r.contains("[")) {
			String exp = "", term = "";
			int currentIndex = 0;
			for(int i = 0; i < r.length(); i++) {

				if(r.charAt(i) == '[') {exp = ""; currentIndex = i;}
				else if(r.charAt(i) != ']') { exp += r.charAt(i); }
				else {
					
					term = r.substring(currentIndex-2, currentIndex);
					if(!(exp.contains("+") || exp.contains("-") || exp.contains("*") || exp.contains("/") || exp.contains("%") || exp.contains("("))) {
						temp++;
						writeToFile("=", exp, "", "_t_"+ (temp), "");							
						r = r.substring(0,currentIndex-2) + "_t_"+ temp + r.substring(i+1);
						
						writeToFile("=[]", term, "_t_"+temp++, "_t_"+ (temp), "");							
						r = r.replaceFirst("_t_"+(temp-1), "_t_"+(temp));
					} else {
						temp++;
						r = r.substring(0,currentIndex-2) + String.valueOf(genParseTree(exp)) + r.substring(i+1);
						temp--;
						
						writeToFile("=", "_t_"+(temp), "", "_t_"+ (++temp), "");	
						writeToFile("=[]", term, "_t_"+(temp),"_t_"+ (++temp), "");							

					}
					
					break;
				}
			}
			
		}
		
		temp++;
		genParseTree(r);
		if(!isPrint) {
			writeToFile("=", "_t_"+(temp-1), "", varName, "");							
		} else {
			if(varName.equals("cin")) {
				writeToFile("cin", "_t_"+(temp), "", "", "");							
			} else {
				writeToFile("cout", "_t_"+(temp-1), "", "", "");							
			}
		}
		if(temp > maxTemp) { this.maxTemp = temp; }
		temp = 0;
	}
	
	public void printOutput() throws IOException {
		int local = this.temp;
		writeToFile("cout", "_t_"+(local-1),"","", "");							
	}
	
	public void printEquals(String target, String exp, boolean isCall) throws IOException {
		int local = this.temp;
		this.temp = 0;
		
		/*Test to see if any arithmetic operations occurred*/
		if(local-1 >= 0) {
			if(exp.contains("+") || exp.contains("-") || exp.contains("*") || exp.contains("/") ||
					exp.contains("%") || exp.contains("(") || exp.contains(")")) {
				writeToFile("=", "_t_"+(local-1), "", target,"");	

			} else {
				writeToFile("=", exp, "", target, "");							
			}
		} else {
			writeToFile("=", exp, "", target, "");							
		}
		if(temp > maxTemp) { this.maxTemp = temp; }
	}	
	
	public int incrementLabel() {
		return ++label;
	}
	
	public String genParseTree(String s) throws IOException {
		
		if(!checkParenthesis(s, '(', ')')) {
			System.out.println("ERROR: Missing '(' or ')'");
			System.exit(0);
		} else if(!checkParenthesis(s, '[', ']')) {
			System.out.println("ERROR: Missing '[' or ']'");
			System.exit(0);
		}
		
		s = s.replaceAll("\\s", "");
		boolean containsExclamation = false;
		
		while(s.contains("(")) {
			
			String exp = "";
			int currentIndex = 0;
			for(int i = 0; i < s.length(); i++) {
				if(s.charAt(i) == '(') {exp = ""; currentIndex = i;}
				else if(s.charAt(i) == '!' && s.charAt(i + 1) != '=') {
					containsExclamation = true;
				} else if(s.charAt(i) != ')') { exp += s.charAt(i); }
				else {
					if(containsExclamation && s.charAt(s.indexOf(exp)-2) == '!') {
						if(s.charAt(currentIndex-1) == '!') {
							s = s.substring(0,currentIndex-1) + String.valueOf(genParseTree(exp)) + s.substring(i+1);
						} else {
							s = s.substring(0,currentIndex) + String.valueOf(genParseTree(exp)) + s.substring(i+1);
						}
						writeToFile("^", "_t_" + (temp-1), "1", "_t_" + (temp-1), "");		
					} else {
						s = s.substring(0,currentIndex) + String.valueOf(genParseTree(exp)) + s.substring(i+1);
					}
					containsExclamation = false;
					break;
				}
			}
			
		}
		
		if(s.contains("&&") && s.indexOf("||") < s.indexOf("&&")) {
			String lhs = genParseTree(s.substring(0, s.lastIndexOf("&&")));
			String rhs = genParseTree(s.substring(s.lastIndexOf("&&")+2));

			int bResult = temp;

			writeToFile("&", "_t_" + lhs, "_t_" + rhs, "_t_" + (bResult),"");
			//writeToFile("||", lhs, rhs, "goto", "L_" + bTrue);							
			
			return ("" + temp++);
		} else if(s.contains("||") && s.indexOf("&&") < s.indexOf("||")) {
			
			String lhs = genParseTree(s.substring(0, s.lastIndexOf("||")));
			String rhs = genParseTree(s.substring(s.lastIndexOf("||")+2));
			int bResult = temp;

			writeToFile("|", "_t_" + lhs, "_t_" + rhs, "_t_" + (bResult),"");
			//writeToFile("||", lhs, rhs, "goto", "L_" + bTrue);							
			
			return ("" + temp++);

		} else if(s.contains("<=")) {
			int bTrue = ++label, bEnd = ++label;
			String lhs = genParseTree(s.substring(0, s.lastIndexOf("<=")));
			String rhs = genParseTree(s.substring(s.lastIndexOf("<=")+2));
			
			writeToFile("<=", lhs, rhs, "goto", "L_" + bTrue);							

			writeToFile("=", "0", "", "_t_" + temp, "");							
			writeToFile("goto", "L_" + bEnd, "", "", "");							
			writeToFile("L_" + bTrue, "", "", "", "");							
			writeToFile("=", "1", "", "_t_" + temp, "");							
			writeToFile("L_" + bEnd, "", "", "", "");	
			
			return ("" + temp++);
		} else if(s.contains(">=")) {
			int bTrue = ++label, bEnd = ++label;
			String lhs = genParseTree(s.substring(0, s.lastIndexOf(">=")));
			String rhs = genParseTree(s.substring(s.lastIndexOf(">=")+2));
			
			writeToFile(">=", lhs, rhs, "goto", "L_" + bTrue);							

			writeToFile("=", "0", "", "_t_" + temp, "");							
			writeToFile("goto", "L_" + bEnd, "", "", "");							
			writeToFile("L_" + bTrue, "", "", "", "");							
			writeToFile("=", "1", "", "_t_" + temp, "");							
			writeToFile("L_" + bEnd, "", "", "", "");	
			
			return ("" + temp++);

		} else if(s.contains("<")) {
			int bTrue = ++label, bEnd = ++label;
			String lhs = genParseTree(s.substring(0, s.lastIndexOf("<")));
			String rhs = genParseTree(s.substring(s.lastIndexOf("<")+1));
			
			writeToFile("<", lhs, rhs, "goto", "L_" + bTrue);							

			writeToFile("=", "0", "", "_t_" + temp, "");							
			writeToFile("goto", "L_" + bEnd, "", "", "");							
			writeToFile("L_" + bTrue, "", "", "", "");							
			writeToFile("=", "1", "", "_t_" + temp, "");							
			writeToFile("L_" + bEnd, "", "", "", "");	
			return ("" + temp++);

		} else if(s.contains(">")) {
			int bTrue = ++label, bEnd = ++label;
			String lhs = genParseTree(s.substring(0, s.lastIndexOf(">")));
			String rhs = genParseTree(s.substring(s.lastIndexOf(">")+1));
			
			writeToFile(">", lhs, rhs, "goto", "L_" + bTrue);							

			writeToFile("=", "0", "", "_t_" + temp, "");							
			writeToFile("goto", "L_" + bEnd, "", "", "");							
			writeToFile("L_" + bTrue, "", "", "", "");							
			writeToFile("=", "1", "", "_t_" + temp, "");							
			writeToFile("L_" + bEnd, "", "", "", "");	
			return ("" + temp++);

		} else if(s.contains("==")) {
			int bTrue = ++label, bEnd = ++label;
			String lhs = genParseTree(s.substring(0, s.lastIndexOf("==")));
			String rhs = genParseTree(s.substring(s.lastIndexOf("==")+2));
			
			writeToFile("==", lhs, rhs, "goto", "L_" + bTrue);							

			writeToFile("=", "0", "", "_t_" + temp, "");							
			writeToFile("goto", "L_" + bEnd, "", "", "");							
			writeToFile("L_" + bTrue, "", "", "", "");							
			writeToFile("=", "1", "", "_t_" + temp, "");							
			writeToFile("L_" + bEnd, "", "", "", "");	
			
			return ("" + temp++);
			
		} else if(s.contains("!=")) {

			int bTrue = ++label, bEnd = ++label;
			String lhs = genParseTree(s.substring(0, s.lastIndexOf("!=")));
			String rhs = genParseTree(s.substring(s.lastIndexOf("!=")+2));
			
			writeToFile("!=", lhs, rhs, "goto", "L_" + bTrue);							

			writeToFile("=", "0", "", "_t_" + temp, "");							
			writeToFile("goto", "L_" + bEnd, "", "", "");							
			writeToFile("L_" + bTrue, "", "", "", "");							
			writeToFile("=", "1", "", "_t_" + temp, "");							
			writeToFile("L_" + bEnd, "", "", "", "");	
			
			return ("" + temp++);
			
		} else if(s.contains("+") ) {
			String lhs = genParseTree(s.substring(0, s.lastIndexOf("+")));
			String rhs = genParseTree(s.substring(s.lastIndexOf("+")+1));

			writeToFile("+", lhs, rhs, "_t_"+temp, "");							
			return ("_t_" + temp++);
			/*return(genParseTree(s.substring(0, s.indexOf("+")))
					+ genParseTree(s.substring(s.indexOf("+")+1)));*/
		} else if(s.contains("-") ) {
			if(s.charAt(0) == '-') {
				String temp = s.substring(1);
				if(temp.contains("-")) {
					writeToFile("-", temp.substring(0, temp.indexOf("-")), "", "_t_"+this.temp, "");							
					s = s.replaceFirst(temp.substring(0, temp.indexOf("-")), "_t_" + this.temp++);
				} else {
					writeToFile("-", temp, "", "_t_"+this.temp, "");							
					return "_t_"+this.temp++;
				}
				s = s.replaceFirst("-", "");
			}
			
			String lhs = genParseTree(s.substring(0, s.indexOf("-")));
			String rhs = genParseTree(s.substring(s.indexOf("-")+1));
			writeToFile("-", lhs, rhs, "_t_"+temp, "");							

			return ("_t_"+temp++);
			/*
			return(genParseTree(s.substring(0, s.indexOf("-")))
					- genParseTree(s.substring(s.indexOf("-")+1)));*/
		} else if(s.contains("*") && s.indexOf("/") < s.indexOf("*")) {
			String lhs = genParseTree(s.substring(0, s.lastIndexOf("*")));
			String rhs = genParseTree(s.substring(s.lastIndexOf("*")+1));
			
			writeToFile("*", lhs, rhs, "_t_"+temp, "");							
			return ("_t_"+temp++);
			/*
			System.out.println("tempMult" + temp++);
			return(genParseTree(s.substring(0, s.indexOf("*")))
					* genParseTree(s.substring(s.indexOf("*")+1)));
			*/
		} else if(s.contains("/") && s.indexOf("*") < s.indexOf("/")) {
			String lhs = genParseTree(s.substring(0, s.lastIndexOf("/")));
			String rhs = genParseTree(s.substring(s.lastIndexOf("/")+1));
			
			writeToFile("/", lhs, rhs, "_t_"+temp, "");							
			return ("_t_"+temp++);
			
			/*return(genParseTree(s.substring(0, s.indexOf("/")))
					/ genParseTree(s.substring(s.indexOf("/")+1)));
					 * 
					 */
		} else if(s.contains("%")) {
			String lhs = genParseTree(s.substring(0, s.lastIndexOf("%")));
			String rhs = genParseTree(s.substring(s.lastIndexOf("%")+1));
			
			writeToFile("%", lhs, rhs, "_t_"+temp, "");							
			return ("_t_"+temp++);
		} else if(s.contains("(")) {
			return(genParseTree(s.substring(s.indexOf("(")+1,s.indexOf(")"))));
		}
		else {
			if(temp > maxTemp) { this.maxTemp = temp; }

			return s;
		}
		
	}	
	public void writeToFile(String index0, String index1, String index2, String index3, String index4) throws IOException {
		s.write(String.format(formatStr, index0, index1, index2, index3, index4));
		s.newLine();
	}
	
	public boolean checkParenthesis(String s, char term, char term2) {
		
		int counter = 0, counter2 = 0;
		for( int i=0; i<s.length(); i++ ) {
		    if( s.charAt(i) == term ) {
		        counter++;
		    } 
		}
		for( int i=0; i<s.length(); i++ ) {
		    if( s.charAt(i) == term2 ) {
		        counter2++;
		    } 
		}
		
		if(counter != counter2) {
			return false;
		}
		
		return true;
	}
	

	
}



/*exp --> exp + term | exp - term | term
term --> term * factor | term / factor | factor
factor --> id | number | (exp)*/

