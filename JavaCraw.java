
/**
 * Created by geshuaiqi on 2017/9/26.
 * Function description: Input a url of a catalog website of ont novel in
 *                       努努书坊（http://www.kanunu8.com/)
 *                       such as
 *                       http://www.kanunu8.com/files/yuanchuang/201102/1530.html
 *                       And then it will return text of this novel.
 * Author: Ge Shuaiqi
 * Time: 2017.9.26
 * Declaration: This is homework of one course of Zhejiang University. If you're a student of Zhejiang University and you're seek reference to
 *              accomplish the homework. I suggest that you could learn something from my code but never copy it.
 */
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;


public class JavaCraw {

    static  String[] rootCatalog;  // String array stores the url of different chapters
    static  int count = 0;         // Total sum of chapters
    static  String novelName;

    /*
        @ para url: Send a Request through internet and return the source code of the website
        @ return str: text of the source code of websites
     */
    static String SendGet(String url){
        String str = "";
        BufferedReader buf = null;

        try{
            URL realUrl = new URL(url); // build a new url object

            URLConnection connection = realUrl.openConnection(); //建立通信链接

            connection.connect(); // connect

            // 建立一个缓存对象，并且把输入流按给定方式解码
            buf = new BufferedReader(new InputStreamReader(connection.getInputStream(),"GB18030"));

            String line;    // deal with source code
            while((line = buf.readLine()) != null){
                str += line + "\n";
            }

        }
        catch (Exception e){
            System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
        finally { // 关闭缓存对象
            try {
                if (buf != null) {
                    buf.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }

        //System.out.println(str);
        return str;

    }

    /*
        @ para targetStr : 网页内容
        @ para patternStr : 抓取模式正则表达
        Description : 正则匹配
        @ return : 返回需要处理好的需要抓取的内容
     */
    static String RegexString(String targetStr, String patternStr)
    {
        // 定义一个样式模板，此中使用正则表达式，括号中是要抓的内容
        // 相当于埋好了陷阱匹配的地方就会掉下去
        Pattern pattern = Pattern.compile(patternStr);
        // 定义一个matcher用来做匹配
        Matcher matcher = pattern.matcher(targetStr);
        // 如果找到了
        if (matcher.find())
        {
            // 打印出结果
            return matcher.group(0);
        }
        return "Nothing";
    }

    /*
        @ para chapter: 章节数
        @ para url: 该章节的url链接
        @ return : null
        Description: 输入该章节url，爬取文档，处理后输入到文本文件中
     */
    static void ReadContentAndWrite(int chapter, String url){
        String result = SendGet(url);

        // 抓取章节题目存储在title中
        Pattern pattern = Pattern.compile("<h2><font color=\"#dc143c\">(.+?)</font></h2>");
        // 定义一个matcher用来做匹配
        Matcher matcher = pattern.matcher(result);
        String title = "";
        if(matcher.find()){
            title = matcher.group(1);
        }


        // 使用正则匹配小说文字内容
        int start = result.indexOf("<p>");
        int end = result.indexOf("</p>");
        String str = result.substring(start+3,end);
        str = str.replace("<br />","");


        // 打印结果
        // System.out.println(str);
        try{
            FileOutputStream fs = new FileOutputStream(new File(novelName),true); // 输入到文件，若无文件则创建文件
            PrintStream p = new PrintStream(fs);
            p.println("第"+chapter+"章 "+title); // print title
            p.println(str); // print text
            p.close();
        }
        catch (Exception we){
            System.out.println(we);
        }
    }

    /*
        @ para catalogUrl: 输入努努书坊某小说的目录url
        @ return null
        Description: 爬取小说目录网站中章节链接并且将其存储到String[] rootCatalog中
     */
    static void GetCatalog(String catalogUrl){

        StringBuilder Catalog = new StringBuilder();
        String result = SendGet(catalogUrl);

        int lastindex = catalogUrl.lastIndexOf("/");
        String root = catalogUrl.substring(0,lastindex+1);
        //System.out.println(root);

        String sum = "";

        // 正则处理章节链接
        Pattern pattern = Pattern.compile(" <td width=\"25%\"><a href=\"(.+?)\">.+?</a></td>");
        // 定义一个matcher用来做匹配
        Matcher matcher = pattern.matcher(result);

        // 遍历整个网站
        while (matcher.find()) {
            // 打印出结果
            //System.out.println(i+ " " + matcher.group(1));
            StringBuilder t = new StringBuilder(root + matcher.group(1) + "!");
            Catalog.append(t);
            count ++;
        }

        pattern = Pattern.compile("<td><a href=\"(.+?)\">.+?</a></td>");
        // 定义一个matcher用来做匹配
        matcher = pattern.matcher(result);
        // 如果找到了
        while (matcher.find()) {
            // 打印出结果
            //System.out.println(i+ " "+ matcher.group(1));
            StringBuilder t = new StringBuilder(root + matcher.group(1) + "!");
            Catalog.append(t);
            count++;
        }

        //System.out.println(count);
        //System.out.println(Catalog);

        rootCatalog = Catalog.toString().split("!"); // 切割文本形成String[]

//      for (int i=0;i<rootCatalog.length;i++){
//           System.out.println(rootCatalog[i]);
//        }
    }



    public static void main(String[] args)
    {

        // 定义即将访问的链接
        //String url = "http://www.kanunu8.com/files/yuanchuang/201102/1530/35121.html";
        // 访问链接并获取页面内容

        // 可自行设置努努小说网站中其他小说的目录链接
        String catalogUrl = "http://www.kanunu8.com/files/yuanchuang/201102/1530.html"; // 努努书坊《诛仙》目录网站url

        novelName = "诛仙.txt"; // 可自行设置小说名字

        GetCatalog(catalogUrl); // 建立目录

        String url;
        for(int i=0;i<rootCatalog.length;i++)
        {
            System.out.println("第"+(i+1)+"章"+",共"+count+"章...");
            // 获取第i+1章url链接
            url = rootCatalog[i];
            // 爬取该章节
            ReadContentAndWrite(i+1,url);
        }


        return;

        //ReadContentAndWrite(url);

    }


}
