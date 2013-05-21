package fatworm.logicplan;
import fatworm.scan.*;
public class DistinctPlan extends Plan {
	Plan source;
	public Plan getSource() {
		return source;
	}
	public void setSource(Plan p) {
		source = p;
	}
	public DistinctPlan(Plan p1, fatworm.driver.Connection c) {
		connection = c;
		source = p1;
	}
	@Override
	public String getPrint(int old) throws Exception {
		return padding(old) + "DistinctPlan(\n" + 
				source.getPrint(old + 1) + padding(old) + ")\n";
	}
	@Override
	public Scan getScan() throws Exception {
		return new DistinctScan(source.getScan(), connection);
	}
}
