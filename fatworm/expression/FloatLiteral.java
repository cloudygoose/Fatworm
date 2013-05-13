package fatworm.expression;
import java.math.BigDecimal;

import fatworm.type.*;
public class FloatLiteral extends Expression{
	private BigDecimal number;
	public BigDecimal getNumber() {
		return number;
	}
	public FloatLiteral(BigDecimal num) {
		number = num;
	}
	@Override
	public String getPrint(int old) {
		return padding(old) + number + "\n";
	}
	@Override
	public FatFloat evaluate() {
		return new FatFloat(number);
	}
}
