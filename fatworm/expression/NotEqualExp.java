package fatworm.expression;

import fatworm.type.FatBoolean;
import fatworm.type.FatType;

public class NotEqualExp extends Expression {
	Expression left, right;
	public Expression getLeft() {
		return left;
	}
	public Expression getRight() {
		return right;
	}
	public NotEqualExp(Expression s1, Expression s2) {
		left = s1; right = s2;
	}
	public NotEqualExp copy() {
		NotEqualExp exp = new NotEqualExp(left.copy(), right.copy());
		exp.setConnection(connection);
		return exp;
	}
	@Override
	public FatType evaluate() throws Exception {
		if (left.evaluate().isNull() || right.evaluate().isNull())
			return new FatBoolean(false);
		int b = left.evaluate().compareTo(right.evaluate());
		if (b == 0)
			return new FatBoolean(false);
		else
			return new FatBoolean(true);
	}
	@Override
	public String getPrint(int old) throws Exception {
		return padding(old) + "NotEqualExp(\n" +
				left.getPrint(old + 1) + 
				right.getPrint(old + 1) + padding(old) + ")And\n";
	}
}
