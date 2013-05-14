package fatworm.executor;

import org.antlr.runtime.tree.CommonTree;

import fatworm.driver.TableMgr;
import fatworm.planner.LogicPlanner;
import fatworm.table.Table;
import fatworm.index.*;

public class CreateIndexExecutor extends Executor {
	CommonTree tree;
	LogicPlanner trans;
	fatworm.driver.Statement statement;
	public CreateIndexExecutor(CommonTree t, fatworm.driver.Statement s) {
		tree = t;
		statement = s;
		trans = statement.getConnection().logicPlanner;
	}
	public void execute() throws Exception{
		TableMgr tableMgr = statement.getConnection().
				getDatabaseMgr().currentTableMgr;
		String indexName = tree.getChild(0).getText();
		String tableName = tree.getChild(1).getText();
		String colName = tree.getChild(2).getText();
		FatIndex index = tableMgr.getTable(tableName).
				createIndex(indexName, colName);
	}
}
