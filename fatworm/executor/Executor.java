package fatworm.executor;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.Tree;

import fatworm.log.*;
import fatworm.parser.FatwormParser;


public class Executor {
	public Executor() {
		
	}
	public boolean isId(Tree t) {
		CommonTree tr = (CommonTree)t;
		String s = FatwormParser.tokenNames[tr.getType()];
		if (s == "ID") return true;
		return false;
	}
	public boolean isInt(Tree t) {
		CommonTree tr = (CommonTree)t;
		String s = FatwormParser.tokenNames[tr.getType()];
		if (s == "INT") return true;
		return false;
	}
	public boolean isFloat(Tree t) {
		CommonTree tr = (CommonTree)t;
		String s = FatwormParser.tokenNames[tr.getType()];
		if (s == "FLOAT") return true;
		return false;
	}
	public boolean isChar(Tree t) {
		CommonTree tr = (CommonTree)t;
		String s = FatwormParser.tokenNames[tr.getType()];
		if (s == "CHAR") return true;
		return false;
	}
	public boolean isDateTime(Tree t) {
		CommonTree tr = (CommonTree)t;
		String s = FatwormParser.tokenNames[tr.getType()];
		if (s == "DATETIME") return true;
		return false;
	}
	public boolean isBoolean(Tree t) {
		CommonTree tr = (CommonTree)t;
		String s = FatwormParser.tokenNames[tr.getType()];
		if (s == "BOOLEAN") return true;
		return false;
	}
	public boolean isDecimal(Tree t) {
		CommonTree tr = (CommonTree)t;
		String s = FatwormParser.tokenNames[tr.getType()];
		if (s == "DECIMAL") return true;
		return false;
	}
	public boolean isTimeStamp(Tree t) {
		CommonTree tr = (CommonTree)t;
		String s = FatwormParser.tokenNames[tr.getType()];
		if (s == "TIMESTAMP") return true;
		return false;
	}
	public boolean isVarChar(Tree t) {
		CommonTree tr = (CommonTree)t;
		String s = FatwormParser.tokenNames[tr.getType()];
		if (s.equals("VARCHAR")) return true;
		return false;
	}
	public boolean isNull(Tree t) {
		CommonTree tr = (CommonTree)t;
		String s = FatwormParser.tokenNames[tr.getType()];
		if (s.equals("NULL")) return true;
		return false;
	}
	public boolean isNot(Tree t) {
		CommonTree tr = (CommonTree)t;
		String s = FatwormParser.tokenNames[tr.getType()];
		if (s.equals("NOT")) return true;
		return false;
	}
	public boolean isAutoIncrement(Tree t) {
		CommonTree tr = (CommonTree)t;
		String s = FatwormParser.tokenNames[tr.getType()];
		if (s.equals("AUTO_INCREMENT")) return true;
		return false;
	}
	public boolean isDefault(Tree t) {
		CommonTree tr = (CommonTree)t;
		String s = FatwormParser.tokenNames[tr.getType()];
		if (s.equals("DEFAULT")) return true;
		return false;
	}
	public boolean isFloatLiteral(Tree t) {
		CommonTree tr = (CommonTree)t;
		String s = FatwormParser.tokenNames[tr.getType()];
		if (s == "FLOAT_LITERAL")
			return true;
		return false;
	}
	public boolean isIntegerLiteral(Tree t) {
		CommonTree tr = (CommonTree)t;
		String s = FatwormParser.tokenNames[tr.getType()];
		if (s == "INTEGER_LITERAL")
			return true;
		return false;		
	}
	public boolean isStringLiteral(Tree t) {
		CommonTree tr = (CommonTree)t;
		String s = FatwormParser.tokenNames[tr.getType()];
		if (s == "STRING_LITERAL")
			return true;
		return false;		
	}
	public boolean isTrue(Tree t) {
		CommonTree tr = (CommonTree)t;
		String s = FatwormParser.tokenNames[tr.getType()];
		if (s.equals("TRUE"))
			return true;
		return false;
	}
	public boolean isFalse(Tree t) {
		CommonTree tr = (CommonTree)t;
		String s = FatwormParser.tokenNames[tr.getType()];
		if (s.equals("FALSE"))
			return true;
		return false;		
	}
	public boolean isCreateDefinition(Tree t) {
		CommonTree tr = (CommonTree)t;
		String s = FatwormParser.tokenNames[tr.getType()];
		if (s.equals("CREATE_DEFINITION"))
			return true;
		return false;		
	}
	public boolean isValues(Tree t) {
		CommonTree tr = (CommonTree)t;
		String s = FatwormParser.tokenNames[tr.getType()];
		if (s.equals("VALUES"))
			return true;
		return false;		
	}
	public boolean isWhere(Tree t) {
		CommonTree tr = (CommonTree)t;
		String s = FatwormParser.tokenNames[tr.getType()];
		if (s.equals("WHERE"))
			return true;
		return false;		
	}
	public boolean isDropDatabase(Tree t) {
		CommonTree tr = (CommonTree)t;
		String s = FatwormParser.tokenNames[tr.getType()];
		if (s.equals("DROP_DATABASE"))
			return true;
		return false;		
	}
	public boolean isPrimaryKey(Tree t) {
		CommonTree tr = (CommonTree)t;
		String s = FatwormParser.tokenNames[tr.getType()];
		if (s.equals("PRIMARY_KEY"))
			return true;
		return false;		
	}
	public void execute() throws Exception{
		throw new DevelopException();
	}
}
