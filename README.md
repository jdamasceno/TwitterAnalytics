TwitterAnalytics
================

Analise de tweets salvos em uma base do MongoDB utilizando o Hadoop

Comandos:
	 	./mahout seq2sparse -i PATH_TO_SEQFILE  -o tmp5  -wt tfidf -chunk 64 --minSupport 2 --minDF  1 --maxDFPercent 99
		./mahout kmeans --input PATH_TO_VECTORS/tfidf-vectors/  --output clusterout -k 5  --maxIter 20 --distanceMeasure org.apache.mahout.common.distance.CosineDistanceMeasure --clustering --method mapreduce --clusters clusterout/clusters

