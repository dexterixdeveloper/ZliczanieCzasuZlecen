package pl.vot.dexterix.zliczanieczasuzlecen;

public class daneKalendarza extends daneKlasaPodstawowa{
    private Long calendar_id;
    private String accountName;
    private String calendarDisplayName;
    private String ownerAccount;

    public Long getCalendar_id() {
        return calendar_id;
    }

    public void setCalendar_id(Long calendar_id) {
        this.calendar_id = calendar_id;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getCalendarDisplayName() {
        return calendarDisplayName;
    }

    public void setCalendarDisplayName(String calendarDisplayName) {
        this.calendarDisplayName = calendarDisplayName;
    }

    public String getOwnerAccount() {
        return ownerAccount;
    }

    public void setOwnerAccount(String ownerAccount) {
        this.ownerAccount = ownerAccount;
    }

    public String toStringDoRecyclerView() {
        return  "ID Kalendarza: " + calendar_id + "\n" +
                "Konto: " + accountName + "\n" +
                "Nazwa Kalendarza: " + calendarDisplayName;
    }

    public daneKalendarza() {
        this.calendar_id = null;
        this.accountName = null;
        this.calendarDisplayName = null;
        this.ownerAccount = null;
        this.uwagi = null;
    }

    public void onCreate(){
        this.setId(0);
        this.setCalendar_id(0L);
        this.setAccountName("");
        this.setCalendarDisplayName("");
        this.setOwnerAccount("");


    }

}
