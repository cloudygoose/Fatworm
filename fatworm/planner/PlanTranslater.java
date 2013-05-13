package fatworm.planner;
import org.antlr.runtime.*;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.Tree;

import fatworm.log.Log;
import fatworm.parser.*;

public class PlanTranslater {
	public boolean isSelect(Tree t) {
		CommonTree tr = (CommonTree)t;
		String s = FatwormParser.tokenNames[tr.getType()];
		if (s == "SELECT") return true;
		return false;
	}
	public boolean isSelectDistinct(Tree t) {
		CommonTree tr = (CommonTree)t;
		String s = FatwormParser.tokenNames[tr.getType()];
		if (s == "SELECT_DISTINCT") return true;
		return false;
	}
	public boolean isFrom(Tree t) {
		CommonTree tr = (CommonTree)t;
		String s = FatwormParser.tokenNames[tr.getType()];
		if (s == "FROM") return true;
		return false;
	}
	public boolean isAS(Tree t) {
		CommonTree tr = (CommonTree)t;
		String s = FatwormParser.tokenNames[tr.getType()];
		if (s == "AS") return true;
		return false;
	}
	public boolean isID(Tree t) {
		CommonTree tr = (CommonTree)t;
		String s = FatwormParser.tokenNames[tr.getType()];
		if (s == "ID") return true;
		return false;
	}
	public boolean isFunc(Tree t) {
		CommonTree tr = (CommonTree)t;
		String s = FatwormParser.tokenNames[tr.getType()];
		// AVG | COUNT | MIN | MAX | SUM
		if (s == "COUNT" || s == "AVG" || s == "MIN" || s == "MAX" || s == "SUM")
			return true;
		return false;
	}
	public boolean isWhere(Tree t) {
		CommonTree tr = (CommonTree)t;
		String s = FatwormParser.tokenNames[tr.getType()];
		if (s == "WHERE")
			return true;
		return false;
	}
	public boolean isGreater(Tree t) {
		CommonTree tr = (CommonTree)t;
		String s = FatwormParser.tokenNames[tr.getType()];
		if (s == "'>'")
			return true;
		return false;
	}
	public boolean isExists(Tree t) {
		CommonTree tr = (CommonTree)t;
		String s = FatwormParser.tokenNames[tr.getType()];
		if (s == "EXISTS")
			return true;
		return false;
	}
	public boolean isNotExists(Tree t) {
		CommonTree tr = (CommonTree)t;
		String s = FatwormParser.tokenNames[tr.getType()];
		if (s == "NOT_EXISTS")
			return true;
		return false;
	}
	public boolean isLess(Tree t) {
		CommonTree tr = (CommonTree)t;
		String s = FatwormParser.tokenNames[tr.getType()];
		if (s == "'<'")
			return true;
		return false;
	}
	public boolean isGreaterEqual(Tree t) {
		CommonTree tr = (CommonTree)t;
		String s = FatwormParser.tokenNames[tr.getType()];
		if (s == "'>='")
			return true;
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
		if (s.equals("STRING_LITERAL"))
			return true;
		return false;				
	}
	public boolean isAsc(Tree t) {
		CommonTree tr = (CommonTree)t;
		String s = FatwormParser.tokenNames[tr.getType()];
		if (s.equals("ASC"))
			return true;
		return false;		
	}
	public boolean isDesc(Tree t) {
		CommonTree tr = (CommonTree)t;
		String s = FatwormParser.tokenNames[tr.getType()];
		if (s.equals("DESC"))
			return true;
		return false;		
	}
	public boolean isOrderBy(Tree t) {
		CommonTree tr = (CommonTree)t;
		String s = FatwormParser.tokenNames[tr.getType()];
		if (s.equals("ORDER"))
			return true;
		return false;		
	}
	public boolean isLessEqual(Tree t) {
		CommonTree tr = (CommonTree)t;
		String s = FatwormParser.tokenNames[tr.getType()];
		if (s == "'<='")
			return true;
		return false;
	}
	public boolean isEqual(Tree t) {
		CommonTree tr = (CommonTree)t;
		String s = FatwormParser.tokenNames[tr.getType()];
		if (s == "'='")
			return true;
		return false;
	}
	public boolean isNotEqual(Tree t) {
		CommonTree tr = (CommonTree)t;
		String s = FatwormParser.tokenNames[tr.getType()];
		if (s.equals("'<>'"))
			return true;
		return false;
	}
	public boolean isAnd(Tree t) {
		CommonTree tr = (CommonTree)t;
		String s = FatwormParser.tokenNames[tr.getType()];
		if (s == "AND")
			return true;
		return false;
	}
	public boolean isOr(Tree t) {
		CommonTree tr = (CommonTree)t;
		String s = FatwormParser.tokenNames[tr.getType()];
		if (s == "OR")
			return true;
		return false;
	}
	public boolean isUnEqual(Tree t) {
		CommonTree tr = (CommonTree)t;
		String s = FatwormParser.tokenNames[tr.getType()];
		if (s == "'<>'")
			return true;
		return false;
	}
	public boolean isDiv(Tree t) {
		CommonTree tr = (CommonTree)t;
		String s = FatwormParser.tokenNames[tr.getType()];
		if (s == "'/'")
			return true;
		return false;
	}	
	public boolean isMod(Tree t) {
		CommonTree tr = (CommonTree)t;
		String s = FatwormParser.tokenNames[tr.getType()];
		if (s == "'%'")
			return true;
		return false;
	}	
	public boolean isMul(Tree t) {
		CommonTree tr = (CommonTree)t;
		String s = FatwormParser.tokenNames[tr.getType()];
		if (s == "'*'")
			return true;
		return false;
	}	
	public boolean isMinus(Tree t) {
		CommonTree tr = (CommonTree)t;
		String s = FatwormParser.tokenNames[tr.getType()];
		if (s == "'-'")
			return true;
		return false;
	}	
	public boolean isPlus(Tree t) {
		CommonTree tr = (CommonTree)t;
		String s = FatwormParser.tokenNames[tr.getType()];
		if (s == "'+'")
			return true;
		return false;
	}	/*
	 * aa.bb
	 */
	public boolean isTuple(Tree t) {
		CommonTree tr = (CommonTree)t;
		String s = FatwormParser.tokenNames[tr.getType()];
		if (s == "'.'")
			return true;
		return false;
	}
	public boolean isStar(Tree t) {
		CommonTree tr = (CommonTree)t;
		String s = FatwormParser.tokenNames[tr.getType()];
		if (s == "'*'")
			return true;
		return false;
	}
	public boolean isAvg(Tree t) {
		CommonTree tr = (CommonTree)t;
		String s = FatwormParser.tokenNames[tr.getType()];
		if (s == "AVG")
			return true;
		return false;	
	}
	public boolean isCount(Tree t) {
		CommonTree tr = (CommonTree)t;
		String s = FatwormParser.tokenNames[tr.getType()];
		if (s == "COUNT")
			return true;
		return false;	
	}
	public boolean isMin(Tree t) {
		CommonTree tr = (CommonTree)t;
		String s = FatwormParser.tokenNames[tr.getType()];
		if (s == "MIN")
			return true;
		return false;	
	}
	public boolean isMax(Tree t) {
		CommonTree tr = (CommonTree)t;
		String s = FatwormParser.tokenNames[tr.getType()];
		if (s == "MAX")
			return true;
		return false;	
	}
	public boolean isSum(Tree t) {
		CommonTree tr = (CommonTree)t;
		String s = FatwormParser.tokenNames[tr.getType()];
		if (s == "SUM")
			return true;
		return false;	
	}
	public boolean isIn(Tree t) {
		CommonTree tr = (CommonTree)t;
		String s = FatwormParser.tokenNames[tr.getType()];
		if (s == "IN")
			return true;
		return false;	
	}
	public boolean isAny(Tree t) {
		CommonTree tr = (CommonTree)t;
		String s = FatwormParser.tokenNames[tr.getType()];
		if (s == "ANY")
			return true;
		return false;	
	}
	public boolean isTrue(Tree t) {
		CommonTree tr = (CommonTree)t;
		String s = FatwormParser.tokenNames[tr.getType()];
		if (s == "TRUE")
			return true;
		return false;	
	}
	public boolean isFalse(Tree t) {
		CommonTree tr = (CommonTree)t;
		String s = FatwormParser.tokenNames[tr.getType()];
		if (s == "FALSE")
			return true;
		return false;	
	}
	public boolean isAll(Tree t) {
		CommonTree tr = (CommonTree)t;
		String s = FatwormParser.tokenNames[tr.getType()];
		if (s == "ALL")
			return true;
		return false;	
	}
	public boolean isGroup(Tree t) {
		CommonTree tr = (CommonTree)t;
		String s = FatwormParser.tokenNames[tr.getType()];
		if (s == "GROUP")
			return true;
		return false;	
	}
	public boolean isHaving(Tree t) {
		CommonTree tr = (CommonTree)t;
		String s = FatwormParser.tokenNames[tr.getType()];
		if (s == "HAVING")
			return true;
		return false;	
	}
	public boolean isQuery(Tree t) {
		return (isSelect(t) || isSelectDistinct(t));
	}
	
	/*
	 * return the first From_clause
	 */
	public CommonTree findFrom(Tree t) {
		CommonTree tr = (CommonTree)t;
		for (int i = 0;i < tr.getChildCount();i++)
			if (isFrom(tr.getChild(i))) return (CommonTree)tr.getChild(i);
		return null;
	}
	public CommonTree findHaving(Tree t) {
		CommonTree tr = (CommonTree)t;
		for (int i = 0;i < tr.getChildCount();i++)
			if (isHaving(tr.getChild(i))) return (CommonTree)tr.getChild(i);
		return null;
	}
	/*
	 * return the first Where_clause
	 */
	public CommonTree findWhere(Tree t) {
		CommonTree tr = (CommonTree)t;
		for (int i = 0;i < tr.getChildCount();i++)
			if (isWhere(tr.getChild(i))) return (CommonTree)tr.getChild(i);
		return null;
	}
	/*
	 * return the first group_clause
	 */
	public CommonTree findGroup(Tree t) {
		CommonTree tr = (CommonTree)t;
		for (int i = 0;i < tr.getChildCount();i++)
			if (isGroup(tr.getChild(i))) return (CommonTree)tr.getChild(i);
		return null;
	}
	public CommonTree findOrderBy(Tree t) {
		CommonTree tr = (CommonTree)t;
		for (int i = 0;i < tr.getChildCount();i++)
			if (isOrderBy(tr.getChild(i))) return (CommonTree)tr.getChild(i);
		return null;
	}
}
