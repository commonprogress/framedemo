package com.dongxl.rootdao.entities;

import androidx.room.ColumnInfo;

public class AddressBean {
    public String street;
    public String state;
    public String city;

    @ColumnInfo(name = "post_code")
    public int postCode;
}
