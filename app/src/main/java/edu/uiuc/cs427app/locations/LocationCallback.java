package edu.uiuc.cs427app.locations;
import java.util.List;

public interface LocationCallback {
    // Called when cities are successfully updated for the user
    void citiesUpdated(List<String> cities);

    // Called when cities update failed due to any reason
    void citiesUpdateFail(String errorMessage);
}
