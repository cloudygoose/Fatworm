package fatworm.type;
import java.io.*;

public class FatNull extends FatType implements Serializable {
	private static final long serialVersionUID = 16L;
	public FatNull() {
		super();
		isNull = true;
	}
	@Override
	public int compareTo(Object o) {
		return -1;
	}
}
