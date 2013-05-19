package fatworm.index;

public class BPlusInsertAction extends BPlusAction {
	IndexPair insertP;
	Integer beforeBlock;
	public BPlusInsertAction(IndexPair ip) {
		super();
		insertP = ip;
		beforeBlock = null;
	}
	public BPlusInsertAction(IndexPair ip, Integer before) {
		super();
		insertP = ip;
		beforeBlock = before;
	}
	public Integer getBeforeBlock() {
		return beforeBlock;
	}
	public IndexPair getInsert() {
		return insertP;
	}
}
