package com.yinglyu.csce608;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class View {
	private  String[] attributes;
	public List<int[]> tuples;
	
	public View(String[] attributes) {
    	this.attributes = attributes;
    	this.tuples = new ArrayList<int[]>();
    }
	
	public String[] attributes() {
		return attributes;
	}
	
	public void insert(int[] tuple) {
		tuples.add(tuple);
	}
	
	public View where(String key, List<Integer> values) {
		int col = -1;
		for (int i = 0; i < attributes.length; i ++) {
			if (key.equals(attributes[i])) {
				col = i;
				break;
			}
		}
		if (col == -1) {
			return null;
		}
		View res = new View(this.attributes);
		Set<Integer> set = new HashSet<>();
		set.addAll(values);
		for (int[] tuple: tuples) {
			if (set.contains(tuple[col])) {
				res.insert(tuple);
			}
		}
		return res;
	}
	
	public int size() {
		return tuples.size();
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (String attr: attributes) {
			sb.append(attr);
			sb.append("\t");
		}
		sb.append("\n");
		for (int[] tuple: tuples) {
			for (int value: tuple) {
				sb.append(value);
				sb.append("\t");
			}
			sb.append("\n");
		}
		
		return sb.toString();
	}
}
