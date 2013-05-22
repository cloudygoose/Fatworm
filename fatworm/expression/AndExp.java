package fatworm.expression;

import fatworm.type.FatType;

public class AndExp extends Expression {
	Expression left, right;
	public Expression getLeft() {
		return left;
	}
	public Expression getRight() {
		return right;
	}
	public AndExp copy() {
		AndExp exp = new AndExp(left.copy(), right.copy());
		exp.setConnection(connection);
		return exp;
	}
	public AndExp(Expression s1, Expression s2) {
		left = s1; right = s2;
	}
	@Override
	public FatType evaluate() throws Exception {
		return left.evaluate().computeAnd(right.evaluate());
	}
	@Override
	public String getPrint(int old) throws Exception {
		return padding(old) + "AndExp(\n" +
				left.getPrint(old + 1) + 
				right.getPrint(old + 1) + padding(old) + ")And\n";
	}
}
