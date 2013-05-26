package fatworm.opt;


import fatworm.log.*;
import fatworm.logicplan.*;
import fatworm.expression.*;
import fatworm.table.*;
import fatworm.type.*;
import fatworm.index.*;
import java.util.*;

public class Patterns {
	/* PatternOne
	 * SlotPlan(
                SOURCE FROM
                ProductPlan(
         ap1        AliasPlan(->MODEL1      a1
                        SlotPlan(
                            SOURCE FROM
                            FetchTablePlan(MODEL)    t1
                            CONDITION ON
                            BNFNULL
                        )SlotPlan
                    )
          ap2       AliasPlan(->T1008290346560        a2
                        SlotPlan(
                            SOURCE FROM
                            FetchTablePlan(ATOM)      t2
                            CONDITION ON
                            BNFNULL
                        )SlotPlan
                    )
                )Product
                CONDITION ON
                BNFListExp(
                    EqualExp(
                        ID:MODEL1.MODEL_ID                    a1             c1  
                        ID:T1008290346560.MODEL_ID            a2             c2(index)
                    )Equal
                )BNFListExp
            )SlotPlan
	 */
	public static boolean tryPatternOne(SlotPlan slot) {
		try {
			String a1, c1, a2, c2, t1, t2;
			BNFList bnf = slot.getBnf();
			if (bnf.size() != 1)
				throw new PatternException();
			EqualExp equal = (EqualExp)bnf.get(0);
			a1 = ((IdExpression)equal.getLeft()).getTableName();
			c1 = ((IdExpression)equal.getLeft()).getColumnName();
			a2 = ((IdExpression)equal.getRight()).getTableName();
			c2 = ((IdExpression)equal.getRight()).getColumnName();
			ProductPlan product = (ProductPlan)slot.getSource();
			AliasPlan ap1 = (AliasPlan)product.getSa();
			AliasPlan ap2 = (AliasPlan)product.getSb();
			if (!ap1.getAlias().equals(a1))
				throw new PatternException();
			if (!ap2.getAlias().equals(a2))
				throw new PatternException();
			t1 = ((FetchTablePlan)(((SlotPlan)(ap1.getSource())).getSource())).getTableName();
			t2 = ((FetchTablePlan)(((SlotPlan)(ap2.getSource())).getSource())).getTableName();
			int order = 1;
			if (!slot.getConnection().getDatabaseMgr().currentTableMgr.getTable(t2).hasIndexOn(c2)) {
				String tmp = a1; a1 = a2; a2 = tmp;
				tmp = t1; t1 = t2; t2 = tmp;
				tmp = c1; c1 = c2; c2 = tmp;
				order = 2;
				//since the slot is pushed down by a WHERE, so we don't worry about the tuple order
			}
			if (!slot.getConnection().getDatabaseMgr().currentTableMgr.getTable(t2).hasIndexOn(c2))
				throw new PatternException();
			
			//Log.v(a1 + " " + t1 + " " + c1);
			//Log.v(a2 + " " + t2 + " " + c2);
			slot.setSource(new PatternOnePlan(a1, t1, c1, a2, t2, c2, slot.getConnection()));
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	/* PatternTwo
	 * SlotPlan(
                SOURCE FROM
                ProductPlan(
                                  
                                  p1

          ap2       AliasPlan(->T1008290346560        a2
                        SlotPlan(
                            SOURCE FROM
                            FetchTablePlan(ATOM)      t2
                            CONDITION ON
                            BNFNULL
                        )SlotPlan
                    )
                )Product
                CONDITION ON
                BNFListExp(
                    EqualExp(
                        ID:MODEL1.MODEL_ID                    e1  
                        ID:T1008290346560.MODEL_ID            a2             c2(index)
                    )Equal
                )BNFListExp
            )SlotPlan
	 */
	public static boolean tryPatternTwo(SlotPlan slot) {
		try {
			String a2, c2, t2;
			IdExpression e1;
			BNFList bnf = slot.getBnf();
			/*
			if (bnf.size() != 1)
				throw new PatternException();
			*/
			EqualExp equal = (EqualExp)bnf.get(0);
			e1 = (IdExpression)equal.getLeft();
			a2 = ((IdExpression)equal.getRight()).getTableName();
			c2 = ((IdExpression)equal.getRight()).getColumnName();
			ProductPlan product = (ProductPlan)slot.getSource();
			Plan p1 = product.getSa();
			AliasPlan ap2 = (AliasPlan)product.getSb();
			Tuple tt = p1.getScan().generateExTuple();
			if (tt.getValueFromIdSW(e1) == null)
				throw new PatternException();
			if (!ap2.getAlias().equals(a2))
				throw new PatternException();
			t2 = ((FetchTablePlan)(((SlotPlan)(ap2.getSource())).getSource())).getTableName();
			if (!slot.getConnection().getDatabaseMgr().currentTableMgr.getTable(t2).hasIndexOn(c2))
				throw new PatternException();
			//Log.v(a1 + " " + t1 + " " + c1);
			//Log.v(a2 + " " + t2 + " " + c2);
			slot.setSource(new PatternTwoPlan(p1, e1, a2, t2, c2, slot.getConnection()));
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	/* PatternTwoTwo
	 *                 SOURCE FROM
                SlotPlan(
                    SOURCE FROM
                    ProductPlan(
                        SlotPlan(
                            SOURCE FROM
                            ProductPlan(
                                SlotPlan(
                                    SOURCE FROM
                                    FetchTablePlan(aircraft)
                                    CONDITION ON
                                    BNFNULL
                                )SlotPlan
                                SlotPlan(
                                    SOURCE FROM
                                    FetchTablePlan(airline)
                                    CONDITION ON
                                    BNFNULL
                                )SlotPlan
                            )Product
                            CONDITION ON
                            BNFNULL
                        )SlotPlan
                        SlotPlan(
                            SOURCE FROM
                            FetchTablePlan(flight)
                            CONDITION ON
                            BNFNULL
                        )SlotPlan
                    )Product
                    CONDITION ON
                    BNFListExp(
                        EqualExp(
                            ID:flight.aircraftcode         a2   c2
                            ID:aircraft.aircraftcode     e1
                        )Equal
	 */
	public static boolean tryPatternTwoTwo(SlotPlan slot) {
		try {
			String a2, c2, t2;
			IdExpression e1;
			BNFList bnf = slot.getBnf();
			/*
			if (bnf.size() != 1)
				throw new PatternException();
			*/
			EqualExp equal = (EqualExp)bnf.get(0);
			e1 = (IdExpression)equal.getRight();
			a2 = ((IdExpression)equal.getLeft()).getTableName();
			c2 = ((IdExpression)equal.getLeft()).getColumnName();
			ProductPlan product = (ProductPlan)slot.getSource();
			Plan p1 = product.getSa();
			FetchTablePlan p2 = (FetchTablePlan)(((SlotPlan)product.getSb()).getSource());
			Tuple tt = p1.getScan().generateExTuple();
			if (tt.getValueFromIdSW(e1) == null)
				throw new PatternException();
			t2 = p2.getTableName();
			if (!t2.equals(a2))
				return false;
			if (!slot.getConnection().getDatabaseMgr().currentTableMgr.getTable(t2).hasIndexOn(c2))
				throw new PatternException();
			//Log.v(a1 + " " + t1 + " " + c1);
			//Log.v(a2 + " " + t2 + " " + c2);
			slot.setSource(new PatternTwoPlan(p1, e1, a2, t2, c2, slot.getConnection()));
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	/* PatternThree
	 * log : statement : ProjectPlan(
        SlotPlan(
            SOURCE FROM
            FetchTablePlan(IndexTest)
            CONDITION ON
            BNFListExp(
                GreaterEqualExp(
                    ID:x
                    MinusExp(
                        0
                        12990
                    )Minus
                )And
                LessEqualExp(
                    ID:x
                    MinusExp(
                        0
                        10143
                    )Minus
                )And
                GreaterEqualExp(
                    ID:y
                    3492
                )And
                LessEqualExp(
                    ID:y
                    MinusExp(
                        0
                        43663
                    )Minus
                )And
            )BNFListExp
        )SlotPlan
	sql : select count(y) from IndexTest where x >= -12990 and x <= -10143 and y >= 3492 and y <= -43663
	 */
	public static boolean tryPatternThree(SlotPlan slot) {
		try {
			BNFList list = slot.getBnf();
			String t = ((FetchTablePlan)slot.getSource()).getTableName();
			Table table = slot.getConnection().getDatabaseMgr().currentTableMgr.getTable(t);
			String c;
			IdExpression id = null;
			FatType low = null, high = null;
			for (int i = 0;i < list.size();i++) {
				if (list.get(i) instanceof GreaterExp) {
					IdExpression nid = (IdExpression)(((GreaterExp)(list.get(i))).getLeft());
					if (id == null || id.strongEquals(nid))
						id = nid;
					else
						continue;
					low = ((GreaterExp)(list.get(i))).getRight().evaluate();
				} else
				if (list.get(i) instanceof GreaterEqualExp) {
					IdExpression nid = (IdExpression)(((GreaterEqualExp)(list.get(i))).getLeft());
					if (id == null || id.strongEquals(nid))
						id = nid;
					else
						continue;
					low = ((GreaterEqualExp)(list.get(i))).getRight().evaluate();
				} else
				if (list.get(i) instanceof LessExp) {
					IdExpression nid = (IdExpression)(((LessExp)(list.get(i))).getLeft());
					if (id == null || id.strongEquals(nid))
						id = nid;
					else
						continue;
					high = ((LessExp)(list.get(i))).getRight().evaluate();
					break;
				} else
				if (list.get(i) instanceof LessEqualExp) {
					IdExpression nid = (IdExpression)(((LessEqualExp)(list.get(i))).getLeft());
					if (id == null || id.strongEquals(nid))
						id = nid;
					else
						continue;
					high = ((LessEqualExp)(list.get(i))).getRight().evaluate();
					break;
				}	
			}
			if (id == null)
				return false;
			FatType type = slot.getScan().generateExTuple().getValueFromIdSW(id);
			if (low == null)
				low = type.newMaxInstance(); //actually min
			c = id.getColumnName();
			FatIndex index = table.getIndex(c);
			if (index == null)
				return false;
			slot.setSource(new PatternThreePlan(index, ((FetchTablePlan)slot.getSource()), id, low, high, slot.getConnection()));
			return true;
		} catch (Exception e) {
			//e.printStackTrace();
			return false;
		}
	}
	/*
	 *  OrderPlan(
    ORDERLIST:
    ID:b
    ASC
    SOURCE:
    ProjectPlan(
        ExpList:(
            ID:a
        )ExpList
        	FetchTablePlan(test1)
    	)Project
	)OrderPlan
	 */
	public static Plan fuckOrderProjectPlan(Plan p) {
		try {
			OrderPlan order = (OrderPlan)p;
			ArrayList<IdExpression> ids = order.getIdList();
			ArrayList<Boolean> bools = order.getAscList();
			ProjectPlan project = (ProjectPlan)order.getSource();
			Tuple testT = project.getScan().generateExTuple();
			boolean need = false;
			for (int i = 0;i < ids.size();i++) 
				if (testT.getValueFromIdStrong(ids.get(i)) == null) {
					need = true;
					break;
				}
			if (!need)
				return p;
			Plan ss = project.getSource();
			order.setSource(ss);
			project.setSource(order);
			//Log.v("fuck!!");
			return project;
		} catch (Exception e) {
			return p;
		}
	}
}
