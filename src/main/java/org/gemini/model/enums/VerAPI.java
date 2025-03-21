package org.gemini.model.enums;

public enum VerAPI {
    V1BETA("v1beta");

    final String verApi;
    VerAPI(String verApi){
        this.verApi = verApi;
    }

    @Override
    public String toString() {
        return verApi;
    }
}
