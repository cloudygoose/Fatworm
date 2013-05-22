package fatworm.opt;
import fatworm.expression.*;
import fatworm.table.*;
import java.util.*;
public class DomainList {
	private ArrayList<IdExpression> domainList;
	public DomainList() {
		domainList = new ArrayList<IdExpression>();
	}
	public int size() {
		return domainList.size();
	}
	public void add(IdExpression e) {
		domainList.add(e);
	}
	public IdExpression get(int ind) {
		return domainList.get(ind);
	}
	public boolean tableNameAllNull() {
		Iterator<IdExpression> iter = domainList.iterator();
		while (iter.hasNext()) 
			if (!iter.next().getTableName().equals(""))
				return false;
		return true;
	}
	public boolean allInTuple(Tuple t) {
		Iterator<IdExpression> iter = domainList.iterator();
		while (iter.hasNext()) {
			if (t.getValueFromIdSW(iter.next()) == null)
				return false;
		}
		return true;
	}
}	
