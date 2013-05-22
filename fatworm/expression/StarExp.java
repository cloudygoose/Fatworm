package fatworm.expression;

import fatworm.type.FatFloat;
public class StarExp extends Expression {
	public StarExp() {
	}
	public StarExp copy() {
		StarExp exp = new StarExp();
		exp.setConnection(connection);
		return exp;
	}
	@Override
	public String getPrint(int old) {
		return padding(old) + "StarExp\n";
	}
}
