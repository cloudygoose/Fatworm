package fatworm.logicplan;

import fatworm.expression.Expression;
import fatworm.scan.*;

public class SelectPlan extends Plan{
	fatworm.driver.Connection connection;
	Plan source;
	Expression exp;
	public Plan getSource() {
		return source;
	}
	public Expression getCondition() {
		return exp;
	}
	/*
	 * construct from a FromClause
	 */
	public SelectPlan(Plan s, Expression e, fatworm.driver.Connection c) {
		connection = c;
		s.setFather(this);
		source = s; exp = e;
	}
	@Override
	public String getPrint(int old) throws Exception {
		return padding(old) + "SelectPlan(\n" + 
				padding(old + 1) + "SOURCE FROM\n" + 
				source.getPrint(old + 1) + 
				padding(old + 1) + "CONDITION ON\n" + 
				exp.getPrint(old + 1) + padding(old) + ")SelectPlan\n";
	}
	@Override
	public Scan getScan() throws Exception {
		return new SelectScan(source.getScan(), exp, connection);
	}
}
