package fatworm.scan;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Locale;

import fatworm.expression.*;
import fatworm.log.*;
import fatworm.logicplan.*;
import fatworm.table.*;

public class Scan {
	public Scan() {
		
	}
	public void open() throws Exception{
		throw new DevelopException();
	}
	public void close() {
		throw new DevelopException();
	}
	public boolean next() throws Exception {
		throw new DevelopException();
	}
	public Tuple getTuple() throws Exception {
		throw new DevelopException();
	}
	public void beforeFirst() {
		throw new DevelopException();
	}
	public Tuple generateExTuple() throws Exception {
		throw new DevelopException();
	}
	public String padding(int kk) {
		String result = "";
		for (int i = 1;i <= kk;i++)
			result += "    ";
		return result;
	}
	public String getPrint(int old) throws Exception{
		throw new DevelopException();
	}
}
