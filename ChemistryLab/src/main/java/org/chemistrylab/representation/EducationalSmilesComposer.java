package org.chemistrylab.representation;

import java.util.List;
import java.util.StringJoiner;

public final class EducationalSmilesComposer {

    private EducationalSmilesComposer() {
    }

    public static String metalBoundToOxoanion(String metal, String oxoanionCore) {
        return "[" + metal + "]O" + oxoanionCore;
    }

    public static String divalentMetalWithTwoOxoanions(String metal, String oxoanionCore) {
        return "O" + oxoanionCore + "O[" + metal + "]O" + oxoanionCore + "O";
    }

    public static String trivalentMetalWithThreeOxoanions(String metal, String oxoanionCore) {
        return "O" + oxoanionCore + "O[" + metal + "](O" + oxoanionCore + "O)O" + oxoanionCore + "O";
    }

    public static String twoMonovalentMetalsWithOxoanion(String metal, String oxoanionCore) {
        return "[" + metal + "]O" + oxoanionCore + "O[" + metal + "]";
    }

    public static String threeMonovalentMetalsWithOxoanion(String metal, String oxoanionCore) {
        return "[" + metal + "]O" + oxoanionCore + "(O[" + metal + "])O[" + metal + "]";
    }

    public static String ammoniumBoundToOxoanion(String oxoanionCore) {
        return "[NH4]O" + oxoanionCore;
    }

    public static String twoAmmoniumWithOxoanion(String oxoanionCore) {
        return "[NH4]O" + oxoanionCore + "O[NH4]";
    }

    public static String threeAmmoniumWithOxoanion(String oxoanionCore) {
        return "[NH4]O" + oxoanionCore + "(O[NH4])O[NH4]";
    }

    public static String chain(List<String> fragments) {
        StringJoiner joiner = new StringJoiner("");
        for (String fragment : fragments) {
            joiner.add(fragment);
        }
        return joiner.toString();
    }
}
