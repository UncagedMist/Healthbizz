package com.ihuntech.healthbizz.CartDB.DataSource;

import java.util.List;

import com.ihuntech.healthbizz.CartDB.Model.Cart;
import io.reactivex.Flowable;

public interface ICartDataSource {
    Flowable<List<Cart>> getCartItems();

    Flowable<List<Cart>> getCartItemById(int cartItemId);

    int countCartItems();

    void emptyCart();

    void insertToCart(Cart...carts);

    void updateCart(Cart...carts);

    void deleteCartItem(Cart cart);


}
