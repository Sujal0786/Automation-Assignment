import com.microsoft.playwright.*;
import com.microsoft.playwright.options.*;

public class QuickProbe {
  public static void main(String[] args) {
    try (Playwright pw = Playwright.create()) {
      Browser b = pw.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
      Page p = b.newContext().newPage();
      p.navigate("https://casekaro.com/");
      p.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Mobile Covers")).first().click();
      p.waitForLoadState(LoadState.NETWORKIDLE);
      System.out.println("URL after nav: " + p.url());
      var inputs = p.locator("input").all();
      for (int i = 0; i < inputs.size(); i++) {
        var el = inputs.get(i);
        String ph = el.getAttribute("placeholder");
        String id = el.getAttribute("id");
        String name = el.getAttribute("name");
        String type = el.getAttribute("type");
        if (ph != null || (id != null && id.toLowerCase().contains("model")) || (name != null && name.toLowerCase().contains("model")))
          System.out.println(i + " placeholder=" + ph + " id=" + id + " name=" + name + " type=" + type + " visible=" + el.isVisible());
      }
      p.locator("text=Phone cases by model").first().scrollIntoViewIfNeeded();
      var sb = p.locator("input[placeholder*='phone' i], input[placeholder*='model' i], input[placeholder*='Search' i]");
      System.out.println("search candidates: " + sb.count());
      if (sb.count() > 0) {
        sb.first().click();
        sb.first().fill("iPhone 16 Pro");
        p.waitForTimeout(2000);
        System.out.println("options iPhone 16 Pro exact: " + p.getByText("iPhone 16 Pro", new Page.GetByTextOptions().setExact(true)).count());
        System.out.println("links iPhone 16 Pro: " + p.locator("a").filter(new Locator.FilterOptions().setHasText("iPhone 16 Pro")).count());
      }
      b.close();
    }
  }
}
