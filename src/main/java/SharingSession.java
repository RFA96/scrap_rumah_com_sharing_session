import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v117.network.Network;
import org.openqa.selenium.devtools.v117.network.model.RequestId;
import org.openqa.selenium.devtools.v117.network.model.Response;

import java.util.Objects;
import java.util.Optional;

public class SharingSession {
    public static void main(String[] args) throws InterruptedException {
        System.setProperty("webdriver.chrome.driver", "/Users/raka_matsukaze/chromedriver");

        String propertyURL = "https://www.rumah.com/listing-properti/proyek/dijual-balboa-estate-pondok-cabe-oleh-pt-teman-properti-aradhana-21409781#1454";

        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("start-maximized");

        ChromeDriver chromeDriver = new ChromeDriver(chromeOptions);

        DevTools devTools = chromeDriver.getDevTools();
        devTools.createSession();
        devTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));

        final RequestId[] requestId = new RequestId[1];
        devTools.addListener(Network.responseReceived(), entry -> {
            Response responseNetwork = entry.getResponse();
            requestId[0] = entry.getRequestId();

            if (responseNetwork.getUrl().contains("nearbySearch")) {
                String[] urlSplit = responseNetwork.getUrl().split("location=");
                String[] urlSplit2 = urlSplit[1].split("&");
                String[] urlSplit3 = urlSplit2[0].split("%2C");

                System.out.println("Longitude: "+urlSplit3[0]+" | Latitude: "+urlSplit3[1]);

                // get response body
                String responseBody = devTools.send(Network.getResponseBody(requestId[0])).getBody();
                System.out.println(responseBody);
            }
        });

        chromeDriver.get(propertyURL);
        chromeDriver.executeScript(
                "arguments[0].scrollIntoView(true);",
                chromeDriver.findElement(By.ByXPath.xpath("//*[@id=\"location-section\"]")));

        if(Objects.equals(chromeDriver.findElement(By.ByXPath.xpath("//*[@id=\"location-section\"]/div[1]/h2")).getText(), "Tentang lokasi")) {
            chromeDriver.findElement(
                    By.ByXPath.xpath("//*[@id=\"location-section\"]/div[1]/div/nav/a[2]")).click();
        } else {
            chromeDriver.executeScript(
                    "arguments[0].scrollIntoView(true);",
                    chromeDriver.findElement(By.ByXPath.xpath("//*[@id=\"map-accordion\"]")));
        }

        Thread.sleep(20000);
        chromeDriver.close();
    }
}
