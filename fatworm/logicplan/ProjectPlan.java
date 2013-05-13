package fatworm.logicplan;
import fatworm.expression.*;
import fatworm.scan.*;
public class ProjectPlan extends Plan {
	Plan source;
	ExpList expList;
	fatworm.driver.Connection connection;
	public Plan getSource() {
		return source;
	}
	public ExpList getExpList() {
		return expList;
	}
	public ProjectPlan(ExpList lis, Plan s, fatworm.driver.Connection c) {
		connection = c;
		source = s;
		s.setFather(this);
		expList = lis;
	}
	@Override
	public String getPrint(int old) throws Exception {
		return padding(old) + "ProjectPlan(\n" + 
				expList.getPrint(old + 1) + 
				source.getPrint(old + 1) + padding(old) + ")Project\n";
	}
	public Scan getScan() throws Exception {
		return new ProjectScan(source.getScan(), expList, connection);
	}
}
