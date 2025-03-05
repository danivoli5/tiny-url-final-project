package com.handson.tinyurl;

import com.handson.tinyurl.model.ShortUrl;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

public class ShortUrlTest {

    @Test
    public void testDefaultClicksIsEmpty() {
        ShortUrl shortUrl = new ShortUrl();
        assertNotNull(shortUrl.getClicks(), "Clicks map should not be null");
        assertTrue(shortUrl.getClicks().isEmpty(), "Clicks map should be empty by default");
    }

    @Test
    public void testSetAndGetClicks() {
        ShortUrl shortUrl = new ShortUrl();
        Map<String, Integer> clicksMap = new HashMap<>();
        clicksMap.put("2023-03", 10);
        shortUrl.setClicks(clicksMap);

        Map<String, Integer> retrievedClicks = shortUrl.getClicks();
        assertEquals(1, retrievedClicks.size(), "Clicks map should contain one entry");
        assertEquals(10, retrievedClicks.get("2023-03"), "The value for key '2023-03' should be 10");
    }

    @Test
    public void testEqualsAndHashCode() {
        ShortUrl shortUrl1 = new ShortUrl();
        ShortUrl shortUrl2 = new ShortUrl();

        // Initially, both ShortUrl objects have empty maps, so they should be equal.
        assertEquals(shortUrl1, shortUrl2, "Both ShortUrl objects should be equal when their maps are empty");
        assertEquals(shortUrl1.hashCode(), shortUrl2.hashCode(), "Hash codes should match for equal objects");

        // Update the map in shortUrl1.
        Map<String, Integer> clicksMap = new HashMap<>();
        clicksMap.put("2023-03", 10);
        shortUrl1.setClicks(clicksMap);

        // They should now differ.
        assertNotEquals(shortUrl1, shortUrl2, "Objects should not be equal after modifying one");

        // Set the same map in shortUrl2.
        shortUrl2.setClicks(new HashMap<>(clicksMap));
        assertEquals(shortUrl1, shortUrl2, "Both ShortUrl objects should be equal after setting the same map");
        assertEquals(shortUrl1.hashCode(), shortUrl2.hashCode(), "Hash codes should match after making the objects equal");
    }

    @Test
    public void testToString() {
        ShortUrl shortUrl = new ShortUrl();
        Map<String, Integer> clicksMap = new HashMap<>();
        clicksMap.put("2023-03", 10);
        shortUrl.setClicks(clicksMap);

        String expected = "ShortUrl{" +
                "clicks=" + clicksMap +
                '}';
        assertEquals(expected, shortUrl.toString(), "toString output should match the expected string");
    }
}
