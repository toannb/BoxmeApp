package com.shipchung.config;

import com.shipchung.bean.LineItem;
import com.shipchung.bean.PutAwayBean;
import com.shipchung.bean.UIDItemBean;
import com.shipchung.bean.UserInfoBean;

import java.util.ArrayList;

/**
 * Created by ToanNB on 8/3/2015.
 */
public class Variables {
    public static UserInfoBean mUserInfo = new UserInfoBean();
    public static ArrayList<PutAwayBean> mArrPutAway = new ArrayList<>();
    public static ArrayList<UIDItemBean> mArrUIDPutawayed = new ArrayList<>();
    public static ArrayList<UIDItemBean> mArrUIDPickuped = new ArrayList<>();
    public static ArrayList<UIDItemBean> mArrUidUpdated = new ArrayList<>();
    public static ArrayList<UIDItemBean> mArrUidShipment = new ArrayList<>();
    public static ArrayList<UIDItemBean> mArrUidUpdatedRestockReturn = new ArrayList<>();
    public static ArrayList<LineItem> mArrLineItem = new ArrayList<>();
    public static String shipment_code = "";
    public static int mStatusCode = 0;
}
