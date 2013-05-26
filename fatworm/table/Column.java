package fatworm.table;
import java.io.Serializable;

import fatworm.type.*;
public class Column implements Serializable {
	private static final long serialVersionUID = 8L;
	String name;
	FatType type;
	boolean notNull;
	boolean auto_increment;
	FatType defa;
	FatType autoValue;
	public Column() {
		type = null;
		notNull = false;
		auto_increment = false;
		defa = null;
	}
	public void setType(FatType t) {
		type = t;
	}
	public FatType getType() {
		return type;
	}
	public void setNotNull(boolean n) {
		notNull = n;
	}
	public boolean getNotNull() {
		return notNull;
	}
	
	public void setAutoIncrement(boolean a) {
		auto_increment = a;
		if (a)
			autoValue = type.newZeroInstance();
	}
	public FatType getAutoValueAfterInc() {
		autoValue = autoValue.computeAdd(new FatInteger(1));
		return autoValue;
	}
	public boolean getAutoIncrement() {
		return auto_increment;
	}
	public FatType getAutoValue() {
		return autoValue;
	}
	public void setAutoValue(FatType tt) {
		autoValue = tt;
	}
	public void setDefault(FatType d) {
		defa = d;
	}
	public FatType getDefault() {
		return defa;
	}
	public void setName(String n) {
		name = n;
	}
	public String getName() {
		return name;
	}
}
