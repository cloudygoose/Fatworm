package fatworm.index;
import java.io.File;


import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.io.Serializable;

import fatworm.log.*;
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
		try {
			file = new RandomAccessFile(fileName, "rw");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		keyType = key;
		maxLevel = 1;
		rootBlock = 0;
		BPlusNode rootNode = new BPlusNode(keyType, this, 1, true, true);
		maxPointerNum = (Driver.BLOCKLENGTH - 8) / (key.getByteArrayLength() + 4);
		Log.v("maxPointerNum : " + maxPointerNum);
		rootNode.storeToFatBlock(); //initialize the file
		nextNewBlock = 1;
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
	public void logBPlus() {
		BPlusNode root = getRoot();
		root.LogBPlus();
	}
	public void insertPair(FatType key, Integer offset) {
		BPlusNode root = getRoot(); 
		IndexPair pair = new IndexPair(key, offset);
		BPlusAction todo = root.insertPair(pair);
		BPlusAction res = null;
		IndexPair rightPair = null;
		while (todo != null) {
			if (todo instanceof BPlusInsertAction) {
				rightPair = ((BPlusInsertAction)todo).insertP;
			}
			todo = todo.getNextAction();
		}
		
		if (rightPair == null)
			return;
		IndexPair leftPair = new IndexPair(root.pairs.get(0).getKey(), rootBlock);
		
		rootBlock = getNextNewBlockNumber();
		root = new BPlusNode(keyType, this, 1, true, true);
		root.doInsertAction(new BPlusInsertAction(leftPair), null);
		root.doInsertAction(new BPlusInsertAction(rightPair), null);
	}
	private BPlusNode getRoot() {
		return BPlusNode.getInstanceFromFatBlock(rootBlock, keyType, this, 1, true, true);
	}
}
