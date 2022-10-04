package lesson7.project;

public final class ApplicationGlobalState {

    private static ApplicationGlobalState INSTANCE;
    private String selectedCity = null;
   private final String API_KEY = "B9ZwmXuOpCmBlYCtAFMASK9mjt6TIAXm";
   private final String DB_FILENAME = "application.db";
    private ApplicationGlobalState() {
    }

    // Ќепотокобезопасный код дл€ упрощени€
    public static ApplicationGlobalState getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new ApplicationGlobalState();
        }

        return INSTANCE;
    }

    public String getSelectedCity() {
        return selectedCity;
    }

    public void setSelectedCity(String selectedCity) {
        this.selectedCity = selectedCity;
    }

    public String getApiKey() {
        return this.API_KEY;
    }

    public String getDbFileName() {
        return this.DB_FILENAME;
    }
}

