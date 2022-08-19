package mlrbinder;

/**
 * represents object of verb
 */
public class Object {
	private String name;

	public Object(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}
}
