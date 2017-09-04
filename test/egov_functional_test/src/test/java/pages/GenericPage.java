package pages;

import com.testvagrant.stepdefs.utils.FileExtension;
import com.testvagrant.stepdefs.utils.FileFinder;
import org.apache.commons.lang.math.RandomUtils;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import steps.GenericSteps;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static com.jayway.awaitility.Awaitility.await;
import static com.testvagrant.stepdefs.core.Tapster.tapster;

public class GenericPage extends BasePage {

    private WebDriver driver;

    public GenericPage(WebDriver driver) {
        this.driver = driver;
    }

    public String checkValueCanBeAutoGeneratedOrNot(String v) {
        String value = v.replaceAll("\"", "");
        if (value.contains("--")) {
            if (value.contains(",") && !value.contains("characters")) {
                value = (value.split(",")[0] +
                        getRandomNumber(Integer.parseInt(value.split(",")[1].replaceAll("[^0-9]+", ""))))
                        .replaceAll("--", "");
            } else if (value.contains("characters")) {
                value = (value.split(",")[0] +
                        getRandomCharacters(Integer.parseInt(value.split(",")[1].replaceAll("[^0-9]+", ""))))
                        .replaceAll("--", "");
            } else if (value.contains("email")) {
                value = getRandomEmail().replaceAll("--", "");
            } else {
                value = getRandomNumber(Integer.parseInt(value.replaceAll("[^0-9]+", ""))).replaceAll("--", "");
            }
        }
        return value;
    }

    private String getRandomNumber(int c) {
        Random random = new Random();
        char[] digits = new char[c];
        digits[0] = (char) (random.nextInt(9) + '1');
        for (int i = 1; i < c; i++) {
            digits[i] = (char) (random.nextInt(10) + '0');
        }
        return new String(digits);
    }

    private String getRandomCharacters(int noOfCharacters) {
        Random random = new Random();
        String[] alphabet = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z",
                "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};
        String required = "";

        for (int i = 0; i < noOfCharacters; i++) {
            required = required + alphabet[random.nextInt(52 - 0)];
        }

        return required;
    }

    private String getRandomEmail() {
        return "email" + String.valueOf(100 + (RandomUtils.nextInt(9999))) + "@xyz.com";
    }

    public WebElement buildElement(String screen, String element, String value) throws IOException {
        WebElement webElement = null;

        FileFinder p = FileFinder.fileFinder("src/test/resources/elements/");
        File f = p.find(screen, FileExtension.ELEMENTS);
        BufferedReader in = new BufferedReader(new FileReader(f));
        String str = "";
        StringBuilder json = new StringBuilder();

        while ((str = in.readLine()) != null) {
            json.append(str);
        }
        in.close();

        JSONObject jsonObject = new JSONObject(json.toString());
        int pos = 0;

        for (int i = 0; i < jsonObject.getJSONArray("elements").length(); i++) {
            if ((jsonObject.getJSONArray("elements").getJSONObject(pos).getString("elementName").equals(element)))
                break;
            else pos++;
        }

        if (jsonObject.getJSONArray("elements").getJSONObject(pos).getString("value").contains("%s")) {
            String locator = jsonObject.getJSONArray("elements").getJSONObject(pos).getString("value");
            jsonObject.getJSONArray("elements").getJSONObject(pos).put("value", locator.replace("%s", value));
        }

        str = jsonObject.getJSONArray("elements").getJSONObject(pos).getString("value");
        switch (jsonObject.getJSONArray("elements").getJSONObject(pos).getString("identifier")) {
            case "id":
                waitForTheElementToBePresent((By.id(str)));
                webElement = driver.findElement(By.id(str));
                break;

            case "css":
                waitForTheElementToBePresent((By.cssSelector(str)));
                webElement = driver.findElement(By.cssSelector(str));
                break;

            case "xpath":
                waitForTheElementToBePresent((By.xpath(str)));
                webElement = driver.findElement(By.xpath(str));
                break;

            case "linkText":
                waitForTheElementToBePresent((By.linkText(str)));
                webElement = driver.findElement(By.linkText(str));
                break;

            case "className":
                waitForTheElementToBePresent((By.className(str)));
                webElement = driver.findElement(By.className(str));
                break;

            case "name":
                waitForTheElementToBePresent((By.name(str)));
                webElement = driver.findElement(By.name(str));
                break;

            case "tagName":
                waitForTheElementToBePresent((By.tagName(str)));
                webElement = driver.findElement(By.tagName(str));
                break;

            case "partialLinkText":
                waitForTheElementToBePresent((By.partialLinkText(str)));
                webElement = driver.findElement(By.partialLinkText(str));
                break;
        }
        return webElement;
    }

    public void clickOnDropdown(WebElement webElement, String value) {

        clickOnButton(webElement, driver);
        await().atMost(10, TimeUnit.SECONDS).until(() -> driver.findElements(By.cssSelector("div[role=\"presentation\"]:nth-child(1) div div span div div div")).size() >= 1);

        List<WebElement> dropdown = driver.findElements(By.cssSelector("div[role=\"presentation\"]:nth-child(1) div div span div div div"));
        for (WebElement w : dropdown) {
            if (w.getText().equals(value)) {
                try {
                    clickOnButton(w, driver);
                    break;
                } catch (Exception e) {
                    jsClick(w, driver);
                }
            }
        }
    }

    public void actionOnSuggestionBox(WebElement webElement, String value) {
        waitForElementToBeVisible(webElement, driver);
        webElement.sendKeys(value);
        webElement.sendKeys(Keys.ARROW_DOWN, Keys.ENTER);
    }

    public WebElement openApplication(String number) throws IOException {

        List<WebElement> totalRows;
        totalRows = buildElement("Home","dashBoardApplications","").findElements(By.tagName("tr"));
        try {
            for (WebElement applicationRow : totalRows) {
                if (applicationRow.findElements(By.tagName("td")).get(4).getText().contains(number)) {
                    return applicationRow;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new RuntimeException("No application row found in Inbox -- " + number);
    }

    private void waitForTheElementToBePresent(By by) throws IOException {
        await().atMost(20, TimeUnit.SECONDS).until(() -> driver.findElements(by).size() > 0);
    }

    public String findDataIsComingFromDataTable(String v) {
        if (v.contains("<"))
            return GenericSteps.dataTableStore.get(GenericSteps.i++);
        return v;
    }

    public void tapsterServesActionWithElement(String consumer, String screen, String action, String value, WebElement webElement) throws IOException {
        tapster().useDriver(driver)
                .asConsumer(consumer)
                .onScreen(screen)
                .doAction(action)
                .withValue(value)
                .serveWithElement(webElement);
    }

    public void tapsterServesAction(String consumer, String screen, String element, String action, String value) throws IOException {
        tapster().useDriver(driver)
                .asConsumer(consumer)
                .onScreen(screen)
                .onElement(element)
                .doAction(action)
                .withValue(value)
                .serve();
    }

    public void performsAction(String consumer, String screen, String action, String element, String value) throws IOException {

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        WebElement webElement;

        if (!value.equals("null")) {

            value = findDataIsComingFromDataTable(value);
            if (GenericSteps.copyValues.containsKey(value))
                value = GenericSteps.copyValues.get(value);
            else
                value = String.valueOf(checkValueCanBeAutoGeneratedOrNot(value));

            if (isValidFormat("dd/MM/yyyy", value))
                value = value.replaceAll("/", "");

            switch (action) {

                case "selects":
                    webElement = buildElement(screen, element, value);
                    clickOnDropdown(webElement, value);
                    break;

                case "uploads":
                    tapsterServesAction(consumer, screen, element, action, System.getProperty("user.dir") + "/src/test/resources/" + value);
                    break;

                default:
                    webElement = buildElement(screen, element, value);
                    tapsterServesActionWithElement(consumer, screen, action, value, webElement);
            }
        }
    }

    public boolean checkValidDataEnteredOrNot(String inValidMessage) {
        String e = "//*[text()='" + inValidMessage + "']";
        await().atMost(20, TimeUnit.SECONDS).until(() -> driver.findElements(By.xpath(e)).size() == 1);
        if (driver.findElements(By.xpath(e)).size() == 1)
            return true;
        else
            return false;
    }

    private boolean isValidFormat(String format, String value) {
        Date date = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            date = sdf.parse(value);
            if (!value.equals(sdf.format(date))) {
                date = null;
            }
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        return date != null;
    }
}
