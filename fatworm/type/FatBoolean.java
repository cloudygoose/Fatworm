package fatworm.type;
import java.io.Serializable;
import java.nio.ByteBuffer;

import fatworm.expression.*;
import fatworm.log.*;
public class FatBoolean extends FatType implements Serializable {
	private static final long serialVersionUID = 10L;
	boolean b;
	public FatBoolean() {
		super();
	}
	public FatBoolean(boolean bb) {
		super();
		b = bb;
	}
	@Override
	public void storeIntoByteBuffer(ByteBuffer bb) {
		if (isNull)
			bb.put((byte)1);
		else
			bb.put((byte)0);
		byte[] bt = new byte[1];
		if (this.b == true)
			bt[0] = 1;
		else bt[0] = 0;
		bb.put(bt);
	}
	@Override
	public int getByteArrayLength() {
		return 1 + 1;
	}
	@Override
	public FatBoolean newNullInstance() {
		FatBoolean b = new FatBoolean();
		b.isNull = true;
		return b;
	}

	@Override
	public FatType newInstanceFromByteBuffer(ByteBuffer bb) {
		FatBoolean f = new FatBoolean();
		if (bb.get() == 1)
			f.isNull = true;
		if (bb.get() == 0)
			f.b = false;
		else
			f.b = true;;
		return f;
	}
	public FatBoolean newInstance(FatType b) throws Exception {
		if (b instanceof FatBoolean) {
			FatBoolean bb = new FatBoolean();
			bb.isNull = b.isNull;
			bb.b = ((FatBoolean)b).getBool();
			return bb;
		} else
		throw new DevelopException();
	}
	@Override
	public FatBoolean newInstance(Expression b) throws Exception {
		if (b instanceof BooleanLiteral) {
			FatBoolean bb = new FatBoolean();
			bb.b = ((BooleanLiteral)b).getBool();
			return bb; 
		} else
		throw new DevelopException();
	}
	@Override
	public FatBoolean newZeroInstance() {
		return new FatBoolean(false);
	}
	public boolean getBool() {
		return this.b;
	}
	@Override
	public FatBoolean computeAnd(FatType t) {
		if (t instanceof FatBoolean) {
			boolean bb = ((FatBoolean)t).b;
			return new FatBoolean(b & bb);
		} else
		throw new DevelopException();
	}
	@Override
	public FatBoolean computeOr(FatType t) {
		if (t instanceof FatBoolean) {
			boolean bb = ((FatBoolean)t).b;
			return new FatBoolean(b | bb);
		} else
		throw new DevelopException();
	}

	@Override
	public String getPrint(int old) {
		return Log.padding(old) + "FatBoolean:" + b;
	}
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof FatBoolean))
			return false;
		return ((FatBoolean)o).b == b;
	}
	@Override
	public int compareTo(Object o) {
		if (!(o instanceof FatBoolean))
			throw new DevelopException();
		if (((FatBoolean)o).b == b)
			return 0;
		if (((FatBoolean)o).b == false)
			return 1;
		return -1;
	}
}
