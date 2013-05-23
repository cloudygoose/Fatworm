package fatworm.opt;
import fatworm.log.*;
import fatworm.logicplan.*;
import fatworm.expression.*;
import fatworm.table.*;

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
}
