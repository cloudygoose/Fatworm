package fatworm.index;

import java.io.File;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Serializable;

import fatworm.log.*;
import fatworm.type.*;
import fatworm.driver.Connection;
import fatworm.driver.Driver;
import fatworm.table.*;
import fatworm.storage.*;
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
		maxLevel++;
		root = new BPlusNode(keyType, this, 1, true, true);
		rootBlock = root.blockNum;
		root.doInsertAction(new BPlusInsertAction(leftPair), null);
		root.doInsertAction(new BPlusInsertAction(rightPair), null);
	}
	public void deletePair(FatType key, Integer offset) {
		BPlusNode root = getRoot();
		IndexPair pair = new IndexPair(key, offset);
		BPlusAction toDo = root.deletePair(pair);
		if (toDo != null && toDo instanceof BPlusNotMeAction)
			throw new DevelopException("delete not exist");
		if (root.pairs.size() == 1 && this.maxLevel > 1) {
			int newRoot = root.pairs.get(0).getFileOffset();
			//connection.bufferManager.dumpPageId(new PageId(fileName, root.blockNum, file));
			rootBlock = newRoot;
			maxLevel--;
		}
	}
	public void close() {
		connection.bufferManager.dumpAll(fileName);
		try {
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	//set to be only called by fatworm.table.drop
	public void drop() {
		connection.bufferManager.dumpAll(fileName);
		try {
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		File f = new File(fileName);
		f.delete();
		//Log.v("!!!indexDelete" + fileName);
	}
	private BPlusNode getRoot() {
		return BPlusNode.getInstanceFromFatBlock(rootBlock, keyType, this, 1, true, true);
	}
}
