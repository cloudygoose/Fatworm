package fatworm.driver;
import java.io.Serializable;
import java.util.*;

import fatworm.table.*;
public class DatabaseMgr implements Serializable{
	private static final long serialVersionUID = 3L;
	public TableMgr currentTableMgr = null;
	public HashMap<String, TableMgr> dbs;
	transient fatworm.driver.Connection connection;
	public void setConnection(fatworm.driver.Connection c) {
		connection = c;
		Iterator<String> iter = dbs.keySet().iterator();
		while (iter.hasNext()) {
			dbs.get(iter.next()).setConnection(connection);
		}
	}
	public DatabaseMgr(fatworm.driver.Connection c) {
		dbs = new HashMap<String, TableMgr>();
		connection = c;
	}
	public void close() {
		Iterator<String> iter = dbs.keySet().iterator();
		while (iter.hasNext()) {
			dbs.get(iter.next()).close();
		}
	}
	public void createDatabase(String s) {
		dbs.put(s, new TableMgr(s, connection));
	}
	public void useDatabase(String s) {
		currentTableMgr = dbs.get(s);
	}
	public void drop(String name) {
		TableMgr database = dbs.get(name);
		if (database != null)
			database.drop();
		dbs.remove(name);
	}
}
