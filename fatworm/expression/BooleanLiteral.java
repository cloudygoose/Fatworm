package fatworm.expression;
import fatworm.type.*;

public class BooleanLiteral extends Expression {
	private boolean bool;
	public Boolean getBool() {
		return bool;
	}
	public BooleanLiteral(boolean v) {
		bool = v;
	}
	public BooleanLiteral copy() {
		BooleanLiteral exp = new BooleanLiteral(bool);
		exp.setConnection(connection);
		return exp;
	}
	@Override
	public String getPrint(int old) {
		return padding(old) + bool + "\n";
	}
	@Override
	public FatBoolean evaluate() {
		return new FatBoolean(bool);
	}
}
