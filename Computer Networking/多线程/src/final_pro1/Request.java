package final_pro1;

import java.net.Socket;


import java.io.* ;
import java.net.* ;
import java.util.* ;

final class Request implements Runnable {
    final static String CRLF = "\r\n";
    Socket socket;

    //构造请求类
    public Request(Socket socket) throws Exception {
        this.socket = socket;
    }

    //启动线程
    public void run() {
        try {
            processRequest();
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }


    public void processRequest() throws Exception {
        //获取套接字的输入流，即从服务器端发回的数据流
        InputStream is = socket.getInputStream();
        DataOutputStream os = new DataOutputStream(socket.getOutputStream());
        //缓冲字符输入流
        BufferedReader br = new BufferedReader(new InputStreamReader(is));

        // 获取http的request信息
        String requestLine = br.readLine();
        //获取套接字的输出流，返回的输出流就是将要发送到服务器端的数据流

        // 从请求行中提取文件信息
        StringTokenizer tokens = new StringTokenizer(requestLine);
        // 由于token将请求行分解为了单词，我们在这里跳过第一个单词，即“GET”
        tokens.nextToken();

        // GET之后即为文件名
        String fileName = tokens.nextToken();
        System.out.println(fileName);
        if (fileName.equals("/shutdown")) {
            WebServer.closeServer = true;//接受关闭服务器指令
            System.out.println(WebServer.closeServer);
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // 为了将文件转化到当前目录下，我们在其之前加一个“.”
        fileName = "." + fileName ;



        // 打开文件
        FileInputStream fis = null ;
        boolean fileExists = true ;

        //判断文件是否查询到
        try {
            fis = new FileInputStream(fileName);
        }
        catch (FileNotFoundException e) {
            fileExists = false ;
        }


        //测试输出
        System.out.println("\n*************Request**************\n");
        System.out.println(requestLine);
        String headerLine = null;
        while ((headerLine = br.readLine()).length() != 0) {
            System.out.println(headerLine);
        }

        // 构造response信息
        String statusLine = null;
        String contentTypeLine = null;
        String entityBody = null;

        if (fileExists) {
            //执行成功的话返回“200 OK”
            statusLine = "HTTP/1.0 200 OK" + CRLF;
            contentTypeLine = "Content-Type: "+contentType(fileName) + CRLF;
        }

        else {
            //执行失败，文件未找到的时候返回“404 File Not Found”
            statusLine = "HTTP/1.0 404 File Not Found" + CRLF;
            contentTypeLine = "Content-Type: text/html" + CRLF;
            entityBody = "<HTML>" +
                    "<HEAD><TITLE>404 Not Found</TITLE></HEAD>"+
                    "<BODY>404 Not Found</BODY></HTML>";
        }

        // 将上述返回值依次输出
        os.writeBytes(statusLine);
        os.writeBytes(contentTypeLine);
        os.writeBytes(CRLF);
        System.out.println("\n*************Response**************\n");
        System.out.println(statusLine);


        //调用文件类型输出方法
        if (fileExists) {
            sendBytes(fis, os);
            fis.close();
        }
        else {
            os.writeBytes(entityBody) ;
        }

        // 结束关闭
        os.close();
        br.close();
        socket.close();

    }

    private static void sendBytes(FileInputStream fis, OutputStream os) throws Exception {
        //构造一个1K缓冲区，承接套接字的内容
        byte[] buffer = new byte[1024];
        int bytes = 0;

        //将请求的文件复制到套接字的输出流中。
        while ((bytes = fis.read(buffer)) != -1) {
            os.write(buffer, 0, bytes);
        }
    }

    private static String contentType(String fileName) {
        if(fileName.endsWith(".htm") || fileName.endsWith(".html")) {
            return "text/html";
        }
        else return "other Content-type" ;
    }
}