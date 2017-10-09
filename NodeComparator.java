package search;

import java.util.Comparator;

public class NodeComparator implements Comparator<Node> {

	@Override
	public int compare(Node n1, Node n2) {
		if(n1.getCost()<n2.getCost()){
			return -1;
		} else {
			return 1;
		}
	}

}
