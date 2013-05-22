package fatworm.expression;
import fatworm.logicplan.*;
import fatworm.scan.Scan;
import fatworm.table.*;
import fatworm.type.*;
public class InExp extends Expression {
	ExpList expList;
	Plan source;
	public ExpList getExpList() {
		return expList;
	}
	public Plan getSource() {
		return source;
	}
	public InExp(ExpList list, Plan s) {
		source = s;
		expList = list;
	}
	public InExp copy() {
		InExp exp = new InExp(expList.copy(), source);
		exp.setConnection(connection);
		return exp;
	}
	@Override
	public FatType evaluate() throws Exception {
		Tuple tuple = expList.evaluate();
		Scan scan = source.getScan();
		scan.open();
		boolean inn = false;
		while (scan.next())
			if (scan.getTuple().testValueEqual(tuple)) {
				inn = true;
				break;
			}
		scan.close();
		return new FatBoolean(inn);
	}
	@Override
	public String getPrint(int old) throws Exception {
		return padding(old) + "InExp(\n" +
				padding(old + 1) + "EXPLIST:\n" +
				expList.getPrint(old + 1) +
				padding(old + 1) + "SOURCE:\n" +
				source.getPrint(old + 1) +
				padding(old) + ")InExp\n";
	}
}
