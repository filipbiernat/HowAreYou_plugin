package agh.heart.callbacks;

import heart.Callback;

public class Application_GoogleMaps extends Application_Generic implements Callback {
    private static final String APPLICATION_NAME = "google_maps";
    private static final String APPLICATION_PACKAGE = "com.google.android.apps.maps";

    public Application_GoogleMaps(){
        super(APPLICATION_NAME, APPLICATION_PACKAGE);
    }
}