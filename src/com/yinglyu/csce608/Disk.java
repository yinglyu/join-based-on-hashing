package com.yinglyu.csce608;

import java.util.ArrayList;
import java.util.List;

public class Disk {
	private static List<int[][]> blocks = new ArrayList<>();	
	static int count = 0;
	

	static public int[][] read(int index) {
		count ++;
		return blocks.get(index);
		
	}
	static public int write(int[][] block) {
		count ++;
		blocks.add(block);
		return size()-1;
	}
	
	static public int size(){
		return blocks.size();
	}
	
	static public int count() {
		return count;
	}
}
