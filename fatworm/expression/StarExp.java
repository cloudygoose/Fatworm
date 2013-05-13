package fatworm.expression;

import fatworm.type.FatFloat;
public class StarExp extends Expression {
	public StarExp() {
	}
	@Override
	public String getPrint(int old) {
		return padding(old) + "StarExp\n";
	}
}
