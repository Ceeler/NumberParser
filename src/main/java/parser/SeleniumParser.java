package parser;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.*;


public class SeleniumParser implements Parser{

    private List<String> urls;
    private ChromeOptions options;
    private WebDriver driver;

    public SeleniumParser(List<String> urls) {
        options = new ChromeOptions();
        options.addArguments("--headless");
        driver = new ChromeDriver(options);
        driver.manage().window().setSize(new Dimension(400, 300));
        this.urls = urls;
    }

    @Override
    public Map<String, Set<String>> parse() {

        Map<String, Set<String>> numbers = new HashMap<>();
        int i = 0;
        for(String url : urls) {
            numbers.put(i++ + "", getSiteNumbers(url));

        }

        driver.close();

        return numbers;
    }

    private Set<String> getSiteNumbers(String url) {
        Set<String> numbers = new HashSet<>();
        driver.get(url);
        List<WebElement> links = driver.findElements(By.xpath("//a[@href]"));
        for(WebElement w : links){
            String attr = w.getAttribute("href");
            if(attr.indexOf("tel:") != -1){
                numbers.add(w.getAttribute("href"));
            }
        }
        return numberFormatter(numbers);
    }

    private Set<String> numberFormatter(Set<String> numbers) {
        Set<String> result = new HashSet<>();
        for(String num : numbers) {
            String digits = num.replaceAll("\\D", "");
            if(digits.length() == 7) {
                digits= "8495" + digits;
                result.add(digits);
            } else {
                if(digits.charAt(0) == '7'){
                    digits = "8" + digits.substring(1);
                    result.add(digits);
                } else if (digits.charAt(0) == '8') {
                    result.add(digits);
                }
            }
        }

        return result;
    }

}
