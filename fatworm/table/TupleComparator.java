package fatworm.table;

import java.util.ArrayList;
import fatworm.type.*;
import fatworm.expression.IdExpression;
import fatworm.log.Log;

public class TupleComparator {
	ArrayList<Boolean> ascList;
	ArrayList<IdExpression> ids;
	public TupleComparator(ArrayList<Boolean> asc, ArrayList<IdExpression> id) {
		ascList = asc;
		ids = id;
	}
	public boolean bigger(Tuple t1, Tuple t2) {
		if (ids != null) {
			for (int i = 0;i < ascList.size();i++) {
				FatType f1 = t1.getValueFromIdSW(ids.get(i));
				FatType f2 = t2.getValueFromIdSW(ids.get(i));
				//Log.v(f1.getPrint(0) + " com " + f2.getPrint(0));
				int bb = f1.compareTo(f2);
				//Log.v(bb + " " + ascList.get(i));
				if (bb > 0) {
					if (ascList.get(i).equals(false))
						return true;
					else
						return false;
				}
				if (bb < 0) {
					if (ascList.get(i).equals(false))
						return false;
					else
						return true;
				}
			}
			return false;
		} else
		{
			for (int i = 0;i < t1.size();i++) {
				if (t1.get(i).getValue().compareTo(t2.get(i).getValue()) > 0)
					return true;
			}
		}
		return false;
	}
}
