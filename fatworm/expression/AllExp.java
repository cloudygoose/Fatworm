package fatworm.expression;
import java.lang.reflect.Method;
import fatworm.table.*;

import fatworm.logicplan.*;
import fatworm.scan.Scan;
import fatworm.type.*;
import fatworm.scan.*;
import fatworm.log.*;

public class AllExp extends Expression {
	Expression value;
	String cop;
	Plan source;
	public Plan getSource() {
		return source;
	}
	public String getCop() {
		return cop;
	}
	public Expression getValue() {
		return value;
	}
	public AllExp(Expression val, String op, Plan s) {
		value = val;
		cop = op;
		source = s;
	}
	public AllExp copy() {
		AllExp exp = new AllExp(value.copy(), cop, source);
		exp.setConnection(connection);
		return exp;
	}
	@Override
	public FatType evaluate() throws Exception {
		FatType fo = value.evaluate();
		Scan scan = source.getScan();
		scan.open();
		while (scan.next()) {
			Tuple tuple = scan.getTuple();
			if (tuple.size() > 1) 
				throw new DevelopException();
			FatType ne = tuple.get(0).getValue();
			int res = fo.compareTo(ne);
			if (res > 0) {
				if (cop.equals("<") || cop.equals("<=") || cop.equals("=")) {
					scan.close();
					return new FatBoolean(false); }
			} else
			if (res == 0) {
				if (cop.equals(">") || cop.equals("<") || cop.equals("<>")) {
					scan.close();
					return new FatBoolean(false); }
			} else
			{
				if (cop.equals(">") || cop.equals(">=") || cop.equals("=")) {
					scan.close();
					return new FatBoolean(false); }
			}
		}
		scan.close();
		return new FatBoolean(true);
	}
	@Override
	public String getPrint(int old) throws Exception {
		return padding(old) + "AllExp(\n" +
				padding(old + 1) + "VALUE:\n" +
				value.getPrint(old + 1) +
				padding(old + 1) + "COP:" + cop + "\n" +
				padding(old + 1) + "SOURCE:\n" +
				source.getPrint(old + 1) +
				padding(old) + ")AllExp\n";
	}
}
