package com.ihuntech.healthbizz.CartDB.Local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.ihuntech.healthbizz.CartDB.Model.Cart;

@Database(entities = {Cart.class},version = 1)
public abstract class CartDatabase extends RoomDatabase {

    public abstract CartDAO cartDAO();

    private static CartDatabase instance;

    public static CartDatabase getInstance(Context context) {
        if (instance == null)   {
            instance = Room.databaseBuilder(
                            context,
                            CartDatabase.class,
                            "HalthBizz_DB")
                    .allowMainThreadQueries()
                    .build();
        }
        return instance;
    }
}
