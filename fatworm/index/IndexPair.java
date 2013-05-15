package fatworm.index;
import java.nio.ByteBuffer;

import fatworm.type.*;
public class IndexPair {
	FatType key;
	Integer fileOffset; 
	//for a interior node, it the blockId, for a leaf node, its the offset of byte in the file
	public IndexPair(FatType k, Integer f) {
		key = k;
		fileOffset = f;
	}
	public FatType getKey() {
		return key;
	}
	public Integer getFileOffset() {
		return fileOffset;
	}
	public int getByteArrayLength() {
		return key.getByteArrayLength() + 4;
	}
	public byte[] getByteArray() {
		byte[] b = new byte[getByteArrayLength()];
		ByteBuffer bb = ByteBuffer.wrap(b);
		key.storeIntoByteBuffer(bb);
		bb.putInt(fileOffset);
		return bb.array();
	}
	public void storeIntoByteBuffer(ByteBuffer bb) {
		key.storeIntoByteBuffer(bb);
		bb.putInt(fileOffset);
	}
	public IndexPair getInstanceFromByteBuffer(ByteBuffer bb) {
		IndexPair ip = new IndexPair(key.newInstanceFromByteBuffer(bb), bb.getInt());
		return ip;
	}
}
