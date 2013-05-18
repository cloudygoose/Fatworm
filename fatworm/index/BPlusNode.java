package fatworm.index;
import fatworm.type.*;


import java.nio.ByteBuffer;
import java.util.*;

import fatworm.log.*;
import fatworm.driver.Driver;
import fatworm.storage.*;
public class BPlusNode {
	protected FatType keyType;
	protected Integer leftBrotherB;
	protected Integer rightBrotherB;
	protected FatIndex index;
	protected boolean isLeftM;
	protected boolean isRightM;
	protected int level;
	protected int blockNum;
	protected List<IndexPair> pairs;
	//pairs, leftChildB, rightChildB are data (called value) stored in file, others are get from meta
	protected FatBlock fatBlock;
	public int getBlockNum() {
		return blockNum;
	}
	public BPlusNode() {
		
	}
	public BPlusNode(FatType key, FatIndex i, int l, boolean isL, boolean isR) {
		keyType = key;
		index = i;
		level = l;
		leftBrotherB = -1;
		rightBrotherB = -1;
		isLeftM = isL;
		isRightM = isR;
		pairs = new ArrayList<IndexPair>();
		blockNum = index.getNextNewBlockNumber();
		//Log.v("!!new block!! : " + blockNum);
		fatBlock = index.connection.bufferManager
				.getPage(new PageId(index.fileName, blockNum, index.file));
		storeToFatBlock();
	}
	public boolean isLeaf() {
		return (level == index.maxLevel);
	}
	public boolean isRoot() {
		return (level == 1);
	}
	public List<IndexPair> splitRight(List<IndexPair> vict) {
		Log.assertTrue(pairs.size() == index.maxPointerNum + 1);
		List<IndexPair> newList = pairs.subList(pairs.size() / 2, pairs.size());
		pairs = pairs.subList(0, pairs.size() / 2);
		return newList;
	}
	public BPlusAction insertPair(IndexPair pair) {
		//for insert there is either a insertAction or and insertAction after an exchangeAction
		if (isLeaf()) {
			BPlusAction res = null;
			res = doInsertAction(new BPlusInsertAction(pair), res);
			return res;
		}
		int in = 0;
		while (in < pairs.size() && pairs.get(in).getKey().compareTo(pair.getKey()) <= 0)
			++in;
		if (in > 0)
			in--;
		BPlusNode son = getInstanceFromFatBlock(pairs.get(in).fileOffset, keyType, index, level + 1, in == 0, in == pairs.size() - 1);
		BPlusAction toDo = son.insertPair(pair);
		BPlusAction invoke = doAllActionInvoke(toDo);
		return invoke;
	}
	public BPlusAction doAllActionInvoke(BPlusAction action) {
		//first exchange, then delete, then insert
		BPlusAction res = new BPlusAction();
		BPlusAction tmp = action;
		//Exchange
		while (action != null) {
			if (action instanceof BPlusExchangeAction)
				res = doExchangeAction((BPlusExchangeAction)action, res);
			action = action.getNextAction();
		}
		//TODO:Delete
		//Insert
		action = tmp;
		while (action != null) {
			if (action instanceof BPlusInsertAction)
				res = doInsertAction((BPlusInsertAction)action, res);
			action = action.getNextAction();
		}
		return res.getNextAction();
	}
	//An insertAction won't insert at the head, so it won't invoke the exchangeAction
	public BPlusAction doInsertAction(BPlusInsertAction insertA, BPlusAction res) {
		IndexPair oldFirstP = null, pair = insertA.getInsert();
		if (pairs.size() > 0)
			oldFirstP = pairs.get(0);
		int in = 0;
		while (in < pairs.size() && pairs.get(in).getKey().compareTo(pair.getKey()) < 0)
			++in;
		//Log.v("before : in : " + in + " pairs.size() : " + pairs.size() + " blockNum : " + blockNum);		
		pairs.add(in, pair);
		//Log.v("after : in : " + in + " pairs.size() : " + pairs.size() + " blockNum : " + blockNum);		
		if (pairs.size() > index.maxPointerNum) {
			//Log.v("splitting");
			List<IndexPair> newList = splitRight(pairs);
			BPlusNode newBrother = new BPlusNode(keyType, index, level, false, isRightM);
			newBrother.leftBrotherB = blockNum;
			newBrother.rightBrotherB = rightBrotherB;
			rightBrotherB = newBrother.blockNum;
			newBrother.pairs = newList;
			res = addAction(res, new BPlusInsertAction(new IndexPair(newList.get(0).getKey(), newBrother.blockNum)));
			isRightM = false;
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
		IndexPair oldFirstP = pairs.get(0), pair = changeA.getFrom();
		for (in = 0;in < pairs.size();in++)
			if (pairs.get(in).equals(pair))
				break;
		Log.assertTrue(in != pairs.size());
		pairs.set(in, changeA.getTo());
		if (in == 0)
			res = addAction(res, new BPlusExchangeAction(new IndexPair(oldFirstP.getKey(), blockNum),
					new IndexPair(pairs.get(0).getKey(), blockNum)));
		storeToFatBlock();
		return res;
	}
	public BPlusAction doDeleteAction(BPlusDeleteAction delA, BPlusAction res) {
		int in;
		IndexPair oldFirstP = pairs.get(0), pair = delA.getDeletePair();
		for (in = 0;in < pairs.size();in++)
			if (pairs.get(in).equals(pair))
				break;
		pairs.remove(in);
		if (isRoot()) {
			storeToFatBlock();
			return res;
		}
		if (!pairs.get(0).equals(pair))
			res = addAction(res, new BPlusExchangeAction(new IndexPair(oldFirstP.getKey(), blockNum),
					new IndexPair(pairs.get(0).getKey(), blockNum)));
		storeToFatBlock();
		return res;
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
		leftBrotherB = bb.getInt();
		rightBrotherB = bb.getInt();
		IndexPair p = new IndexPair(keyType, 0); //factory
		int nowHave = 0;
		pairs = new ArrayList<IndexPair>();
		//Log.v(index.maxPointerNum);
		while (nowHave < index.maxPointerNum) {
			//Log.v("get now : " + now.getPrint() + " blockNum : " + blockNum);
			IndexPair now = p.getInstanceFromByteBuffer(bb);
			if (now.getFileOffset() == -1)
				break;
			pairs.add(now);
			nowHave++;
		}
	}
	public void storeToFatBlock() {
		if (!fatBlock.inBuffer())
			fatBlock = index.connection.bufferManager
				.getPage(new PageId(index.fileName, blockNum, index.file));
		byte[] b = new byte[Driver.BLOCKLENGTH];
		ByteBuffer bb = ByteBuffer.wrap(b);
		bb.putInt(leftBrotherB);
		bb.putInt(rightBrotherB);
		Iterator<IndexPair> iter = pairs.iterator();
		while (iter.hasNext()) {
			iter.next().storeIntoByteBuffer(bb);
		}
		//add the end mark
		//Log.v("compare : " + pairs.size() + " " + index.maxPointerNum);
		if (pairs.size() < index.maxPointerNum) {
			//Log.v("!!!put null!!!" + blockNum);
			IndexPair nullPair = new IndexPair(keyType, -1);
			nullPair.storeIntoByteBuffer(bb);
		}
		fatBlock.putBytes(bb.array(), 0);
	}
	public static BPlusNode getInstanceFromFatBlock(int block, FatType key, FatIndex i, int l, boolean isL, boolean isR) {
		BPlusNode bp = new BPlusNode();
		bp.keyType = key;
		bp.index = i;
		bp.level = l;
		bp.blockNum = block;
		
		bp.fatBlock = i.connection.bufferManager
				.getPage(new PageId(i.fileName, block, i.file));
		bp.getValuesFromFatBlock();
		bp.isLeftM = isL;
		bp.isRightM = isR;
		return bp;
	}
	public void LogBPlus() {
		Log.v("logging BPlus block: " + blockNum + " level : " + level + " maxLevel : " + index.maxLevel);
		if (isLeaf()) {
			for (int i = 0;i < pairs.size();i++)
				Log.v(pairs.get(i).getPrint());
			return;
		}
		for (int i = 0;i < pairs.size();i++) {
			BPlusNode son = getInstanceFromFatBlock(pairs.get(i).getFileOffset(), keyType, index, level + 1, i == 0, i == pairs.size() - 1);
			son.LogBPlus();
		}
	}
}
