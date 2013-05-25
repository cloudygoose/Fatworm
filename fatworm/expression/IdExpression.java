package fatworm.expression;

import fatworm.func.FuncPair;
import fatworm.log.DevelopException;
import fatworm.log.Log;
import fatworm.table.*;
import fatworm.type.*;
public class IdExpression extends Expression {
	private String tableName, columnName;
	public IdExpression(String t, String c) {
		tableName = t;
		if (t == null)
			tableName = "";
		columnName = c;
		if (c == null)
			columnName = "";
	}
	public String getTableName() {
		return tableName;
	}
	public String getColumnName() {
		return columnName;
	}
	public void setColumnName(String s) {
		columnName = s;
	}
	public IdExpression copy() {
		IdExpression exp = new IdExpression(tableName, columnName);
		exp.setConnection(connection);
		return exp;
	}
	@Override 
	public FatType evaluate() throws Exception {
		//Log.v("tupleStack");
		//for (int i = connection.tupleStack.size() - 1;i >= 0;i--) {
		//	Log.v(connection.tupleStack.get(i).getPrint());
		//}
		
		
		for (int i = connection.tupleStack.size() - 1;i >= 0;i--) {
			Tuple tuple = connection.tupleStack.get(i);
			FatType f = tuple.getValueFromIdStrong(this);
			if (f != null) { 
				f.assocColumnName = columnName;
				f.assocTableName = tableName;
				/*
				if (tableName.equals("C1") && columnName.equals("type"))
					Log.v("strongC1.type : " + f.getPrint(0));
				if (tableName.equals("C2") && columnName.equals("type"))
					Log.v("strongC2.type : " + f.getPrint(0));
				if (tableName.equals("C1") && columnName.equals("country"))
					Log.v("strongC1.country : " + f.getPrint(0));
				if (tableName.equals("C2") && columnName.equals("country"))
					Log.v("strongC2.country : " + f.getPrint(0));
				*/
				return f;
			}
			
			//fuck
			f = tuple.getValueFromIdWeak(this);
			if (f != null) { 
				f.assocColumnName = columnName;
				f.assocTableName = tableName;
				return f;
			}
		}
		for (int i = connection.tupleStack.size() - 1;i >= 0;i--) {
			Tuple tuple = connection.tupleStack.get(i);
			FatType f = tuple.getValueFromIdWeak(this);
			if (f != null) { 
				f.assocColumnName = columnName;
				f.assocTableName = tableName;
				return f;
			}
		}
		throw new DevelopException("Id can't find in stack");
	}
	@Override
	public String getPrint(int old) {
		String result = padding(old) + "ID:";
		if (tableName != "")
			result += tableName + ".";
		result += columnName + "\n";
		return result;
	}
	@Override
	public boolean weakEquals(Object o) {
		if (o instanceof IdExpression) {
			IdExpression i = (IdExpression)o;
			return ((this.tableName.equals("") || i.tableName.equals("")) 
					&& Log.stringNameEqual(columnName, i.columnName));
		} else
		throw new DevelopException();
	}
	@Override
	public boolean strongEquals(Object o) {
		if (o instanceof IdExpression) {
			IdExpression i = (IdExpression)o;
			return Log.stringNameEqual(columnName, i.columnName) && 
					Log.stringNameEqual(tableName, i.tableName);
		} else {
			Log.v(o.toString());
			throw new DevelopException();
		}
	}
}
