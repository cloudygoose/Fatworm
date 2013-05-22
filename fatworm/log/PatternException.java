package fatworm.log;

public class PatternException extends RuntimeException{
	String s;
	public PatternException() {
		
	}
	public PatternException(String news) {
		s = news;
	}
}
