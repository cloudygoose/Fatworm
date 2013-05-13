package fatworm.driver;
import java.util.*;
import java.io.*;

import fatworm.log.*;
import fatworm.table.Table;
public class TableMgr implements Serializable{
	private static final long serialVersionUID = 4L;
	String name;
	HashMap<String, Table> tables;
	transient fatworm.driver.Connection connection;
	public TableMgr(String n, fatworm.driver.Connection c) {
		tables = new HashMap<String, Table>();
		name = n;
		connection = c;
	}
	public void setConnection(fatworm.driver.Connection c) {
		connection = c;
		Iterator<String> iter = tables.keySet().iterator();
		while (iter.hasNext()) {
			String s = iter.next();
			tables.get(s).setConnection(connection);
			tables.get(s).setFile();
		}
	}
	public void close() {
		Iterator<String> iter = tables.keySet().iterator();
		while (iter.hasNext()) {
			tables.get(iter.next()).close();
		}
	}
	public Table createTable(String n) throws Exception{
		if (tables.get(n) != null) {
			throw new DevelopException("creation : table already exist!!");
		}
		Table t = new Table(n, name, connection);
		tables.put(n.toLowerCase(), t);
		return t;
	}
	public Table getTable(String name) {
		return tables.get(name.toLowerCase());
	}
	public void drop() {
		Iterator<String> iter = tables.keySet().iterator();
		ArrayList<String> names = new ArrayList<String>();
		while (iter.hasNext()) {
			names.add(iter.next());
		}
		for (int i = 0;i < names.size();i++) {
			tables.get(names.get(i).toLowerCase()).drop();
			tables.remove(names.get(i).toLowerCase());
		}
	}
	public void drop(String name) {
		tables.get(name.toLowerCase()).drop();
		tables.remove(name.toLowerCase());
	}
}
