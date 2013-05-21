package fatworm.logicplan;

import java.util.ArrayList;

import fatworm.expression.*;
import fatworm.scan.*;

public class OrderPlan extends Plan{
	ArrayList<IdExpression> idList;
	ArrayList<Boolean> ascList;
	Plan source;
	public void setSource(Plan p) {
		source = p;
	}
	public Plan getSource() {
		return source;
	}
	public ArrayList<Boolean> getAscList() {
		return ascList;
	}
	public ArrayList<IdExpression> getIdList() {
		return idList;
	}
	public OrderPlan(ArrayList<IdExpression> ids, ArrayList<Boolean> ascs, Plan s, fatworm.driver.Connection c) {
		connection = c;
		idList = ids;
		ascList = ascs;
		source = s;
	}
	@Override
	public String getPrint(int old) throws Exception {
		String result = padding(old) + "OrderPlan(\n" +
				padding(old + 1) + "ORDERLIST:\n";
		for (int i = 0;i < idList.size();i++) {
			result += idList.get(i).getPrint(old + 1);
			if (ascList.get(i).equals(true))
				result += padding(old + 1) + "ASC\n";
			else
				result += padding(old + 1) + "DESC\n";
		}
		result += padding(old + 1) + "SOURCE:\n" +
				source.getPrint(old + 1);
		result += padding(old) + ")OrderPlan\n";
		return result;
	}
	@Override
	public Scan getScan() throws Exception {
		return new OrderScan(idList, ascList, source.getScan(), connection);
	}
}