package SurveyRequest;

import com.google.gson.annotations.SerializedName;

public class RequestData {

    @SerializedName("serviceNeed")
    public String serviceRequested;

    @SerializedName("addressInfo")
    public String addressInfo;

    @SerializedName("date")
    public String date;

    @SerializedName("expertId")
    public String selectedExpertId;

    @SerializedName("userId")
    public String userIdWhoRequest;

    @SerializedName("clientName")
    public String clientName;

    public RequestData() {
    }

    public RequestData(String serviceRequested, String addressInfo, String date, String selectedExpertId, String userIdWhoRequest, String clientName) {
        this.serviceRequested = serviceRequested;
        this.addressInfo = addressInfo;
        this.date = date;
        this.selectedExpertId = selectedExpertId;
        this.userIdWhoRequest = userIdWhoRequest;
        this.clientName = clientName;
    }

    public String getServiceRequested() {
        return serviceRequested;
    }

    public void setServiceRequested(String serviceRequested) {
        this.serviceRequested = serviceRequested;
    }

    public String getAddressInfo() {
        return addressInfo;
    }

    public void setAddressInfo(String addressInfo) {
        this.addressInfo = addressInfo;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSelectedExpertId() {
        return selectedExpertId;
    }

    public void setSelectedExpertId(String selectedExpertId) {
        this.selectedExpertId = selectedExpertId;
    }

    public String getUserIdWhoRequest() {
        return userIdWhoRequest;
    }

    public void setUserIdWhoRequest(String userIdWhoRequest) {
        this.userIdWhoRequest = userIdWhoRequest;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }
}
