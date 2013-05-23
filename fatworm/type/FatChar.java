package fatworm.type;
import fatworm.expression.*;
import fatworm.log.*;
import java.io.*;
import java.nio.ByteBuffer;
public class FatChar extends FatType implements Serializable {
	private static final long serialVersionUID = 11L;
	int length;
	String s;
	public FatChar() {
		super();
		length = -1;
		s = "";
	}
	public FatChar(String ss) {
		super();
		s = ss;
	}
	public FatChar(int l) {
		super();
		length = l;
		s = "";
		for (int i = 0;i < l;i++)
			s += " ";
	}
	public int getLength() {
		return length;
	}
	public String getString() {
		return s;
	}
	@Override
	public int getByteArrayLength() {
		//Log.v("length : " + length);
		return length + 1;
	}
	@Override
	public FatChar newNullInstance() {
		FatChar b = new FatChar();
		b.s = "";
		b.isNull = true;
		b.length = this.length;
		return b;
	}

	@Override
	public void storeIntoByteBuffer(ByteBuffer bb) {
		if (isNull) {
			bb.put((byte)1);
			byte[] b = new byte[length];
			bb.put(b);
			return;
		}
		else
			bb.put((byte)0);
		try {
			Log.assertTrue(s.length() == length);
		} catch (Exception e) {
			e.printStackTrace();
		}
		bb.put(s.getBytes());
	}
	@Override
	public FatType newInstanceFromByteBuffer(ByteBuffer bb) {
		FatChar c = new FatChar(length);
		if (bb.get() == 1)
			c.isNull = true;
		byte[] bs = new byte[length];
		bb.get(bs);
		c.s = new String(bs);
		return c;
	}
	@Override
	public FatChar newInstance(FatType ss) throws Exception {
		if (ss.isNull)
			return newNullInstance();
		FatChar fc = new FatChar(length);
		String s1;
		if (ss instanceof FatChar) {
			s1 = ((FatChar)ss).getString();
			//Log.v("seeingFatChar!!");
		}
		else throw new DevelopException();

		fc.s = s1;
		if (length == -1)
			return fc;
		int sl = s1.length();
		if (s1.length() < length)
			for (int j = 0;j < length - sl;j++)
				s1 = s1 + " ";
		if (sl > length)
			s1 = s1.substring(0, length);
		fc.s = s1;

		return fc;
	}
	@Override
	public FatChar newInstance(Expression ss) throws Exception {
		FatChar fc = new FatChar(length);
		String s1;
		if (ss instanceof StringLiteral) {
			s1 = ((StringLiteral)ss).getString();
		}
		else throw new DevelopException();

		fc.s = s1;
		if (length == -1)
			return fc;
		int sl = s1.length();
		if (s1.length() < length)
			for (int j = 0;j < length - sl;j++)
				s1 = s1 + " ";
		if (sl > length)
			s1 = s1.substring(0, length);
		fc.s = s1;
		return fc;
	}
	@Override
	public FatType newZeroInstance() {
		String s = "";
		for (int i = 0;i < length;i++)
			s += " ";
		FatChar c = new FatChar(s);
		c.length = length;
		return c;
	}
	@Override
	public String getPrint(int old) {
		return Log.padding(old) + "FatChar(" + length + "):" + s;
	}
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof FatChar))
			return false;
		return ((FatChar)o).s == s && ((FatChar)o).length == length;
	}
	@Override
	public int compareTo(Object o) {
		if (o instanceof FatChar)
			return Log.stripStringTail(s.toLowerCase()).compareTo(Log.stripStringTail(((FatChar)o).s.toLowerCase()));
		if (o instanceof FatVarChar)
			return Log.stripStringTail(s.toLowerCase()).compareTo(Log.stripStringTail(((FatVarChar)o).s.toLowerCase()));
		throw new DevelopException();
	}
}
