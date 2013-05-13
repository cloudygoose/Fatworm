package fatworm.executor;


import org.antlr.runtime.tree.CommonTree;

import fatworm.driver.Statement;
import fatworm.logicplan.*;
import fatworm.log.*;
import fatworm.scan.*;
import fatworm.table.*;

public class InsertQueryExecutor extends Executor {
	CommonTree tree;
	Statement statement;
	public InsertQueryExecutor(CommonTree t, Statement s) {
		tree = t;
		statement = s;
	}
	public void execute() throws Exception {
		String tableName = tree.getChild(0).getText();
		TableCursor table = statement.getConnection().getDatabaseMgr()
				.currentTableMgr.getTable(tableName).getTableCursor();
		Plan plan = statement.getConnection().logicPlanner.translate(tree.getChild(1));
		Scan scan = plan.getScan();
		scan.open();
		table.open();
		int added = 0;
		while (scan.next()) {
			Tuple old = scan.getTuple();
			table.insert(table.getSchema().newTupleFromTuple(old));
			added++;
		}
		table.addTupleNumber(added);
		table.close();
		scan.close();
	}
}
