package fatworm.executor;

import org.antlr.runtime.tree.CommonTree;

import fatworm.driver.TableMgr;
import fatworm.planner.LogicPlanner;
import fatworm.table.Table;

public class DropDatabaseExecutor extends Executor {
	CommonTree tree;
	LogicPlanner trans;
	fatworm.driver.Statement statement;
	public DropDatabaseExecutor(CommonTree t, fatworm.driver.Statement s) {
		tree = t;
		statement = s;
		trans = statement.getConnection().logicPlanner;
	}
	public void execute() throws Exception{
		String name = tree.getChild(0).getText();
		statement.getConnection().getDatabaseMgr().drop(name);
	}
}
