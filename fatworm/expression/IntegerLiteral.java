package fatworm.expression;
import fatworm.type.*;
public class IntegerLiteral extends Expression{
	private int number;
	public int getNumber() {
		return number;
	}
	public IntegerLiteral(int num) {
		number = num;
	}
	public IntegerLiteral copy() {
		IntegerLiteral exp = new IntegerLiteral(number);
		exp.setConnection(connection);
		return exp;
	}
	@Override
	public String getPrint(int old) {
		return padding(old) + number + "\n";
	}
	@Override
	public FatInteger evaluate() {
		return new FatInteger(number);
	}
}
