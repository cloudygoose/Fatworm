package fatworm.table;
import fatworm.type.*;
import fatworm.expression.*;
import fatworm.log.Log;

public class TupleColumn {
	String tableName;
	String columnName;
	String hisColumnName;
	FatType value;
	public TupleColumn(String t, String c, FatType v) {
		tableName = t;
		columnName = c;
		hisColumnName = "";
		value = v;
	}
	public TupleColumn(String t, String c, String hist, FatType v) {
		tableName = t;
		columnName = c;
		hisColumnName = hist;
		value = v;
	}
	public String getTableName() {
		return tableName;
	}
	public String getColumnName() {
		return columnName;
	}
	public void setTableName(String s) {
		tableName = s;
	}
	public TupleColumn copy() {
		return new TupleColumn(tableName, columnName, value.copy());
	}
	public IdExpression getIdExpression() {
		return new IdExpression(tableName, columnName);
	}
	public boolean strongEquals(IdExpression s) {
		return (new IdExpression(tableName, columnName)).strongEquals(s);
	}
	public boolean weakEquals(IdExpression s) {
		return (new IdExpression(tableName, columnName)).weakEquals(s);
	}
	public boolean histEquals(IdExpression s) {
		return (new IdExpression(tableName, hisColumnName)).strongEquals(s);
	}
	public FatType getValue() {
		return value;
	}
	public void setValue(FatType v) {
		value = v;
	}
	public String getPrint() throws Exception {
		//Log.v(value.toString());
		return "(" + tableName + ", " + columnName + ", " + value.getPrint(0) + ")"; 
	}
}
