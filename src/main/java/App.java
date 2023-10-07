import parser.Parser;
import parser.RequestParser;
import parser.SeleniumParser;

import java.io.IOException;

import java.util.*;
import java.util.concurrent.ExecutionException;


public class App {

    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {

        List<String> urls = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            urls.add("https://hands.ru/company/about/");
            urls.add("https://repetitors.info");
        }
        
        Parser requestParser = new RequestParser(urls);
        Parser seleniumParser = new SeleniumParser(urls);

        Map<String, Set<String>> result = seleniumParser.parse();

        System.out.println(result);

    }

}
