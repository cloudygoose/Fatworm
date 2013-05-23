package fatworm.logicplan;
import fatworm.scan.*;
public class AliasPlan extends Plan {
	private String alias;
	private Plan source;
	public Plan getSource() {
		return source;
	}
	public void setSource(Plan p) {
		source = p;
	}
	public String getAlias() {
		return alias;
	}
	public AliasPlan(Plan s, String change, fatworm.driver.Connection c) {
		alias = change;
		source = s;
		connection = c;
	}
	@Override
	public String getPrint(int old) throws Exception {
		return padding(old) + "AliasPlan(->" + alias + "\n" +
				source.getPrint(old + 1) + padding(old) + ")AliasPlan\n";
	}
	public Scan getScan() throws Exception {
		return new AliasScan(source.getScan(), alias, connection);
	}
}
