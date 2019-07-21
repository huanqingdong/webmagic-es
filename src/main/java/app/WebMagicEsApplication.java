package app;

import app.processor.EsDocPageProcessor;
import app.util.WordCountUtil;

/**
 * 主类
 *
 * @author faith.huan 2019-07-21 04:00:54
 */
public class WebMagicEsApplication {

    public static void main(String[] args) {
        // 爬取开始路径
        String beginUrl = "https://www.elastic.co/guide/en/elasticsearch/reference/current/index.html";
        // 爬取结果存放文件夹
        String saveDir = "D:/es-doc";
        EsDocPageProcessor.crawl(beginUrl, saveDir);
        
        /*
          词频统计结果名,会写两个文件
          ${resultFileName}SortedByKey.txt:存储按字母排序的结果
          ${resultFileName}SortedByValue.txt:存储按词频逆序排序的结果
         */
        String resultFileName = "result";
        WordCountUtil.wordCount(saveDir, resultFileName);
    }

}
