package fatworm.expression;
import fatworm.log.Log;
import fatworm.type.*;
public class EqualExp extends Expression {
	Expression left, right;
	public Expression getLeft() {
		return left;
	}
	public Expression getRight() {
		return right;
	}
	public EqualExp(Expression s1, Expression s2) {
		left = s1; right = s2;
	}
	@Override
	public FatType evaluate() throws Exception {
		int b = left.evaluate().compareTo(right.evaluate());
		if (b == 0)
			return new FatBoolean(true);
		else 
			return new FatBoolean(false);
	}
	@Override
	public String getPrint(int old) throws Exception {
		return padding(old) + "EqualExp(\n" +
				left.getPrint(old + 1) +
				right.getPrint(old + 1) + padding(old) + ")Equal\n";
	}
}
