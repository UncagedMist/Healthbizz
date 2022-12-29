package com.ihuntech.healthbizz.Common;

import android.text.Html;

import com.ihuntech.healthbizz.CartDB.DataSource.CartRepository;
import com.ihuntech.healthbizz.CartDB.Local.CartDatabase;
import com.ihuntech.healthbizz.CartDB.Model.Cart;
import com.ihuntech.healthbizz.Model.Product;
import com.ihuntech.healthbizz.Model.User;
import com.ihuntech.healthbizz.Remote.IMyAPI;
import com.ihuntech.healthbizz.Remote.RetrofitClient;

import java.util.List;

public class Common {
    public static final String OFFICIAL_URL = "https://healthbizz.in/";

    public static final String BASE_URL = OFFICIAL_URL+"AndroidApp/";

    public static final String IMAGE_URL = OFFICIAL_URL+"assets/images/";

    public static User CURRENT_USER;
    public static Product CURRENT_PRODUCT;


    public static String CURRENT_PRICE = "";
    public static String SUB_TOTAL = "";
    public static String TOTAL = "";

    public static final int DOLLAR_VALUE = 74;
    public static String EMAIL_KEY = "Email";
    public static String PWD_KEY = "Password";

    public static String FIRST_NAME = "";
    public static String LAST_NAME = "";
    public static String EMAIL = "";
    public static String PHONE = "";
    public static String ADDRESS_1 = "";
    public static String ADDRESS_2 = "";
    public static String CITY = "";
    public static String ZIP = "";
    public static String COUNTRY = "";

    public static String FIRST_NAME_1 = "";
    public static String LAST_NAME_1 = "";
    public static String EMAIL_1 = "";
    public static String PHONE_1 = "";
    public static String ADDRESS_1_1 = "";
    public static String ADDRESS_2_1 = "";
    public static String CITY_1 = "";
    public static String ZIP_1 = "";
    public static String COUNTRY_1 = "";
    public static String COUNTRY_ID = "";

    public static String STATE = "";

    public static int CHECK_STATUS = 0;
    public static int PAYMENT_METHOD = 0;

    public static List<Cart> CARTS;

    public static IMyAPI getAPI() {
        return RetrofitClient.getClient(BASE_URL).create(IMyAPI.class);
    }

    public static CartDatabase cartDatabase;
    public static CartRepository cartRepository;

    public static String stripHtml(String html) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY).toString();
        } else {
            return Html.fromHtml(html).toString();
        }
    }

    public static int calculatePrice(float price)  {
        float result = (price * DOLLAR_VALUE);

        return Math.round(result);
    }

    public static int calculateDiscount(float original, float discount)    {

        float org = calculatePrice(original);
        float dis = calculatePrice(discount);

        float result = (((org - dis) / org) * 100);

        return Math.round(result);
    }
}
