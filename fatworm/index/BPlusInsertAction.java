package fatworm.index;

public class BPlusInsertAction extends BPlusAction {
	IndexPair insertP;
	public BPlusInsertAction(IndexPair ip) {
		insertP = ip;
	}
	public IndexPair getInsert() {
		return insertP;
	}
}
