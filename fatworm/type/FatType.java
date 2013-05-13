package fatworm.type;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.io.*;

import fatworm.expression.*;
import fatworm.log.*;
public class FatType implements Comparable, Serializable {
	private static final long serialVersionUID = 18L;
	protected boolean isNull;
	public String assocTableName;
	public String assocColumnName;
	public boolean isNull() {
		return isNull;
	}
	public void setNull() {
		isNull = true;
	}
	public FatType() {
		isNull = false;
		assocTableName = null;
		assocColumnName = null;
	}
	public int getByteArrayLength() {
		throw new DevelopException();
	}
	public FatType newNullInstance() {
		throw new DevelopException();
	}
	public FatType newInstanceFromByteBuffer(ByteBuffer bb) {
		throw new DevelopException();
	}
	public void storeIntoByteBuffer(ByteBuffer bb) {
		throw new DevelopException();
	}
	public FatType newInstance(Expression e) throws Exception {
		throw new DevelopException();
	}
	public FatType newInstance(FatType s) throws Exception {
		throw new DevelopException();
	}
	public FatType newInstance(BigDecimal s) {
		throw new DevelopException();
	}
	public FatType newZeroInstance() {
		throw new DevelopException();
	}
	public FatType newMaxInstance() {
		throw new DevelopException();
	}
	public FatType newMinInstance() {
		throw new DevelopException();
	}
	public FatType newInstance(int num) {
		throw new DevelopException();
	}
	public String getPrint(int old) {
		throw new DevelopException();
	}
	public FatType computeAnd(FatType t) {
		throw new DevelopException();
	}
	public FatType computeOr(FatType t) {
		throw new DevelopException();
	}
	public FatType computeAdd(FatType t) {
		throw new DevelopException();
	}
	public FatType computeMinus(FatType t) {
		throw new DevelopException();
	}
	public FatType computeMul(FatType t) {
		throw new DevelopException();
	}
	public FatType computeDiv(FatType t) {
		throw new DevelopException();
	}
	public FatType computeMod(FatType t) {
		throw new DevelopException();
	}
	public BigDecimal getBigDecimal() {
		throw new DevelopException();
	}
	/*
	//: '<' | '>' | '=' | '<=' | '>=' | '<>'
	public boolean computeLess(FatType t) throws Exception {
		throw new DevelopException();
	}
	public boolean computeGreater(FatType t) throws Exception{
		throw new DevelopException();
	}
	public boolean computeEqual(FatType t) throws Exception{
		throw new DevelopException();
	}
	public boolean computeLessEqual(FatType t) throws Exception{
		throw new DevelopException();
	}
	public boolean computeGreaterEqual(FatType t) throws Exception{
		throw new DevelopException();
	}
	public boolean computeNotEqual(FatType t) throws Exception{
		throw new DevelopException();
	}
	public boolean equals(Object o) {
		throw new DevelopException();
	}
	*/
	@Override
	public int compareTo(Object o) {
		throw new DevelopException();
	}
}
