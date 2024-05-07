package com.example.edermacarelatestt;
import java.util.List;
public interface OnFetchClinicAddressesListener {
    void onSuccess(List<String> clinicAddresses);
    void onFailure(String errorMessage);
}
