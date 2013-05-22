package fatworm.expression;

import fatworm.type.*;

public class LessEqualExp extends Expression {
	Expression left, right;
	public Expression getLeft() {
		return left;
	}
	public Expression getRight() {
		return right;
	}
	public LessEqualExp(Expression s1, Expression s2) {
		left = s1; right = s2;
	}
	public LessEqualExp copy() {
		LessEqualExp exp = new LessEqualExp(left.copy(), right.copy());
		exp.setConnection(connection);
		return exp;
	}
	@Override
	public FatType evaluate() throws Exception {
		if (left.evaluate().isNull() || right.evaluate().isNull())
			return new FatBoolean(false);
		int b = left.evaluate().compareTo(right.evaluate());
		if (b <= 0)
			return new FatBoolean(true);
		else
			return new FatBoolean(false);
	}
	@Override
	public String getPrint(int old) throws Exception {
		return padding(old) + "LessEqualExp(\n" +
				left.getPrint(old + 1) + 
				right.getPrint(old + 1) + padding(old) + ")And\n";
	}
}
