package fatworm.logicplan;
import fatworm.expression.*;
import fatworm.scan.*;

public class GroupPlan extends Plan {
	fatworm.driver.Connection connection;
	Plan source;
	ExpList expList;
	IdExpression idExp;
	Expression having;
	public Plan getSource() {
		return source;
	}
	public ExpList getExpList() {
		return expList;
	}
	public IdExpression getIdExp() {
		return idExp;
	}
	public Expression getHaving() {
		return having;
	}
	public GroupPlan(ExpList list, Plan s, IdExpression id, Expression h, fatworm.driver.Connection c) {
		connection = c;
		source = s;
		expList = list;
		idExp = id;
		s.setFather(this);
		having = h;
	}
	@Override
	public String getPrint(int old) throws Exception {
		String result = padding(old) + "GroupPlan(\n" + 
				expList.getPrint(old + 1) + 
				padding(old + 1) + "GROUP BY\n" + 
				idExp.getPrint(old + 1) +
				padding(old + 1) + "SOURCE FROM\n" + source.getPrint(old + 1); 
		if (having != null)
			result += padding(old + 1) + "HAVING:\n" +
				having.getPrint(old + 1);
		result += padding(old) + ")GroupPlan\n";
		return result;
	}
	@Override
	public Scan getScan() throws Exception {
		return new GroupScan(
				source.getScan(),
				expList, idExp, having, connection);
	}
}
