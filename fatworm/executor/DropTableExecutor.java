package fatworm.executor;

import org.antlr.runtime.tree.CommonTree;

import fatworm.planner.LogicPlanner;

public class DropTableExecutor extends Executor {
	CommonTree tree;
	LogicPlanner trans;
	fatworm.driver.Statement statement;
	public DropTableExecutor(CommonTree t, fatworm.driver.Statement s) {
		tree = t;
		statement = s;
		trans = statement.getConnection().logicPlanner;
	}
	public void execute() throws Exception{
		String name = tree.getChild(0).getText();
		statement.getConnection().getDatabaseMgr().currentTableMgr.drop(name);
	}
}
