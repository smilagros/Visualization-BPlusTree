# Visualization-BPlusTree
 Implementar un algoritmo de visualizacion de un arbol B+-tree para algoritmos de insert/remove/find.
 
 

B+Tree INSERTION ALGORITHM:


** B+ Nodes maintain n-1 key values and n pointers

1. 	Find the leaf page in which the insertion should
	take place.

2.	If there is space, put the new key in, rearrange
	keys and exit.

3. 	If there is no space for the newly arrived key,
	then allocate a new leaf node.
	Split the keys between the two nodes.
        ceil(n/2) keys/values go into the 1st block
	and the remaining into the 2nd.
	Propagate upwards the first key/value of the
	2nd block.

4. 	If insertion in the internal node is possible
		then do the insertion (of the just propagated key),
		re-arrange pointers properly, and exit.
	Otherwise,
		The internal node has to be split up:
		- Acquire a new block.
                - All the keys less than the MEDIAN go
                  to the 1st block and all the keys greater
                  than the median go to the 2nd (WITHOUT the median).
		- Rearrange and keep track of pointers properly.
		- The median needs to be up-propagated
        Goto to 4.


B+Tree DELETION ALGORITHM:


** B+ Nodes maintain n-1 key values and n pointers

1. 	Find the entry to be deleted.

2.	Delete entry from leaf. If this leaf, call it Z,
	is left with less than the required # of entries go to 3.
	Otherwise, Exit.

3.	If neither the right nor the left sibling of Z
	        (off the same parent) has more
                than ceil[(n-1)/2] then GoTo 4.
	Otherwise, redistribute the entries from ONE sibling
	        with those in Z, so that half of the total are
                in sibling and half in Z.
		* If one of the siblings has more records than
		  the other use it.
		* If both have the same # of keys, use the left one
		  (arbitrary choice)
	Change the new distinguishing key of Z (from its sibling)
	        in the parent and Exit.

4.	Combine the entries of one sibling with those in Z.
	 /* this happens only when BOTH ADJACENT siblings
	    have exactly ceil[n/2] entries;
            u may choose the left sibling if there is one
	  */
	Delete from parent the distinguishing key of Z (from
	its siblings ) and delete the address of Z.

	If the parent is left with less than the necessary
	        entries (sparse) GoTo 5.
	Otherwise, Exit.

	If the parent is the root, it cannot become sparse
	        unless no value is left. In this case, the present
	        node with the records of Z  and its sibling, become
	        the root which is now the entire tree.

5.	Internal Node Redistribution:
        If an internal node is left sparse, temporarily concatanate this
        internal node with the larger of its siblings (or left if both are
        of equal size) ALONG WITH the key distinguishing it from its sibling.

	If there are exactly (n-1) keys in this concatanation GoTo 6.
	Otherwise (there are more than [n-1] keys),
                redistribute values by placing the new middle value
                in the parent in the place of the previous distinguishing
	        value and Exit.

6.	The (n-1) keys are now merged to become one internal node.
        The key and addresses in the parent are deleted.
        If the parent is too sparse GoTo 5.
	Otherwise, Exit.
	
	
	References 
	 https://www.cs.usfca.edu/~galles/visualization/BTree.html
