package com.solvemate.config;

import java.util.Map;

public final class SolventHazardNotes {

    private SolventHazardNotes() {}

    private static final Map<String, String> NOTES = Map.ofEntries(
            Map.entry("benzene", "it is a known human carcinogen linked to leukemia with long-term exposure"),
            Map.entry("chloroform", "it is classified as a probable human carcinogen and is heavily restricted in many countries"),
            Map.entry("toluene", "repeated exposure is linked to nervous system damage and reproductive harm"),
            Map.entry("formaldehyde", "it is classified as a human carcinogen and a common cause of respiratory irritation"),
            Map.entry("carbon tetrachloride", "it is banned under the Montreal Protocol and linked to liver damage and cancer risk"),
            Map.entry("n-hexane", "prolonged exposure is linked to nerve damage (peripheral neuropathy)"),
            Map.entry("xylene", "exposure is linked to headaches, dizziness and long-term nervous system effects"),
            Map.entry("nitrobenzene", "it is toxic if absorbed through skin and is a suspected carcinogen")
    );

    /** Returns a known health/safety hazard note for a solvent name, or null if none is on record. */
    public static String lookup(String solventName) {
        if (solventName == null) return null;
        return NOTES.get(solventName.trim().toLowerCase());
    }
}