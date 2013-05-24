package fatworm.expression;
import java.lang.*;
import java.util.ArrayList;
import java.util.Iterator;

import fatworm.log.*;
import fatworm.type.*;
import fatworm.table.*;
public class ExpList {
	ArrayList<Expression> expList;
	public ArrayList<Expression> getExpList() {
		return expList;
	}
	public ExpList(ArrayList<Expression> list) {
		expList = list;
	}
	public ExpList copy() {
		ArrayList<Expression> kk = new ArrayList<Expression>();
		Iterator<Expression> iter = expList.iterator();
		while (iter.hasNext()) 
			kk.add(iter.next().copy());
		return new ExpList(kk);
	}
	public String getPrint(int old) throws Exception{
		String result = padding(old) + "ExpList:(\n";
		for (int i = 0;i < expList.size();i++) {
			result += expList.get(i).getPrint(old + 1);
		}
		result += padding(old) + ")ExpList\n";
		return result;
	}
	public Tuple evaluate() throws Exception{
		Tuple tuple = new Tuple();
		for (int i = 0;i < expList.size();i++) {
			FatType value = expList.get(i).evaluate();
			TupleColumn col = new TupleColumn(value.assocTableName, value.assocColumnName, value);
			tuple.addColumn(col);
		}
		return tuple;
	}
	public Tuple evaluateFirstStar(Tuple tuple) throws Exception{
		for (int i = 1;i < expList.size();i++) {
			FatType value = expList.get(i).evaluate();
			TupleColumn col = new TupleColumn(value.assocTableName, value.assocColumnName, value);
			tuple.addColumn(col);
		}
		return tuple;
	}
	public boolean firstStarExp() {
		boolean findStar = false;
		for (int i = 0;i < 1;i++)
			if (expList.get(i) instanceof StarExp) {
				findStar = true;
			}
		if (findStar) {
			return true;
		}
		return false;
	}
	public String padding(int kk) {
		String result = "";
		for (int i = 1;i <= kk;i++)
			result += "    ";
		return result;
	}
}
