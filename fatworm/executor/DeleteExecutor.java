package fatworm.executor;

import org.antlr.runtime.tree.CommonTree;

import fatworm.driver.Statement;
import fatworm.log.Log;
import fatworm.logicplan.Plan;
import fatworm.scan.Scan;
import fatworm.table.*;
import fatworm.expression.*;
import fatworm.type.*;

public class DeleteExecutor extends Executor {
	CommonTree tree;
	Statement statement;
	public DeleteExecutor(CommonTree t, Statement s) {
		tree = t;
		statement = s;
	}
	public void execute() throws Exception {
		String tableName = tree.getChild(0).getText();
		TableCursor table = statement.getConnection().getDatabaseMgr()
				.currentTableMgr.getTable(tableName).getTableCursor();
		Expression condition = null;
		if (tree.getChildCount() > 1)
			condition = statement.getConnection().logicPlanner.translateExpression(
					(CommonTree)(tree.getChild(1).getChild(0)));
		table.open();
		while (table.next()) {
			Tuple t = table.getTuple();
			statement.getConnection().tupleStack.push(t.copy());
			FatType f;
			if (condition != null)
				f = condition.evaluate();
			else
				f = new FatBoolean(true);
			if (Log.checkFatBoolean(f))
				table.delete();
			statement.getConnection().tupleStack.pop();
		}
		table.close();
	}	
}
