package fatworm.logicplan;
import fatworm.scan.*;
import fatworm.driver.*;
//Used for "select const";
public class OneTuplePlan extends Plan{
	private fatworm.driver.Connection connection;
	public OneTuplePlan(fatworm.driver.Connection c) {
		connection = c;
	}
	@Override
	public String getPrint(int old) {
		return padding(old) + "OneTuplePlan()\n";
	}
	public Scan getScan() {
		return new OneTupleScan(connection);
	}
}
