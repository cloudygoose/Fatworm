package fatworm.logicplan;

import fatworm.expression.Expression;
import fatworm.scan.*;

public class SelectPlan extends Plan{
	Plan source;
	Expression exp;
	boolean hasBeenPushDowned;
	public boolean getHasBeenPushDowned() {
		return hasBeenPushDowned;
	}
	public Plan getSource() {
		return source;
	}
	public Expression getExp() {
		return exp;
	}
	public void setSource(Plan p) {
		source = p;
	}
	public Expression getCondition() {
		return exp;
	}
	public void setBeenPushDown() {
		hasBeenPushDowned = true;
	}
	/*
	 * construct from a FromClause
	 */
	public SelectPlan(Plan s, Expression e, fatworm.driver.Connection c) {
		connection = c;
		source = s; exp = e;
		hasBeenPushDowned = false;
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
