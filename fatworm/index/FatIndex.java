package fatworm.index;
import java.io.File;

import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.io.Serializable;

import fatworm.type.*;
import fatworm.driver.Connection;
import fatworm.driver.Driver;
import fatworm.table.*;
public class FatIndex implements Serializable {
	private static final long serialVersionUID = 26L;
	Table table;
	String columnName;
	String fileName;
	String indexName;
	int maxLevel;
	int maxPointerNum;
	FatType keyType;
	//root has the maxLevel, the leaf has level 1
	int nextNewBlock;
	int rootBlock;
	transient RandomAccessFile file;
	transient fatworm.driver.Connection connection;
	public String getColumnName() {
		return columnName;
	}
	public int getMaxLevel() {
		return maxLevel;
	}
	public int getRootBlockNumber() {
		return rootBlock;
	}
	public void setNewRootBlockNumber(int r) {
		rootBlock = r;
	}
	public int getNextNewBlockNumber() {
		return nextNewBlock++;
	}
	public FatIndex(String indexN, Table t, String col, FatType key, fatworm.driver.Connection c) {
		table = t;
		columnName = col;
		connection = c;
		indexName = indexN;
		fileName = table.getFileName() + "_" + columnName;
		maxLevel = 1;
		rootBlock = 0;
		nextNewBlock = 0;
		maxPointerNum = (Driver.BLOCKLENGTH - 8) / (key.getByteArrayLength() + 4);
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
