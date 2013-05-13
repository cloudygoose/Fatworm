package fatworm.storage;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

import fatworm.driver.Driver;
import fatworm.log.Log;

public class FatBlock {
	ByteBuffer bb;
	TimePageId pi;
	boolean inBuffer;
	boolean dirty;
	public FatBlock(byte[] bs, TimePageId p) {
		bb = ByteBuffer.wrap(bs);
		inBuffer = false;
		pi = p;
		dirty = false;
	}
	public boolean writeBack() {
		if (!dirty)
			return true;
		byte[] b = bb.array();
		RandomAccessFile file = pi.getFile();
		try {
			file.seek(pi.getId() * Driver.BLOCKLENGTH);
			file.write(b);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	public TimePageId getTimePageId() {
		return pi;
	}
	public boolean isDirty() {
		return dirty;
	}
	public void setDirty() {
		dirty = true;
	}
	public boolean inBuffer() {
		return inBuffer;
	}
	public void setInBuffer(boolean f) {
		inBuffer = f;
	}
	public byte[] getBytes(int offset, int len) {  
		byte[] b = new byte[len];
		bb.position(offset);
		bb.get(b);
		return b;
	}
	public byte[] getBytes() {
		return bb.array();
	}
	public void putBytes(byte[] b, int offset) {
		bb.position(offset);
		bb.put(b);
		dirty = true;
	}
}
