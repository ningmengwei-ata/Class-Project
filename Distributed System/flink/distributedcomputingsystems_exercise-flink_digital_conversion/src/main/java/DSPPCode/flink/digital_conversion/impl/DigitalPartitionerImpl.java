package DSPPCode.flink.digital_conversion.impl;

import DSPPCode.flink.digital_conversion.question.DigitalPartitioner;

public class DigitalPartitionerImpl extends DigitalPartitioner {

  public int partition(String key, int numPartitions) {
    //[0,4]划分给任务一 [5,9]划分给任务二
    if (Double.parseDouble(key) <= 4 && Double.parseDouble(key) >= 0) {
      return 0;
    }

    return 1;


  }
}
