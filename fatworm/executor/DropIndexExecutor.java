package fatworm.executor;

import org.antlr.runtime.tree.CommonTree;

import fatworm.driver.Statement;

public class DropIndexExecutor extends Executor {
	CommonTree tree;
	Statement statement;
	public DropIndexExecutor(CommonTree t, Statement s) {
		tree = t;
		statement = s;
	}
	public void execute() throws Exception {
		//leave the drop job to the tables
	}
}
