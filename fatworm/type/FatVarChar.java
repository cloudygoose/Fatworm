package fatworm.type;
import fatworm.expression.*;
import fatworm.log.*;
import java.io.*;
import java.nio.ByteBuffer;
public class FatVarChar extends FatType implements Serializable {
	private static final long serialVersionUID = 19L;
	int length;
	String s;
	public FatVarChar(int l) {
		super();
		length = l;
	}
	public String getString() {
		return s;
	}
	@Override
	public int getByteArrayLength() {
		return length + 1 + 4;
	}
	@Override
	public void storeIntoByteBuffer(ByteBuffer bb) {
		if (isNull)
			bb.put((byte)1);
		else
			bb.put((byte)0);
		if (s == null)
			s = "";
		bb.putInt(s.length());
		bb.put(s.getBytes());
		if (length - s.length() > 0) {
			byte[] tmp = new byte[length - s.length()];
			bb.put(tmp);
		}
	}
	@Override
	public FatVarChar newInstanceFromByteBuffer(ByteBuffer bb) {
		FatVarChar fv = new FatVarChar(length);
		if (bb.get() == 1)
			fv.isNull = true;
		int len = bb.getInt();
		byte[] tm = new byte[len];
		bb.get(tm);
		fv.s = new String(tm);
		if (length > len) {
			byte[] tmp = new byte[length - len];
			bb.get(tmp);
		}
		return fv;
	}
	@Override
	public FatVarChar newInstance(Expression ss) throws DevelopException {
		FatVarChar fvc = new FatVarChar(length);
//		Log.v(ss.toString());
		if (ss instanceof StringLiteral) {
			fvc.s = ((StringLiteral)ss).getString();  
			if (fvc.s.length() > length)
				fvc.s = fvc.s.substring(0, length);
			return fvc;
		} else
		throw new DevelopException();
	}
	@Override
	public FatVarChar newNullInstance() {
		FatVarChar b = new FatVarChar(-1);
		b.isNull = true;
		b.s = "";
		return b;
	}

	@Override
	public FatVarChar newInstance(FatType ss) throws DevelopException {
		if (ss.isNull)
			return newNullInstance();
		FatVarChar fvc = new FatVarChar(length);
//		Log.v(ss.toString());
		if (ss instanceof FatChar) {
			fvc.s = ((FatChar)ss).getString();
		} else
		if (ss instanceof FatVarChar) {
			fvc.s = ((FatVarChar)ss).getString();
		} else
		throw new DevelopException();
		if (fvc.s.length() > length)
			fvc.s = fvc.s.substring(0, length);
		return fvc;
	}
	@Override
	public FatType newZeroInstance() {
		FatVarChar c = new FatVarChar(length);
		c.s = "";
		return c;
	}
	@Override
	public String getPrint(int old) {
		return Log.padding(old) + "FatVarChar(" + length + "):" + s;
	}
	@Override
	public int compareTo(Object o) {
		if (o instanceof FatVarChar)
			return s.toLowerCase().compareTo(((FatVarChar)o).s.toLowerCase());
		if (o instanceof FatChar)
			return s.toLowerCase().compareTo(((FatChar)o).s.toLowerCase());
		throw new DevelopException();
	}
}
