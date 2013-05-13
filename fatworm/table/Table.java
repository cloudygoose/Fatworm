package fatworm.table;
import java.util.*;

import java.io.*;
import fatworm.storage.*;
import fatworm.log.Log;
import fatworm.type.*;
import fatworm.log.*;
public class Table implements Serializable {
	private static final long serialVersionUID = 5L;
	TableCursorPageId insertCursor;
	Schema schema;
	String dbName;
	String name;
	String fileName;
	transient RandomAccessFile file;
	int tuplesNum;	//exact valid tuple number
	transient ArrayList<Tuple> records;
	transient fatworm.driver.Connection connection;
	public Table(String n, String db, fatworm.driver.Connection c) {
		name = n;
		dbName = db;
		connection = c;
		String ss = connection.folder + File.separator + dbName + "_" + name;
		fileName = ss;
		try {
			file = new RandomAccessFile(ss, "rw");
			//Log.v(ss);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		insertCursor = new TableCursorPageId(ss, 0, this);
		schema = new Schema(name);
		records = new ArrayList<Tuple>();
		tuplesNum = 0;
	}
	public void setFile() {
		try {
			String ss = connection.folder + File.separator + dbName + "_" + name;
			file = new RandomAccessFile(ss, "rw");
			insertCursor.setNullBlock();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public RandomAccessFile getFile() {
		return file;
	}
	public String getName() {
		return name;
	}
	public void setConnection(fatworm.driver.Connection c) {
		connection = c;
		setFile();
	}
	public fatworm.driver.Connection getConnection() {
		return connection;
	}
	public void addTuple(int num) {
		tuplesNum += num;
	}
	public void deleteTuple() {
		tuplesNum--;
	}
	public void addColumn(Column column) {
		schema.addColumn(column);
	}
	public Schema getSchema() {
		return schema; 
	}
	public void close() {
		try {
			file.close();
			Log.v(fileName + " file closed!!!!(by close)");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void drop() {
		connection.bufferManager.dumpAll(fileName);
		try {
			file.close();
			Log.v(fileName + " file closed!!!!(by drop)");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public TableCursorPageId getStartTableCursorPageId() {
		return new TableCursorPageId(fileName, 0, this);
	}
	public TableCursor getTableCursor() {
		return new RealTableCursor(name, records, schema, this);
	}
}
