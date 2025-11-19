package microui.core;

import java.util.ArrayList;
import java.util.List;

public class MultiLineTextController {
	private final List<SingleLineTextController> list;

	public MultiLineTextController() {
		super();
		list = new ArrayList<SingleLineTextController>();
	}

	public boolean isEmpty() {
		return list.isEmpty();
	}
	
	
}