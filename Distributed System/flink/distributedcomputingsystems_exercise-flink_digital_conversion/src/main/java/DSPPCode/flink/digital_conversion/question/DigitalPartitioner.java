package DSPPCode.flink.digital_conversion.question;

import org.apache.flink.api.common.functions.Partitioner;


public abstract class DigitalPartitioner implements Partitioner<String> {

  /**
   * Computes the partition for the given key.
   *
   * @param key           The key.
   * @param numPartitions The number of partitions to partition into.
   * @return The partition index.
   */
  @Override
  public abstract int partition(String key, int numPartitions);
}
