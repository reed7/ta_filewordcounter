Count the word appearance on a text file. Supports file size up to tens of gigabytes.
Result will be printed out on logs.

* This program is only capable of handling text files
* Words not from dictionary might cause problem e.g. a 1GB string without any space can crash the application.
* This program counts words disregarding case
* "He's" will be counted as two words "he" and "s"

# Usage
java -jar -Dlog4j.configurationFile=log4j.properties filewordcount-[version]-jar-with-dependencies.jar [options]

## Mandatory option:
-f,--file <arg>      File to be processed

## Optional options:
-o,--order <arg>     Specifies the order in which results are displayed: [inc]reasing | [dec]reasing.Default "dec".<br/>
-W,--workers <arg>   Number of workers.Default 2xCPU_cores<br/>
-w,--words <arg>     Number of top words statistics to show.Default 10.<br/>

# Recommend JVM options
-Xms4G<br/>
-Xmx4G

# Build the source code
mvn package

# Sample result output
2018-06-10 12:57:12,077 c.t.e.f.FileWordCounter [main] Counting finish in 246793 ms! Found 2125960 different words on the file.<br/>
2018-06-10 12:57:12,497 c.t.e.f.Main [main] the: 80937452<br/>
2018-06-10 12:57:12,497 c.t.e.f.Main [main] review: 55850363<br/>
2018-06-10 12:57:12,497 c.t.e.f.Main [main] and: 39792781<br/>
2018-06-10 12:57:12,498 c.t.e.f.Main [main] a: 37752566<br/>
2018-06-10 12:57:12,498 c.t.e.f.Main [main] of: 34191930<br/>
2018-06-10 12:57:12,498 c.t.e.f.Main [main] to: 30432879<br/>
2018-06-10 12:57:12,498 c.t.e.f.Main [main] is: 25975298<br/>
2018-06-10 12:57:12,498 c.t.e.f.Main [main] it: 22609895<br/>
2018-06-10 12:57:12,498 c.t.e.f.Main [main] i: 21809717<br/>
2018-06-10 12:57:12,498 c.t.e.f.Main [main] in: 20938574<br/>


Several typical test cases can be found under test_results/

