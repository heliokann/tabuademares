package com.novoideal.tabuademares;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RefreshCooldownTest {

    @Test
    public void cooldown_notActiveOnFirstCall() {
        // lastRefreshAt=0 (initial state); now is a realistic epoch-millis
        // now - 0 >> REFRESH_COOLDOWN_MS → cooldown not active
        long now = 1_700_000_000_000L;
        assertFalse(MainActivity.isCooldownActive(now, 0L, MainActivity.REFRESH_COOLDOWN_MS));
    }

    @Test
    public void cooldown_activeWhenWithinWindow() {
        long lastRefresh = 1_700_000_000_000L;
        long now = lastRefresh + MainActivity.REFRESH_COOLDOWN_MS - 1;
        assertTrue(MainActivity.isCooldownActive(now, lastRefresh, MainActivity.REFRESH_COOLDOWN_MS));
    }

    @Test
    public void cooldown_notActiveWhenExactlyAtBoundary() {
        long lastRefresh = 1_700_000_000_000L;
        long now = lastRefresh + MainActivity.REFRESH_COOLDOWN_MS;
        assertFalse(MainActivity.isCooldownActive(now, lastRefresh, MainActivity.REFRESH_COOLDOWN_MS));
    }

    @Test
    public void cooldown_notActiveAfterWindowExpires() {
        long lastRefresh = 1_700_000_000_000L;
        long now = lastRefresh + MainActivity.REFRESH_COOLDOWN_MS + 1;
        assertFalse(MainActivity.isCooldownActive(now, lastRefresh, MainActivity.REFRESH_COOLDOWN_MS));
    }

    @Test
    public void cooldownMs_is5000() {
        assertEquals(5000L, MainActivity.REFRESH_COOLDOWN_MS);
    }
}
