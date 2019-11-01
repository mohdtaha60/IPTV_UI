package osmandroid.iptv.home;


public class User {
    String name,planName,expDate;

    public User() {}

    public User(String name, String planName, String expDate) {
        this.name = name;
        this.planName = planName;
        this.expDate = expDate;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public String getExpDate() {
        return expDate;
    }

    public void setExpDate(String expDate) {
        this.expDate = expDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
