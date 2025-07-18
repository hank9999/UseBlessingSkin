package com.github.hank9999.useblessingskin.shared.model;

public final class MineSkinData {
    private final String value;
    private final String signature;

    public MineSkinData(String value, String signature) {
        this.value = value;
        this.signature = signature;
    }

    public String getValue() {
        return value;
    }

    public String getSignature() {
        return signature;
    }

    @Override
    public String toString() {
        return "MineSkinResult{" +
                "value='" + value + '\'' +
                ", signature='" + signature + '\'' +
                '}';
    }
}