package fatworm.index;
import java.nio.ByteBuffer;

import fatworm.log.Log;
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
		//Log.v("bytebuffer pos : " + bb.position());
		IndexPair ip = new IndexPair(key.newInstanceFromByteBuffer(bb), bb.getInt());
		return ip;
	}
	@Override
	public boolean equals(Object o) {
		IndexPair ip = (IndexPair)o;
		return (key.compareTo(ip.key) == 0 && fileOffset.equals(ip.fileOffset));
	}
	public String getPrint() {
		return "ip : " + key.getPrint(0) + "," + fileOffset;
	}
}
