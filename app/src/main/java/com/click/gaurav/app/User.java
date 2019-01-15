package com.click.gaurav.app;

public class User {
  private String mobileNo;
  private String name;

  public String getMobileNo() {
    return mobileNo;
  }

  public void setMobileNo(String mobileNo) {
    this.mobileNo = mobileNo;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public User(String mobileNo, String name) {
    super();
    this.mobileNo = mobileNo;
    this.name = name;
  }

  public User() {
    super();
  }

}
