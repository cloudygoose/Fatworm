package fatworm.logicplan;
import fatworm.scan.*;
import fatworm.driver.*;
public class FetchTablePlan extends Plan {
	private String tableName;
	fatworm.table.Table table;
	private fatworm.driver.Connection connection;
	public String getTableName() {
		return tableName;
	}
	public FetchTablePlan(String s, fatworm.driver.Connection c) {
		connection = c;
		tableName = s;
	}
	@Override
	public String getPrint(int old) {
		return padding(old) + "FetchTablePlan(" + tableName + ")\n";
	}
	public Scan getScan() {
		return new TableScan(tableName, connection);
	}
}
