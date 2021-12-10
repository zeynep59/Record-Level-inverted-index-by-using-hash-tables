

public class Value {

	private String fileName;
	private int frequency ;
	
	public Value(String fileName, int frequency) {
		this.fileName = fileName;
		this.frequency = frequency;
	}
	
	public void increaseFrequency() {
		frequency = this.frequency+1;
	}
	
	public int getFrequency() {
		return frequency;
	}
	
	public String getFileName() {
		return fileName;
	}
	
}
