package fatworm.type;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.ByteBuffer;

import fatworm.expression.*;
import fatworm.log.*;
import java.io.*;
public class FatFloat extends FatType implements Serializable {
	private static final long serialVersionUID = 14L;
	private BigDecimal number;
	public FatFloat() {
		super();
		number = new BigDecimal(0);
	}
	public FatFloat(BigDecimal num) {
		super();
		number = num;
	}
	public Float floatValue() {
		return number.floatValue();
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
		bb.putFloat(number.floatValue());
	}
	@Override
	public FatFloat newNullInstance() {
		FatFloat b = newZeroInstance();
		b.isNull = true;
		return b;
	}

	@Override
	public FatType newInstanceFromByteBuffer(ByteBuffer bb) {
		FatFloat ff = new FatFloat();
		if (bb.get() == 1)
			ff.isNull = true;
		ff.number = new BigDecimal(bb.getFloat());
		return ff;
	}
	@Override
	public FatFloat newInstance(Expression s) throws DevelopException {
		if (s instanceof FloatLiteral) {
			FatFloat ff = new FatFloat();
			ff.number = ((FloatLiteral)s).getNumber();
			return ff;
		} else
		throw new DevelopException();
	}
	@Override
	public FatFloat newInstance(FatType s) throws Exception {
		if (s.isNull)
			return newNullInstance();
		if (s instanceof FatFloat) {
			FatFloat ff = new FatFloat();
			ff.number = ((FatFloat)s).getNumber();
			return ff; 
		} else
		if (s instanceof FatInteger) {
			FatFloat ff = new FatFloat();
			ff.number = ((FatInteger)s).getBigDecimal();
			return ff;
		} else
		throw new DevelopException();
	}
	@Override
	public FatFloat newInstance(BigDecimal s) {
		return new FatFloat(s);
	}
	public BigDecimal getNumber() {
		return number;
	}
	public String getText() {
		return "FatFloat:" + number.toPlainString();
	}
	@Override
	public FatType computeAdd(FatType t) {
		if (t instanceof FatFloat) {
			FatFloat d = (FatFloat)t;
			return newInstance(number.add(d.getNumber()));
		} else
		if (t instanceof FatInteger) {
			FatInteger d = (FatInteger)t;
			return newInstance(number.add(d.getBigDecimal()));
		} else
		throw new DevelopException();
	}
	@Override
	public FatType computeMinus(FatType t) {
		if (t instanceof FatFloat) {
			FatFloat d = (FatFloat)t;
			return newInstance(number.subtract(d.getNumber()));
		} else
		throw new DevelopException();
	}
	@Override
	public FatType computeMul(FatType t) {
		if (t instanceof FatFloat) {
			FatFloat d = (FatFloat)t;
			return newInstance(number.multiply(d.getNumber()));
		} else
		throw new DevelopException();
	}
	@Override
	public FatType computeDiv(FatType t) {
		if (t instanceof FatFloat) {
			FatFloat d = (FatFloat)t;
			return newInstance(number.divide(d.getNumber(), 4, RoundingMode.HALF_EVEN));
		} else
		if (t instanceof FatInteger) {
			FatInteger d = (FatInteger)t;
			return newInstance(number.divide(d.getBigDecimal(), 4, RoundingMode.HALF_EVEN));
		}
		throw new DevelopException();
	}
	@Override
	public FatFloat newZeroInstance() {
		return new FatFloat(new BigDecimal(0));
	}
	@Override
	public FatFloat newMinInstance() {
		return new FatFloat(new BigDecimal(100000000));
	}
	@Override
	public FatFloat newMaxInstance() {
		return new FatFloat(new BigDecimal(-100000000));
	}
	@Override
	public String getPrint(int old) {
		return Log.padding(old) + "FatFloat:" + number;
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
