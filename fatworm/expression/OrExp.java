package fatworm.expression;
import fatworm.type.*;
public class OrExp extends Expression {
	Expression left, right;
	public Expression getLeft() {
		return left;
	}
	public Expression getRight() {
		return right;
	}
	public OrExp(Expression s1, Expression s2) {
		left = s1; right = s2;
	}
	public OrExp copy() {
		OrExp exp = new OrExp(left.copy(), right.copy());
		exp.setConnection(connection);
		return exp;
	}
	@Override
	public FatType evaluate() throws Exception {
		return left.evaluate().computeOr(right.evaluate());
	}
	@Override
	public String getPrint(int old) throws Exception {
		return padding(old) + "OrExp(\n" +
				left.getPrint(old + 1) + 
				right.getPrint(old + 1) + padding(old) + ")Or\n";
	}
}
