import java.util.HashMap;

public class Counter {
    private static int pinf = 0;
    private static int epi = 0;
    private static int rnf = 0;
    private static int er = 0;
    private static int anf = 0;
    private static int eti = 0;
    private static int pnf = 0;
    private static int tinf = 0;
    private static int einf = 0;
    private static int eei = 0;
    private static int emi = 0;
    private static int minf = 0;
    private static final HashMap<Integer, Integer> pinfMap = new HashMap<>();
    private static final HashMap<Integer, Integer> epiMap = new HashMap<>();
    private static final HashMap<Integer, Integer> rnfMap = new HashMap<>();
    private static final HashMap<Integer, Integer> erMap = new HashMap<>();
    private static final HashMap<Integer, Integer> anfMap = new HashMap<>();
    private static final HashMap<Integer, Integer> etiMap = new HashMap<>();
    private static final HashMap<Integer, Integer> pnfMap = new HashMap<>();
    private static final HashMap<Integer, Integer> tinfMap = new HashMap<>();
    private static final HashMap<Integer, Integer> einfMap = new HashMap<>();
    private static final HashMap<Integer, Integer> eeiMap = new HashMap<>();
    private static final HashMap<Integer, Integer> emiMap = new HashMap<>();
    private static final HashMap<Integer, Integer> minfMap = new HashMap<>();

    public int getPinf() {
        return pinf;
    }

    public int getEpi() {
        return epi;
    }

    public int getRnf() {
        return rnf;
    }

    public int getEr() {
        return er;
    }

    public int getAnf() {
        return anf;
    }

    public int getEti() {
        return eti;
    }

    public int getPnf() {
        return pnf;
    }

    public int getTinf() {
        return tinf;
    }

    public int getEinf() {
        return einf;
    }

    public int getEei() {
        return eei;
    }

    public int getEmi() {
        return emi;
    }

    public int getMinf() {
        return minf;
    }

    public int getPinfCnt(int id) {
        return pinfMap.getOrDefault(id, 0);
    }

    public int getEpiCnt(int id) {
        return epiMap.getOrDefault(id, 0);
    }

    public int getRnfCnt(int id) {
        return rnfMap.getOrDefault(id, 0);
    }

    public int getErCnt(int id) {
        return erMap.getOrDefault(id, 0);
    }

    public int getAnfCnt(int id) {
        return anfMap.getOrDefault(id, 0);
    }

    public int getEtiCnt(int id) {
        return etiMap.getOrDefault(id, 0);
    }

    public int getPnfCnt(int id) {
        return pnfMap.getOrDefault(id, 0);
    }

    public int getTinfCnt(int id) {
        return tinfMap.getOrDefault(id, 0);
    }

    public int getEinfCnt(int id) {
        return einfMap.getOrDefault(id, 0);
    }

    public int getEeiCnt(int id) {
        return eeiMap.getOrDefault(id, 0);
    }

    public int getEmiCnt(int id) {
        return emiMap.getOrDefault(id, 0);
    }

    public int getMinfCnt(int id) {
        return minfMap.getOrDefault(id, 0);
    }

    public void addPinf(int id) {
        if (pinfMap.containsKey(id)) {
            int cnt = pinfMap.get(id);
            pinfMap.put(id, cnt + 1);
        } else {
            pinfMap.put(id, 1);
        }
        pinf++;
    }

    public void addEpi(int id) {
        if (epiMap.containsKey(id)) {
            int cnt = epiMap.get(id);
            epiMap.put(id, cnt + 1);
        } else {
            epiMap.put(id, 1);
        }
        epi++;
    }

    public void addRnf(int id1, int id2) {
        if (rnfMap.containsKey(id1)) {
            int cnt = rnfMap.get(id1);
            rnfMap.put(id1, cnt + 1);
        } else {
            rnfMap.put(id1, 1);
        }
        if (id1 != id2) {
            if (rnfMap.containsKey(id2)) {
                int cnt = rnfMap.get(id2);
                rnfMap.put(id2, cnt + 1);
            } else {
                rnfMap.put(id2, 1);
            }
        }
        rnf++;
    }

    public void addEr(int id1, int id2) {
        if (erMap.containsKey(id1)) {
            int cnt = erMap.get(id1);
            erMap.put(id1, cnt + 1);
        } else {
            erMap.put(id1, 1);
        }
        if (id1 != id2) {
            if (erMap.containsKey(id2)) {
                int cnt = erMap.get(id2);
                erMap.put(id2, cnt + 1);
            } else {
                erMap.put(id2, 1);
            }
        }
        er++;
    }

    public void addAnf(int id) {
        if (anfMap.containsKey(id)) {
            int cnt = anfMap.get(id);
            anfMap.put(id, cnt + 1);
        } else {
            anfMap.put(id, 1);
        }
        anf++;
    }

    public void addEti(int id) {
        if (etiMap.containsKey(id)) {
            int cnt = etiMap.get(id);
            etiMap.put(id, cnt + 1);
        } else {
            etiMap.put(id, 1);
        }
        eti++;
    }

    public void addPnf(int id1, int id2) {
        if (pnfMap.containsKey(id1)) {
            int cnt = pnfMap.get(id1);
            pnfMap.put(id1, cnt + 1);
        } else {
            pnfMap.put(id1, 1);
        }
        if (id1 != id2) {
            if (pnfMap.containsKey(id2)) {
                int cnt = pnfMap.get(id2);
                pnfMap.put(id2, cnt + 1);
            } else {
                pnfMap.put(id2, 1);
            }
        }
        pnf++;
    }

    public void addTinf(int id) {
        if (tinfMap.containsKey(id)) {
            int cnt = tinfMap.get(id);
            tinfMap.put(id, cnt + 1);
        } else {
            tinfMap.put(id, 1);
        }
        tinf++;
    }

    public void addEinf(int id) {
        if (einfMap.containsKey(id)) {
            int cnt = einfMap.get(id);
            einfMap.put(id, cnt + 1);
        } else {
            einfMap.put(id, 1);
        }
        einf++;
    }

    public void addEei(int id) {
        if (eeiMap.containsKey(id)) {
            int cnt = eeiMap.get(id);
            eeiMap.put(id, cnt + 1);
        } else {
            eeiMap.put(id, 1);
        }
        eei++;
    }

    public void addEmi(int id) {
        if (emiMap.containsKey(id)) {
            int cnt = emiMap.get(id);
            emiMap.put(id, cnt + 1);
        } else {
            emiMap.put(id, 1);
        }
        emi++;
    }

    public void addMinf(int id) {
        if (minfMap.containsKey(id)) {
            int cnt = minfMap.get(id);
            minfMap.put(id, cnt + 1);
        } else {
            minfMap.put(id, 1);
        }
        minf++;
    }
}
