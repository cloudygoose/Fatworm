package fatworm.type;
import java.nio.ByteBuffer;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.io.*;
import fatworm.log.*;
public class FatDateTime extends FatType implements Serializable {
	private static final long serialVersionUID = 12L;
	Timestamp date;
	public Timestamp getDate() {
		return date;
	}
	public FatDateTime() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	}
	public FatDateTime(java.sql.Timestamp d) {
		date = d;
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
		bb.putLong(date.getTime());
	}
	@Override
	public FatDateTime newNullInstance() {
		FatDateTime b = new FatDateTime();
		b.isNull = true;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		String s = dateFormat.format(new java.util.Date()).toString();
		java.sql.Timestamp ss = null;
		try {
			ss = new java.sql.Timestamp(dateFormat.parse(s).getTime());
		} catch (ParseException e) {
			e.printStackTrace();
		} 
		b.date = ss;
		return b;
	}
	@Override
	public FatType newInstanceFromByteBuffer(ByteBuffer bb) {
		FatDateTime fd = new FatDateTime();
		if (bb.get() == 1)
			fd.isNull = true;
		fd.date = new Timestamp(bb.getLong());
		return fd;
	}
	@Override
	public FatDateTime newInstance(FatType f) {
		if (f.isNull)
			return newNullInstance();
		FatDateTime fd = new FatDateTime();
		if (f instanceof FatChar) {
			try {
			String s = ((FatChar)f).s;
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			fd.date = new Timestamp(dateFormat.parse(s).getTime()); 
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else
		if (f instanceof FatDateTime) {
			return (FatDateTime)f;
		} else
		throw new DevelopException();
		return fd;
	}
	public FatDateTime newInstance() {
		FatDateTime f = new FatDateTime();
		return f;
	}
	@Override
	public FatDateTime newZeroInstance() {
		FatDateTime f = new FatDateTime();
		f.date = new Timestamp(0);
		return f;
	}
	@Override
	public String getPrint(int old) {
		return Log.padding(old) + "FatDateTime : " + date.toString();
	}
	@Override
	public int compareTo(Object o) {
		if (o instanceof FatDateTime) {
			return date.compareTo(((FatDateTime)o).date);
		} else
		if (o instanceof FatChar) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			Timestamp oD = null;
			try {
				oD = new Timestamp(dateFormat.parse(((FatChar)o).getString()).getTime());
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return date.compareTo(oD);
		} else
		throw new DevelopException();
	}
}
