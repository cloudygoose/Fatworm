package fatworm.type;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.ByteBuffer;
import java.io.*;

import fatworm.expression.*;
import fatworm.log.*;
public class FatInteger extends FatType implements Serializable {
	private static final long serialVersionUID = 15L;
	private Integer number;
	public FatInteger(int num) {
		super();
		number = num;
	}
	public FatInteger() {
		super();
		number = 0;
	}
	public int getNumber() {
		return number;
	}
	public String getText() {
		return "FatInteger:" + Integer.toString(number);
	}
	@Override
	public int getByteArrayLength() {
		return 4 + 1;
	}
	@Override
	public void storeIntoByteBuffer(ByteBuffer bb) {
		if (isNull)
			bb.put((byte)1);
		else
			bb.put((byte)0);
		bb.putInt(number);
	}
	@Override
	public FatInteger newNullInstance() {
		FatInteger b = newZeroInstance();
		b.isNull = true;
		return b;
	}
	@Override
	public FatType newInstanceFromByteBuffer(ByteBuffer bb) {
		FatInteger fi = new FatInteger();
		if (bb.get() == 1)
			fi.isNull = true;
		fi.number = bb.getInt();
		return fi;
	}
	@Override
	public FatInteger newInstance(Expression num) throws Exception {
		if (num instanceof IntegerLiteral) {
			FatInteger fi = new FatInteger();
			fi.number = ((IntegerLiteral)num).getNumber();
			return fi;
		} else
		throw new DevelopException();
	}
	@Override
	public FatInteger newInstance(FatType num) throws Exception{
		if (num.isNull)
			return newNullInstance();
		if (num instanceof FatInteger) {
			FatInteger fi = new FatInteger();
			fi.number = ((FatInteger)num).getNumber();
			return fi;
		} else
		if (num instanceof FatFloat) {
			FatInteger fi = new FatInteger();
			fi.number = ((FatFloat)num).getNumber().intValue();
			return fi;
		} else
		throw new DevelopException();
	}
	@Override
	public FatInteger newZeroInstance() {
		return new FatInteger(0);
	}
	@Override
	public FatInteger newMinInstance() {
		return new FatInteger(10000000);
	}
	@Override
	public FatInteger newMaxInstance() {
		return new FatInteger(-10000000);
	}
	@Override
	public FatInteger newInstance(int num) {
		return new FatInteger(num);
	}
	@Override
	public FatType computeAdd(FatType t) {
		if (t instanceof FatInteger) {
			return newInstance(number + ((FatInteger)t).getNumber());
		} else
		if (t instanceof FatFloat) {
			FatFloat f = (FatFloat)t;
			return f.newInstance(new BigDecimal(number).add(f.getNumber()));
		} else
		if (t instanceof FatDecimal) {
			FatDecimal f = (FatDecimal)t;
			return f.newInstance(new BigDecimal(number).add(f.getNumber()));
		} else
		throw new DevelopException();
	}
	@Override
	public FatType computeMinus(FatType t) {
		if (t instanceof FatInteger) {
			return newInstance(number - ((FatInteger)t).getNumber());
		} else
		if (t instanceof FatFloat) {
			FatFloat f = (FatFloat)t;
			return f.newInstance(new BigDecimal(number).subtract(f.getNumber()));
		} else
		if (t instanceof FatDecimal) {
			FatDecimal f = (FatDecimal)t;
			return f.newInstance(new BigDecimal(number).subtract(f.getNumber()));
		} else
		throw new DevelopException();
	}
	@Override
	public BigDecimal getBigDecimal() {
		return new BigDecimal(number);
	}
	@Override
	public FatInteger computeMul(FatType t) {
		if (t instanceof FatInteger) {
			return newInstance(number * ((FatInteger)t).getNumber());
		} else
		throw new DevelopException();
	}
	@Override
	public FatInteger computeMod(FatType t) {
		if (t instanceof FatInteger) {
			return newInstance(number % ((FatInteger)t).getNumber());
		} else
		throw new DevelopException();
	}
	@Override
	public FatType computeDiv(FatType t) {
		if (t instanceof FatInteger) {
			return new FatFloat(getBigDecimal().divide(((FatInteger)t).getBigDecimal(),
					4,
					RoundingMode.HALF_UP));
		} else
		throw new DevelopException();
	}
	@Override
	public String getPrint(int old) {
		return Log.padding(old) + "FatInteger:" + number;
	}
	@Override
	public int compareTo(Object o) {
		int b;
		if (o instanceof FatInteger || o instanceof FatFloat || o instanceof FatDecimal) 
			b = this.getBigDecimal().compareTo(((FatType)o).getBigDecimal());
		else
		if (o instanceof FatChar) {
			Integer oI = new Integer(((FatChar)o).getString());
			return (this.number.compareTo(oI));
		}			
		else throw new DevelopException();
		return b;
	}
}
