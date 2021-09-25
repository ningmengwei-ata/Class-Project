package DSPPCode.mapreduce.kmeans.question.utils;


import DSPPCode.mapreduce.kmeans.question.KMeansRunner;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CentersOperation {

  /**
   * 从指定路径读取中心点数据
   *
   * @param centersPath 中心数据路径
   * @param isDirectory 标识路径是否为目录
   * @return 返回中心数据
   */
  public static List<List<Double>> getCenters(String centersPath, boolean isDirectory) {
    List<List<Double>> centers = new ArrayList<>();

    try {
      if (isDirectory) {
        List<String> paths = FileOperation.getPaths(centersPath);
        if (paths == null) {
          throw new Exception(centersPath + "centers directory is empty");
        }
        for (String path : paths) {
          centers.addAll(getCenters(path, false));
        }
        return centers;
      }

      InputStream inputStream = FileOperation.read(centersPath);
      BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
      String line;
      while ((line = reader.readLine()) != null) {
        String[] datas = line.split(",");
        // 用集合保存每个中心信息
        List<Double> center = new ArrayList<>();
        for (String data : datas) {
          center.add(Double.parseDouble(data));
        }
        centers.add(center);
      }

    } catch (Exception e) {
      e.printStackTrace();
    }

    return centers;
  }

  /**
   * 比较新旧中心数据，如果满足迭代终止条件，返回 true
   *
   * @param centerPath    旧的中心数据路径。即初始时刻设定的中心数据路径
   * @param newCenterPath mapReduce 生成的新的中心数据
   */
  public static boolean compareCenters(String centerPath, String newCenterPath) {
    List<List<Double>> centers = getCenters(centerPath, true);
    List<List<Double>> newCenters = getCenters(newCenterPath, true);
    //判断是否满足迭代终止条件
    boolean flag = true;
    double distance = 0;
    for (int i = 0; i < centers.size(); i++) {
      distance = 0;
      for (int j = 1; j < centers.get(0).size(); j++) {
        // 计算两个中心之间的距离
        distance += Math.pow(centers.get(i).get(j) - newCenters.get(i).get(j), 2);
      }
      if (Math.sqrt(distance) >= KMeansRunner.sigma) {
        flag = false;
        break;
      }
    }
    return flag;
  }
}
