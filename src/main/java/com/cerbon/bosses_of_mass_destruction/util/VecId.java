package com.cerbon.bosses_of_mass_destruction.util;

import java.util.Arrays;

public enum VecId {
    BrimstoneParticleEffect;

    public static VecId fromInt(int value){
        return Arrays.stream(values()).filter(vecId -> vecId.ordinal() == value).findFirst().orElse(null);
    }
}
