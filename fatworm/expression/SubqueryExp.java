package fatworm.expression;
import fatworm.logicplan.*;
import fatworm.scan.*;
import fatworm.table.*;
import fatworm.log.*;
import fatworm.type.*;
public class SubqueryExp extends Expression {
	Plan plan;
	public SubqueryExp(Plan p) {
		plan = p;
	}
	public Plan getPlan() {
		return plan;
	}
	public SubqueryExp copy() {
		SubqueryExp exp = new SubqueryExp(plan);
		exp.setConnection(connection);
		return exp;
	}
	@Override
	public String getPrint(int old) {
		return padding(old) + "SubqueryExp()\n";
	}
	public FatType evaluate() throws Exception {
		Scan s = plan.getScan();
		s.open();
		if (!s.next())
			throw new DevelopException();
		Tuple t = s.getTuple();
		if (t.size() != 1)
			throw new DevelopException();
		FatType f = t.get(0).getValue();
		if (s.next())
			throw new DevelopException();
		s.close();
		return f;
	}
}
