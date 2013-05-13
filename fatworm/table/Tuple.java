package fatworm.table;
import java.nio.ByteBuffer;
import java.util.*;
import fatworm.type.*;
import fatworm.expression.*;
import fatworm.log.*;
public class Tuple {
	ArrayList<TupleColumn> tupleColumns;
	public Tuple() {
		tupleColumns = new ArrayList<TupleColumn>();
	}
	public void addColumn(TupleColumn t) {
		tupleColumns.add(t);
	}
	public int getByteArrayLength() {
		int len = 0;
		for (int i = 0;i < tupleColumns.size();i++)
			len += tupleColumns.get(i).getValue().getByteArrayLength();
		return len;
	}
	public byte[] getByteArray() {
		byte[] by = new byte[getByteArrayLength()];
		ByteBuffer bb = ByteBuffer.wrap(by);
		for (int i = 0;i < tupleColumns.size();i++)
			tupleColumns.get(i).getValue().storeIntoByteBuffer(bb);
		return by;
	}
	public TupleColumn get(int k) {
		return tupleColumns.get(k);
	}
	public Tuple copy() {
		Tuple t = new Tuple();
		for (int i = 0;i < tupleColumns.size();i++)
			t.addColumn(tupleColumns.get(i));
		return t;
	}
	/*
	public FatType findById(IdExpression id) {
		for (int i = 0;i < tupleColumns.size();i++)
			if (tupleColumns.get(i).getIdExpression().strongEquals(id))
				return tupleColumns.get(i).getValue();
		for (int i = 0;i < tupleColumns.size();i++)
			if (tupleColumns.get(i).getIdExpression().weakEquals(id))
				return tupleColumns.get(i).getValue();
		throw new DevelopException();
	}
	*/
	public void set(IdExpression id, FatType value) throws Exception {
		for (int i = 0;i < tupleColumns.size();i++)
			if (tupleColumns.get(i).getColumnName().equals(id.getColumnName())) {
				FatType f = tupleColumns.get(i).getValue();
				tupleColumns.get(i).setValue(f.newInstance(value));
				return;
			}
	}
	public FatType getValueFromIdStrong(IdExpression id) {
		for (int i = 0;i < tupleColumns.size();i++)
			if (tupleColumns.get(i).strongEquals(id))
				return tupleColumns.get(i).getValue();
		return null;
	}
	public FatType getValueFromIdWeak(IdExpression id) {
		for (int i = 0;i < tupleColumns.size();i++)
			if (tupleColumns.get(i).weakEquals(id))
				return tupleColumns.get(i).getValue();
		return null;
	}
	public FatType getValueFromIdSW(IdExpression id) {
		for (int i = 0;i < tupleColumns.size();i++)
			if (tupleColumns.get(i).strongEquals(id))
				return tupleColumns.get(i).getValue();
		for (int i = 0;i < tupleColumns.size();i++)
			if (tupleColumns.get(i).weakEquals(id))
				return tupleColumns.get(i).getValue();
		return null;
	}
	public boolean testValueEqual(Tuple t) {
		if (tupleColumns.size() != t.size())
			throw new DevelopException();
		for (int i = 0;i < t.size();i++) {
			if (tupleColumns.get(i).getValue().compareTo(t.get(i).getValue()) != 0)
				return false;
		}
		return true;
	}

	public int size() {
		return tupleColumns.size();
	}
	public String getPrint() throws Exception {
		String re = "(";
		for (int i = 0;i < tupleColumns.size();i++) 
			re += tupleColumns.get(i).getPrint() + ",";
		re += ")";
		return re;
	}
}
