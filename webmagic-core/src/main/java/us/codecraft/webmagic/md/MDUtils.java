package us.codecraft.webmagic.md;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

public class MDUtils {

    public static String mdFileKey = "fileName";
    public static String mdContentKey = "fileBody";

    public static String connect(List<String> list,String sep){
        if(list.size() == 0) return "";
        StringBuilder start = new StringBuilder(list.get(0));
        for(int i=1;i<list.size();i++)start.append(sep).append(list.get(i));
        return start.toString();
    }

    public static List<String> filter(List<String> list, Function<String,Boolean> tupleFilter){
        List<String> result = new ArrayList<>();
        for(String s:list) if(tupleFilter.apply(s)) result.add(s);
        return result;
    }
    public static List<String> map(List<String> list, Function<String,String> tupleMap){
        List<String> result = new ArrayList<>();
        for(String s:list) result.add(tupleMap.apply(s));
        return result;
    }

    public static String windowsFileFilter(String name){
        return name.replaceAll("[#%&*|\\\\:\"<>?/.\\s]+","-");
    }

    public static String downloadPicture(String urlStr, String filePath, String fileName) {
        try {
            URL url;
            url = new URL(urlStr);
            DataInputStream dataInputStream = new DataInputStream(url.openStream());
            File bp = new File(filePath);
            if(!bp.exists()) bp.mkdir();
            File file = new File(filePath + fileName);
            if(file.exists()){
                fileName = UUID.randomUUID().toString();
                file = new File(filePath + fileName);
            }
//            file.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            ByteArrayOutputStream output = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024];
            int length;
            while ((length = dataInputStream.read(buffer)) > 0) output.write(buffer, 0, length);

            fileOutputStream.write(output.toByteArray());
            dataInputStream.close();
            fileOutputStream.close();
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileName;
    }

    public static String getImgNameFromUrl(String url){
        return url.substring(url.lastIndexOf("/") + 1);
    }

}
