package fatworm.type;
import java.nio.ByteBuffer;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.io.*;
import fatworm.log.*;
public class FatTimeStamp extends FatType implements Serializable {
	private static final long serialVersionUID = 17L;
	java.sql.Timestamp timeStamp;
	public FatTimeStamp() {
	}
	public FatTimeStamp(java.sql.Timestamp d) {
		timeStamp = d;
	}
	public FatTimeStamp(long ll) {
		timeStamp = new java.sql.Timestamp(ll);
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
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		Long kk = bb.getLong();
		String ss = dateFormat.format(new Date(kk));
		//Log.v(ss);
			fs.timeStamp = new Timestamp(kk);
			//Log.v("!!!" + fs.timeStamp.toString());
			//fs.timeStamp = new Timestamp(dateFormat.parse(ss).getTime());
			//Log.v(fs.timeStamp.toString());
		return fs;
	}
	@Override
	public FatTimeStamp newInstance(FatType f) {
		if (f.isNull) 
			return newNullInstance();
		if (f instanceof FatTimeStamp) {
			return new FatTimeStamp(((FatTimeStamp)f).timeStamp);
		} else
		if (f instanceof FatChar) {
			try {
				return new FatTimeStamp(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse((((FatChar)f).s)).getTime());
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return null;
		} else
		throw new DevelopException();
	}
	@Override
	public FatTimeStamp newNullInstance() {
		FatTimeStamp b = newZeroInstance();
		b.isNull = true;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		String s = dateFormat.format(new java.util.Date()).toString();
		java.sql.Timestamp ss = null;
		try {
			ss = new java.sql.Timestamp(dateFormat.parse(s).getTime());
		} catch (ParseException e) {
			e.printStackTrace();
		} 
		b.timeStamp = ss;
		return b;
	}
	@Override
	public String getPrint(int old) {
		return Log.padding(old) + "FatTimeStamp : " + timeStamp.toString();
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
		if (t instanceof FatChar) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			Timestamp oD = null;
			try {
				oD = new Timestamp(dateFormat.parse(((FatChar)t).getString()).getTime());
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return timeStamp.compareTo(oD);
		} else
		throw new DevelopException();
	}
}
