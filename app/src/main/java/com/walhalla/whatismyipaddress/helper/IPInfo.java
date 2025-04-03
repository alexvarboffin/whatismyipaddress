package com.walhalla.whatismyipaddress.helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class IPInfo {

    public EntityWrapper InfoConnection;
    public IPInfoLocal InfoLocal;
    public IPInfoRemote InfoRemote;

    public IPInfo(String[] values) {
        setArray(values);
    }

    public IPInfo() {
    }

    public String[] getArray() {
        List<String> ret = new ArrayList<>();
        ret.add(this.InfoRemote != null ? "Y" : "N");
        ret.add(this.InfoLocal != null ? "Y" : "N");
        ret.add(this.InfoConnection != null ? "Y" : "N");
        if (this.InfoRemote != null) {
            ret.addAll(this.InfoRemote.getQArray());
        }
        if (this.InfoLocal != null) {
            ret.addAll(this.InfoLocal.getArray());
        }
        if (this.InfoConnection != null) {
            ret.addAll(this.InfoConnection.getArray());
        }
        return ret.toArray(new String[0]);
    }

    public void setArray(String[] values) {
        setArray(Arrays.asList(values).iterator());
    }

    public void setArray(Iterator<String> values) {
        boolean isRemote;
        boolean isLocal;
        boolean isConnection;
        isRemote = "Y".equals(values.next());
        isLocal = "Y".equals(values.next());
        isConnection = "Y".equals(values.next());
        if (isRemote) {
            this.InfoRemote = new IPInfoRemote(values);
        }
        if (isLocal) {
            this.InfoLocal = new IPInfoLocal(values);
        }
        if (isConnection) {
            this.InfoConnection = new EntityWrapper(values);
        }
    }
}
