package fatworm.executor;

import org.antlr.runtime.tree.CommonTree;

import fatworm.driver.Statement;
import fatworm.expression.Expression;
import fatworm.log.Log;
import fatworm.table.Column;
import fatworm.table.Table;
import fatworm.table.TableCursor;
import fatworm.table.Tuple;
import fatworm.table.TupleColumn;
import fatworm.type.*;

import java.nio.ByteBuffer;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.*;

public class InsertColumnsExecutor extends Executor {
	CommonTree tree;
	Statement statement;
	public InsertColumnsExecutor(CommonTree t, Statement s) {
		tree = t;
		statement = s;
	}
	public void execute() throws Exception {
		String tableName = tree.getChild(0).getText();
		TableCursor table = statement.getConnection().getDatabaseMgr()
				.currentTableMgr.getTable(tableName).getTableCursor();
		
		table.open();
		
		//Log.v(tableName);
		ArrayList<String> colNames = new ArrayList<String>();
		HashMap<String, FatType> hash = new HashMap<String, FatType>();
		int i = 1;
		while (!isValues(tree.getChild(i))) {
			colNames.add(tree.getChild(i).getText());
			//Log.v(tree.getChild(i).getText());
			i++;
		}
		CommonTree values = (CommonTree)(tree.getChild(i));
		for (int j = 0;j < values.getChildCount();j++) {
			CommonTree value = (CommonTree)(values.getChild(j));
			Column c = table.getSchema().getColumn(j);
			FatType v;
			if (isDefault(value)) {
				v = c.getDefault();
			} else
			{
				Expression exp = statement.getConnection().logicPlanner.
						translateExpression(value);
				v = exp.evaluate();
			}
			hash.put(colNames.get(j), c.getType().newInstance(v));
		}
		Tuple tuple = new Tuple();

		for (int j = 0;j < table.getSchema().getColumnNumber();j++) {
			Column c = table.getSchema().getColumn(j);
			FatType v;
			boolean isNull = false;
			if (hash.get(c.getName()) != null)
				v = hash.get(c.getName());
			else
			if (c.getDefault() != null)
				v = c.getDefault();
			else {
				if (c.getAutoIncrement()) {
					v = c.getAutoValueAfterInc();
				} else {
					if (c.getType() instanceof FatDateTime) {
						SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
						String s = dateFormat.format(new java.util.Date()).toString();
						Date date = new Date(dateFormat.parse(s).getTime()); 
						v = new FatDateTime(date);
					} else
					if (c.getType() instanceof FatTimeStamp) {
						SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
						String s = dateFormat.format(new java.util.Date()).toString();
						java.sql.Timestamp ss = new java.sql.Timestamp(dateFormat.parse(s).getTime()); 
						v = new FatTimeStamp(ss);
					}
					else {
						v = c.getType().newNullInstance();
						isNull = true;
					}
				}
			}
			tuple.addColumn(new TupleColumn(tableName, c.getName(), 
					c.getType().newInstance(v)));
		}
		table.insert(tuple);
		table.addTupleNumber(1);
/*
		Log.v("original tuple : " + tuple.getPrint());
		ByteBuffer bb = ByteBuffer.wrap(tuple.getByteArray());
		Tuple t2 = table.getSchema().getTupleFromByteBuffer(bb);
		Log.v("tuple now : " + t2.getPrint());
*/
		table.close();
	}
}
