package com.example.ali_arslan_project2;

public class Item {
    public int id;
    public String name;
    public String location;
    public int stock;
    public boolean notification;
    public int lessThan;
    public int userId;
    public int stockId;

    public Item(int id, String name, String location, int stock, boolean notification, int lessThan, int userId, int stockId) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.stock = stock;
        this.notification = notification;
        this.lessThan = lessThan;
        this.userId = userId;
        this.stockId = stockId;
    }

    // Getter ve setter metodları ekleyebilirsiniz
}
