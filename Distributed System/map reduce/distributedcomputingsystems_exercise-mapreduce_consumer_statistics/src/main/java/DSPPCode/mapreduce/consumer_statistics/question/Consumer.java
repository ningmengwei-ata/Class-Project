package DSPPCode.mapreduce.consumer_statistics.question;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.math.BigInteger;

import org.apache.hadoop.io.Writable;

public class Consumer implements Writable {

  // 消费者id
  private String id;
  // 消费金额
  private int money;
  // 消费者是否是会员
  private boolean isVip;

  public Consumer() {
  }

  public Consumer(String id, int money, boolean isVip) {
    this.id = id;
    this.money = money;
    this.isVip = isVip;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public int getMoney() {
    return money;
  }

  public void setMoney(int money) {
    this.money = money;
  }

  public boolean isVip() {
    return isVip;
  }

  public void setVip(boolean vip) {
    isVip = vip;
  }

  @Override
  public void write(DataOutput out) throws IOException {
    out.writeUTF(id);
    out.writeInt(money);
    out.writeBoolean(isVip);
  }

  @Override
  public void readFields(DataInput in) throws IOException {
    this.id = in.readUTF();
    this.money = in.readInt();
    this.isVip = in.readBoolean();
  }
}
