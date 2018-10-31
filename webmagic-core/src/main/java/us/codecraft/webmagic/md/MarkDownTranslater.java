package us.codecraft.webmagic.md;

import org.jsoup.nodes.*;
import org.jsoup.select.Elements;
import us.codecraft.webmagic.selector.Selectable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class MarkDownTranslater {

    private static String basePath = "./pics/";
    private static String imgPath;

    public static void setImgPath(String ip){
        imgPath = ip;
    }

    public static String title(String title){
        String trim = title.trim();
        if(trim.startsWith("#"))return trim;
        return "#"+trim;
    }

    private static Function<Tuple2<List<String>,String>, String> comb = s -> {
        List<String> list = s.getV1();
        String sep = s.getV2();
        if(list.size()<1) return "";
        StringBuilder start = new StringBuilder(list.get(0));
        for(int i=1;i<list.size();i++){
            start.append(sep).append(list.get(i).replaceAll("[\\u00A0]+", " "));
        }
        return start.toString();
    };

    private static Function<List<Node>, List<String>> loop = nodes -> {
        ArrayList<String> result = new ArrayList<>();
        for(Node node:nodes){
            if(node instanceof Comment) continue;
            else if(node instanceof TextNode){
                String text = ((TextNode) node).text().trim();
                if(!text.isEmpty()) result.add(text);
            } else if(node instanceof DataNode)
                System.out.println("========");
            else if(node instanceof Element){
                String ns = do4Element((Element)node);
                if(!ns.isEmpty()) result.add(ns);
            }else new RuntimeException("loop node = "+node);
        }
        return result;
    };

    public static String a(Selectable a){
        String linkto = a.xpath("a/@href").get();
        String value = a.xpath("a/text()").get();
        return String.format("[%s](%s)",value,linkto);
    }

    private static String span(Element element, Function<List<Node>, List<String>> loop, Function<Tuple2<List<String>, String>, String> combiner){
        return combiner.apply(new Tuple2<>(loop.apply(element.childNodes())," "));
    }

    private static String p(Element element, Function<List<Node>, List<String>> loop, Function<Tuple2<List<String>, String>, String> combiner){
        return combiner.apply(new Tuple2<>(loop.apply(element.childNodes())," "));
    }
    private static String div(Element element, Function<List<Node>, List<String>> loop, Function<Tuple2<List<String>, String>, String> combiner){
        if(element.hasClass("crayon-main")) return "";
        if(element.hasClass("crayon-plain-wrap"))
            return String.format("```\n%s\n```\n",combiner.apply(new Tuple2<>(loop.apply(element.childNodes())," ")));
        return combiner.apply(new Tuple2<>(loop.apply(element.childNodes())," "));
    }

    private static String textarea(Element element, Function<List<Node>, List<String>> loop, Function<Tuple2<List<String>, String>, String> combiner){
        return combiner.apply(new Tuple2<>(loop.apply(element.childNodes())," "));
    }

    private static String a(Element element, Function<List<Node>, List<String>> loop, Function<Tuple2<List<String>, String>, String> combiner){
        return String.format("[%s](%s)",combiner.apply(new Tuple2<>(loop.apply(element.childNodes()), " ")),element.attr("href"));
    }

    private static String br(Element element, Function<List<Node>, List<String>> loop, Function<Tuple2<List<String>, String>, String> combiner){
        return String.format("%s\n",combiner.apply(new Tuple2<>(loop.apply(element.childNodes()), " ")));
    }

    private static String i(Element element, Function<List<Node>, List<String>> loop, Function<Tuple2<List<String>, String>, String> combiner){
        String inner = combiner.apply(new Tuple2<>(loop.apply(element.childNodes()), " ")).trim();
        if(inner.isEmpty()) return "";
        else return String.format("*%s*", inner);
    }

    private static String b(Element element, Function<List<Node>, List<String>> loop, Function<Tuple2<List<String>, String>, String> combiner){
        String inner = combiner.apply(new Tuple2<>(loop.apply(element.childNodes()), " ")).trim();
        if(inner.isEmpty()) return "";
        else return String.format("**%s**", inner);
    }

    private static String img(Element element, Function<List<Node>, List<String>> loop, Function<Tuple2<List<String>, String>, String> combiner){
        String srcset = element.attr("srcset");
        String[] set = srcset.split(",");
        String real = null;
        for(String s:set){
            String src = s.trim().split(" ")[0];
            String imgName = MDUtils.downloadPicture(src, imgPath, MDUtils.getImgNameFromUrl(src));

            if(real==null)real = imgName;
            if(imgName.length() < real.length()) real = imgName;
        }
        String alt = element.attr("alt");
        return String.format("![%s](%s)",alt == null?"":alt,real == null?"":basePath+real);
    }

    private static String h(int n, Element element, Function<List<Node>, List<String>> loop, Function<Tuple2<List<String>, String>, String> combiner){
        String str = combiner.apply(new Tuple2<>(loop.apply(element.childNodes()), " "));
        StringBuilder s= new StringBuilder();
        for(int i=0;i<n/2+1;i++) s.append("#");
        return String.format("\n%s %s\n",s,str);
    }

    private static String ul(Element element, Function<List<Node>, List<String>> loop, Function<Tuple2<List<String>, String>, String> combiner){
        return String.format("\n%s\n",combiner.apply(new Tuple2<>(loop.apply(element.childNodes()), "")).trim());
    }

    private static String li(Element element, Function<List<Node>, List<String>> loop, Function<Tuple2<List<String>, String>, String> combiner){
        return String.format("- %s\n",combiner.apply(new Tuple2<>(loop.apply(element.childNodes()), " ")).trim());
    }
    private static String link(Element element, Function<List<Node>, List<String>> loop, Function<Tuple2<List<String>, String>, String> combiner){
        if("stylesheet".equals(element.attr("rel"))){
            return "";
        }
        System.out.println("=============");
        return "";
    }

    private static String style(Element element, Function<List<Node>, List<String>> loop, Function<Tuple2<List<String>, String>, String> combiner){
        return "";
    }

    private static String script(Element element, Function<List<Node>, List<String>> loop, Function<Tuple2<List<String>, String>, String> combiner){
        return "";
    }

    private static String form(Element element, Function<List<Node>, List<String>> loop, Function<Tuple2<List<String>, String>, String> combiner){
        return "";
    }

    private static String strong(Element element, Function<List<Node>, List<String>> loop, Function<Tuple2<List<String>, String>, String> combiner){
        String inner = combiner.apply(new Tuple2<>(loop.apply(element.childNodes()), " ")).trim();
        if(inner.isEmpty()) return "";
        else return String.format("**%s**", inner);
    }

    private static String em(Element element, Function<List<Node>, List<String>> loop, Function<Tuple2<List<String>, String>, String> combiner){
        return combiner.apply(new Tuple2<>(loop.apply(element.childNodes()), " ")).trim();
    }

    private static String table(Element element, Function<List<Node>, List<String>> loop, Function<Tuple2<List<String>, String>, String> combiner){
        return String.format("\n%s\n",combiner.apply(new Tuple2<>(loop.apply(element.childNodes()), " ")).trim());
//        List<String> ll = loop.apply(element.childNodes());
//        if(ll.size()>1){
//            String head = ll.get(0);
//            int column = (head.length() - head.replace("|","").length());
//            String tail = comb.apply(new Tuple2<>(ll.subList(1, ll.size()), "\n"));
//            StringBuilder header = new StringBuilder("\n|");
//            for(int i=1;i<column;i++) header.append(":-:|");
//            return comb.apply(new Tuple2<>(Arrays.asList(head,tail),header.append("\n").toString()));
//        }else if(ll.size() == 1) return ll.get(0);
//        else return "";
    }

    private static String tr(Element element, Function<List<Node>, List<String>> loop, Function<Tuple2<List<String>, String>, String> combiner){
        return String.format("| %s |",combiner.apply(new Tuple2<>(loop.apply(element.childNodes()), " | ")).trim());
    }

    private static String td(Element element, Function<List<Node>, List<String>> loop, Function<Tuple2<List<String>, String>, String> combiner){
        return combiner.apply(new Tuple2<>(loop.apply(element.childNodes()), " | ")).replaceAll("\n"," ").trim();
    }

    private static String th(Element element, Function<List<Node>, List<String>> loop, Function<Tuple2<List<String>, String>, String> combiner){
        return combiner.apply(new Tuple2<>(loop.apply(element.childNodes()), " | ")).replaceAll("\n"," ").trim();
    }
    private static String tbody(Element element, Function<List<Node>, List<String>> loop, Function<Tuple2<List<String>, String>, String> combiner){
        List<String> ll = loop.apply(element.childNodes());
        if(ll.size()>1){
            String head = ll.get(0);
            int column = (head.length() - head.replace("|","").length());
            String tail = comb.apply(new Tuple2<>(ll.subList(1, ll.size()), "\n"));
            StringBuilder header = new StringBuilder("\n|");
            for(int i=1;i<column;i++) header.append(":-:|");
            return comb.apply(new Tuple2<>(Arrays.asList(head,tail),header.append("\n").toString()));
        }else if(ll.size() == 1) return ll.get(0);
        else return "";
    }

    private static String ol(Element element, Function<List<Node>, List<String>> loop, Function<Tuple2<List<String>, String>, String> combiner){
        return String.format("\n %s\n",combiner.apply(new Tuple2<>(loop.apply(element.childNodes()), " ")).trim());
    }

    private static String blockquote(Element element, Function<List<Node>, List<String>> loop, Function<Tuple2<List<String>, String>, String> combiner){
        return combiner.apply(new Tuple2<>(loop.apply(element.childNodes()), " ")).trim();
    }


    public static String do4Element(Element e){
        String nodeName = e.nodeName();
        if("p".equals(nodeName)){
            return p(e,loop,comb);
        }else if("div".equals(nodeName)){
            return div(e,loop,comb);
        }else if("ul".equals(nodeName)){
            return ul(e,loop,comb);
        }else if("li".equals(nodeName)){
            return li(e,loop,comb);
        }else if("#text".equals(nodeName)){
            System.out.println();
        }else if("span".equals(nodeName)){
            return span(e,loop,comb);
        }else if("a".equals(nodeName)){
            return a(e,loop,comb);
        }else if("br".equals(nodeName)){
            return br(e,loop,comb);
        }else if("i".equals(nodeName)){
            return i(e,loop,comb);
        }else if("img".equals(nodeName)){
            return img(e,loop,comb);
        }else if("h1".equals(nodeName)){
            return h(1,e,loop,comb);
        }else if("h3".equals(nodeName)){
            return h(3,e,loop,comb);
        }else if("h4".equals(nodeName)){
            return h(4,e,loop,comb);
        }else if("h5".equals(nodeName)){
            return h(5,e,loop,comb);
        }else if("h6".equals(nodeName)){
            return h(6,e,loop,comb);
        }else if("b".equals(nodeName)){
            return b(e,loop,comb);
        }else if("link".equals(nodeName)){
            return link(e,loop,comb);
        }else if("textarea".equals(nodeName)){
            return textarea(e,loop,comb);
        }else if("style".equals(nodeName)){
            return style(e,loop,comb);
        }else if("script".equals(nodeName)){
            return script(e,loop,comb);
        }else if("form".equals(nodeName)){
            return form(e,loop,comb);
        }else if("strong".equals(nodeName)){
            return strong(e,loop,comb);
        }else if("em".equals(nodeName)){
            return em(e,loop,comb);
        }else if("table".equals(nodeName)){
            return table(e,loop,comb);
        }else if("tr".equals(nodeName)){
            return tr(e,loop,comb);
        }else if("td".equals(nodeName)){
            return td(e,loop,comb);
        }else if("th".equals(nodeName)){
            return th(e,loop,comb);
        }else if("tbody".equals(nodeName)){
            return tbody(e,loop,comb);
        }else if("ol".equals(nodeName)){
            return ol(e,loop,comb);
        }else if("blockquote".equals(nodeName)){
            return blockquote(e,loop,comb);
        }
        throw new RuntimeException("nodeName = "+nodeName);
    }

//    public static

}
