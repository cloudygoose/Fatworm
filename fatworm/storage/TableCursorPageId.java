package fatworm.storage;

import java.io.RandomAccessFile;
import java.io.Serializable;

import fatworm.driver.Driver;
import fatworm.log.Log;
import fatworm.table.Table;

public class TableCursorPageId extends PageId implements Serializable {
	private static final long serialVersionUID = 22L;
	int nowP;
	int markP;
	int markId;
	transient FatBlock nowBlock;
	public TableCursorPageId(String s, int i, Table t) {
		super(s, i, t);
		nowP = 0;
		markP = -1;
		markId = -1;
		nowBlock = null;
	}
	public int getPosition() {
		return nowP;
	}
	public PageId getPageId() {
		return new PageId(fileName, id, table);
	}
	public void setNullBlock() {
		nowBlock = null;
	}
	public void beforeFirst() {
		id = 0;
		nowP = 0;
		markP = -1;
		markId = -1;
		nowBlock = null;
	}
	public void mark() {
		markP = nowP;
		markId = id;
	}
	public byte getNullSymbol() {
		if (nowBlock == null || !nowBlock.inBuffer())
			nowBlock = table.getConnection().bufferManager.getPage(getPageId());
		byte[] ss = nowBlock.getBytes(nowP, 1);
		return ss[0];
	}
	public byte[] getTupleByteArray(int tupleLength) {
		if (nowBlock == null || !nowBlock.inBuffer())
			nowBlock = table.getConnection().bufferManager.getPage(getPageId());
		byte[] ss = nowBlock.getBytes(nowP + 1, tupleLength);
		/*
		if (tupleLength == 10)
			for (int i = 0;i < ss.length;i++)
				Log.v("byte[]!!!" + i + ":" + ss[i]);
		Log.v("");
		*/
		return ss;
	}
	public void forwardTuple(int tupleLength) {
		nowP += tupleLength + 1;
		if (nowP + tupleLength + 1 > Driver.BLOCKLENGTH) {
			nowP = 0;
			id++;
			nowBlock = null;
		}
	}
	@Override
	public String getPrint() {
		return "TableCursorPageId : " + fileName + " " + id;
	}
	public void putBytes(int offset, byte[] b) {
		if (nowBlock == null || !nowBlock.inBuffer()) {
			nowBlock = table.getConnection().
				bufferManager.getPage(getPageId()); 
		}
		nowBlock.putBytes(b, nowP + offset);
	}
	public void returnMark() {
		Log.assertTrue(markP != -1 && markId != -1);
		if (markId != id) {
			nowBlock = null;
		}
		nowP = markP; id = markId;
		markP = -1; markId = -1;
	}
	
}
