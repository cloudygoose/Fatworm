package fatworm.table;

import java.io.Serializable;
import java.util.*;

import fatworm.driver.Driver;
import fatworm.expression.IdExpression;
import fatworm.log.DevelopException;
import fatworm.log.Log;
import fatworm.storage.PageId;
import fatworm.storage.TableCursorPageId;
import fatworm.type.FatType;
public class RealTableCursor extends TableCursor {
	TableCursorPageId cursor;
	TableCursorPageId insertCursor;
	int tupleLength;
	int lastPos;
	public RealTableCursor(String n, ArrayList<Tuple> r, Schema s, Table t) {
		super(n, r, s, t);
		tupleLength = t.getSchema().getByteArrayLength();
		//Log.v("tupleLength : " + tupleLength);
	}
	public void open() {
		cursor = table.getStartTableCursorPageId();
		tmpSize = table.tuplesNum;
		iter = -1;
		insertCursor = table.insertCursor;
	}
	public void close() {
		
	}
	@Override
	public int getLastPos() {
		return lastPos;
	}
	public boolean next() {
		nextT = null;
		if (iter + 1 < tmpSize) {
			iter++;
			while (cursor.getNullSymbol() == 1) {
				cursor.forwardTuple(tupleLength);
			}
			lastPos = cursor.getId() * Driver.BLOCKLENGTH + cursor.getPosition();
			cursor.mark();
			//if (name.equals("test1"))
			//	Log.v(cursor.getId() + " aa " + cursor.getPosition());
			
			nextT = schema.getTupleFromByteArray(cursor.getTupleByteArray(tupleLength));
			cursor.forwardTuple(tupleLength);
			return true;
		} else
		return false;
	}	
	public Tuple getTuple() {
		//Log.v("" + iter + " " + records.size());
		if (nextT != null)
			return nextT;
		else
			throw new DevelopException();
	}
	public void update(Tuple t) {
		cursor.returnMark();
		byte[] s = new byte[1];
		s[0] = 0;
		cursor.putBytes(0, s);
		cursor.putBytes(1, t.getByteArray());
		cursor.forwardTuple(tupleLength);
	}
	public void delete() {
		cursor.returnMark();
		lastPos = cursor.getId() * Driver.BLOCKLENGTH + cursor.getPosition();
		byte[] s = new byte[1];
		s[0] = 1;
		cursor.putBytes(0, s);
		cursor.forwardTuple(tupleLength);
		table.deleteTuple();
	}
	//return the B point
	public int insert(Tuple t) {
		byte[] s = new byte[1];
		s[0] = 0;
		this.lastPos = insertCursor.getId() * Driver.BLOCKLENGTH + insertCursor.getPosition();
		int kk = insertCursor.getId() * Driver.BLOCKLENGTH + insertCursor.getPosition();
		insertCursor.putBytes(0, s);
		insertCursor.putBytes(1, t.getByteArray());
		insertCursor.forwardTuple(tupleLength);
		//table.addTuple(); now handled by executors
		return kk;
	}
	public void addTupleNumber(int num) {
		table.addTuple(num);
	}
	public void beforeFirst() {
		iter = -1;
		cursor.beforeFirst();
	}
}
