# Twitter Trend

#### Steps to run the assessment:

1. Its a gradle based project. To build the project and downloads its dependencies, use following command:
   ./gradlew
2. Import the project into IDE of choice (IntelliJ, Eclipse etc)
3. The project is self contained. The in folder contains:
    a) The small dataset file - twitter.json
    b) The file containing ignore words - IgnoreWords.txt
4. The driver program - TwitterTrend takes following arguments:
   a) TOP N
   b) Unit of Time (DAY|MINUTE|HOUR|MONTH|YEAR)
   c) The name of file to be present in the "in" folder. If no name is given, program will try to load twitter.jsons
5. To run large set on spark, please follow the following steps:
   a) Make a jar using command:
      ./gradlew jar
      This will produce a jar by the name TwitterTrend.jar in folder build/lib
   b) Copy TwitterTrend.jar to apache spark distribution folder
   c) Make a directory "in" and copy large dataset & IgnoreWords.txt files
   d) Run large data use using command:
      ./spark-2.3.0-bin-hadoop2.7/bin/spark-submit --executor-memory 4G TwitterTrend.jar [TOP N] [UNIT OF TIME] [NAME OF THE DATASET]
# TwitterTrend
