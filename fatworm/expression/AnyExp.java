package fatworm.expression;
import fatworm.log.DevelopException;
import fatworm.logicplan.*;
import fatworm.scan.Scan;
import fatworm.table.Tuple;
import fatworm.type.FatBoolean;
import fatworm.type.FatType;
public class AnyExp extends Expression {
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
	public AnyExp(Expression val, String op, Plan s) {
		value = val;
		cop = op;
		source = s;
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
				if (cop.equals(">")) {
					scan.close();
					return new FatBoolean(true); }
				if (cop.equals("<>")) {
					scan.close();
					return new FatBoolean(true);
				}
			} else
			if (res == 0) {
				if (cop.equals("=")) {
					scan.close();
					return new FatBoolean(true); 
				}
			} else
			{
				if (cop.equals("<")) {
					scan.close();
					return new FatBoolean(true); }
				if (cop.equals("<>")) {
					scan.close();
					return new FatBoolean(true);
				}
			}
		}
		scan.close();
		if (cop.equals("<>"))
			return new FatBoolean(true);
		return new FatBoolean(false);
	}
	@Override
	public String getPrint(int old) throws Exception {
		return padding(old) + "AnyExp(\n" +
				padding(old + 1) + "VALUE:\n" +
				value.getPrint(old + 1) +
				padding(old + 1) + "COP:" + cop + "\n" +
				padding(old + 1) + "SOURCE:\n" +
				source.getPrint(old + 1) + 
				padding(old) + ")AnyExp\n";
	}
}
