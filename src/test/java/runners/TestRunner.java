package runners;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"stepdefinitions", "base"},
        plugin = {
                "pretty",
                "html:target/cucumber-report.html"
        },
        tags = "@CaseKaro",
        monochrome = true
)
public class TestRunner {
}
