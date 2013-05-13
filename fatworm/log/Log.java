package fatworm.log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;

import org.antlr.runtime.tree.CommonTree;

import fatworm.expression.ExpList;
import fatworm.expression.Expression;
import fatworm.expression.FuncExp;
import fatworm.logicplan.Plan;
import fatworm.scan.Scan;
import fatworm.type.*;
public class Log {
	public static boolean open = true;
	public static FileOutputStream oo;
	public static void openFile(boolean b) {
		if (!b) return; 
		try {
			oo = new FileOutputStream(new File("files/logg"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	public static void closeFile() {
		if (oo == null)
			return;
		try {
			oo.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void v(String str) {
		if (open) {
			if (oo != null)
				try {
					oo.write(("log : " + str + "\n").getBytes());
				} catch (IOException e) {
					e.printStackTrace();
				}
			else
				System.out.println("log : " + str);
		}
	}
	public static void v(int k) {
		if (open)
			System.out.println("log : " + k);
	}
	public static void v(boolean k) {
		if (open)
			System.out.println("log : " + k);
	}
	public static void assertTrue(boolean k) {
		if (!k) 
			throw new DevelopException();
	}
	public static boolean checkFatBoolean(FatType t) {
		if (t instanceof FatBoolean) {
			FatBoolean b = (FatBoolean)t;
			if (b.getBool()) 
				return true;
			else
				return false;
		} else
		throw new DevelopException();
	}
	public static boolean stringNameEqual(String s1, String s2) {
		return s1.toLowerCase().equals(s2.toLowerCase());
	}
	public static String padding(int kk) {
		String result = "";
		for (int i = 1;i <= kk;i++)
			result += "    ";
		return result;
	}
	public static void LogAST(CommonTree t, String old) {
		Log.v(old + "-" + t.getType() + t.getText());
		int child = t.getChildCount();
		for (int i = 0;i < child;i++)
			LogAST((CommonTree)t.getChild(i), old + "-" + t.getText());
	}
	public static Object getter(Object obj, String att) throws Exception {
		Method method = obj.getClass().getMethod("get" + att);
	    //System.out.println(method.invoke(obj));
	    return method.invoke(obj);
	}
	public static void getAllFunc(Object ob, LinkedList<FuncExp> list) throws Exception {
		Field[] fields = ob.getClass().getDeclaredFields();
   		if (ob instanceof FuncExp) {
   			list.add((FuncExp)ob);
   			//Log.v("!!find");
   			return;
   		}
   		if (ob instanceof ExpList) {
       		ArrayList<Expression> l = ((ExpList)ob).getExpList();
       		//Log.v(((ExpList)child).getPrint(0));
       		for (int j = 0;j < l.size();j++) {
       			getAllFunc(l.get(j), list);
       		}
       		return;
   		}
		for (int i = 0; i < fields.length; i++) {
//          int mo = field[i].getModifiers();
//          String priv = Modifier.toString(mo);  //About public private balalala
            

//            Class<?> type = field[i].getType();
//            Log.v(type.getName() + " " +
//                    field[i].getName() + ";");
        	
        	String a = fields[i].getName();
        	String a1 = a.substring(0, 1).toUpperCase();
        	a = a.substring(1, a.length());
        	Object child = Log.getter(ob, a1 + a);
        	//Log.v(a1 + a);
        	if (child instanceof fatworm.driver.Connection) {
        		
        	} else
        	if (child instanceof Scan) {
        		Scan scan = (Scan)child;
        		//getAllFunc(scan, list);
        	} else
            if (child instanceof Plan) {
            	Plan plan = (Plan)child;
            	//getAllFunc(plan, list);
            } else
           	if (child instanceof Expression) {
           		Expression exp = (Expression)child;
           		getAllFunc(exp, list);
           	} else
           	if (child instanceof ExpList) {
           		getAllFunc(child, list);
           	}
        }
	}
}