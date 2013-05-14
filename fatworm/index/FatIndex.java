package fatworm.index;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.io.Serializable;

import fatworm.driver.Connection;
import fatworm.table.*;
public class FatIndex implements Serializable {
	private static final long serialVersionUID = 26L;
	Table table;
	String columnName;
	String fileName;
	String indexName;
	transient RandomAccessFile file;
	transient fatworm.driver.Connection connection;
	public String getColumnName() {
		return columnName;
	}
	public FatIndex(String indexN, Table t, String col, fatworm.driver.Connection c) {
		table = t;
		columnName = col;
		connection = c;
		indexName = indexN;
		fileName = table.getFileName() + "_" + columnName;
		try {
			file = new RandomAccessFile(fileName, "rw");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	public void setFile() {
		try {
			file = new RandomAccessFile(fileName, "rw");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	public void setConnection(fatworm.driver.Connection c) {
		connection = c;
	}
}
