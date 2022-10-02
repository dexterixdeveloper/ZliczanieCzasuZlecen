package pl.vot.dexterix.zliczanieczasuzlecen;

public class daneZlecenRaporty {
    private int firmaId;
    private float stawkaWysokosc;
    private long czas;
    private String firmaNazwa;
    private float czasS;

    @Override
    public String toString() {
        czasS = Float.valueOf(czas/1000);
        if (czas > 0L) {
            return firmaNazwa +
                    " | " + stawkaWysokosc +
                    " | " + String.format("%.2f", czasS / 60 / 60) +
                    " | " + String.format("%.2f", czasS / 60 / 60 * stawkaWysokosc) +
                    "\n";
        }else{
            return "";
        }
    }

    public String getFirmaNazwa() {
        return firmaNazwa;
    }

    public void setFirmaNazwa(String firmaNazwa) {
        this.firmaNazwa = firmaNazwa;
    }

    public daneZlecenRaporty(int firmaId, float stawkaWysokosc, long czas, String fimraNazwa) {
        this.firmaId = firmaId;
        this.stawkaWysokosc = stawkaWysokosc;
        this.czas = czas;
        this.firmaNazwa = fimraNazwa;
    }

    public float getStawkaWysokosc() {
        return stawkaWysokosc;
    }

    public void setStawkaWysokosc(float stawkaWysokosc) {
        this.stawkaWysokosc = stawkaWysokosc;
    }

    public int getFirmaId() {
        return firmaId;
    }

    public void setFirmaId(int firmaId) {
        this.firmaId = firmaId;
    }

    public long getCzas() {
        return czas;
    }

    public void setCzas(long czas) {
        this.czas = czas;
    }
}

