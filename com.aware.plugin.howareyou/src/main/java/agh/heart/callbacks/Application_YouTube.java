package agh.heart.callbacks;

import heart.Callback;

public class Application_YouTube extends Application_Generic implements Callback {
    private static final String APPLICATION_NAME = "youtube";
    private static final String APPLICATION_PACKAGE = "com.google.android.youtube";

    public Application_YouTube(){
        super(APPLICATION_NAME, APPLICATION_PACKAGE);
    }
}