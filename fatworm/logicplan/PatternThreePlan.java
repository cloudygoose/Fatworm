package fatworm.logicplan;

import fatworm.opt.*;
import fatworm.driver.*;
import fatworm.index.*;
import fatworm.expression.*;
import fatworm.scan.*;
import fatworm.scan.Scan;
import fatworm.type.*;

public class PatternThreePlan extends Plan {
	FatIndex index;
	IdExpression id;
	FatType low;
	FatType high;
	FetchTablePlan plan;
	fatworm.driver.Connection connection;
	public PatternThreePlan(FatIndex index, FetchTablePlan plan, IdExpression id, FatType low, FatType high, fatworm.driver.Connection connection) {
		this.index = index;
		this.id = id;
		this.low = low;
		this.high = high;
		this.plan = plan;
		this.connection = connection;
	}
	@Override
	public Scan getScan() {
		return new PatternThreeScan(index, plan.getScan(), id, low, high, connection);
	}
	@Override
	public String getPrint(int old) throws Exception {
		return padding(old) + "PatternThreePlan(\n" + id.getPrint(old + 1) + 
				low.getPrint(old + 1) + high.getPrint(old + 1) + padding(old) + ")PatternThreePlan\n";
	}
}
