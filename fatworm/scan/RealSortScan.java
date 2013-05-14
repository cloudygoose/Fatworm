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
	TupleComparator comparator;
	public RealSortScan(Scan s, ArrayList<Boolean> asc, ArrayList<IdExpression> id, fatworm.driver.Connection c) {
		super(s, asc, id, c);
		comparator = new TupleComparator(ascList, ids);
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
		tupleNum = 0;
		source.open();
		while (source.next()) {
			mainCursor.insertTuple(source.getTuple().getByteArray());
			mainCursor.forward();
			++tupleNum;
		}
		//Log.v("tupleNum : " + tupleNum);
		source.close();
		/*
		RATFileCursor cursorI = mainCursor.getCursor(0);
		RATFileCursor cursorJ;
		for (int i = 0;i < tupleNum - 1;i++) {
			cursorJ = cursorI.getCursor(i + 1);
			for (int j = i + 1;j < tupleNum;j++) {
				Tuple ti = exT.getTupleFromByteArray(cursorI.getTypleArray());
				Tuple tj = exT.getTupleFromByteArray(cursorJ.getTypleArray());
				if (comparator.bigger(tj, ti))
				{
					cursorI.insertTuple(tj.getByteArray());
//					Log.v("ti now" +  exT.getTupleFromByteArray(cursorI.getTypleArray()).getPrint());
					cursorJ.insertTuple(ti.getByteArray());
//					Log.v("tj now" +  exT.getTupleFromByteArray(cursorJ.getTypleArray()).getPrint());
				}
				cursorJ.forward();
			}
			cursorI.forward();
		}
		*/
		qsort(0, tupleNum - 1, mainCursor);
		nextT = null;
		iter = -1;
		mainCursor.set(0);
	}
	public void qsort(int l, int r, RATFileCursor mainC) {
		int i = l, j = r;
		RATFileCursor cursorI = mainC.getCursor(i), cursorJ = mainC.getCursor(j);
		Tuple tX = exT.getTupleFromByteArray(mainC.getCursor((l + r)/ 2).getTypleArray());
		while (i <= j) {
			while (comparator.bigger(getTuple(cursorI), tX)) { i++; cursorI.forward(); }
			while (comparator.bigger(tX, getTuple(cursorJ))) { j--; cursorJ.backward(); }
			if (i <= j) {
				Tuple tmp = getTuple(cursorI);
				cursorI.insertTuple(getTuple(cursorJ).getByteArray());
				cursorJ.insertTuple(tmp.getByteArray());
				i++; cursorI.forward();
				j--; cursorJ.backward();
			}
		}
		//Log.v("now sorting r : " + r + " weishu : " + (new Integer(r)).toString().length());
		if (j > l)
			qsort(l, j, mainC);
		if (i < r)
			qsort(i, r, mainC);
	}
	public Tuple getTuple(RATFileCursor c) {
		return exT.getTupleFromByteArray(c.getTypleArray());
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
