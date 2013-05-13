package fatworm.scan;
import fatworm.expression.Expression;
import fatworm.log.Log;
import fatworm.table.*;
import fatworm.type.*;
import fatworm.log.*;
public class SelectScan extends Scan {
	public Scan source;
	public Expression condition;
	fatworm.driver.Connection connection;
	Tuple nextT;
	public Scan getSource() {
		return source;
	}
	public Expression getCondition() {
		return condition;
	}
	public SelectScan(Scan s, Expression c, fatworm.driver.Connection c1) {
		connection = c1;
		source = s;
		condition = c;
	}
	@Override
	public Tuple generateExTuple() throws Exception {
		return source.generateExTuple();
	}
	@Override
	public void open() throws Exception {
		source.open();
		nextT = null;
	}
	@Override 
	public void beforeFirst() {
		source.beforeFirst();
		nextT = null;
	}
	@Override
	public boolean next() throws Exception {
		nextT = null;
		while (source.next()) {
			nextT = source.getTuple();
			connection.tupleStack.push(nextT.copy());
			FatType f = condition.evaluate();
			connection.tupleStack.pop();
			if (Log.checkFatBoolean(f))
				return true;
		}
		return false;
	}
	@Override
	public Tuple getTuple() {
		if (nextT != null)
			return nextT;
		else
			throw new DevelopException();
	}
	@Override
	public void close() {
		source.close();
	}
	@Override
	public String getPrint(int old) throws Exception {
		return padding(old) + "SelectScan(\n" + 
				padding(old + 1) + "SOURCE FROM\n" + 
				source.getPrint(old + 1) + 
				padding(old + 1) + "CONDITION ON\n" + 
				condition.getPrint(old + 1) + padding(old) + ")SelectScan\n";
	}
	public fatworm.driver.Connection getConnection() {
		return connection;
	}
}
