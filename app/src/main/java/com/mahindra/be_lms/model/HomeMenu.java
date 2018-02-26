package com.mahindra.be_lms.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Pravin on 11/22/16.
 */
public class HomeMenu implements Parcelable {
    public static final Creator<HomeMenu> CREATOR = new Creator<HomeMenu>() {
        @Override
        public HomeMenu createFromParcel(Parcel source) {
            return new HomeMenu(source);
        }

        @Override
        public HomeMenu[] newArray(int size) {
            return new HomeMenu[size];
        }
    };
    private String menuName;
    private int menuIcon;
    private int back_color;

    public HomeMenu(String menuName, int menuIcon, int back_color) {
        this.menuName = menuName;
        this.menuIcon = menuIcon;
        this.back_color = back_color;
    }

    protected HomeMenu(Parcel in) {
        this.menuName = in.readString();
        this.menuIcon = in.readInt();
        this.back_color = in.readInt();
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public int getMenuIcon() {
        return menuIcon;
    }

    public void setMenuIcon(int menuIcon) {
        this.menuIcon = menuIcon;
    }

    public int getBack_color() {
        return back_color;
    }

    public void setBack_color(int back_color) {
        this.back_color = back_color;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.menuName);
        dest.writeInt(this.menuIcon);
        dest.writeInt(this.back_color);
    }

    @Override
    public String toString() {
        return menuName;
    }
}
