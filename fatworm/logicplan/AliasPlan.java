package fatworm.logicplan;
import fatworm.scan.*;
public class AliasPlan extends Plan {
	fatworm.driver.Connection connection;
	private String alias;
	private Plan source;
	public Plan getSource() {
		return source;
	}
	public String getAlias() {
		return alias;
	}
	public AliasPlan(Plan s, String change, fatworm.driver.Connection c) {
		alias = change;
		source = s;
		connection = c;
		s.setFather(this);
	}
	@Override
	public String getPrint(int old) throws Exception {
		return padding(old) + "AliasPlan(->" + alias + "\n" +
				source.getPrint(old + 1) + padding(old) + ")\n";
	}
	public Scan getScan() throws Exception {
		return new AliasScan(source.getScan(), alias, connection);
	}
}
