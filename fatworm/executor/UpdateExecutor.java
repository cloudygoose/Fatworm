package fatworm.executor;

import org.antlr.runtime.tree.CommonTree;

import fatworm.driver.Driver;
import fatworm.driver.Statement;
import fatworm.logicplan.Plan;
import fatworm.scan.Scan;
import fatworm.table.*;
import fatworm.expression.*;
import java.util.*;
import fatworm.log.*;
import fatworm.planner.*;
public class UpdateExecutor extends Executor {
	CommonTree tree;
	Expression condition;
	fatworm.driver.Statement statement;
	public UpdateExecutor(CommonTree t, Statement s) {
		tree = t;
		statement = s;
	}
	public void execute() throws Exception {
		String tableName = tree.getChild(0).getText();
		Table ta = statement.getConnection().getDatabaseMgr()
				.currentTableMgr.getTable(tableName);
		TableCursor table = ta.getTableCursor();
		tree.deleteChild(0);
		LogicPlanner planner = statement.getConnection().logicPlanner;
		ArrayList<UpdatePair> pairs = new ArrayList<UpdatePair>();
		while (tree.getChildCount() > 0 && !isWhere(tree.getChild(0))) {
			 pairs.add(new UpdatePair(planner.translateExpression((CommonTree)(tree.getChild(0).getChild(0))),
					 planner.translateExpression((CommonTree)(tree.getChild(0).getChild(1)))));
			 tree.deleteChild(0);
		}
		condition = null;
		if (tree.getChildCount() > 0) {
			condition = planner.translateExpression((CommonTree)(tree.getChild(0).getChild(0)));
		}
		table.open();
		while (table.next()) {
			Tuple t = table.getTuple();
			int pos = table.getLastPos();
			Tuple old = ta.getSchema().newTupleFromTuple(t);
			//statement.getConnection().tupleStack.push(t.copy()); I found that update does not use old values.
			statement.getConnection().tupleStack.push(t);
			boolean cc = true;
			if (condition != null)
				cc = Log.checkFatBoolean(condition.evaluate()); 
			if (cc) {
				for (int i = 0;i < pairs.size();i++) {
					t.set(pairs.get(i).getId(), pairs.get(i).getValue().evaluate());
				}
				table.update(t);
				ta.indexDealDeleteTuple(pos, old);
				ta.indexDealInsertTuple(pos, t);
			}
			statement.getConnection().tupleStack.pop();
		}
		table.close();
	}	
}
class UpdatePair {
	IdExpression id;
	Expression value;
	UpdatePair(Expression i, Expression v) {
		if (i instanceof IdExpression) {
			id = (IdExpression)i; value = v;
		} else
		throw new DevelopException("not Id Expression");
	}
	public IdExpression getId() {
		return id;
	}
	public Expression getValue() {
		return value;
	}
}
