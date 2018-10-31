package us.codecraft.webmagic.processor.example;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.md.MDUtils;
import us.codecraft.webmagic.md.MarkDownTranslater;
import us.codecraft.webmagic.pipeline.FilePipeline;
import us.codecraft.webmagic.pipeline.MarkdownFilePipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;
import us.codecraft.webmagic.target.handler.DataArtisansHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static us.codecraft.webmagic.md.MDUtils.windowsFileFilter;

public class DataArtisansBlogPageProcessor implements PageProcessor {
    private Site site = Site.me().setTimeOut(5000).setRetryTimes(3).setSleepTime(1000).setUseGzip(true);

    @Override
    public void process(Page page) {
        Selectable postSelect = page.getHtml().xpath("//div[@class='post-content']");
        String s = postSelect.xpath("html()").toString();

        String title = postSelect.xpath("//h1[@class='post-title']/text()").toString();
        String date = DataArtisansHandler.date(postSelect.xpath("//p[@class='post-date']"));
        page.putField(MDUtils.mdFileKey,windowsFileFilter("todo-"+DataArtisansHandler.date2str(date)+"-"+title.trim()));
//        String date = DataArtisansHandler.date(postSelect.xpath("//p[@class='post-date']"));
//        page.putField("date", date);
//        String coauthors = DataArtisansHandler.coauthors(postSelect.xpath("//p[@class='coauthors-links']"));
//        page.putField("coauthors", coauthors);

        Document doc = Jsoup.parse(s);
        List<Element> elements = contentNodes(doc);
        List<String> bodies = new ArrayList<>();
        for(Element element:elements){
            if(
                    element.hasClass("post-categories")
                    ||element.hasClass("synved-social-container")
                    ||element.hasClass("post-share")
            ){
                continue;
            }
            bodies.add(MarkDownTranslater.do4Element(element));
        }
        List<String> no160Blank = MDUtils.map(bodies, element -> element.replaceAll("[\\u00A0]+", " "));
        List<String> noEmpty = MDUtils.filter(no160Blank, element -> !element.trim().isEmpty());
        String body = MDUtils.connect(noEmpty, "\n");
        page.putField(MDUtils.mdContentKey, body);
    }

    private List<Element> contentNodes(Document doc){
        for(Element d:doc.body().children()){
            if("div".equals(d.nodeName())){
                return d.children();
            }
        }
        return new ArrayList<>();
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
        Spider.create(new DataArtisansBlogPageProcessor())
            .addUrl("https://data-artisans.com/blog/how-to-get-started-with-data-artisans-platform-on-aws-eks")
            .addPipeline(new MarkdownFilePipeline("E:\\temp"))
            .thread(2).run();

    }
}
