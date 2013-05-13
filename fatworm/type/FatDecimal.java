package fatworm.type;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.ByteBuffer;
import java.io.*;

import fatworm.expression.*;
import fatworm.log.*;
public class FatDecimal extends FatType implements Serializable {
	private static final long serialVersionUID = 13L;
	public int serialLength = 20;
	int p1, p2;
	public BigDecimal number;
	public FatDecimal() {
		super();
		p1 = -1; p2 = -1;
	}
	public FatDecimal(int a1, int a2) {
		super();
		p1 = a1; p2 = a2;
		while (serialLength < a1 + a2 + 5) {
			serialLength += 10;
		}
	}
	public FatDecimal(BigDecimal num) {
		super();
		p1 = -1; p2 = -1;
		number = num;
	}
	public BigDecimal getNumber() {
		return number;
	}
	@Override
	public FatDecimal newZeroInstance() {
		return new FatDecimal(new BigDecimal(0));
	}
	@Override
	public FatDecimal newMinInstance() {
		return new FatDecimal(new BigDecimal(-100000000));
	}
	@Override
	public FatDecimal newMaxInstance() {
		return new FatDecimal(new BigDecimal(100000000));
	}
	@Override
	public FatDecimal newInstance(FatType num) throws DevelopException {
		if (num.isNull())
			return newNullInstance();
		FatDecimal fd = new FatDecimal(p1, p2);
		if (num instanceof FatDecimal) {
			fd.number = ((FatDecimal)num).getNumber();
		} else
		if (num instanceof FatFloat) {
			fd.number = ((FatFloat)num).getNumber();
		} else
		if (num instanceof FatInteger) {
			fd.number = new BigDecimal(((FatInteger)num).getNumber());
		} else
		throw new DevelopException();
 		return fd;
	}
	@Override
	public void storeIntoByteBuffer(ByteBuffer bb) {
		String s = number.toPlainString();
		if (s.length() > serialLength)
			s = s.substring(0, serialLength);
		byte[] bs = s.getBytes();
		int len = bs.length;
		bb.putInt(len);
		bb.put(bs);
		if (serialLength - len > 0) {
			byte[] tmp = new byte[serialLength - len];
			bb.put(tmp);
		}
	}
	@Override
	public int getByteArrayLength() {
		return serialLength + 4 + 1;
	}
	@Override
	public FatType newInstanceFromByteBuffer(ByteBuffer bb) {
		FatDecimal f = new FatDecimal(p1, p2);
		if (bb.get() == 1)
			f.isNull = true;
		int len = bb.getInt();
		byte[] bs = new byte[len];
		bb.get(bs, 0, len);
		if (serialLength - len > 0) {
			byte[] tmp = new byte[serialLength - len];
			bb.get(tmp);
		}
		f.number = new BigDecimal(new String(bs));
		return f;
	}
	@Override
	public FatDecimal newNullInstance() {
		FatDecimal b = newZeroInstance();
		b.isNull = true;
		return b;
	}

	@Override
	public FatDecimal newInstance(BigDecimal num) {
		FatDecimal res = new FatDecimal(p1, p2);
		res.number = num;
		return res;
	}
	@Override
	public FatType computeAdd(FatType t) {
		if (t instanceof FatDecimal) {
			FatDecimal d = (FatDecimal)t;
			return newInstance(number.add(d.getNumber()));
		} else
		throw new DevelopException();
	}
	@Override
	public FatType computeMinus(FatType t) {
		if (t instanceof FatDecimal) {
			FatDecimal d = (FatDecimal)t;
			return newInstance(number.subtract(d.getNumber()));
		} else
		throw new DevelopException();
	}
	@Override
	public FatType computeMul(FatType t) {
		if (t instanceof FatDecimal) {
			FatDecimal d = (FatDecimal)t;
			return newInstance(number.multiply(d.getNumber()));
		} else
		throw new DevelopException();
	}
	@Override
	public FatType computeDiv(FatType t) {
		if (t instanceof FatDecimal) {
			FatDecimal d = (FatDecimal)t;
			return newInstance(number.divide(d.getNumber(), RoundingMode.HALF_EVEN));
		} else
		throw new DevelopException();
	}
	@Override
	public String getPrint(int old) {
		return Log.padding(old) + "FatDecimal(" + p1 + "," + p2 + "):" + number;
	}
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof FatDecimal)) 
			return false;
		return (((FatDecimal)o).p1 == p1 && ((FatDecimal)o).p2 == p2 && 
				((FatDecimal)o).number == number);
	}
	@Override
	public BigDecimal getBigDecimal() {
		return number;
	}
	@Override
	public int compareTo(Object o) {
		int b;
		if (o instanceof FatInteger || o instanceof FatFloat || o instanceof FatDecimal) 
			b = getBigDecimal().compareTo(((FatType)o).getBigDecimal());
		else
			throw new DevelopException();
		return b;
	}
}
