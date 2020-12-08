package com.yinglyu.csce608;

import java.util.Collections;
import java.util.List;

public class Test {
	public static void main(String[] args) {
		
		int start; //count times of I/O
		String attributesS[] = {"B", "C"}, attributesR[] = {"A", "B"};
		Relation S, R;
		View T;
		Operation o = new Operation();
		
		S = new Relation(10000, 50000, 5000, attributesS, "B");
		System.out.println("Generate relation S(B, C) of 5,000 tuples, the attribute B are random integers between 10,000 and 50,000");
		R = new Relation(S.getKeys(), 1000, attributesR,  "B");
		System.out.println("Generate relation R(A, B) of 1,000 tuples, the attribute B are randomly picked from that in the relation S(B, C)");
		

		start = Disk.count();		
		T = o.Join(R, S);
		System.out.println("Nature join R and S");
		System.out.print("Times of IO: ");
		System.out.println(Disk.count() - start);
		List<Integer> target = S.getKeys();
		Collections.shuffle(target);
		System.out.println("Size of joined result: " + T.size());
		System.out.println("Picked B value:" +  target.subList(0, 20));
		System.out.println(T.where("B", target.subList(0, 20)));
		
		R = new Relation(20000, 30000, 1200, attributesR, "B");
		System.out.println("Generate relation R'(A, B) of 1,000 tuples, the attribute B are randomly picked from that in the relation S(B, C)");
		
		start = Disk.count();
		T = o.Join(R, S);
		System.out.println("Nature join R' and S");
		System.out.print("Times of IO: ");
		System.out.println(Disk.count() - start);
		System.out.println("Size of joined result: " + T.size());
		System.out.println(T);
	}
}
