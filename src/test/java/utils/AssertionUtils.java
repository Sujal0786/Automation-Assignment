package utils;

import com.microsoft.playwright.Locator;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Custom assertions wrapper to provide standardized console logging and error descriptions.
 */
public class AssertionUtils {

    public static void assertPageUrlContains(String actualUrl, String expectedSubstring, String errorMessage) {
        System.out.println("ASSERT: Verifying URL '" + actualUrl + "' contains '" + expectedSubstring + "'");
        assertTrue(errorMessage + " (Actual: " + actualUrl + ")", actualUrl.contains(expectedSubstring));
    }

    public static void assertElementVisible(Locator locator, String errorMessage) {
        System.out.println("ASSERT: Verifying element visibility: " + locator);
        assertTrue(errorMessage, locator.isVisible());
    }

    public static void assertConditionTrue(boolean condition, String errorMessage) {
        assertTrue(errorMessage, condition);
    }

    public static void assertConditionFalse(boolean condition, String errorMessage) {
        assertFalse(errorMessage, condition);
    }

    public static void assertEqualsWithMessage(Object expected, Object actual, String errorMessage) {
        assertEquals(errorMessage, expected, actual);
    }
}
