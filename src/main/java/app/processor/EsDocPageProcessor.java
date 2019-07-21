package app.processor;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.JsonFilePipeline;
import us.codecraft.webmagic.processor.PageProcessor;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 爬取逻辑
 *
 * @author faith.huan 2019-07-21 04:03:07
 */
@Slf4j
public class EsDocPageProcessor implements PageProcessor {


    /**
     *  部分一：抓取网站的相关配置，包括编码、抓取间隔、重试次数等
      */
    private Site site = Site.me().setRetryTimes(3).setSleepTime(1000).setTimeOut(10000);

    @Override
    public void process(Page page) {

        String currentUrl = page.getUrl().toString();
        if (StringUtils.endsWith(currentUrl, "current/index.html")) {
            List<String> subUrls = page.getHtml().links().all().stream().filter(StringUtils::isNotBlank).collect(Collectors.toList());
            page.addTargetRequests(subUrls);
        } else {
            String title = page.getHtml().xpath("//*[@class='title']/text()").toString();
            String content = String.join(" ", page.getHtml().xpath("//p/text()").all());
            if (StringUtils.isAnyBlank(title, content)) {
                page.setSkip(true);
            } else {
                page.putField("title", title);
                page.putField("content", content);
                page.putField("url", currentUrl);
                page.putField("date", LocalDateTime.now().toString());
            }
        }
    }

    @Override
    public Site getSite() {
        return site;
    }

    /**
     * 开始爬取
     *
     * @param url "https://www.elastic.co/guide/en/elasticsearch/reference/current/index.html"
     */
    public static void crawl(String url, String dir) {
        try {
            File file = new File(dir);
            if (!file.exists()) {
                boolean res = file.mkdir();
                if(res) {
                    log.info("创建目录成功");
                }
            }
            Spider.create(new EsDocPageProcessor())
                    //从url开始抓
                    .addUrl(url)
                    //设置Scheduler，使用Redis来管理URL队列
                    //.setScheduler(new RedisScheduler("localhost"))
                    //设置Pipeline，将结果以json方式保存到文件
                    .addPipeline(new JsonFilePipeline(dir))
                    //开启5个线程同时执行
                    .thread(5)
                    //启动爬虫
                    .run();

        } catch (Exception e) {
            log.error("启动爬虫发生异常", e);
        }
    }
}