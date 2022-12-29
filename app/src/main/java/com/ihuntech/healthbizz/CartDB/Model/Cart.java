package com.ihuntech.healthbizz.CartDB.Model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Cart")
public class Cart {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public int id;

    @ColumnInfo(name = "productId")
    public String productId;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "slug")
    public String slug;

    @ColumnInfo(name = "photo")
    public String photo;

    @ColumnInfo(name = "category")
    public String category;

    @ColumnInfo(name = "qty")
    public int qty;

    @ColumnInfo(name = "price")
    public String price;

    @ColumnInfo(name = "main_price")
    public String main_price;

    @ColumnInfo(name = "subTotal")
    public String subTotal;

    @ColumnInfo(name = "Total")
    public String Total;
}
