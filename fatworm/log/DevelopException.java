package fatworm.log;

public class DevelopException extends RuntimeException{
	String s;
	public DevelopException() {
		
	}
	public DevelopException(String news) {
		s = news;
	}
}
