package com.example.backendtest.type;

import lombok.Getter;

@Getter
public enum MemberType {
    PLATINUM("Platinum", "platinum"),
    GOLD("Gold", "gold"),
    SILVER("Silver", "silver"),
    UNKNOWN("Unknown", "unknown");

    private final String displayValue;
    private final String dbValue;

    MemberType(String displayValue, String dbValue) {
        this.displayValue = displayValue;
        this.dbValue = dbValue;
    }

    public static MemberType findTypeBySalary(Integer salary) {
        if (salary > 50000) {
            return PLATINUM;
        } else if (salary >= 30000) {
            return GOLD;
        } else if (salary >= 15000) {
            return SILVER;
        } else {
            return UNKNOWN;
        }
    }

    public static MemberType findDbValue(String dbValue) {
        for (MemberType type : MemberType.values()) {
            if (type.getDbValue().equals(dbValue)) {
                return type;
            }
        }
        return UNKNOWN;
    }
}
