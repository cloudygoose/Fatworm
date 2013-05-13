package fatworm.table;
import java.util.*;

import fatworm.expression.*;
import fatworm.type.*;
import java.io.*;
import java.nio.ByteBuffer;
public class Schema implements Serializable {
	private static final long serialVersionUID = 7L;
	private ArrayList<Column> columns;
	String tableName;
	public Schema(String n) {
		tableName = n;
		columns = new ArrayList();
	}
	public int getColumnNumber() {
		return columns.size();
	}
	public Column getColumn(int i) {
		return columns.get(i);
	}
	public void addColumn(Column column) {
		columns.add(column);
	}
	public int getByteArrayLength() {
		int len = 0;
		for (int i = 0;i < columns.size();i++)
			len += columns.get(i).getType().getByteArrayLength();
		return len;
	}
	public Tuple getTupleFromByteArray(byte[] b) {
		ByteBuffer bb = ByteBuffer.wrap(b);
		Tuple t = new Tuple();
		for (int i = 0;i < columns.size();i++) {
			FatType f = columns.get(i).getType().newInstanceFromByteBuffer(bb);
			t.addColumn(new TupleColumn(tableName, columns.get(i).getName(), f));
		}
		return t;
	}
	public Tuple newTupleFromTuple(Tuple old) throws Exception {
		Tuple t = new Tuple();
		for (int i = 0;i < columns.size();i++) {
			FatType f = columns.get(i).getType().newInstance(old.get(i).getValue());
			t.addColumn(new TupleColumn(tableName, columns.get(i).getName(), f));
		}
		return t;
	}
}
