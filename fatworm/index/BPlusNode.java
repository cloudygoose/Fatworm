package fatworm.index;
import fatworm.type.*;

import java.nio.ByteBuffer;
import java.util.*;

import fatworm.driver.Driver;
import fatworm.log.Log;
import fatworm.storage.*;
public class BPlusNode {
	protected FatType keyType;
	protected Integer leftChildB;
	protected Integer rightChildB;
	protected FatIndex index;
	protected int level;
	protected int blockNum;
	protected ArrayList<IndexPair> pairs;
	//pairs, leftChildB, rightChildB are data (called value) stored in file, others are get from meta
	protected FatBlock fatBlock;
	public int getBlockNum() {
		return blockNum;
	}
	public BPlusNode() {
		
	}
	public BPlusNode(FatType key, FatIndex i, int l) {
		keyType = key;
		index = i;
		level = l;
		leftChildB = -1;
		rightChildB = -1;
		pairs = new ArrayList<IndexPair>();
		blockNum = index.getNextNewBlockNumber();
		fatBlock = index.connection.bufferManager
				.getPage(new PageId(index.fileName, blockNum, index.file));
	}
	public boolean isLeaf() {
		return (level == index.maxLevel);
	}
	public boolean isRoot() {
		return (level == 1);
	}
	public ArrayList<IndexPair> splitRight(ArrayList<IndexPair> vict) {
		ArrayList<IndexPair> newList = new ArrayList<IndexPair>();
		Log.assertTrue(pairs.size() == index.maxLevel + 1);
		newList = (ArrayList<IndexPair>) pairs.subList(pairs.size() / 2, pairs.size());
		pairs = (ArrayList<IndexPair>) pairs.subList(0, pairs.size() / 2);
		return newList;
	}
	public BPlusAction insertPair(IndexPair pair) {
		//for insert there is either a insertAction or and insertAction after an exchangeAction
		BPlusAction res = null;
		if (isLeaf()) {
			res = doInsertAction(new BPlusInsertAction(pair), res);
			return res;
		}
		int in = 0;
		while (in < pairs.size() && pairs.get(in).getKey().compareTo(pair.getKey()) <= 0)
			++in;
		if (in > 0)
			in--;
		BPlusNode son = getInstanceFromFatBlock(pairs.get(in).fileOffset, keyType, index, level + 1);
		
		return null;
	}
	//An insertAction won't insert at the head, so it won't invoke the exchangeAction
	public BPlusAction doInsertAction(BPlusInsertAction insertA, BPlusAction res) {
		IndexPair oldFirstP = null, pair = insertA.getInsert();
		if (pairs.size() > 0)
			oldFirstP = pairs.get(0);
		int in = 0;
		while (in < pairs.size() && pairs.get(in).getKey().compareTo(pair.getKey()) < 0)
			++in;
		pairs.add(in, pair);
		if (pairs.size() > index.maxPointerNum) {
			ArrayList<IndexPair> newList = splitRight(pairs);
			BPlusNode newBrother = new BPlusNode(keyType, index, level);
			newBrother.leftChildB = blockNum;
			newBrother.rightChildB = rightChildB;
			rightChildB = newBrother.blockNum;
			newBrother.pairs = newList;
			res = addAction(res, new BPlusInsertAction(newList.get(0)));
			newBrother.storeToFatBlock();
		}
		storeToFatBlock();
		if (oldFirstP != null && !oldFirstP.equals(pairs.get(0)))
			res = addAction(res, new BPlusExchangeAction(new IndexPair(oldFirstP.getKey(), blockNum),
					new IndexPair(pairs.get(0).getKey(), blockNum)));
		return res;
	}
	public BPlusAction doExchangeAction(BPlusExchangeAction changeA, BPlusAction res) {
		int in;
		IndexPair oldFirstP = null, pair = insertA.getInsert();
		for (in = 0;in < pairs.size();in++)
			if (pairs.get(in).equals(changeA.getFrom()))
				break;
		
	}
	private BPlusAction addAction(BPlusAction bp, BPlusAction np) {
		if (bp == null)
			return np;
		bp.setLastAction(np);
		return bp;
	}
	public void getValuesFromFatBlock() {
		if (!fatBlock.inBuffer())
			fatBlock = index.connection.bufferManager
				.getPage(new PageId(index.fileName, blockNum, index.file));
		ByteBuffer bb = ByteBuffer.wrap(fatBlock.getBytes());
		leftChildB = bb.getInt();
		rightChildB = bb.getInt();
		IndexPair p = new IndexPair(keyType, 0); //factory
		IndexPair now = p.getInstanceFromByteBuffer(bb);
		while (now.getFileOffset() != -1) {
			pairs.add(now);
			now = p.getInstanceFromByteBuffer(bb);
		}
	}
	public void storeToFatBlock() {
		if (!fatBlock.inBuffer())
			fatBlock = index.connection.bufferManager
				.getPage(new PageId(index.fileName, blockNum, index.file));
		byte[] b = new byte[Driver.BLOCKLENGTH];
		ByteBuffer bb = ByteBuffer.wrap(b);
		bb.putInt(leftChildB);
		bb.putInt(rightChildB);
		Iterator<IndexPair> iter = pairs.iterator();
		while (iter.hasNext()) {
			iter.next().storeIntoByteBuffer(bb);
		}
		fatBlock.putBytes(bb.array(), 0);
	}
	public static BPlusNode getInstanceFromFatBlock(int block, FatType key, FatIndex i, int l) {
		BPlusNode bp = new BPlusNode();
		bp.keyType = key;
		bp.index = i;
		bp.level = l;
		bp.blockNum = block;
		bp.pairs = new ArrayList<IndexPair>();
		bp.fatBlock = i.connection.bufferManager
				.getPage(new PageId(i.fileName, block, i.file));
		bp.getValuesFromFatBlock();
		return bp;
	}
}
