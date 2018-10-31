package us.codecraft.webmagic.pipeline;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.md.MDUtils;
import us.codecraft.webmagic.md.MarkDownTranslater;
import us.codecraft.webmagic.utils.FilePersistentBase;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class MarkdownFilePipeline extends FilePersistentBase implements Pipeline {

    private Logger logger = LoggerFactory.getLogger(getClass());

    public MarkdownFilePipeline() {
        setPath("/data/webmagic/");
    }

    public MarkdownFilePipeline(String path) {
        setPath(path);
        MarkDownTranslater.setImgPath(path+PATH_SEPERATOR+"pics"+PATH_SEPERATOR);
    }

    @Override
    public void process(ResultItems resultItems, Task task) {
        String mdFileName = resultItems.get(MDUtils.mdFileKey).toString();
        String mdFileContent = resultItems.get(MDUtils.mdContentKey).toString();
        String path = this.path + PATH_SEPERATOR ;
        try {
            PrintWriter printWriter = new PrintWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(
                                    getFile(path + mdFileName + ".md")), StandardCharsets.UTF_8));
            printWriter.println("原文 url:\t" + resultItems.getRequest().getUrl());
            printWriter.print(mdFileContent);
//
//            for (Map.Entry<String, Object> entry : resultItems.getAll().entrySet()) {
//                if (entry.getValue() instanceof Iterable) {
//                    Iterable value = (Iterable) entry.getValue();
//                    printWriter.println(entry.getKey() + ":");
//                    for (Object o : value) {
//                        printWriter.println(o);
//                    }
//                } else {
//                    printWriter.println(entry.getKey() + ":\t" + entry.getValue());
//                }
//            }
            printWriter.close();
        } catch (IOException e) {
            logger.warn("write file error", e);
        }
    }
}
