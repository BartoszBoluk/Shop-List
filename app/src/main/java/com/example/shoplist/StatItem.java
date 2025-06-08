package com.example.shoplist;

public class StatItem {
    private String name;
    private long count;
    private long lastAdded;

    public StatItem(String name, long count, long lastAdded) {
        this.name = name;
        this.count = count;
        this.lastAdded = lastAdded;
    }

    public String getName() {
        return name;
    }

    public long getCount() {
        return count;
    }

    public long getLastAdded() {
        return lastAdded;
    }
}
