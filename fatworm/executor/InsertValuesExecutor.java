package fatworm.executor;

import org.antlr.runtime.tree.CommonTree;
import fatworm.table.*;
import fatworm.log.Log;
import fatworm.type.*;
import fatworm.expression.*;

public class InsertValuesExecutor extends Executor {
	CommonTree tree;
	fatworm.driver.Statement statement; 
	public InsertValuesExecutor(CommonTree t, fatworm.driver.Statement s) {
		tree = t;
		statement = s;
	}
	public void execute() throws Exception {
		String tableName = tree.getChild(0).getText();
		Table ta = statement.getConnection().getDatabaseMgr()
				.currentTableMgr.getTable(tableName);
		TableCursor table = ta.getTableCursor();
		
		table.open();
		
		CommonTree values = (CommonTree)(tree.getChild(1));
		Tuple tuple = new Tuple();
		for (int i = 0;i < values.getChildCount();i++) {
			CommonTree value = (CommonTree)(values.getChild(i));
			Column c = table.getSchema().getColumn(i);
			FatType v = null;
			if (isDefault(value) || isNull(value)) {
				v = c.getDefault();
				if (v == null) {
					if (c.getAutoIncrement()) {
						v = c.getAutoValueAfterInc();
					} else
					v = c.getType().newNullInstance();
				}
//				if (isNull(value))
//					v = c.getType().newNullInstance();
			} else
			{
				Expression exp = statement.getConnection().logicPlanner.
						translateExpression(value);
				v = exp.evaluate();
			}
			if (c.getAutoIncrement()) {
				FatType kk = c.getAutoValue();
				if (kk.compareTo(v) < 0)
					c.setAutoValue(v);
			}
			//Log.v(v.getPrint(0));
			tuple.addColumn(new TupleColumn(tableName, c.getName(), 
					c.getType().newInstance(v)));
		}
		
		table.insert(tuple);
		ta.indexDealInsertTuple(table.getLastPos(), tuple);
		table.addTupleNumber(1);
		//table.next();
		//Log.v(table.getTuple().getPrint());
		table.close();
	}
}
