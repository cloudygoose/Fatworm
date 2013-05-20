package fatworm.table;

import fatworm.index.*;


import java.util.*;

import java.io.*;
import fatworm.index.*;
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
	TreeMap<String, FatIndex> indexs;
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
		indexs = new TreeMap<String, FatIndex>();
	}
	public String getFileName() {
		return fileName;
	}
	public void setFile() {
		try {
			file = new RandomAccessFile(fileName, "rw");
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
		Iterator<FatIndex> iter = indexs.values().iterator();
		while (iter.hasNext()) {
			FatIndex index = iter.next();
			index.setConnection(connection);
			index.setFile();
		}
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
		Iterator<FatIndex> indexIter = indexs.values().iterator();
		while (indexIter.hasNext()) {
			indexIter.next().drop();
		}
		try {
			file.close();
			File newFile = new File(fileName);
			newFile.delete();
			Log.v(fileName + " file closed!!!!(by drop)");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public FatIndex createIndex(String indexName, String colName) {
		int k;
		for (k = 0;k < schema.getColumnNumber();k++)
			if (schema.getColumn(k).getName().equals(colName))
				break;
		FatIndex index = new FatIndex(indexName, this, colName, schema.getColumn(k).getType(), connection);
		Log.assertTrue(indexs.get(colName) == null);
		indexs.put(colName, index);
		return index;
	}
	public TableCursorPageId getStartTableCursorPageId() {
		return new TableCursorPageId(fileName, 0, this);
	}
	public TableCursor getTableCursor() {
		return new RealTableCursor(name, records, schema, this);
	}
}
