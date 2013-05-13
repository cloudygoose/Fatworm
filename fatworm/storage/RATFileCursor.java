package fatworm.storage;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import fatworm.driver.Driver;
import fatworm.table.*;
//RandomAccessTemporalFile
public class RATFileCursor {
	int tupleId;
	int nowB;
	int nowP;
	int tupleLength;
	String fileName;
	FatBlock nowBlock;
	RandomAccessFile file;
	fatworm.driver.Connection connection;
	static Integer tmpAll = 0; 
	public RATFileCursor(int len, fatworm.driver.Connection c) {
		tupleLength = len;
		nowB = -1;
		nowP = -1;
		connection = c;
	}
	public void newFileInit() {
		fileName = "tmp" + tmpAll.toString();
		try {
			file = new RandomAccessFile(connection.folder + File.separator + fileName, "rw");
			nowB = 0;
			nowP = 0;
			nowBlock = null;
			tupleId = 0;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
	}
	public RATFileCursor getCursor(int newTId) {
		RATFileCursor cursor = new RATFileCursor(tupleLength, connection);
		cursor.tupleId = newTId;
		cursor.fileName = fileName;
		cursor.nowBlock = null;
		//begins at 0
		cursor.nowB = newTId / (Driver.BLOCKLENGTH / tupleLength);
		cursor.nowP = (newTId - nowB * (Driver.BLOCKLENGTH / tupleLength)) * tupleLength;
		return cursor;
	}
	public void insertTuple(byte[] b) {
		if (!nowBlock.inBuffer() || nowBlock == null)
			nowBlock = connection.bufferManager.getPage(new PageId(fileName, nowB));
		nowBlock.putBytes(b, nowP);
	}
	public byte[] getTypleArray() {
		if (!nowBlock.inBuffer() || nowBlock == null)
			nowBlock = connection.bufferManager.getPage(new PageId(fileName, nowB));
		return nowBlock.getBytes(nowP, tupleLength);
	}
	public void forward() {
		nowP += tupleLength;
		if (nowP >= Driver.BLOCKLENGTH) {
			nowB++;
			nowP = 0;
			nowBlock = null;
		}
		tupleId++;
	}
	public void backward() {
		nowP -= tupleLength;
		if (nowP < 0) {
			nowB--;
			nowP = (Driver.BLOCKLENGTH / tupleLength - 1) * tupleLength;
			nowBlock = null;
		}
		tupleId--;
	}
	public void close() {
		try {
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
