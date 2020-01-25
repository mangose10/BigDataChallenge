class TrieNode { 
    
    // Alphabet size (# of letters + 1 for space) 
    static final int ALPHABET_SIZE = 26 + 1; 
    static final int CATEGORY_COUNT = 8;

    TrieNode[] children = new TrieNode[ALPHABET_SIZE]; 
    double[] category = new double[CATEGORY_COUNT];         /* used to store category weights and the disaster weight*/
    boolean isWord = false;
      
    TrieNode(){ 
        for (int i = 0; i < ALPHABET_SIZE; i++) { 
            children[i] = null; 
        }
    } 
}; 