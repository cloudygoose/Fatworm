package fatworm.func;
import fatworm.index.*;
import fatworm.type.FatType;
import fatworm.log.*;
import java.util.*;
import fatworm.expression.*;
import fatworm.table.*;
public class FuncEnv {
	private ArrayList<FuncPair> funcs;
	private ArrayList<FatTreeMap<FatType, Aggregator>> maps;
	private IdExpression groupId;
	Iterator<FatType> iter;//Scans the different groups
	Set<FatType> groups;
	FatType nextGroup;
	public FuncEnv(IdExpression g) {
		funcs = new ArrayList<FuncPair>();
		maps = new ArrayList<FatTreeMap<FatType, Aggregator>>();
		groupId = g;
	}
	public FatTreeMap<FatType, Aggregator> getMap(FuncPair func) {
		int i;
		for (i = 0;i < funcs.size();i++)
			if (func.strongEquals(funcs.get(i)))
				return maps.get(i);
		for (i = 0;i < funcs.size();i++)
			if (func.weakEquals(funcs.get(i)))
				return maps.get(i);
		throw new DevelopException();
	}
	public void prepareMaps(LinkedList<FuncExp> fs) {
		for (int i = 0;i < fs.size();i++) {
			funcs.add(new FuncPair(fs.get(i).getToken(), fs.get(i).getId()));
			maps.add(new FatTreeMap<FatType, Aggregator>());
		}
	}
	public void accept(Tuple t) throws Exception {
		FatType group = t.getValueFromIdSW(groupId);
		//Log.v("accepting tuple : " + t.getPrint());
		if (group == null)
			throw new DevelopException();
		for (int i = 0;i < funcs.size();i++) {
			FatType value = t.getValueFromIdSW(funcs.get(i).getId());
			if (value == null)
				continue;
			FatTreeMap<FatType, Aggregator> map = maps.get(i);
			Aggregator agg = map.get(group);
			if (agg == null)
				agg = new Aggregator(funcs.get(i).getToken(), value);
			agg.accept(value);
			map.put(group, agg);
			//Log.v("!!!" + this);
		}
	}
	public void open() {
		groups = maps.get(0).getKeySet();
		iter = groups.iterator();
	}
	public void close() {
		
	}
	public void beforeFirst() {
		iter = groups.iterator();
	}
	public boolean next() {
		if (iter.hasNext()) {
			nextGroup = iter.next();
			return true;
		} else
		return false;
	}
	public FatType getCurrentGroup() {
		return nextGroup;
	}
}
