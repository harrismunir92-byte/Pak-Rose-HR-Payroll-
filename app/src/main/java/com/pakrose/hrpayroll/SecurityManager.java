
package com.pakrose.hrpayroll;

import android.content.Context;
import android.content.SharedPreferences;

import java.security.MessageDigest;

public class SecurityManager {

    private static final String PREFS = "pakrose_security";
    private static final String PIN_KEY = "pin_hash";
    private static final String LOCK_KEY = "pin_enabled";

    private final SharedPreferences prefs;

    public SecurityManager(Context context) {
        prefs = context.getSharedPreferences(
                PREFS, Context.MODE_PRIVATE);
    }

    public boolean isPinEnabled() {
        return prefs.getBoolean(LOCK_KEY, false);
    }

    public boolean hasPin() {
        return prefs.contains(PIN_KEY);
    }

    public boolean setPin(String pin) {
        if (pin == null || !pin.matches("[0-9]{4,8}")) {
            return false;
        }

        String hash = hashPin(pin);

        if (hash == null) return false;

        return prefs.edit()
                .putString(PIN_KEY, hash)
                .putBoolean(LOCK_KEY, true)
                .commit();
    }

    public boolean verifyPin(String pin) {
        if (pin == null || !hasPin()) return false;

        String saved = prefs.getString(PIN_KEY, "");
        String entered = hashPin(pin);

        return saved != null
                && entered != null
                && saved.equals(entered);
    }

    public boolean disablePin(String currentPin) {
        if (!verifyPin(currentPin)) return false;

        return prefs.edit()
                .remove(PIN_KEY)
                .putBoolean(LOCK_KEY, false)
                .commit();
    }

    private String hashPin(String pin) {
        try {
            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] bytes = digest.digest(
                    pin.getBytes("UTF-8"));

            StringBuilder result = new StringBuilder();

            for (byte b : bytes) {
                result.append(String.format("%02x", b));
            }

            return result.toString();

        } catch (Exception e) {
            return null;
        }
    }
}
