package fatworm.parser;
import java.io.ByteArrayInputStream;


import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.tree.CommonTree;

import fatworm.log.Log;
public class ParserManager {
	/*
	 * log AST of t. please first set old as ""
	 */
	public static void LogAST(CommonTree t, String old) {
		Log.v(old + "-" + t.getType() + t.getText());
		int child = t.getChildCount();
		for (int i = 0;i < child;i++)
			LogAST((CommonTree)t.getChild(i), old + "-" + t.getText());
	}
	public CommonTree getCommonTree(String str) {
		try {
			ByteArrayInputStream stream = new ByteArrayInputStream(str.getBytes());
			//Create an input character stream from standard in
			ANTLRInputStream input = new ANTLRInputStream(stream);
			//Create an ExprLexer that feeds from that stream
			FatwormLexer lexer = new FatwormLexer(input);
			// Create a stream of tokens fed by the 
			CommonTokenStream tokens = new CommonTokenStream(lexer);
			// Create a parser that feeds off the token stream
			FatwormParser parser = new FatwormParser(tokens);
			// Begin parsing at rule statement
			FatwormParser.statement_return r = parser.statement();
			// WALK RESULTING TREE
			CommonTree t = (CommonTree)r.getTree(); // get tree from parser
			return t;
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
