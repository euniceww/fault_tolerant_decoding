package my_FTD;



public class Node {
	public double probability;
	public boolean character_type;
	public byte character_value;
	public Node left_child, right_child, parent;

	public Node() {
		probability = 0;
		character_type = false;
		character_value = (byte) 0;
		left_child = null;
		right_child = null;
		parent = null;
	}

}
