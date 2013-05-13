package fatworm.executor;


import org.antlr.runtime.tree.CommonTree;

import fatworm.driver.Connection;
import fatworm.log.Log;

public class CreateDatabaseExecutor {
	CommonTree tree;
	fatworm.driver.Statement statement; 
	public CreateDatabaseExecutor(CommonTree t, fatworm.driver.Statement s) {
		tree = t;
		statement = s;
	}
	public void execute() {
		String name = tree.getChild(0).getText();
		statement.getConnection().getDatabaseMgr()
			.createDatabase(name);
	}
}
