# Spherical-kMeans-Clustering
   
####Objective
The purpose of this project is to write a clustering algorithm based on spherical k-means for clustering objects corresponding to sparse high dimensional vectors. The project consists of multiple components that involve getting the dataset, selecting the subset to cluster, pre-processing the dataset to convert it into a sparse representation, clustering the dataset, and evaluating the quality of the clustering solution. The steps associated with subset selection and pre-processing need to be done using scripts in any combination of bash, csh, python, or perl, whereas the actual clustering needs to be written in either java, c++, or c.   
   
####DataSet
The dataset for this assignment will be derived from the "Twenty Newsgroups Data Set" that is available at the [UCI Machine Learning Repository](https://archive.ics.uci.edu/ml/datasets/Twenty+Newsgroups). Download the 20_newsgroups.tar.gz file from the "Data Folder" (the file is 17MB compress). Also, read the various html files in the above folder to get an idea as to what the dataset is all about.   
   
####Selecting the Subset of the Dataset for Clustering
From each of the newsgroup, select only the postings that correspond to original postings and not replies. You can simply use the following rule to select that subset of postings. Any postings that have a "Re:" or a "re:" as the first part of their "Subject:" line is a reply and should be eliminated.   
   
####Obtaining the Sparse Representation
Process each posting that remains and extract the text associated with that posting. The text will be extracted by combining the text associated with the "Subject:" line and all the text that comes after the "Lines:" line. Once you have extracted the text for each posting, you need to derive two types of representations: (i) bag of words, and (ii) bag of n-grams. For both of these representations, you first need to clean up the text. To do that perform the following steps in that sequence:   
1. Eliminate any non-ascii characters.   
2. Change the character case to lower-case.   
3. Replace any non alphanumeric characters with space.   
4. Split the text into tokens, using space as the delimiter.   
5. Eliminate any tokens that contain only digits.    
   
####Bag of Words Representation
Collect all the tokens that remained after step 5 (above) across all postings and use them to represent each posting as a frequency vector in the distinct token space.   
   
####N-Gram Representation
For each posting, construct a space separating string that contains all the tokens that survived step 5 above and make sure that these tokens are in the same sequence as they appear in the original posting. Treat each of these strings as a sequence of characters and generate all n-character n-grams using a sliding window approach in which you slide the window by a single character. The value of n is a parameter in this method and in your experiments I will like you to try values of 3, 5. and 7. The unique set of extracted n-grams over all postings will define the dimensions of the feature space of the representation. Using this feature space, represent each positing as a frequency vector of the n-grams that they contain.   
   
####Clustering
Develop a partitional clustering algorithm based on spherical k-means. Your program should take as input the vector-space representation of the objects, the number of clusters, the number of trials that it will perform (each trial will be seeded with a different randomly selected set of objects), and the class labels of the objects. The class label of an object is the newsgroup that the corresponding posting appeared. Upon completion, your program should write the clustering solution to a file, and report the value of the objective function for the best trial and that solution will be used to analyze the characteristics of the clusters in terms of the class distribution of the objects that they contain. To do that, your program should output a two dimensional matrix of dimensions (# of clusters)*(# of classes) whose entries will be the number of objects of a particular class that belongs to a particular cluster. To evaluate the quality of the clustering solution that you obtain, your program needs to compute the entropy and purity of the clustering solution with respect to that class distribution (you can find the equations for these two measures in slide no. 120 in the clustering slides).   
Here is a sample command line for your program:   
```
sphkmeans input-file class-file #clusters #trials output-file
```
The format of the input file must be in the (i,j,v) format, where i is the object #, j is the dimension #, and v is the corresponding value (frequency). The object # is a unique ID given to each posting. Note that the input file should be a comma-separated file. 
   
The format of the class file must be in the (i, label) comma-separated format, where i is the object# and label is the newsgroup name.
   
The format of the output file must be in the (i, cluster#) comma-separated format, where i is the object# and cluster# is a number between 0 and #clusters-1 indicating the cluster in which object i belongs to.
   
In your experiments, you should set #trials to be 20. Please use these 20 seeds: [1,3,5,7,9,11,13,15,17,19,21,23,25,27,29,31,33,35,37,39].
   
Upon finishing, your program should print the value of the objective function for the best trial in addition to both the entropy and purity of the best clustering solution to the standard output.
   
