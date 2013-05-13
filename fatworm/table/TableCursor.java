package fatworm.table;
import java.io.Serializable;
import java.util.*;

import fatworm.expression.IdExpression;
import fatworm.log.DevelopException;
import fatworm.log.Log;
import fatworm.type.FatType;
public class TableCursor {
	String name;
	ArrayList<Tuple> records;
	int iter, tmpSize;
	Tuple nextT;
	Schema schema;
	Table table;
	public TableCursor(String n, ArrayList<Tuple> r, Schema s, Table t) {
		name = n;
		records = r;
		iter = -1;
		schema = s;
		table = t;
	}
	public Schema getSchema() {
		return schema;
	}
	public void open() {
		iter = -1;
		tmpSize = records.size();
	}
	public void close() {
		
	}
	public boolean next() {
		nextT = null;
		if (iter + 1 < tmpSize) {
			iter++;
			Tuple t = records.get(iter);
			nextT = new Tuple();
			for (int i = 0;i < schema.getColumnNumber();i++) {
				Column c = schema.getColumn(i);
				nextT.addColumn(new TupleColumn(name, c.getName(), t.get(i).getValue()));
			}
			return true;
		} else
		return false;
	}	
	/*should use tuple.set
	public void set(IdExpression id, FatType value) throws Exception {
		for (int i = 0;i < schema.getColumnNumber();i++)
			if (schema.getColumn(i).getName().equals(id.getColumnName())) {
				FatType f = records.get(iter).get(i).getValue();
				records.get(iter).get(i).setValue(f.newInstance(value));
				return;
			}
	}*/
	public void update(Tuple t) {
		records.set(iter, t);
	}
	public void addTupleNumber(int num) {
		table.addTuple(num);
	}
	public Tuple getTuple() {
		//Log.v("" + iter + " " + records.size());
		if (nextT != null)
			return nextT;
		else
			throw new DevelopException();
	}
	public void delete() {
		records.remove(iter);
		iter--;
		tmpSize--;
	}
	public void insert(Tuple t) {
		records.add(t);
		//table.addTuple(); now handled by executors
	}
	public void beforeFirst() {
		iter = -1;
	}
}
