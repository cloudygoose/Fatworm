package fatworm.scan;
import fatworm.table.*;
import java.util.*;
import fatworm.expression.*;
import fatworm.log.DevelopException;
import fatworm.log.Log;
public class SortScan extends Scan {
	protected Scan source;
	protected ArrayList<Boolean> ascList;
	protected ArrayList<IdExpression> ids;
	protected ArrayList<Tuple> tuples;
	protected int iter;
	protected Tuple nextT;
	protected fatworm.driver.Connection connection;
	public SortScan(Scan s, ArrayList<Boolean> asc, ArrayList<IdExpression> id, fatworm.driver.Connection c) {
		source = s;
		ids = id;
		ascList = asc;
		connection = c;
	}
	@Override
	public Tuple generateExTuple() throws Exception {
		return source.generateExTuple();
	}
	@Override
	public void open() throws Exception {
		tuples = new ArrayList<Tuple>();
		int kk = 0;
		source.open();
		while (source.next()) {
			tuples.add(source.getTuple());
			/*
			kk++;
			if (kk % 100 == 0) {
				Log.v("kk : " + kk);
			}
			*/
		}
		source.close();
		TupleComparator comparator = new TupleComparator(ascList, ids);
		iter = -1;
		for (int i = 0;i < tuples.size() - 1;i++)
			for (int j = i + 1;j < tuples.size();j++) {
			//	Log.v("comparing : " + tuples.get(j).getPrint() + " " + tuples.get(i).getPrint() + " " + 
			//			comparator.bigger(tuples.get(j), tuples.get(i)));
				
				if (comparator.bigger(tuples.get(j), tuples.get(i)))
				{
					Tuple tmp = tuples.get(i);
					tuples.set(i, tuples.get(j));
					tuples.set(j, tmp);
				}
			}
		nextT = null;
		//for (int i = 0;i < tuples.size();i++)
		//Log.v("sortScan log : " + tuples.get(i).getPrint());
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
