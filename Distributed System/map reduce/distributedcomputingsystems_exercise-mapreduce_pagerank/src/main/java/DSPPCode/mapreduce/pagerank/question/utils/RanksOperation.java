package DSPPCode.mapreduce.pagerank.question.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class RanksOperation {

  /**
   * 从指定路径读取网页排名
   *
   * @param ranksPath   网页排名数据路径
   * @param isDirectory 标识路径是否为目录
   * @return 返回中心数据
   */
  public static List<Rank> getRanks(String ranksPath, boolean isDirectory) {
    List<Rank> ranks = new ArrayList<>();

    try {
      InputStream inputStream = FileOperation.read(ranksPath);
      BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
      String line;
      while ((line = reader.readLine()) != null) {
        String[] datas = line.split(" ");
        // 用集合保存每个中心信息
        Rank rank = new Rank();
        for (int i = 0; i < datas.length; i++) {
          if (i % 2 == 0) {
            rank.setPageName(datas[i]);
          } else {
            rank.setRank(Double.parseDouble(datas[i]));
          }

        }
        ranks.add(rank);
      }

    } catch (Exception e) {
      e.printStackTrace();
    }

    return ranks;
  }

}
