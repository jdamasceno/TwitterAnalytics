TwitterAnalytics
================

Analise de tweets salvos em uma base do MongoDB utilizando o Hadoop

Comando para gerar SparseVector:  ./mahout seq2sparse -i ~/Desenvolvimento/workspace/TwitterAnalytics/tmp/chunk-0  -o tmp5  -wt tfidf -chunk 64 --minSupport 2 --minDF  1 --maxDFPercent 99