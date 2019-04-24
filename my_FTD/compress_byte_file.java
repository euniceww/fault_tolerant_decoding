package my_FTD;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

import my_FTD.Node;
import my_FTD.read_file_to_byte;

public class compress_byte_file {
	
	private TreeMap<Byte, String> character_map_table = new TreeMap<Byte, String>();
	//private byte[] text_byte_vector = null;
	
	private void build_node_tree(byte[] text_byte_vector ) {
		Node root_node = null;
		TreeMap<Byte, Double> character_treemap = new TreeMap<Byte, Double>();
		calculate_character_probability(character_treemap,text_byte_vector );
		LinkedList<Node> node_list = new LinkedList<Node>();
		LinkedList<Node> node_list_1 = new LinkedList<Node>();// node_list_1是nodelist备份，用nodelist构建huffman
																// tree，再依次取node_list_1中的值在树中找到它们的huffman编码
		// System.out.println("character_treemap.size()="+character_treemap.size());
		for (Map.Entry<Byte, Double> entry : character_treemap.entrySet()) {
			Node one_node = new Node();
			one_node.character_type = true;
			one_node.character_value = entry.getKey();
			one_node.probability = entry.getValue();
			one_node.left_child = null;
			one_node.right_child = null;
			one_node.parent = null;
			insert_node_to_nodelist(one_node, node_list); // node_list is a set
															// of
															// node,probability
															// from small to big
		}
		// System.out.println("node_list.size()="+node_list.size());
		for (int i = 0; i < node_list.size(); i++) {
			Node one_node = node_list.get(i);
			node_list_1.add(one_node);
			// System.out.println(one_node);
		}
		// LinkedList<Node> node_list_1=node_list;
		while (node_list.size() > 1) {
			Node one_node_1 = node_list.get(0);
			Node one_node_2 = node_list.get(1);
			Node one_node_3 = new Node();
			one_node_3.character_type = false;
			one_node_3.character_value = 0;
			one_node_3.probability = one_node_1.probability + one_node_2.probability;
			one_node_3.left_child = one_node_2;
			one_node_3.right_child = one_node_1;
			one_node_1.parent = one_node_3;
			one_node_2.parent = one_node_3;
			node_list.removeFirst();
			node_list.removeFirst();
			insert_node_to_nodelist(one_node_3, node_list);
			// System.out.println("node_list.size()="+node_list.size());
		}
		if (node_list.size() == 1)
			root_node = node_list.get(0);
		else {
			System.err.println("node_list.size may be wrong!     " + "node_list.size=" + node_list.size());
			System.exit(1);
		}
		// System.out.println(node_list_1.size());
		for (int i = 0; i < node_list_1.size(); i++) {
			Node one_node = node_list_1.get(i);
			String[] huffman_code = new String[1];
			get_huffman_code(one_node, huffman_code);
			character_map_table.put((Byte) one_node.character_value, (String) huffman_code[0]);
			// System.out.println(one_node);
		}
	}

	private void get_huffman_code(Node one_node, String[] huffman_code) {
		huffman_code[0] = "";
		String huffman_code_1 = new String();
		huffman_code_1 = "";
		// System.out.println("huffman_code_1="+huffman_code_1);
		// huffman_code_1 += "0";
		// System.out.println("huffman_code_1="+huffman_code_1);
		while (one_node.parent != null) {
			if (one_node.parent.left_child == one_node)
				huffman_code_1 += "0";
			else if (one_node.parent.right_child == one_node)
				huffman_code_1 += "1";
			else {
				System.err.println("one_node.parent's left_child and right_childe !=one_node");
				System.exit(1);
			}
			one_node = one_node.parent;
		}
		for (int i = 0; i < huffman_code_1.length(); i++)
			huffman_code[0] += String.valueOf(huffman_code_1.charAt(huffman_code_1.length() - i - 1));
		// System.out.println("huffman_code="+huffman_code );
		huffman_code_1 = null;
	}

	private void insert_node_to_nodelist(Node one_node, LinkedList<Node> node_list) {
		boolean add_node = false;
		if (node_list.size() == 0) {
			node_list.add(one_node);
			add_node = true;
		} else {
			for (int i = 0; i < node_list.size(); i++) {
				if (node_list.get(i).probability > one_node.probability) {
					node_list.add(i, one_node);
					add_node = true;
					break;
				}
			}
		}
		if (!add_node)
			node_list.add(one_node);
	}

	private void calculate_character_probability(TreeMap<Byte, Double> character_treemap,byte[] text_byte_vector ) {
		// System.out.println("text_file_byte_vector's
		// length"+text_file_byte_vector.length);
		for (int i = 0; i < text_byte_vector.length; i++) {
			if (character_treemap.containsKey(text_byte_vector[i])) {
				Double value = character_treemap.get(text_byte_vector[i]);
				value++;
				character_treemap.put((Byte) text_byte_vector[i], (Double) value);
			} else {
				character_treemap.put((Byte) text_byte_vector[i], (Double) (double) 1);
			}
		}
		for (Map.Entry<Byte, Double> entry : character_treemap.entrySet()) {
			Double value = entry.getValue() / ((double) text_byte_vector.length / (double) 1000);
			character_treemap.put((Byte) entry.getKey(), (Double) value);
		}
	}
	
	public byte[] compress_file(byte[] text_byte_vector ) {
		build_node_tree(text_byte_vector );
		int bit_offset = 0;
		byte byte_buffer = 0;
		ArrayList<Byte> compressed_file_arraylist = new ArrayList<Byte>();
		//整个j、i循环是按位读huffman码，然后八位存储，因为1 byte有8位
		for (int j = 0; j < text_byte_vector.length; j++) {
			String huffman_code = character_map_table.get(text_byte_vector[j]);
			for (int i = 0; i < huffman_code.length(); i++) {
				// System.out.println(huffman_code.charAt(i) == '1');
				if (huffman_code.charAt(i) == '1') {
					byte bit_set = 1;
					bit_set <<= bit_offset;//bit_set 左移bit_offset位，之后补零
					byte_buffer |= bit_set;//byte_buffer按位或bit_set，然后赋给byte_buffer
					bit_offset++;
				} else if (huffman_code.charAt(i) == '0')
					bit_offset++;
				else {
					System.err.println("huffman_code may be wrong!   huffman_code=" + huffman_code);
					System.exit(1);
				}
	
				if (bit_offset == 8
						|| (j == (text_byte_vector.length - 1) && i == (huffman_code.length() - 1))) {
					compressed_file_arraylist.add(byte_buffer);
					byte_buffer = (byte) 0;
					bit_offset = 0;
				} else if (bit_offset > 8) {
					System.err.println("bit_offset may be wrong!   bit_offset=" + bit_offset);
					System.exit(1);
				}
			}
		}
		byte[] compressed_file_byte_vector = new byte[compressed_file_arraylist.size()];
		for (int i = 0; i < compressed_file_arraylist.size(); i++) {
			compressed_file_byte_vector[i] = compressed_file_arraylist.get(i);
		}
		compressed_file_arraylist = null;
		return compressed_file_byte_vector;
	}
	
	public static void main(String[] args) 
	{
		String filename="C3-Art0002.txt";
		String file_absolute_address = System.getProperty("user.dir")+"\\"+filename;
		byte[] text_byte_vector=null;
		text_byte_vector=read_file_to_byte.read_file(file_absolute_address);
		compress_byte_file compress = new compress_byte_file();
		byte[]compressed_byte_vector = compress.compress_file(text_byte_vector );
		
		
		
		try {
			read_file_to_byte.saveFile("C3-Art0002compress", compressed_byte_vector);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}

}
