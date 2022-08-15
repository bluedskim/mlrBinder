package mlrbinder;

import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class MlrBinder {
	private Path mlrPath;
	public Path getPath() {
		return mlrPath;
	}

	private List<Flag> flags = new ArrayList<>();
	public List<Flag> getFlags() {
		return flags;
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

	public MlrBinder flag(Flag flag) {
		flags.add(flag);
		return this;
	}

	public void run(InputStreamReader isr) {

	}
}
