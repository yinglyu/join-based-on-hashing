package com.yinglyu.csce608;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Relation {

    public static final int tuplesPerBlock = 8;
	private List<Integer> range;
	private int size;
	private int blocks;
	private  String[] attributes;
    private List<Integer> diskAddress ;
    public List<int[]> output;
    
    public Relation(int start, int end, int size, String[] attributes, String key) {
		this(IntStream.rangeClosed(start, end).boxed().collect(Collectors.toList()), size, attributes, key);
	}
    
    public Relation(List<Integer> range, int size, String[] attributes, String key) {
    	Collections.shuffle(range);
    	this.range = new ArrayList<>(range.subList(0, size));
		this.blocks = (size + tuplesPerBlock - 1) / tuplesPerBlock;
		this.diskAddress = new ArrayList<>();
		this.attributes = attributes;
		this.size = size;
		int keyIndex = 0;
		for (int i = 0; i < attributes.length ; i++) {
			if (attributes[i].equals(key)) {
				keyIndex = i;
				break;
			}
		}
		for (int blockSeq = 0; blockSeq < blocks; blockSeq++) {
			int block[][] = new int[tuplesPerBlock][attributes.length];
			for (int tupleSeq = 0; tupleSeq < tuplesPerBlock; tupleSeq ++ ) {
				int i = blockSeq * tuplesPerBlock + tupleSeq;
				if (i >= size) {
					break;
				}
				for (int j = 0; j < attributes.length; j ++) {
					block[tupleSeq][j] = i;
				}
				block[tupleSeq][keyIndex] = range.get(i);
				
			}
			diskAddress.add(Disk.write(block));
		}
    }
    
    public Relation(String[] attributes) {
    	this.diskAddress = new ArrayList<>();
    	this.attributes = attributes;
    	this.output = new ArrayList<int[]>();
    }
	
	
	
	public int size() {
		return size;
	}
	
	public int blocks() {
		return blocks;
	}
	
	public String[] attributes() {
		return attributes;
	}
	
	public List<Integer> diskAddress () {
		List<Integer> address = new ArrayList<>();
		address.addAll(diskAddress);
//		System.out.println("diskAdd getter: " + diskAddress);
		return address;
	}
	
	public List<Integer> getKeys(){
		return range.subList(0, size);
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (String attr: attributes) {
			sb.append(attr);
			sb.append("\t");
		}
		
		sb.append("\n");
		for (int blockSeq = 0; blockSeq < blocks; blockSeq++) {
			int block[][] = Disk.read(diskAddress.get(blockSeq));
			for (int tupleSeq = 0; tupleSeq < tuplesPerBlock; tupleSeq ++ ) {
				int i = blockSeq * tuplesPerBlock + tupleSeq;
				if (i >= size) {
					break;
				}
				for (int value: block[tupleSeq]) {
					sb.append(value);
					sb.append("\t");
				}
				sb.append("\n");
			}
		}
		return sb.toString();
	}

}
