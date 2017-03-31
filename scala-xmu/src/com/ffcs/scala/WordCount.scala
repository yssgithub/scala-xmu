package com.ffcs.scala

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext

object WordCount {
  def main(args: Array[String]) {
//    val inputFile = "D:\\DEV\\workspace_scala\\scala-xmu\\resources\\word.txt"
    val inputFile = "D:\\Documents\\GitHub\\scala-xmu\\scala-xmu\\resources\\word.txt"
    val conf = new SparkConf().setAppName("WordCount").setMaster("local[2]")
  
//    val inputFile = "hdfs://192.168.18.66:9000/Hadoop/Input/word.txt"
//    val conf = new SparkConf().setAppName("WordCount").setMaster("spark://192.168.18.66:7077")
   
    val sc = new SparkContext(conf)
    sc.addJar("D:\\DEV\\workspace_scala\\spark-test001.jar")
    val textFile = sc.textFile(inputFile)
    
    println("文件是否为空"+textFile.isEmpty())
    println("textFile.name:"+textFile.name)
    textFile.partitioner.foreach(println)
    
    //保存到哪个路径下
    textFile.saveAsTextFile("D:\\Documents\\GitHub\\scala-xmu\\scala-xmu\\resources\\word4save3")
//    textFile.saveAsTextFile("file:///user/local/spark/word")
    
    val wordCount = textFile.flatMap(line => line.split(" ")).map(word => (word, 1)).reduceByKey((a, b) => a + b)
    println("these words:")
    wordCount.foreach(println)
  }
}