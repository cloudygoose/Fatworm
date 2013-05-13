package fatworm.type;
import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.io.*;
import fatworm.log.*;
public class FatTimeStamp extends FatType implements Serializable {
	private static final long serialVersionUID = 17L;
	Timestamp timeStamp;
	public FatTimeStamp() {
	}
	public FatTimeStamp(java.sql.Timestamp d) {
		timeStamp = d;
	}
	public Timestamp getTimeStamp() {
		return timeStamp;
	}
	public FatTimeStamp newInstance() {
		return new FatTimeStamp();
	}
	@Override 
	public int getByteArrayLength() {
		return 8 + 1;
	}
	@Override
	public void storeIntoByteBuffer(ByteBuffer bb) {
		if (isNull)
			bb.put((byte)1);
		else
			bb.put((byte)0);
		bb.putLong(timeStamp.getTime());
	}
	@Override
	public FatTimeStamp newInstanceFromByteBuffer(ByteBuffer bb) {
		FatTimeStamp fs = new FatTimeStamp();
		if (bb.get() == 1)
			fs.setNull();
		fs.timeStamp = new Timestamp(bb.getLong());
		return fs;
	}
	@Override
	public FatTimeStamp newInstance(FatType f) {
		if (f.isNull) 
			return newNullInstance();
		if (f instanceof FatTimeStamp) {
			return (FatTimeStamp)f;
		} else
		throw new DevelopException();
	}
	@Override
	public FatTimeStamp newNullInstance() {
		FatTimeStamp b = newZeroInstance();
		b.isNull = true;
		return b;
	}
	@Override
	public FatTimeStamp newZeroInstance() {
		return new FatTimeStamp(new java.sql.Timestamp(0));
	}
	@Override
	public int compareTo(Object t) {
		if (t instanceof FatTimeStamp) {
			return timeStamp.compareTo(((FatTimeStamp)t).getTimeStamp());
		} else
		throw new DevelopException();
	}
}
