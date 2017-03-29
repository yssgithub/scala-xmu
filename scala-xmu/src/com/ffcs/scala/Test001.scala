package com.ffcs.scala

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.sql.SQLContext

object Test001 {
  def main(args: Array[String]): Unit = {
    //val conf = new SparkConf().setAppName("test001").setMaster("local")
    val conf = new SparkConf().setAppName("test001").setMaster("spark://192.168.18.66:7077")
    val context = new SparkContext(conf)
    context.addJar("D:\\DEV\\workspace_scala\\mysql-connector-java-5.1.30.jar")
    context.addJar("D:\\DEV\\workspace_scala\\spark-test001.jar")
    val spark = new SQLContext(context)

    val jdbcDF = spark.read.format("jdbc")
      .option("url", "jdbc:mysql://192.168.23.189:3306/spark")
      .option("driver", "com.mysql.jdbc.Driver")
      .option("dbtable", "student")
      .option("user", "root")
      .option("password", "12345678").load()

    jdbcDF.show()

    import java.util.Properties
    import org.apache.spark.sql.types._
    import org.apache.spark.sql.Row

    //下面我们设置两条数据表示两个学生信息
    val studentRDD = spark.sparkContext.parallelize(Array("5 youss M 29", "6 gaodd F 25")).map(_.split(" "))

    //下面要设置模式信息
    val schema = StructType(List(StructField("id", IntegerType, true), StructField("name", StringType, true), StructField("gender", StringType, true), StructField("age", IntegerType, true)))

    //下面创建Row对象，每个Row对象都是rowRDD中的一行
    val rowRDD = studentRDD.map(p => Row(p(0).toInt, p(1).trim, p(2).trim, p(3).toInt))

    //建立起Row对象和模式之间的对应关系，也就是把数据和模式对应起来
    val studentDF = spark.createDataFrame(rowRDD, schema)

    //下面创建一个prop变量用来保存JDBC连接参数
    val prop = new Properties()
    prop.put("user", "root") //表示用户名是root
    prop.put("password", "12345678") //表示密码是12345678
    prop.put("driver", "com.mysql.jdbc.Driver") //表示驱动程序是com.mysql.jdbc.Driver

    //下面就可以连接数据库，采用append模式，表示追加记录到数据库spark的student表中
    studentDF.write.mode("append").jdbc("jdbc:mysql://192.168.23.189:3306/spark", "spark.student", prop)

  }
}