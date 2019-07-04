package com.dev.util;
@KeepNameIfNecessary
public class ColorStore {
    private int color;
    @KeepMemberIfNecessary
    public int getColor() {
        return color;
    }
    @KeepMemberIfNecessary
    public void setColor(int color) {
        this.color = color;
    }
}
