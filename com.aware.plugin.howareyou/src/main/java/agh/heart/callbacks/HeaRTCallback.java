package agh.heart.callbacks;

import android.content.ContentResolver;


public class HeaRTCallback {

    protected static final String TAG = "AWARE::HowAreYou::HeaRT";

    static ContentResolver resolver = null;

    public static void register(ContentResolver resolver) {
        HeaRTCallback.resolver = resolver;
    }

    public static void unregister() {
        resolver = null;
    }
}
