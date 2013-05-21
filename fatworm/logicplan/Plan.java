package fatworm.logicplan;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedList;

import fatworm.expression.ExpList;
import fatworm.expression.Expression;
import fatworm.expression.FuncExp;
import fatworm.log.DevelopException;
import fatworm.log.Log;
import fatworm.scan.*;
public class Plan {
	protected fatworm.driver.Connection connection;
	protected boolean addedSlot = false;
	public boolean getAddedSlot() {
		return addedSlot;
	}
	public void setAddedSlot() {
		addedSlot = true;
	}
	public fatworm.driver.Connection getConnection() {
		return connection;
	}
	/*
	public void setFather(Plan father) {
		fatherPlan = father;
	}
	*/
	public String getPrint(int old) throws Exception {
		throw new DevelopException();
	}
	public String padding(int kk) {
		String result = "";
		for (int i = 1;i <= kk;i++)
			result += "    ";
		return result;
	}
	public Scan getScan() throws Exception {
		throw new DevelopException();
	}
}
