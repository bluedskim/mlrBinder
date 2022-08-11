package mlrbinder;

import java.nio.file.Path;

public class MlrBinder {
	private Path mlrPath;

	public Path getPath() {
		return mlrPath;
	}

	public MlrBinder() {
		super();
	}

	public MlrBinder(Path mlrPath) {
		this();
		this.mlrPath = mlrPath;
	}

	public MlrBinder path(Path mlrPath) {
		this.mlrPath = mlrPath;
		return this;
	}
}
