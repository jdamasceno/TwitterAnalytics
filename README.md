TwitterAnalytics
================

export HADOOP_HOME=~/Software/hadoop

Analise de tweets salvos em uma base do MongoDB utilizando o Hadoop

#Comando para gerar SparseVector: 
./mahout seq2sparse -i ~/dados/tweeter-analytics/sequence-files-mongodb/ -o ~/dados/tweeter-analytics/sparse-vector -wt tfidf -chunk 64 --minSupport 2 --minDF 1 --maxDFPercent 99

#Gerar cluster com k-means
./mahout kmeans --input ~/dados/tweeter-analytics/sparse-vector/tfidf-vectors --output ~/dados/tweeter-analytics/kmeans-cluster -k 5 --maxIter 20 --distanceMeasure org.apache.mahout.common.distance.CosineDistanceMeasure --clustering --method mapreduce --clusters ~/dados/tweeter-analytics/kmeans-cluster/clusters

