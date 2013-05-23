package fatworm.index;
import fatworm.type.*;
import java.util.*;

import fatworm.driver.Driver;
import fatworm.log.*;
import fatworm.table.*;
import fatworm.storage.*;
public class BPlusCursor {
	FatIndex index;
	BPlusNode nowNode;
	public int nowOffset;
	BPlusNode beginNode;
	int beginOffset;
	Tuple nextT;
	fatworm.driver.Connection connection;
	public BPlusCursor(FatIndex i) {
		index = i;
		connection = index.connection;
	}
	public void setFirstBiggerThan(FatType first) {
		int level = 1;
		//Log.v("BPlus : " + index.rootBlock);
		nowNode = BPlusNode.getInstanceFromFatBlock(index.rootBlock, index.keyType, index, 1, true, true);
		while (level != index.maxLevel) {
			//if (first instanceof FatInteger && ((FatInteger) first).getNumber() == -4893)
				//Log.v(nowOffset + " " + nowNode.blockNum + " " + nowNode.pairs.size());
			List<IndexPair> pairs = nowNode.pairs;
			int i;
			for (i = 0;i < pairs.size();i++)
				if (i == pairs.size() - 1 || pairs.get(i).getKey().compareTo(first) > 0 ||
				(pairs.get(i).getKey().compareTo(first) <= 0 && pairs.get(i + 1).getKey().compareTo(first) >= 0))
				{
					nowNode = BPlusNode.getInstanceFromFatBlock(pairs.get(i).getFileOffset(), 
							index.keyType, index, level + 1, false, false);
					level = level + 1;
					break;
				}
			if (i == pairs.size())
				throw new DevelopException();
		}
		//Log.v("BPlusCursor : " + nowNode.pairs.size());
		nowOffset = 0;
		while (true) {
			if (nowOffset == nowNode.pairs.size() && nowNode.rightBrotherB == -1)
				break;
			if (nowOffset == nowNode.pairs.size()) {
				nowNode = BPlusNode.getInstanceFromFatBlock(nowNode.rightBrotherB, index.keyType, index, 1, true, true);
				nowOffset = 0;
			}
			if (nowNode.pairs.get(nowOffset).getKey().compareTo(first) >= 0)
				break;
			nowOffset++;
		}
		beginNode = nowNode;
		beginOffset = nowOffset;
		if (first instanceof FatInteger && ((FatInteger) first).getNumber() == -4893) {
			//index.logBPlus();
			//Log.v(nowOffset + " " + nowNode.blockNum);
		}
	}
	public boolean next() {
		nextT = null;
		if (nowOffset == nowNode.pairs.size())
			return false;

		int pos = nowNode.pairs.get(nowOffset).getFileOffset();
		int Bid = pos / Driver.BLOCKLENGTH;
		pos = (pos % Driver.BLOCKLENGTH) + 1;
		FatBlock block = index.connection.bufferManager.getPage(new PageId(index.table.getFileName(), Bid, index.table));
		byte[] bt = block.getBytes(pos, index.table.getSchema().getByteArrayLength());
		this.nextT = index.table.getSchema().getTupleFromByteArray(bt);
		nowOffset++;
		if (nowOffset == nowNode.pairs.size()) {
			if (nowNode.rightBrotherB != -1) {
				nowNode = BPlusNode.getInstanceFromFatBlock(nowNode.rightBrotherB, index.keyType, index, 1, true, true);
				nowOffset = 0;
			}
		}
		return true;
	}
	public void beforeFirst() {
		nowNode = beginNode;
		nowOffset = beginOffset;
	}
	public Tuple getT() {
		if (nextT == null)
			throw new DevelopException();
		return nextT;
	}
}
