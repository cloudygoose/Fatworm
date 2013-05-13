package fatworm.scan;
import fatworm.table.*;
public class TableScan extends Scan {
	String name;
	TableCursor table;
	fatworm.driver.Connection connection;
	@Override
	public void open() {
		table = connection.getDatabaseMgr().currentTableMgr.getTable(name).getTableCursor();
		table.open();
	}
	@Override
	public Tuple generateExTuple() {
		Table ta = connection.getDatabaseMgr().currentTableMgr.getTable(name);
		Tuple t = new Tuple();
		for (int i = 0;i < ta.getSchema().getColumnNumber();i++) 
			t.addColumn(new TupleColumn(
					name, ta.getSchema().getColumn(i).getName(),
					ta.getSchema().getColumn(i).getType().newZeroInstance()));
		return t;
	}
	@Override
	public boolean next() {
		return table.next();
	}
	@Override
	public Tuple getTuple() {
		return table.getTuple();
	}
	@Override
	public void close() {
		table.close();
	}
	@Override
	public void beforeFirst() {
		table.beforeFirst();
	}
	public String getName() {
		return name;
	}

	public TableScan(String tab_name, fatworm.driver.Connection c) {
		name = tab_name;
		connection = c;
		table = null;
	}
	@Override
	public String getPrint(int old) {
		return padding(old) + "TableScan(\n" + 
				padding(old + 1) + name + "\n" +
				padding(old) + ")TableScan\n";
	}
	public fatworm.driver.Connection getConnection() {
		return connection;
	}
}
