/*    Dylan Porter     */
/*    C++ Compiler     */
/*    Compiler.java    */

import java.io.IOException;

public class Compiler {

	public static void main(String[] args) throws IOException {
		String fileCode = "C:\\Users\\Dylan\\Desktop\\Code.txt",
			   fileOutput = "C:\\Users\\Dylan\\Desktop\\Output.txt",
			   fileQuadruples = "C:\\Users\\Dylan\\Desktop\\Quadruples.txt",
			   fileTarget = "C:\\Users\\Dylan\\Desktop\\target.cpp";
		
		LexicalAnalyzer analyzer = new LexicalAnalyzer();
		LinearProbingHashST st;
		st = analyzer.read(fileCode, fileOutput);

		Parser parse = new Parser();
		st = parse.read(st, fileOutput, fileQuadruples);

		Compile compile = new Compile();
		compile.execute(fileTarget, fileQuadruples,
						parse.parse.constantList, st, parse);
	}
}