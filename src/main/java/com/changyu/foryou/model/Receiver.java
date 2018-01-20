package com.changyu.foryou.model;

public class Receiver extends ReceiverKey {
	
	private String userId;
	private String addressId;
    private String phone;

    private String name;

    private String address;

    private Short isDefault;
    

    public Receiver(){
    	
    }
    
    public Receiver(String userId,String phone, String name, String address,Integer campusId) {
		this.userId= userId;
		this.phone = phone;
		this.name = name;
		this.address = address;
	}


	public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone == null ? null : phone.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address == null ? null : address.trim();
    }

	public Short getIsDefault() {
		return isDefault;
	}

	public void setIsDefault(Short isDefault) {
		this.isDefault = isDefault;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getAddressId() {
		return addressId;
	}

	public void setAddressId(String addressId) {
		this.addressId = addressId;
	}
}