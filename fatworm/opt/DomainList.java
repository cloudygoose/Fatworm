package fatworm.opt;
import fatworm.expression.*;
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
}	
