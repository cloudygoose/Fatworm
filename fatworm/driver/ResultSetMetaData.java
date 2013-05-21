package fatworm.driver;

import java.sql.SQLException;
import fatworm.table.*;
import fatworm.type.*;
public class ResultSetMetaData implements java.sql.ResultSetMetaData{
	Tuple tuple;
	public ResultSetMetaData(Tuple t) {
		tuple = t;
	}
	public int getColumnCount() {
		if (tuple == null)
			return 0;
		return tuple.size();
	}
	public int getColumnType(int columnIndex) {
		FatType f = tuple.get(columnIndex - 1).getValue();
		if (f instanceof fatworm.type.FatBoolean)
			return java.sql.Types.BOOLEAN;
		else 
		if (f instanceof fatworm.type.FatChar)
			return java.sql.Types.CHAR;
		else 
		if (f instanceof fatworm.type.FatDateTime)
			return java.sql.Types.TIMESTAMP;
		else 
		if (f instanceof fatworm.type.FatDecimal)
			return java.sql.Types.DECIMAL;
		else 
		if (f instanceof fatworm.type.FatFloat)
			return java.sql.Types.FLOAT;
		else 
		if (f instanceof fatworm.type.FatInteger)
			return java.sql.Types.INTEGER;
		else 
		if (f instanceof fatworm.type.FatTimeStamp)
			return java.sql.Types.TIMESTAMP;
		else 
		if (f instanceof fatworm.type.FatVarChar)
			return java.sql.Types.VARCHAR;
		//NULL
		return -1;
	}
	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getCatalogName(int column) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getColumnClassName(int column) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public int getColumnDisplaySize(int column) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public String getColumnLabel(int column) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getColumnName(int column) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getColumnTypeName(int column) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public int getPrecision(int column) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public int getScale(int column) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public String getSchemaName(int column) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getTableName(int column) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public boolean isAutoIncrement(int column) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean isCaseSensitive(int column) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean isCurrency(int column) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean isDefinitelyWritable(int column) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public int isNullable(int column) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public boolean isReadOnly(int column) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean isSearchable(int column) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean isSigned(int column) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean isWritable(int column) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}
}
