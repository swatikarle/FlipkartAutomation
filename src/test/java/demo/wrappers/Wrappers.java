package demo.wrappers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class Wrappers {
    /*
     * Write your selenium wrappers here
     */
    private WebDriver driver;
    private WebDriverWait wait;

    public Wrappers(WebDriver driver) {
        // Initializing the driver
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    public void searchProduct(String keyWord) {
        try {
            // Locating the search box element
            WebElement searchBoxElement = wait.until(ExpectedConditions
                    .visibilityOfElementLocated(By.xpath("//input[contains(@placeholder,'Search for Products')]")));
            searchBoxElement.clear();
            searchBoxElement.sendKeys(keyWord);
            WebElement searchButton = driver.findElement(By.xpath("//button[@type='submit']"));
            clickOnMe(searchButton);
        } catch (Exception e) {
            System.out.println("Exception Occured in searchProduct: " + e.getMessage());
        }
    }

    public void clickOnMe(WebElement element) {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(element)).click();
        } catch (Exception e) {
            System.out.println("Exception Occured in clickOnMe: " + e.getMessage());
        }
    }

    public void countLessThanFour() {
        try {
            Thread.sleep(1000);
            List<WebElement> ratings = wait
                    .until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//div[@class='XQDdHH']")));
            int count = 0;
            // Iterating over list of webelement
            for (WebElement e : ratings) {
                try {
                    double rating = Double.parseDouble(e.getText());
                    if (rating <= 4) {
                        count++;
                    }
                } catch (NumberFormatException t) {
                    System.out.println("Unable to parse rating: " + e.getText());
                }
            }
            System.out.println("Number of products with rating <= 4: " + count);
        } catch (Exception e) {
            System.out.println("Exception Occured in countLessThanFour: " + e.getMessage());
        }
    }

    public void closingLoginPopUP() {
        try {
            WebElement crossButton = driver.findElement(By.xpath("//div[@class='JFPqaw']/span"));
            // verifying cross button is visible or not
            if (crossButton.isDisplayed() || crossButton.isEnabled()) {
                crossButton.click();
            }
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }
    }

    public void printNameAndDiscount() {
        try {
            List<WebElement> discounts = wait.until(
                    ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//div[@class='UkUFwK']/span")));
            // Iterate over the list of WebElements
            for (WebElement e : discounts) {
                String text = e.getText().replace("% off", "").trim();
                try {
                    int textValue = Integer.parseInt(text);
                    if (textValue > 17) {
                        WebElement iphoneNameElement = e.findElement(By.xpath(
                                "//div[@class='UkUFwK']/span[text()='" + text + "% off']/../../../../../div/div"));
                        System.out.println("Name: " + iphoneNameElement.getText() + " Discount: " + text + "% off");
                    }
                } catch (NumberFormatException t) {
                    System.out.println("Exception Occured in printNameAndDiscount: " + t.getMessage());
                }
            }
            // Handle cases where title or discount might not be present
        } catch (Exception e) {
            System.out.println("Exception Occured in printNameAndDiscount: " + e.getMessage());
        }
    }
        // Get list of all items
    public List<String> titleAndImageUrl() {
        // Initialising the HashMap for the list of top 5 products
        HashMap<String, Integer> result = new HashMap<>();
        List<String> topFiveTitles = new ArrayList<>();

        try {
            List<WebElement> titles;
            List<WebElement> attr;

            for (int attempt = 0; attempt < 3; attempt++) {
                try {
                    titles = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(
                            By.xpath("//div[@class='slAVV4']//a[@rel='noopener noreferrer'][2]")));
                    attr = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(
                            By.xpath("//div[@class='slAVV4']//span[@class='Wphh3N']")));

                    // Verifying the size
                    if (titles.size() != attr.size()) {
                        System.out.println("Mismatch in size of lists, modifying to adjust");
                        int minSize = Math.min(titles.size(), attr.size());
                        titles = titles.subList(0, minSize);
                        attr = attr.subList(0, minSize);
                    }

                    for (int i = 0; i < titles.size(); i++) {
                        String title = titles.get(i).getText();
                        String attribute = attr.get(i).getText().replace("(", "").replace(")", "").replace(",", "");
                        try {
                            Integer attributeInt = Integer.parseInt(attribute);
                            result.putIfAbsent(title, attributeInt);
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid rating format for: " + title);
                        }
                    }
                    break;

                } catch (StaleElementReferenceException e) {
                    System.out.println("Stale element encountered, retrying... Attempt: " + (attempt + 1));

                    Thread.sleep(1000);
                }
            }

            topFiveTitles = result.entrySet().stream()
                    .sorted((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()))
                    .limit(5)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            System.out.println("Exception Occurred in titleAndImageUrl: " + e.getMessage());
        }

        return topFiveTitles;
    }

}
