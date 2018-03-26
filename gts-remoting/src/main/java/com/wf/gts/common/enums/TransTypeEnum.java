

package com.wf.gts.common.enums;


public enum TransTypeEnum {

    ROOT(1),
    BRANCH(2);

    int id;

    TransTypeEnum(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public  static TransTypeEnum  valueOf(int id) {
        switch (id) {
            case 1:
                return ROOT;
            case 2:
                return BRANCH;
            default:
                return null;
        }
    }

}
