package fatworm.scan;
import fatworm.table.*;
import java.util.*;
import fatworm.expression.*;
import fatworm.log.DevelopException;
import fatworm.log.Log;
import fatworm.storage.*;
public class RealSortScan extends SortScan {
	int tupleNum;
	RATFileCursor mainCursor;
	Tuple exT;
	public RealSortScan(Scan s, ArrayList<Boolean> asc, ArrayList<IdExpression> id, fatworm.driver.Connection c) {
		super(s, asc, id, c);
		try {
			exT = source.generateExTuple();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Override
	public Tuple generateExTuple() throws Exception {
		return source.generateExTuple();
	}
	@Override
	public void open() throws Exception {
		tuples = new ArrayList<Tuple>();
		mainCursor = new RATFileCursor(exT.getByteArrayLength(),
				connection);
		mainCursor.newFileInit();
		int tupleNum = 0;
		source.open();
		while (source.next()) {
			mainCursor.insertTuple(source.getTuple().getByteArray());
			mainCursor.forward();
			++tupleNum;
		}
		source.close();
		TupleComparator comparator = new TupleComparator(ascList, ids);
		RATFileCursor cursorI = mainCursor.getCursor(0);
		RATFileCursor cursorJ;
		for (int i = 0;i < tupleNum - 1;i++) {
			cursorJ = cursorI.getCursor(i + 1);
			Tuple ti = exT.getTupleFromByteArray(cursorI.getTypleArray());
			for (int j = i + 1;j < tupleNum;j++) {
				Tuple tj = exT.getTupleFromByteArray(cursorJ.getTypleArray());
				if (comparator.bigger(tj, ti))
				{
					cursorI.insertTuple(tj.getByteArray());
					cursorJ.insertTuple(ti.getByteArray());
				}
				cursorJ.forward();
			}
			cursorI.forward();
		}
		nextT = null;
		iter = -1;
		mainCursor.set(0);
	}
	@Override
	public void close() {
		mainCursor.close();
	}
	@Override
	public void beforeFirst() {
		iter = -1;
		mainCursor.set(0);
		nextT = null;
	}
	@Override
	public boolean next() {
		Log.v("!!real!!");
		nextT = null;
		if (iter + 1 < tupleNum) {
			iter++;
			nextT = exT.getTupleFromByteArray(mainCursor.getTypleArray());
			mainCursor.forward();
			return true;
		}
		else
			return false;
	}
	@Override
	public Tuple getTuple() {
		if (nextT != null)
			return nextT;
		throw new DevelopException();
	}
}
