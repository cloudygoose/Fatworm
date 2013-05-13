package fatworm.scan;
import fatworm.table.*;
import java.util.*;
import fatworm.expression.*;
import fatworm.log.DevelopException;
import fatworm.log.Log;
public class SortScan extends Scan {
	Scan source;
	ArrayList<Boolean> ascList;
	ArrayList<IdExpression> ids;
	ArrayList<Tuple> tuples;
	int iter;
	Tuple nextT;
	public SortScan(Scan s, ArrayList<Boolean> asc, ArrayList<IdExpression> id) {
		source = s;
		ids = id;
		ascList = asc;
	}
	@Override
	public Tuple generateExTuple() throws Exception {
		return source.generateExTuple();
	}
	@Override
	public void open() throws Exception {
		tuples = new ArrayList<Tuple>();
		source.open();
		while (source.next()) {
			tuples.add(source.getTuple());
		}
		source.close();
		TupleComparator comparator = new TupleComparator(ascList, ids);
		iter = -1;
		for (int i = 0;i < tuples.size() - 1;i++)
			for (int j = i + 1;j < tuples.size();j++) {
				//Log.v(tuples.get(j).getPrint() + " " + tuples.get(i).getPrint() + " " + 
				//		comparator.bigger(tuples.get(j), tuples.get(i)));
				
				if (comparator.bigger(tuples.get(j), tuples.get(i)))
				{
					Tuple tmp = tuples.get(i);
					tuples.set(i, tuples.get(j));
					tuples.set(j, tmp);
				}
			}
		nextT = null;
	}
	@Override
	public void close() {
		//DONE in open()
	}
	@Override
	public void beforeFirst() {
		iter = -1;
		nextT = null;
	}
	@Override
	public boolean next() {
		nextT = null;
		if (iter + 1 < tuples.size()) {
			iter++;
			nextT = tuples.get(iter);
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
