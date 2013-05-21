package fatworm.opt;
import fatworm.expression.*;
import fatworm.log.Log;
import fatworm.type.FatBoolean;
import fatworm.type.FatType;

import java.util.*;
public class BNFList extends fatworm.expression.Expression{
	private ArrayList<Expression> andList;
	public BNFList() {
		andList = new ArrayList<Expression>();
	}
	public void add(Expression e) {
		andList.add(e);
	}
	public int size() {
		return andList.size();
	}
	public void remove(int ind) {
		andList.remove(ind);
	}
	@Override
	public FatType evaluate() throws Exception {
		for (int i = 0;i < andList.size();i++)
			if (!Log.checkFatBoolean(andList.get(i).evaluate()))
				return new FatBoolean(false);
		return new FatBoolean(true);
	}
	@Override
	public String getPrint(int old) throws Exception {
		String res = padding(old) + "BNFListExp(\n";
		for (int i = 0;i < andList.size();i++)
			res += andList.get(i).getPrint(old + 1);
		res = res + ")BNFListExp\n";
		return res;
	}
}
