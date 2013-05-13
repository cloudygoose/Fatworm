package fatworm.scan;
import fatworm.log.Log;
import fatworm.table.*;
public class AliasScan extends Scan {
	fatworm.driver.Connection connection;
	Scan source;
	String alias;
	Tuple nextT;
	public AliasScan(Scan s, String a, fatworm.driver.Connection c) {
		connection = c;
		source = s;
		alias = a;
	}
	public void open() throws Exception {
		source.open();
		nextT = null;
	}
	@Override 
	public Tuple generateExTuple() throws Exception {
		Tuple t = source.generateExTuple();
		for (int i = 0;i < t.size();i++)
			t.get(i).setTableName(alias);
		return t;
	}
	@Override
	public boolean next() throws Exception {
		nextT = null;
		return source.next();
	}
	@Override
	public Tuple getTuple() throws Exception{
		if (nextT != null)
			return nextT;
		nextT = source.getTuple().copy();
		for (int i = 0;i < nextT.size();i++)
			nextT.get(i).setTableName(alias);
		return nextT;
	}
	@Override
	public void beforeFirst() {
		source.beforeFirst();
		nextT = null;
	}
	@Override
	public void close() {
		source.close();
	}
	@Override
	public String getPrint(int old) throws Exception {
		return padding(old) + "AliasScan(\n" +
			padding(old + 1) + "Alias to : " + alias + "\n" +
			source.getPrint(old + 1) + 
			padding(old) + ")AliasScan\n";
	}
	public Scan getSource() {
		return source;
	}
	public String getAlias() {
		return alias;
	}
	public fatworm.driver.Connection getConnection() {
		return connection;
	}
}
