import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * @author Rain Xuanyu Zhang
 * zhan2223
 * 4642453
 */

class sphkmeans
{
    public static void main(String[] args) throws IOException
    {
        SKmeansCluster sc = new SKmeansCluster();
        if (args.length > 0)
        {
            try
            {
                sc.KmeansCaller(args[0], args[1], args[2], Integer.parseInt(args[3]), Integer.parseInt(args[4]));
                //sc.KmeansCaller("bag.csv", "result.csv", "newsgroups.class.csv", Integer.parseInt(args[0]), 20);
            }
            catch (NullPointerException e)
            {
                e.printStackTrace();
            }
        }





//        int[] k = {20, 40, 60};
//        String[] input = {"bag.csv", "char3.csv", "char5.csv", "char7.csv"};
//        for (int i = 0; i < 3; i++)
//        {
//            for (int j = 0; j < 4; j++)
//            {
//                sc.KmeansCaller(input[j], k[i] + input[j] + "result.csv", "newsgroups.class.csv", k[i], 20);
//            }
//        }





//        sc.KmeansCaller("bag.csv", "result.csv", "newsgroups.class.csv", 10, 20);

    }

}

class SKmeansCluster
{
    public void KmeansCaller(String inputFilename, String outputFilename,
            String classFiles, int numClusters, int numTrials) throws IOException
    {
        System.out.println("Loading data");
        Map<String, Map<String, Double>> input = getInput(inputFilename);
        System.out.println("Finished loading data");
        System.out.println("Starting clustering, will have " + numClusters + " clusters");
        Map<String,Integer> KmeansClusterResult;
        KmeansClusterResult = Cluster(input, numClusters);
        System.out.println("Finished clustering, now writing output file");
        printClusterResult(KmeansClusterResult,outputFilename);
        System.out.println("Finished");
        System.out.println("Entropy is: " + computeEntropyPurityMatrix(outputFilename, numClusters));
    }

    private Map<String, Map<String, Double>> getInput (String filename) throws IOException
    {
        Map<String,Map<String, Double>> rawInput = new HashMap<>();

        BufferedReader br = null;
        String line = "";
        String csvSplitBy = ",";

        try
        {
            br = new BufferedReader(new FileReader(filename));
            while ((line = br.readLine()) != null)
            {
                String[] data = line.split(csvSplitBy);
                if (rawInput.containsKey(data[0]))
                {
                    rawInput.get(data[0]).put(data[1], Double.parseDouble(data[2]));
                }
                else
                {
                    Map<String, Double> newMap = new HashMap<>();
                    newMap.put(data[1], Double.parseDouble(data[2]));
                    rawInput.put(data[0], newMap);
                }
            }
        }
        catch (FileNotFoundException e)
        {
        }
        finally
        {
            if (br != null)
            {
                try
                {
                    br.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
        return rawInput;
    }

    private Map<Integer, Map<String, Double>> getInitPoint(Map<String, Map<String, Double>> InputData, int K)
    {
        Map<Integer, Map<String, Double>> InitPoints = new TreeMap<>();
        System.out.println("Initial Centroids are：");

        for (int i = 0; i < K; i++)
        {
            String temp = Integer.toString(i*2+1);
            Map<String, Double> thisvalue = InputData.get(temp);
            System.out.println((i*2+1) + " contians " + thisvalue.size() + " dimensions");
            InitPoints.put(i, thisvalue);
        }
        return InitPoints;
    }

    private void printClusterResult(Map<String, Integer> kmeansClusterResult, String ResultFile) throws IOException
    {
        FileWriter resWriter = new FileWriter(ResultFile);
        Set<Map.Entry<String,Integer>> ResultSet = kmeansClusterResult.entrySet();

        for (Map.Entry<String, Integer> entry : ResultSet)
        {
            resWriter.append(entry.getKey() + "," + entry.getValue() + "\n");
        }

        resWriter.flush();
        resWriter.close();
    }

    //Function doing the main iteration
    private Map<String, Integer> Cluster(Map<String, Map<String, Double>> InputData, int clusterNum)
    {
        int iterationTime = 0;
        String[] AllObjs = new String[InputData.size()];
        int dataSize = InputData.size();
        for (int i = 1; i <= dataSize; i++)
        {
            String temp = Integer.toString(i);
            AllObjs[i-1] = temp;
        }
        int [] clusterNumOfObj = new int[dataSize];
        TreeMap<Integer, LinkedList<Integer>> clusterObjects = new TreeMap<>();
        List<Integer> objectCandidates = new LinkedList<>();
        Map<Integer, Map<String, Double>> InitPoints = getInitPoint(InputData, clusterNum);
        double [][] distance = new double[dataSize][clusterNum];
        while (true)
        {
            System.out.println("Now iterating (" + (iterationTime++) + ")");
            for (int i = 0; i < dataSize; i++)
            {
                for(int j = 0; j < clusterNum; j++)
                {
                    distance[i][j] = 1 - cosineSimilarity(InputData.get(AllObjs[i]),InitPoints.get(j));
                }
            }
            int[] closestCentroid = new int[dataSize];
            for (int i = 0; i < dataSize; i++)
            {
                closestCentroid[i] = getClosestCentroid(distance, i);
            }
            int perfectObjectsNum = 0;
            for (int i = 0; i < dataSize; i++)
            {
                if(closestCentroid[i] == clusterNumOfObj[i])
                    perfectObjectsNum++;
            }
            if(perfectObjectsNum == dataSize || iterationTime >= 20)
                break;
            clusterObjects.clear();
            for(int i = 0; i < dataSize; i++)
            {
                clusterNumOfObj[i] = closestCentroid[i];

                if(clusterObjects.containsKey(closestCentroid[i]))
                {
                    clusterObjects.get(closestCentroid[i]).add(i);
                }
                else
                {
                    objectCandidates.clear();
                    objectCandidates.add(i);
                    LinkedList<Integer> tempMem = new LinkedList<>();
                    tempMem.addAll(objectCandidates);
                    clusterObjects.put(closestCentroid[i], tempMem);
                }
            }
            for(int i = 0; i < clusterNum; i++)
            {
                if(!clusterObjects.containsKey(i))
                {
                    continue;
                }
                Map<String, Double> newMean = updateCentroid(clusterObjects.get(i), InputData, AllObjs);
                Map<String, Double> tempMean = new TreeMap<String, Double>();
                tempMean.putAll(newMean);
                InitPoints.put(i, tempMean);
            }
        }
        Map<String, Integer> result = new TreeMap<String, Integer>();
        for(int i = 0; i < dataSize; i++)
        {
            result.put(AllObjs[i], clusterNumOfObj[i]);
        }
        return result;
    }

    private int getClosestCentroid(double[][] distance,int m)
    {
        double abtd = 5;
        int ii = 0;
        for(int i = 0; i < distance[m].length; i++)
        {
            if(distance[m][i] < abtd)
            {
                abtd = distance[m][i];
                ii = i;
            }
        }
        return ii;
    }

    private Map<String, Double> updateCentroid(List<Integer> clusters,
            Map<String, Map<String, Double>> DataSet, String[] AllObjectNums)
    {
        double memberNum = (double)clusters.size();
        Map<String, Double> newCentroids = new TreeMap<>();
        Map<String, Double> temp = new TreeMap<>();
        for (int c : clusters)
        {
            temp = DataSet.get(AllObjectNums[c]);
            Set<Map.Entry<String, Double>> tempSet = temp.entrySet();

            for (Map.Entry<String, Double> entry : tempSet)
            {
                if(newCentroids.containsKey(entry.getKey()))
                {
                    newCentroids.put(entry.getKey(), newCentroids.get(entry.getKey()) + entry.getValue());
                }

                else
                {
                    newCentroids.put(entry.getKey(), entry.getValue());
                }
            }
        }
        Set<Map.Entry<String, Double>> newCentroidsSet = newCentroids.entrySet();
        for (Map.Entry<String, Double> entry : newCentroidsSet)
        {
            newCentroids.put(entry.getKey(), newCentroids.get(entry.getKey()) / memberNum);
        }
        return newCentroids;
    }

    //compute cosine similarity
    private double cosineSimilarity(Map<String, Double> obj1, Map<String, Double> obj2)
    {
        double result = 0;
        Set<Map.Entry<String, Double>> obj1Entries = obj1.entrySet();
        for (Map.Entry<String, Double> entry : obj1Entries)
        {
            if(obj2.containsKey(entry.getKey()))
            {
                result += entry.getValue()*obj2.get(entry.getKey());
            }
        }
        return result ;
    }

    private double computeEntropyPurityMatrix(String clusterResultFile, int K) throws IOException
    {
        int[][] matrix = new int[K][20];
        FileReader reader = new FileReader(clusterResultFile);
        BufferedReader br = new BufferedReader(reader);
        String[] fpair;
        String line;
        Map<String,String> frequencyPairs = new TreeMap<>();
        double[] objsInClst = new double[K];//记录每个聚类的文件数
        double[] entropies = new double[K];//记录每个聚类的熵
        double entropy = 0;
        while ((line = br.readLine()) != null)
        {
            fpair = line.split(",");
            frequencyPairs.put(fpair[0], fpair[1]);
        }
        SortedSet<String> cateNames = new TreeSet<>();
        Set<Map.Entry<String, String>> fentry = frequencyPairs.entrySet();
        for (Map.Entry<String, String> e : fentry)
        {
            cateNames.add(e.getValue());
        }
        String[] cateNamesArray = cateNames.toArray(new String[0]);
        Map<String,Integer> cateNamesToIndex = new TreeMap<>();
        for (int i = 0; i < cateNamesArray.length; i++)
        {
            cateNamesToIndex.put(cateNamesArray[i],i);
        }
        for (Map.Entry<String, String> me : fentry)
        {
            matrix[Integer.parseInt(frequencyPairs.get(me.getKey()))][cateNamesToIndex.get(me.getValue())]++;
        }
        //for (int i = 0; i < K; i++)
        //{
        //    if (objsInClst[i] != 0)
//            {
//                for(int j = 0; j < 20; j++)
//                {
//                     double p = (double)matrix[i][j]/objsInClst[i];
//                     if(p != 0)
//                     {
//                         entropies[i] += -p * Math.log(p);
//                     }
//                }
//                //entropy += objsInClst[i]/(double)frequencyPairs.size() * entropies[i];
//            }
//        }
        try
        {
            FileWriter writer = new FileWriter("Matrix.txt", true);
            writer.write("    ");
            for (int i = 0; i < 20; i++)
            {
                writer.write(i + "    ");
            }
            writer.write("\r\n");

            for (int i = 0; i < K; i++)
            {
                writer.write(i + "    ");
                for (int j = 0; j < 20; j++)
                {
                    objsInClst[i] += matrix[i][j];
                    writer.write(matrix[i][j]+"    ");
                }
                writer.write("\r\n");
            }
            writer.write("\r\n");
            writer.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return entropy;
    }
}

