package DSPPCode.mapreduce.kmeans.question.utils;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class FileOperation {

  /**
   * 用于标识传入路径为 HDFS 路径
   */
  private static final String HDFS = "hdfs";

  /**
   * 从指定路径获取输入流
   *
   * @param path 本地路径或 HDFS 路径
   */
  public static InputStream read(String path) {
    boolean isHDFS = path.contains(HDFS);
    InputStream inputStream = null;
    try {
      if (isHDFS) {
        FileSystem fs = getFileSystem(path);
        inputStream = fs.open(new Path(path));
      } else {
        inputStream = new FileInputStream(new File(path));
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return inputStream;
  }

  /**
   * 从指定路径获取输出流
   *
   * @param path        本地路径或 HDFS 路径
   * @param isOverwrite 标识文件内容是否需要覆盖
   */
  public static OutputStream write(String path, boolean isOverwrite) {
    boolean isHDFS = path.contains(HDFS);
    OutputStream outputStream = null;
    try {
      if (isHDFS) {
        FileSystem fs = getFileSystem(path);
        outputStream = fs.create(new Path(path), isOverwrite);
      } else {
        boolean isAppend = !isOverwrite;
        outputStream = new FileOutputStream(new File(path), isAppend);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return outputStream;
  }

  /**
   * 获取目录下的文件信息
   *
   * @param directory 目录路径
   * @return 目录下的文件路径集合
   */
  public static List<String> getPaths(String directory) {
    List<String> paths = new ArrayList<>();
    boolean isHDFS = directory.contains(HDFS);
    try {
      if (isHDFS) {
        FileSystem fs = getFileSystem(directory);
        // 读取目录下文件信息
        FileStatus[] fileStatuses = fs.listStatus(new Path(directory));
        for (FileStatus fileStatus : fileStatuses) {
          paths.add(fileStatus.getPath().toString());
        }
      } else {
        File root = new File(directory);
        File[] files = root.listFiles();
        if (files != null) {
          for (File file : files) {
            String path = file.getPath();
            // 本地文件系统中需要对输出文件进行过滤，不过滤可能产生错误
            if (path.matches(".*[0~9]+") || path.contains("centers")) {
              paths.add(file.getPath());
            }
          }
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return paths.size() > 0 ? paths : null;
  }

  /**
   * 用于删除输出目录，便于下一次输出
   *
   * @param path 目录路径
   */
  public static void deletePath(String path, boolean isDirectory) {
    boolean isHDFS = path.contains(HDFS);
    try {
      if (isHDFS) {
        FileSystem fs = getFileSystem(path);
        if (fs.exists(new Path(path))) {
          fs.delete(new Path(path), isDirectory);
        }
      } else {
        File file = new File(path);
        if (file.exists()) {
          // 本地目录递归删除
          if (isDirectory) {
            File[] subFiles = file.listFiles();
            for (File subFile : subFiles) {
              if (subFile.isFile()) {
                subFile.delete();
              } else {
                deletePath(subFile.getPath(), true);
              }
            }
          }
          file.delete();
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * @param path HDFS 文件系统的目标路径，此路径决定将要使用的文件系统，如果没有指定则会使用默认的文件系统
   * @return 返回 FileSystem 实例，用于访问文件系统
   */
  public static FileSystem getFileSystem(String path) {
    FileSystem fs = null;
    try {
      fs = FileSystem.get(URI.create(path), new Configuration());
    } catch (IOException e) {
      e.printStackTrace();
    }
    return fs;
  }
}
