package fatworm.expression;
import fatworm.type.*;
public class MinusExp extends Expression {
	Expression left, right;
	public Expression getLeft() {
		return left;
	}
	public Expression getRight() {
		return right;
	}
	public MinusExp(Expression s1, Expression s2) {
		left = s1; right = s2;
	}
	@Override
	public FatType evaluate() throws Exception {
		return left.evaluate().computeMinus(right.evaluate());
	}

	@Override
	public String getPrint(int old) throws Exception {
		return padding(old) + "MinusExp(\n" +
				left.getPrint(old + 1) + 
				right.getPrint(old + 1) + padding(old) + ")Minus\n";
	}
}
