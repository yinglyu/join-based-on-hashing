# join-based-on-hashing
This part of the project implements the two-pass join algorithm based on hashing. Assume that you have two relations R(A,B) and S(B,C), and you want to compute their natural join R(A, B) I S(B, C) using the two-pass join algorithm based on hashing. Assume that each block can hold upto 8 tuples of the relations, and that we have a virtual main memory of 15 blocks and a virtual disk whose size is unlimited. You can pick any data structures for the virtual main memory and virtual disk. However, the join operation can only be performed when the tuples are in the virtual main memory.
## Relation generation
The list of generated data will be passed to the constructor function for a relation class as well as the attributes and key information.
Once a block of tuples has been generated. They will be written to a disk and the disk address will be saved to the relation object.

## Virtual disk 
The Disk is defined as a Class. Since the space of the disk is supposed to be unlimited. I use a list of a 2D array to represent blocks. The first dimension is the tuple sequence, the second dimension is the column sequence.

## Main memory
The main memory is represented by a 3D array in the operation function. The blocks in memory are fixed as defined in the project requirement.


## Hashing
The purpose of the hash function is to split the tuples into several buckets according to the common attribute. Therefore, I use `mod 14` operation to divided the tuples into 14 buckets according to the value of the common attribute.

## Join algorithm
I find the common attributes to determine the attributes of the join result. And then the one-pass function will be called if the smaller relation can fit into memory. Otherwise, the two-pass function will be called.
In the two-pass function, the two relations will be hashed. A list of disk addresses will be stored in an array according to bucketSeq. the one-pass function will be called for each bucket with the two relations in the current bucket if the bucket of the smaller relation can fit into memory.

