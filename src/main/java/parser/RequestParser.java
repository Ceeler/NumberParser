package parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class RequestParser implements Parser {

    private List<String> urls;

    private boolean isRegexSearch;

    public RequestParser(boolean regex) {
        isRegexSearch = regex;
    }

    public RequestParser(List<String> urls) {
        isRegexSearch = false;
        this.urls = urls;
    }


    @Override
    public Map<String, Set<String>> parse() throws InterruptedException, ExecutionException {

        ExecutorService executor = Executors.newFixedThreadPool(10);

        List<Future<Set<String>>> s = executor.invokeAll(urls.stream().map(url -> new NumberParse(url, isRegexSearch)).collect(Collectors.toSet()));

        Map<String, Set<String>> result = new HashMap<>();

        int i = 0;
        for(Future<Set<String>> a : s) {
            Set<String> q = a.get();
            result.put(i++ + "", q);
        }

        return result;
    }



    class NumberParse implements Callable<Set<String>> {

        public String url;

        private boolean isRegexSearch;

        public NumberParse(String url, boolean isRegexSearch) {
            this.url = url;
            this.isRegexSearch = isRegexSearch;
        }

        @Override
        public Set<String> call() throws Exception {
            Set<String> result;
            Document doc;
            try {
                doc = Jsoup.connect(url).get();
            } catch (IOException e) {
                return Collections.EMPTY_SET;
            }

            if(isRegexSearch){
                result = regExpSearch(doc);
            } else {
                result = tagSearch(doc);
            }
            return numberFormatter(result);
        }

        private Set<String> tagSearch(Document document) {
            Elements links = document.select("a[href~=tel:]");

            Set<String> numbers = new HashSet<>();

            for(Element link : links){
                numbers.add(link.attr("href"));
            }

            return numbers;
        }

        private Set<String> regExpSearch(Document document) {
            Set<String> result = new HashSet<>();
            Pattern numberPattern = Pattern.compile("((8|\\+7)[\\- ]?)?(\\(?\\d{3}\\)?[\\- ]?)?[\\d\\- ]{7,10}");
            Element body = document.getElementsByTag("body").get(0);
            Matcher numberMatcher = numberPattern.matcher(body.text());
            while (numberMatcher.find()){
                result.add(numberMatcher.group());
            }
            return result;
        }

        public static Set<String> numberFormatter(Set<String> numbers) {
            Set<String> result = new HashSet<>();
            for(String num : numbers) {
                String digits = num.replaceAll("\\D", "");
                if(digits.length() == 7) {
                    digits= "8495" + digits;
                    result.add(digits);
                } else {
                    if(digits.charAt(0) == '7'){
                        digits = "8" + digits.substring(1);
                    } else if (digits.charAt(0) == '8') {
                        result.add(digits);
                    }
                }
            }

            return result;
        }

    }

}
