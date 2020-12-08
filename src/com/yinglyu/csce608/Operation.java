package com.yinglyu.csce608;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Operation {
	public static final int blocksPerMem = 15;
	public static int tuplesPerBlock = 8;
	List<Integer>[] bucketAddressR;
	List<Integer>[] bucketAddressS;
	int[] bucketUsageR;
	int[] bucketUsageS;
	int[] commonAttr; // common Attribute pos in R and S
	private View T;
	Map<String, Integer> attrMapR;
	Map<String, Integer> attrMapS;
	Map<String, Integer> attrMapT;

	public void hash(Relation R, List<Integer>[] bucketAddress, int[] bucketUsage, int seq) {
		int size = R.size();
		List<Integer> diskAddress = R.diskAddress();
		String[] attributes = R.attributes();

		int memory[][][] = new int[blocksPerMem][tuplesPerBlock][attributes.length]; // + 1]; //TEST
		for (int blockSeq = 0; blockSeq < R.blocks(); blockSeq++) {
			memory[0] = Disk.read(diskAddress.get(blockSeq));
			for (int tupleSeq = 0; tupleSeq < tuplesPerBlock; tupleSeq++) {
				int i = blockSeq * tuplesPerBlock + tupleSeq;
				if (i >= size) {
					break;
				}
				int bucketSeq = memory[0][tupleSeq][commonAttr[seq]] % 14;
				int bucketTupleSeq = bucketUsage[bucketSeq] % tuplesPerBlock;
				for (int j = 0; j < attributes.length; j++) {
					memory[bucketSeq + 1][bucketTupleSeq][j] = memory[0][tupleSeq][j];
				}
				// memory[bucketSeq + 1][bucketTupleSeq][attributes.length] = bucketSeq; //TEST
				bucketUsage[bucketSeq]++;
				if (bucketUsage[bucketSeq] % tuplesPerBlock == 0) {
					if (bucketAddress[bucketSeq] == null) {
						bucketAddress[bucketSeq] = new ArrayList<>();
					}
					List<Integer> address = bucketAddress[bucketSeq];
					address.add(Disk.write(memory[bucketSeq + 1]));
					memory[bucketSeq + 1] = new int[tuplesPerBlock][attributes.length + 1];

				}
			}
		}
		for (int bucketSeq = 0; bucketSeq < blocksPerMem - 1; bucketSeq++) {
			if (bucketUsage[bucketSeq] % tuplesPerBlock != 0) {
				if (bucketAddress[bucketSeq] == null) {
					bucketAddress[bucketSeq] = new ArrayList<>();
				}
				List<Integer> address = bucketAddress[bucketSeq];
				address.add(Disk.write(memory[bucketSeq + 1]));
			}
			// System.out.println(bucketUsage[bucketSeq] + "," +
			// bucketAddress[bucketSeq].size());
		}
		// print(bucketAddress, bucketUsage);
	}

	public View Join(Relation R, Relation S) {
		if (R.size() > S.size()) {
			return Join(S, R);
		}
		List<Integer> address = new ArrayList<>();
		this.commonAttr = new int[2];
		attrMapR = new HashMap<>();
		attrMapS = new HashMap<>();
		attrMapT = new HashMap<>();
		String[] attributesT = new String[R.attributes().length + S.attributes().length - 1];
		for (int i = 0; i < R.attributes().length; i++) {
			attrMapR.put(R.attributes()[i], i);
			attrMapT.put(R.attributes()[i], i);
			attributesT[i] = R.attributes()[i];
		}
		for (int i = 0; i < S.attributes().length; i++) {
			attrMapS.put(S.attributes()[i], i);
			if (attrMapR.containsKey(S.attributes()[i])) {
				commonAttr[0] = attrMapR.get(S.attributes()[i]);
				commonAttr[1] = i;
			} else {
				int pos = attrMapR.size();
				attributesT[pos] = S.attributes()[i];
				attrMapT.put(S.attributes()[i], pos);
			}
		}
		// for (int i = 0; i < attributesT.length; i ++) {
		// System.out.print(attributesT[i] + "\t");
		// }
		// System.out.print("\n");
		T = new View(attributesT);
		if (R.blocks() < blocksPerMem) {
			onePass(R.diskAddress(), R.size(), S.diskAddress(), S.size());
		} else {
			int ret = twoPass(R, S);
			if (ret < 0) {
				return null;
			}
		}

		return T;
	}

	public int twoPass(Relation R, Relation S) {
		bucketAddressR = (ArrayList<Integer>[]) new ArrayList[blocksPerMem - 1];
		bucketUsageR = new int[blocksPerMem - 1];// usage of bucket
		bucketAddressS = (ArrayList<Integer>[]) new ArrayList[blocksPerMem - 1];
		bucketUsageS = new int[blocksPerMem - 1];// usage of bucket
		hash(R, bucketAddressR, bucketUsageR, 0);
		hash(S, bucketAddressS, bucketUsageS, 1);
		for (int bucketSeq = 0; bucketSeq < blocksPerMem - 1; bucketSeq++) {
			if (bucketAddressR[bucketSeq].size() >= blocksPerMem) {
				System.out.println(bucketSeq);
				System.out.println(bucketAddressR[bucketSeq].size());
				return -1;
			} else {
				onePass(bucketAddressR[bucketSeq], bucketUsageR[bucketSeq], bucketAddressS[bucketSeq],
						bucketUsageS[bucketSeq]);
			}
		}
		return 0;
	}

	public void onePass(List<Integer> addressR, int sizeR, List<Integer> addressS, int sizeS) {
		int memory[][][] = new int[blocksPerMem][tuplesPerBlock][T.attributes().length];
		for (int blockSeq = 0; blockSeq < addressR.size(); blockSeq++) {
			memory[blockSeq] = Disk.read(addressR.get(blockSeq));
			// for (int tupleSeq = 0; tupleSeq < tuplesPerBlock; tupleSeq ++) {
			// for (int value: memory[blockSeq][tupleSeq]) {
			// System.out.print(value + "\t");
			// }
			// System.out.println();
			// }

		}
		String[] attributesT = T.attributes();
		for (int blockSeqS = 0; blockSeqS < addressS.size(); blockSeqS++) {
			memory[blocksPerMem - 1] = Disk.read(addressS.get(blockSeqS));
			for (int tupleSeqS = 0; tupleSeqS < tuplesPerBlock; tupleSeqS++) {
				int i = blockSeqS * tuplesPerBlock + tupleSeqS;
				if (i >= sizeS) {
					break;
				}
				for (int j = 0; j < sizeR; j++) {
					int blockSeqR = j / tuplesPerBlock, tupleSeqR = j % tuplesPerBlock;
					if (memory[blockSeqR][tupleSeqR][commonAttr[0]] == memory[blocksPerMem
							- 1][tupleSeqS][commonAttr[1]]) {
						int[] tuple = new int[T.attributes().length];
						for (int col = 0; col < attributesT.length; col++) {
							String attr = attributesT[col];
							if (attrMapR.containsKey(attr)) {
								attrMapR.get(attr);
								// System.out.print(memory[blockSeqR][tupleSeqR][attrMapR.get(attr)] + "\t");
								tuple[col] = memory[blockSeqR][tupleSeqR][attrMapR.get(attr)];
							} else {
								attrMapS.get(attr);
								// System.out.print(memory[blocksPerMem - 1][tupleSeqS][attrMapS.get(attr)] +
								// "\t");
								tuple[col] = memory[blocksPerMem - 1][tupleSeqS][attrMapS.get(attr)];
							}
						}
						T.insert(tuple);
						// System.out.println();
					}
				}
			}
		}
	}

	private void print(List<Integer>[] bucketAddress, int[] bucketUsage) {
		StringBuilder sb = new StringBuilder();
		sb.append("bucketSeq\n");
		for (int bucketSeq = 0; bucketSeq < blocksPerMem - 1; bucketSeq++) {
			if (bucketAddress[bucketSeq] == null) {
				continue;
			}
			List<Integer> address = bucketAddress[bucketSeq];
			;
			for (int blockSeq = 0; blockSeq < address.size(); blockSeq++) {
				int addr = address.get(blockSeq);
				int block[][] = Disk.read(addr);
				for (int tupleSeq = 0; tupleSeq < tuplesPerBlock; tupleSeq++) {
					int i = blockSeq * tuplesPerBlock + tupleSeq;
					if (i >= bucketUsage[bucketSeq]) {
						break;
					}
					for (int value : block[tupleSeq]) {
						sb.append(value);
						sb.append("\t");
					}
					sb.append("\n");
				}
			}
		}
		System.out.print(sb.toString());
	}
}
