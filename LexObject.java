/* Dylan Porter */
/* C++ Compiler */
/* LexObject.java */

public class LexObject {
	
	private String name;
	private int type;
	private int value;
	private int size;
	private int elType;
	private boolean isInit, isString = false;
	
	public LexObject next;
	
	public LexObject(String name, int type, boolean isString) { this.name = name; this.type = type; this.isString = isString; }
	
	public LexObject(String name, int type) { this.name = name; this.type = type; }
	
	public String getName() { return name; }
	public int getType() { return type; }
	public int getValue() { return value; }
	public int getSize() { return size; }
	public int getelType() { return elType; }
	public boolean getInitialized() { return isInit; }
	public boolean getIfString() { return isString; }
	
	
	public void setName(String name) { this.name = name; }
	public void setType(int type) { this.type = type; }
	public void setValue(int value) { this.value = value; }
	public void setSize(int size) { this.size = size; }
	public void setelType(int elType) { this.elType = elType; }
	public void setInitialized(boolean isInit) { this.isInit = isInit; }
	
}
