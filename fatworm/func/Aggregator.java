package fatworm.func;
import java.math.BigDecimal;

import fatworm.log.Log;
import fatworm.type.*;
public class Aggregator {
	String token;
	FatType min;
	FatType max;
	FatType count;
	FatType sum;
	public Aggregator(String t, FatType factory) {
		token = t;
		if (t.equals("AVG")) {
			count = new FatInteger(0);
			sum = factory.newZeroInstance();
		} else
		if (t.equals("COUNT")) {
			count = new FatInteger(0);
		} else
		if (t.equals("MIN")) {
			min = factory.newMinInstance();
		} else
		if (t.equals("MAX")) {
			max = factory.newMaxInstance();
		} else
		if (t.equals("SUM")) {
			sum = factory.newZeroInstance();
		}
	}
	public void accept(FatType f) {
		if (token.equals("AVG")) {
			count = count.computeAdd(new FatInteger(1));
			sum = sum.computeAdd(f);
		} else
		if (token.equals("COUNT")) {
			count = count.computeAdd(new FatInteger(1));
		} else
		if (token.equals("MIN")) {
			if (min.compareTo(f) > 0)
				min = f;
		} else
		if (token.equals("MAX")) {
			if (max.compareTo(f) < 0)
				max = f;
		} else
		if (token.equals("SUM")) {
			//Log.v("!!sum!!");
			sum = sum.computeAdd(f);
		}
	}
	public FatType getValue() throws Exception {
		if (token.equals("AVG")) {
			if (((FatInteger)count).getNumber() == 0)
				return new FatFloat(new BigDecimal(0));
			return sum.computeDiv(count);
		} else
		if (token.equals("COUNT")) {
			return count;
		} else
		if (token.equals("MIN")) {
			return min;
		} else
		if (token.equals("MAX")) {
			return max;
		} else
		{
			return sum;
		}	
	}
}
