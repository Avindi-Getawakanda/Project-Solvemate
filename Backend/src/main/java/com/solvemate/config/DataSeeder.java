package com.solvemate.config;

import com.solvemate.model.Polymer;
import com.solvemate.model.Solvent;
import com.solvemate.model.User;
import com.solvemate.repository.PolymerRepository;
import com.solvemate.repository.SolventRepository;
import com.solvemate.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final SolventRepository solventRepository;
    private final PolymerRepository polymerRepository;
    private final UserRepository    userRepository;
    private final PasswordEncoder   passwordEncoder;

    public DataSeeder(SolventRepository solventRepository,
                      PolymerRepository polymerRepository,
                      UserRepository userRepository,
                      PasswordEncoder passwordEncoder) {
        this.solventRepository = solventRepository;
        this.polymerRepository = polymerRepository;
        this.userRepository    = userRepository;
        this.passwordEncoder   = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        seedUsers();
        seedPolymers();
        seedSolvents();
    }

    private void seedUsers() {
        if (userRepository.count() > 0) return;
        saveUser("Admin User", "admin@solvemate.com", "admin123", "ADMIN");
        saveUser("Lab User",   "lab@solvemate.com",   "lab123",   "LAB_USER");
        System.out.println("[DataSeeder] Users seeded");
    }

    private void saveUser(String name, String email, String pw, String role) {
        User u = new User();
        u.setFullName(name); u.setEmail(email);
        u.setPasswordHash(passwordEncoder.encode(pw));
        u.setRole(role); u.setStatus("ACTIVE"); u.setVerified(true);
        userRepository.save(u);
    }

    private void seedPolymers() {
        if (polymerRepository.count() > 0) return;
        Object[][] ps = {
                {"Polyethylene (PE)",              "Thermoplastic", 16.9, 3.3,  4.1,  8.1},
                {"Polypropylene (PP)",             "Thermoplastic", 18.1, 1.0,  0.0,  8.0},
                {"Polystyrene (PS)",               "Thermoplastic", 21.3, 5.8,  4.3, 12.7},
                {"Polyvinyl Chloride (PVC)",       "Thermoplastic", 18.2, 7.5,  8.3, 10.6},
                {"Polyvinyl Acetate (PVAc)",       "Thermoplastic", 20.9, 11.3, 9.7, 13.7},
                {"Polyacrylonitrile (PAN)",        "Thermoplastic", 21.7, 14.1, 9.1, 10.9},
                {"Polymethyl Methacrylate (PMMA)","Thermoplastic", 18.6, 10.5, 7.5,  8.2},
                {"Polycarbonate (PC)",             "Thermoplastic", 20.0, 5.9,  4.1,  7.7},
                {"Polyisobutylene (PIB)",          "Elastomer",     16.9, 2.5,  4.0,  7.2},
                {"Polybutadiene (PBD)",            "Elastomer",     17.5, 2.3,  3.4,  6.5},
                {"Nylon 6 (PA6)",                  "Thermoplastic", 17.5, 12.3, 11.1, 11.2},
                {"Nylon 66 (PA66)",                "Thermoplastic", 18.6, 12.3, 11.1, 11.2},
                {"Polyethylene Terephthalate (PET)","Thermoplastic",19.5, 5.9,  8.0,  6.9},
                {"Cellulose Acetate (CA)",         "Biopolymer",    18.6, 12.7, 11.0, 12.3},
                {"Epoxy Resin",                    "Thermoset",     18.1, 11.4, 9.0,  9.1},
                {"Polyurethane (PU)",              "Elastomer",     17.9, 10.4, 8.9,  9.7},
                {"Polytetrafluoroethylene (PTFE)", "Thermoplastic", 16.2, 1.8,  3.4,  5.2},
                {"Polyvinylidene Fluoride (PVDF)", "Thermoplastic", 17.2, 8.6,  4.2,  7.1},
                {"Polydimethylsiloxane (PDMS)",    "Elastomer",     15.5, 2.9,  4.6,  9.5},
                {"Natural Rubber (NR)",            "Elastomer",     16.6, 1.4,  0.8,  9.6},
                {"Polyvinyl Alcohol (PVA)",        "Thermoplastic", 17.0, 9.0,  18.0, 4.0},
                {"Polyethylene Oxide (PEO)",       "Thermoplastic", 17.1, 3.1,  7.3,  7.7},
                {"Polyphenylene Sulfide (PPS)",    "Thermoplastic", 18.7, 3.4,  3.6,  6.0},
                {"Polyimide (PI)",                 "Thermoset",     20.0, 8.9,  6.8,  8.5},
                {"Cellulose",                      "Biopolymer",    16.1, 18.5, 14.5, 9.3},
                {"Chlorinated Polyethylene (CPE)", "Thermoplastic", 18.4, 5.0,  5.8,  7.2},
                {"Styrene-Butadiene Rubber (SBR)", "Elastomer",     17.5, 4.1,  4.0,  7.8},
                {"Acrylonitrile Butadiene (NBR)",  "Elastomer",     18.9, 8.7,  4.3,  9.3},
                {"Polymethyl Acrylate (PMA)",      "Thermoplastic", 17.4, 9.7,  8.2,  9.4},
                {"Polyethyl Acrylate (PEA)",       "Thermoplastic", 16.8, 7.3,  7.3,  9.5},
                {"Polyvinyl Butyral (PVB)",        "Thermoplastic", 18.2, 4.4,  13.0, 10.5},
                {"Polyethylene Naphthalate (PEN)", "Thermoplastic", 19.8, 5.9,  7.7,  6.8},
                {"Polysulfone (PSU)",              "Thermoplastic", 19.9, 8.4,  5.1,  6.7},
                {"Polyetherimide (PEI)",           "Thermoplastic", 20.1, 9.8,  7.2,  7.5},
                {"Polylactic Acid (PLA)",          "Biopolymer",    18.6, 9.9,  6.0,  7.8},
                {"Polyhydroxybutyrate (PHB)",      "Biopolymer",    18.2, 8.7,  5.1,  7.0},
                {"Neoprene (CR)",                  "Elastomer",     17.2, 6.9,  3.3,  8.5},
                {"Butyl Rubber (IIR)",             "Elastomer",     16.2, 2.5,  4.1,  7.9},
                {"Ethylene Propylene Rubber (EPDM)","Elastomer",    16.2, 3.0,  3.0,  8.2},
                {"Polyacetal (POM)",               "Thermoplastic", 17.7, 11.0, 7.5,  8.2},
                {"Polybutylene Terephthalate (PBT)","Thermoplastic",18.3, 5.6,  8.0,  7.4},
                {"Polyamide 11 (PA11)",            "Thermoplastic", 17.8, 10.2, 10.8, 10.5},
                {"Polyamide 12 (PA12)",            "Thermoplastic", 17.5, 9.0,  10.0, 10.2},
                {"Polyoxymethylene (Delrin)",      "Thermoplastic", 17.7, 11.0, 7.5,  8.0},
                {"Fluoroelastomer (FKM)",          "Elastomer",     16.6, 6.8,  3.0,  6.9},
                {"Lignin",                         "Biopolymer",    21.9, 14.1, 16.9, 13.7},
                {"Starch",                         "Biopolymer",    18.6, 14.5, 22.0, 12.0},
                {"Nitrile Rubber (NBR-34)",        "Elastomer",     19.4, 9.4,  4.4,  9.5},
                {"Polyvinyl Formal (PVF)",         "Thermoplastic", 20.0, 11.5, 13.7, 9.8},
                {"Polychloroprene",                "Elastomer",     17.2, 6.9,  3.3,  8.5},
        };
        for (Object[] p : ps) {
            Polymer poly = new Polymer();
            poly.setPolymerName((String)p[0]); poly.setPolymerCategory((String)p[1]);
            poly.setDeltaD((double)p[2]); poly.setDeltaP((double)p[3]);
            poly.setDeltaH((double)p[4]); poly.setR0((double)p[5]);
            polymerRepository.save(poly);
        }
        System.out.println("[DataSeeder] Polymers seeded: " + polymerRepository.count());
    }

    private void seed(String name, double dD, double dP, double dH, double mv,
                      double dT, double cost, String env, boolean ban) {
        if (solventRepository.existsByName(name)) return;
        Solvent s = new Solvent();
        s.setName(name); s.setChemicalFormula("");
        s.setDeltaD(dD); s.setDeltaP(dP); s.setDeltaH(dH);
        s.setMolarVolume(mv); s.setDeltaT(dT); s.setCostPerLiter(cost);
        s.setEnvImpactScore(env); s.setToxicityLevel(0.0); s.setEuBanStatus(ban);
        solventRepository.save(s);
    }

    private void seedSolvents() {
        long before = solventRepository.count();
        seed("Acetaldehyde", 14.7, 12.5, 7.9, 56.6, 20.8507, 12.8, "MEDIUM", false);
        seed("Acetaldoxime", 16.3, 4.0, 20.2, 61.2, 26.2627, 13.1, "HIGH", false);
        seed("Acetamide", 17.3, 18.7, 22.4, 60.8, 33.9226, 13.0, "HIGH", false);
        seed("Acetanilide", 20.6, 13.3, 12.4, 110.9, 27.4774, 15.5, "MEDIUM", false);
        seed("Acetic Acid", 14.5, 8.0, 13.5, 57.1, 21.3659, 12.9, "MEDIUM", false);
        seed("Acetic Anhydride", 16.0, 11.7, 10.2, 94.5, 22.2919, 14.7, "MEDIUM", false);
        seed("Acetone", 15.5, 10.4, 7.0, 74.0, 19.9351, 13.7, "LOW", false);
        seed("Acetonemethyloxime", 14.7, 4.6, 4.6, 96.7, 16.0751, 14.8, "LOW", false);
        seed("Acetonitrile", 15.3, 18.0, 6.1, 52.6, 24.3988, 12.6, "MEDIUM", false);
        seed("Acetoxime", 16.3, 3.7, 10.9, 80.2, 19.9547, 14.0, "MEDIUM", false);
        seed("Acetoxy-1,3-butadiene", 16.1, 4.4, 8.3, 118.4, 18.6403, 15.9, "MEDIUM", false);
        seed("Acetylacetone", 16.1, 11.2, 6.2, 103.0, 20.5692, 15.2, "LOW", false);
        seed("Acetylbromide", 16.7, 10.6, 5.2, 74.0, 20.4521, 13.7, "LOW", false);
        seed("Acetylchloride", 16.2, 11.2, 5.8, 71.4, 20.531, 13.6, "LOW", false);
        seed("Acetylene (Ethyne)", 14.4, 4.2, 11.9, 42.1, 19.1471, 12.1, "MEDIUM", false);
        seed("Acetylfluorid", 14.7, 14.0, 5.7, 62.0, 21.0851, 13.1, "LOW", false);
        seed("Acridine", 21.7, 5.9, 2.0, 163.0, 22.5765, 18.1, "LOW", false);
        seed("Acrolein", 15.0, 7.2, 7.8, 66.7, 18.3761, 13.3, "MEDIUM", false);
        seed("Acrylamide", 15.8, 12.1, 12.8, 63.4, 23.662, 13.2, "MEDIUM", false);
        seed("Acrylic Acid", 17.7, 6.4, 14.9, 68.5, 24.0054, 13.4, "MEDIUM", false);
        seed("Acrylonitrile", 16.0, 12.8, 6.8, 67.1, 21.5889, 13.4, "LOW", false);
        seed("Acrylylchloride", 16.2, 11.6, 5.4, 81.3, 20.6436, 14.1, "LOW", false);
        seed("Adipic Acid (1,6-Hexanedioic)", 17.1, 9.0, 14.6, 107.5, 24.2192, 15.4, "MEDIUM", false);
        seed("Allyl Acetate", 15.7, 4.5, 8.0, 108.5, 18.1863, 15.4, "MEDIUM", false);
        seed("Allyl Acetic Acid", 16.7, 4.7, 11.3, 102.1, 20.7043, 15.1, "MEDIUM", false);
        seed("Allyl Acetonitrile (4-Pentenenitrile)", 16.3, 11.2, 5.0, 98.5, 20.3993, 14.9, "LOW", false);
        seed("Allyl Alcohol", 16.2, 10.8, 16.8, 68.4, 25.7161, 13.4, "HIGH", false);
        seed("Allyl Amine", 15.5, 5.7, 10.6, 74.9, 19.624, 13.7, "MEDIUM", false);
        seed("Allyl Cyanide", 16.0, 14.3, 5.6, 80.5, 22.1777, 14.0, "LOW", false);
        seed("Allyl Formate", 15.7, 5.4, 8.8, 91.0, 18.7907, 14.6, "MEDIUM", false);
        seed("Allyl Mercaptan", 16.4, 6.2, 7.9, 80.2, 19.2304, 14.0, "MEDIUM", false);
        seed("Amino Pyridine", 20.4, 8.1, 12.2, 94.1, 25.1119, 14.7, "MEDIUM", false);
        seed("Amyl Acetate", 15.8, 3.3, 6.1, 148.0, 17.2551, 17.4, "LOW", false);
        seed("Aniline", 19.4, 5.1, 10.2, 91.5, 22.5036, 14.6, "MEDIUM", false);
        seed("Anisole", 17.8, 4.1, 6.7, 119.1, 19.4561, 16.0, "LOW", false);
        seed("Anthraquinone", 20.3, 7.6, 4.8, 145.6, 22.2011, 17.3, "LOW", false);
        seed("Azidoethane", 15.9, 8.9, 12.9, 79.0, 22.3255, 13.9, "MEDIUM", false);
        seed("Benzal Chloride", 19.9, 6.6, 2.4, 134.2, 21.1028, 16.7, "LOW", false);
        seed("Benzaldehyde", 19.4, 7.4, 5.3, 101.5, 21.4292, 15.1, "LOW", false);
        seed("Benzamide", 21.2, 14.7, 11.2, 90.3, 28.1242, 14.5, "MEDIUM", false);
        seed("Benzene", 18.4, 0.0, 2.0, 89.4, 18.5084, 14.5, "HIGH", true);
        seed("Benzenediol", 18.0, 8.4, 21.0, 87.5, 28.9061, 14.4, "HIGH", true);
        seed("Benzisoxazole", 20.6, 11.5, 8.8, 100.7, 25.1803, 15.0, "MEDIUM", false);
        seed("Benzofuran (Cumaron)", 18.7, 5.1, 5.7, 110.2, 20.2037, 15.5, "LOW", false);
        seed("Benzoic Acid", 18.2, 6.9, 9.8, 113.1, 21.792, 15.7, "MEDIUM", false);
        seed("Benzonitrile", 17.4, 9.0, 3.3, 102.6, 19.8658, 15.1, "LOW", false);
        seed("Benzophenone", 19.6, 8.6, 5.7, 164.2, 22.1497, 18.2, "LOW", false);
        seed("Benzothiazole", 20.6, 5.2, 8.4, 108.5, 22.8464, 15.4, "MEDIUM", false);
        seed("Benzotrichloride", 20.2, 6.6, 3.2, 142.1, 21.4905, 17.1, "LOW", false);
        seed("Benzoyl Chloride", 20.7, 8.2, 4.5, 116.0, 22.7152, 15.8, "LOW", false);
        seed("Benzyl Acetate", 18.3, 5.7, 6.0, 142.8, 20.0843, 17.1, "LOW", false);
        seed("Benzyl Alcohol", 18.4, 6.3, 13.7, 103.6, 23.7895, 15.2, "MEDIUM", false);
        seed("Benzyl Amine", 19.2, 4.6, 11.7, 109.2, 22.9497, 15.5, "MEDIUM", false);
        seed("Benzyl Benzoate", 20.0, 5.1, 5.2, 191.2, 21.285, 19.6, "LOW", false);
        seed("Benzyl Butyl Phthalate", 19.0, 11.2, 3.1, 306.0, 22.2722, 25.3, "LOW", false);
        seed("Benzyl Chloride", 18.8, 7.1, 2.6, 115.0, 20.2635, 15.8, "LOW", false);
        seed("Benzylethyl Ether", 18.4, 3.8, 3.8, 144.2, 19.1687, 17.2, "LOW", false);
        seed("Bicyclohexyl", 18.6, 0.0, 0.0, 188.5, 18.6, 19.4, "LOW", false);
        seed("Biphenyl", 19.7, 1.0, 2.0, 155.1, 19.8265, 17.8, "LOW", false);
        seed("Bis(Chloromethyl) Ether", 17.2, 4.9, 6.6, 86.6, 19.0633, 14.3, "LOW", false);
        seed("Biuret", 20.0, 14.6, 18.8, 70.3, 31.0902, 13.5, "HIGH", false);
        seed("Bromine (P From Dipole Moment)", 18.2, 2.1, 0.0, 51.5, 18.3208, 12.6, "LOW", false);
        seed("Bromoacetylene", 15.7, 9.9, 5.6, 67.7, 19.3871, 13.4, "LOW", false);
        seed("Bromobenzene", 20.5, 5.5, 4.1, 105.3, 21.6174, 15.3, "LOW", false);
        seed("Bromochloromethane", 17.3, 5.7, 3.5, 65.0, 18.548, 13.2, "LOW", false);
        seed("Bromoethylene", 16.1, 6.3, 2.3, 71.6, 17.441, 13.6, "LOW", false);
        seed("Bromoform", 21.4, 4.1, 6.1, 87.5, 22.627, 14.4, "LOW", false);
        seed("Bromomethyl Methyl Ether", 16.9, 8.5, 7.0, 81.6, 20.1708, 14.1, "LOW", false);
        seed("Bromotrichloro Methane (P and H from", 18.3, 8.1, 6.0, 99.2, 20.8926, 15.0, "LOW", false);
        seed("Butadiene", 14.7, 1.7, 6.2, 82.3, 16.0443, 14.1, "LOW", false);
        seed("Butadiene-1-ol", 16.2, 6.6, 16.8, 76.5, 24.2537, 13.8, "HIGH", false);
        seed("Butadiene-4-Cyano", 16.2, 11.7, 5.2, 93.7, 20.6487, 14.7, "LOW", false);
        seed("Butadione", 15.7, 5.1, 6.8, 87.8, 17.8533, 14.4, "LOW", false);
        seed("Butandiol Diacrylate", 16.8, 9.1, 4.2, 194.4, 19.5625, 19.7, "LOW", false);
        seed("Butane", 14.1, 0.0, 0.0, 101.4, 14.1, 15.1, "LOW", false);
        seed("Butanediol", 16.6, 10.0, 21.5, 89.9, 28.9449, 14.5, "HIGH", false);
        seed("Butanethiol", 16.3, 5.3, 4.5, 107.8, 17.7209, 15.4, "LOW", false);
        seed("Butanol", 16.0, 5.7, 15.8, 91.5, 23.1976, 14.6, "HIGH", false);
        seed("Butene", 13.2, 1.3, 3.9, 94.3, 13.8253, 14.7, "LOW", false);
        seed("Butenenitrile", 16.2, 14.3, 5.6, 80.5, 22.3224, 14.0, "LOW", false);
        seed("Butyl Benzoate", 18.3, 5.6, 5.5, 178.0, 19.9123, 18.9, "LOW", false);
        seed("Butyl Formate", 15.7, 6.5, 9.2, 114.8, 19.323, 15.7, "MEDIUM", false);
        seed("Butyl Stearate", 14.5, 3.7, 3.5, 382.0, 15.3685, 29.1, "LOW", false);
        seed("Butynedinitrile", 15.2, 16.2, 8.0, 78.4, 23.611, 13.9, "MEDIUM", false);
        seed("Butyraldehyde", 15.6, 10.1, 6.2, 90.5, 19.5911, 14.5, "LOW", false);
        seed("Butyric Acid", 14.9, 4.1, 10.6, 110.0, 18.7398, 15.5, "MEDIUM", false);
        seed("Butyric Anhydride", 16.0, 6.3, 7.7, 164.4, 18.8409, 18.2, "MEDIUM", false);
        seed("Butyronitrile", 15.3, 12.4, 5.1, 87.3, 20.3435, 14.4, "LOW", false);
        seed("Butyrylchloride", 16.8, 9.4, 4.8, 103.6, 19.8404, 15.2, "LOW", false);
        seed("Caprolactone (Epsilon)", 19.7, 15.0, 7.4, 110.8, 25.8428, 15.5, "MEDIUM", false);
        seed("Carbon Dioxide", 15.7, 6.3, 5.7, 38.0, 17.8513, 11.9, "LOW", false);
        seed("Carbon Disulfid", 20.5, 0.0, 0.6, 60.0, 20.5088, 13.0, "HIGH", false);
        seed("Carbon Tetrachloride", 16.1, 8.3, 0.0, 97.1, 18.1135, 14.9, "HIGH", true);
        seed("Carbonyl Sulfid", 17.4, 3.7, 0.0, 51.0, 17.789, 12.6, "LOW", false);
        seed("Cetyl Alcohol (1-Hexadecanol)", 15.1, 3.7, 8.1, 298.7, 17.5303, 24.9, "MEDIUM", false);
        seed("Chloral", 17.2, 7.4, 7.6, 97.5, 20.2079, 14.9, "MEDIUM", false);
        seed("Chlorine", 17.3, 10.0, 0.0, 46.0, 19.9822, 12.3, "LOW", true);
        seed("Chloro Acetaldehyde", 16.2, 16.1, 9.0, 60.4, 24.5489, 13.0, "MEDIUM", false);
        seed("Chloro Acetic Acid", 17.7, 10.4, 12.3, 68.6, 23.932, 13.4, "MEDIUM", false);
        seed("Chloro Methyl Acrylate", 15.9, 7.3, 8.5, 101.4, 19.4512, 15.1, "MEDIUM", false);
        seed("Chloroacetonitrile", 17.4, 13.6, 2.0, 63.3, 22.1748, 13.2, "LOW", false);
        seed("Chloroacetylchloride", 17.5, 9.2, 5.5, 79.5, 20.5217, 14.0, "LOW", false);
        seed("Chloroacetylene", 16.2, 2.1, 2.5, 63.7, 16.5257, 13.2, "LOW", false);
        seed("Chloroallylidene Diacetate", 16.7, 7.3, 8.8, 160.2, 20.2391, 18.0, "MEDIUM", false);
        seed("Chlorobenzene", 19.0, 4.3, 2.0, 102.1, 19.5829, 15.1, "MEDIUM", false);
        seed("Chlorocyclopropane", 17.6, 7.2, 2.2, 84.9, 19.1426, 14.2, "LOW", false);
        seed("Chlorodifluoromethane (Freon 22", 12.3, 6.3, 5.7, 72.9, 14.9489, 13.6, "LOW", false);
        seed("Chloroethyl Acetate", 16.7, 9.6, 8.8, 107.5, 21.1776, 15.4, "MEDIUM", false);
        seed("Chloroform", 17.8, 3.1, 5.7, 80.7, 18.9457, 14.0, "HIGH", false);
        seed("Chloromethylsulfid", 16.6, 6.4, 2.0, 95.0, 17.9031, 14.8, "LOW", false);
        seed("Chloronitromethane", 17.4, 13.5, 5.5, 65.1, 22.6993, 13.3, "LOW", false);
        seed("Chloropicrin (Trichloronitromethane)", 17.6, 6.8, 7.0, 101.7, 20.1246, 15.1, "LOW", false);
        seed("Cis-Decahydronaphthalene", 18.8, 0.0, 0.0, 156.9, 18.8, 17.8, "LOW", false);
        seed("Coumarin", 20.0, 12.5, 6.7, 156.3, 24.5182, 17.8, "LOW", false);
        seed("Cyanamid (Carbamonitrile)", 15.5, 27.6, 16.8, 32.8, 35.8364, 11.6, "HIGH", false);
        seed("Cyanogen", 15.1, 11.8, 0.0, 54.6, 19.1638, 12.7, "LOW", false);
        seed("Cyanogen Bromide", 18.3, 15.2, 0.0, 52.6, 23.7893, 12.6, "LOW", false);
        seed("Cyanogen Chloride", 15.6, 14.5, 0.0, 51.8, 21.2981, 12.6, "LOW", false);
        seed("Cyclobutanone", 18.3, 11.4, 5.2, 73.4, 22.1786, 13.7, "LOW", false);
        seed("Cyclodecanone", 16.8, 8.0, 4.1, 161.0, 19.0539, 18.1, "LOW", false);
        seed("Cycloheptanone", 17.2, 10.6, 4.8, 118.2, 20.7663, 15.9, "LOW", false);
        seed("Cyclohexane", 16.8, 0.0, 0.2, 108.7, 16.8012, 15.4, "MEDIUM", false);
        seed("Cyclohexane-1,2-Dicarboxylic Acid", 16.4, 2.2, 5.0, 422.4, 17.2858, 31.1, "LOW", false);
        seed("Cyclohexanediol", 17.4, 9.8, 18.3, 112.8, 27.0867, 15.6, "HIGH", false);
        seed("Cyclohexanedione", 18.6, 10.3, 8.0, 103.8, 22.7167, 15.2, "MEDIUM", false);
        seed("Cyclohexanol", 17.4, 4.1, 13.5, 106.0, 22.4013, 15.3, "MEDIUM", false);
        seed("Cyclohexanone", 17.8, 6.3, 5.1, 104.0, 19.5586, 15.2, "MEDIUM", false);
        seed("Cyclohexene", 17.2, 1.0, 5.0, 101.9, 17.9399, 15.1, "LOW", false);
        seed("Cyclohexyl Benzene", 18.7, 0.0, 1.0, 169.9, 18.7267, 18.5, "LOW", true);
        seed("Cyclohexylamine", 17.2, 3.1, 6.5, 113.8, 18.6467, 15.7, "LOW", false);
        seed("Cyclohexylchloride", 17.3, 5.5, 2.0, 118.6, 18.2631, 15.9, "LOW", false);
        seed("Cyclooctanone", 17.0, 9.6, 4.5, 131.7, 20.0352, 16.6, "LOW", false);
        seed("Cyclopentadiene", 17.2, 1.9, 6.1, 82.1, 18.3483, 14.1, "LOW", false);
        seed("Cyclopentane", 16.4, 0.0, 1.8, 94.9, 16.4985, 14.7, "LOW", false);
        seed("Cyclopentanone", 17.9, 11.9, 5.2, 89.1, 22.1147, 14.5, "LOW", false);
        seed("Cyclopentene", 16.7, 3.8, 1.7, 89.0, 17.211, 14.4, "LOW", false);
        seed("Cyclopentenyl Alcohol", 18.1, 7.6, 15.6, 86.2, 25.0745, 14.3, "HIGH", false);
        seed("Cyclopropane", 17.6, 0.0, 0.0, 58.3, 17.6, 12.9, "LOW", false);
        seed("Cyclopropene", 17.2, 2.4, 2.0, 50.0, 17.4814, 12.5, "LOW", false);
        seed("Cyclopropylnitrile", 18.6, 16.2, 5.7, 75.4, 25.3158, 13.8, "LOW", false);
        seed("Decane", 15.7, 0.0, 0.0, 195.9, 15.7, 19.8, "LOW", false);
        seed("Decanol", 16.0, 4.7, 10.0, 191.8, 19.4445, 19.6, "MEDIUM", false);
        seed("Decene", 15.8, 1.0, 2.2, 190.6, 15.9837, 19.5, "LOW", false);
        seed("Di-(2-Ethyl Hexyl)azelate", 16.7, 1.4, 4.8, 449.9, 17.4324, 32.5, "LOW", false);
        seed("Di-(2-Ethyl Hexyl)sebacate", 16.8, 1.0, 4.7, 468.7, 17.4737, 33.4, "LOW", false);
        seed("Di-2-Ethyl Hexyl Amine", 15.6, 0.8, 3.2, 301.5, 15.9449, 25.1, "LOW", false);
        seed("Di-Isodecyl Phthalate", 16.6, 6.2, 2.6, 464.2, 17.9098, 33.2, "LOW", false);
        seed("Di-Isoheptyl Phthalate", 16.8, 7.2, 3.4, 364.9, 18.5914, 28.2, "LOW", false);
        seed("Di-Isononyl Adipate", 16.2, 1.8, 4.9, 433.7, 17.0203, 31.7, "LOW", false);
        seed("Di-Isononyl Phthalate", 16.6, 6.6, 2.9, 432.4, 18.0978, 31.6, "LOW", false);
        seed("Di-Isopropyl Methyl Phosphonate", 16.4, 10.0, 5.7, 184.4, 20.0362, 19.2, "LOW", false);
        seed("Di-Isopropyl Phosphonofluoridat", 15.7, 10.2, 5.9, 174.5, 19.6301, 18.7, "LOW", false);
        seed("Diallyl Amine", 15.6, 4.5, 6.7, 124.1, 17.5642, 16.2, "LOW", false);
        seed("Diazomethane", 14.7, 6.1, 11.3, 78.1, 19.519, 13.9, "MEDIUM", false);
        seed("Dibasic Esters (dupont) Mix of", 16.2, 4.7, 8.4, 159.0, 18.8438, 17.9, "MEDIUM", false);
        seed("Dibenzyl Sebacate", 17.8, 2.2, 5.5, 362.1, 18.7598, 28.1, "LOW", false);
        seed("Dibromo Methane", 17.8, 6.4, 7.0, 69.8, 20.1693, 13.5, "LOW", false);
        seed("Dibutyl Amine", 15.0, 3.0, 4.3, 170.0, 15.8899, 18.5, "LOW", false);
        seed("Dibutyl Ketone", 16.0, 7.7, 4.4, 173.4, 18.2934, 18.7, "LOW", false);
        seed("Dibutyl Phthalate", 17.8, 8.6, 4.1, 266.0, 20.1894, 23.3, "LOW", false);
        seed("Dibutyl Sebacate", 16.7, 4.5, 4.1, 339.0, 17.775, 26.9, "LOW", false);
        seed("Dichloroacetaldehyde", 16.7, 9.1, 7.5, 94.0, 20.4438, 14.7, "MEDIUM", false);
        seed("Dichloroacetic Acid", 18.2, 8.1, 12.2, 82.5, 23.36, 14.1, "MEDIUM", false);
        seed("Dichloroacetonitrile", 17.4, 9.4, 6.4, 80.3, 20.7865, 14.0, "LOW", false);
        seed("Dichlorodifluoromethane (Freon 12", 12.3, 2.0, 0.0, 92.3, 12.4615, 14.6, "LOW", false);
        seed("Dichloromethyl Methyl Ether", 17.1, 12.9, 6.5, 90.5, 22.3846, 14.5, "LOW", false);
        seed("Dichloromonofluoromethane (Freon 21", 15.8, 3.1, 5.7, 75.4, 17.0804, 13.8, "LOW", false);
        seed("Diethyl Amine", 14.9, 2.3, 6.1, 103.2, 16.2638, 15.2, "LOW", false);
        seed("Diethyl Carbonate", 15.1, 6.3, 3.5, 121.0, 16.7317, 16.1, "LOW", false);
        seed("Diethyl Ether", 14.5, 2.9, 5.1, 104.8, 15.6419, 15.2, "HIGH", false);
        seed("Diethyl Ketone", 15.8, 7.6, 4.7, 106.4, 18.1519, 15.3, "LOW", false);
        seed("Diethyl Malonate", 16.1, 7.7, 8.3, 152.5, 19.6822, 17.6, "MEDIUM", false);
        seed("Diethyl Oxalate", 16.2, 8.0, 8.8, 136.6, 20.0968, 16.8, "MEDIUM", false);
        seed("Diethyl Phthalate", 17.6, 9.6, 4.5, 198.0, 20.5468, 19.9, "LOW", false);
        seed("Diethyl Sulfate", 15.7, 14.7, 7.1, 131.5, 22.6493, 16.6, "MEDIUM", false);
        seed("Diethyl Sulfid", 16.8, 3.1, 2.0, 107.4, 17.2003, 15.4, "LOW", false);
        seed("Diethyldisulfid", 16.7, 6.7, 5.7, 123.1, 18.8751, 16.2, "LOW", false);
        seed("Diethylene Glycol Monoeth yl Ether", 16.2, 5.1, 9.2, 175.5, 19.3155, 18.8, "MEDIUM", false);
        seed("Dihexyl Phthalate", 17.0, 7.6, 3.6, 332.3, 18.9663, 26.6, "LOW", false);
        seed("Dihydroxybenzene (1,4-", 21.0, 10.2, 27.2, 82.7, 35.8452, 14.1, "HIGH", false);
        seed("Dihydroxybenzene (Catechol)", 20.0, 11.3, 21.8, 81.9, 31.6691, 14.1, "HIGH", false);
        seed("Diisobutyl Adipate", 16.7, 2.5, 6.2, 269.6, 17.9883, 23.5, "LOW", false);
        seed("Diisopropylamine", 14.8, 1.7, 3.5, 141.1, 15.3029, 17.1, "LOW", false);
        seed("Dimethyl Acetylene", 15.1, 3.4, 7.6, 78.9, 17.2433, 13.9, "MEDIUM", false);
        seed("Dimethyl Amine", 15.3, 4.8, 11.2, 66.2, 19.5594, 13.3, "MEDIUM", false);
        seed("Dimethyl Carbonate", 15.5, 3.9, 9.7, 84.2, 18.6963, 14.2, "MEDIUM", false);
        seed("Dimethyl Diketone", 15.7, 5.3, 11.7, 88.2, 20.2847, 14.4, "MEDIUM", false);
        seed("Dimethyl Disulfid", 17.3, 7.8, 6.5, 88.6, 20.0594, 14.4, "LOW", false);
        seed("Dimethyl Ether", 15.2, 6.1, 5.7, 63.2, 17.3419, 13.2, "LOW", false);
        seed("Dimethyl Formamide", 17.4, 13.7, 11.3, 77.0, 24.8624, 13.8, "MEDIUM", false);
        seed("Dimethyl Hydrazine", 15.3, 5.9, 11.0, 76.0, 19.7459, 13.8, "MEDIUM", true);
        seed("Dimethyl Methyl Phosphonate", 16.7, 13.1, 7.5, 106.9, 22.5111, 15.3, "MEDIUM", false);
        seed("Dimethyl Phthalate", 18.6, 10.8, 4.9, 163.0, 22.0592, 18.1, "LOW", false);
        seed("Dimethyl Sebacate", 16.6, 2.9, 6.7, 233.3, 18.1345, 21.7, "LOW", false);
        seed("Dimethyl Sulfate", 17.7, 17.0, 9.7, 94.7, 26.389, 14.7, "MEDIUM", false);
        seed("Dimethyl Sulfid", 16.1, 6.4, 7.4, 73.2, 18.8396, 13.7, "MEDIUM", false);
        seed("Dimethyl Sulfone", 19.0, 19.4, 12.3, 75.0, 29.8102, 13.8, "MEDIUM", false);
        seed("Dimethyl Sulfoxide", 18.4, 16.4, 10.2, 71.3, 26.6751, 13.6, "LOW", false);
        seed("Dioctyl Adipate", 16.7, 2.0, 5.1, 400.0, 17.5756, 30.0, "LOW", false);
        seed("Dioctyl Phthalate", 16.6, 7.0, 3.1, 377.0, 18.2803, 28.9, "LOW", false);
        seed("Diphenyl Ether", 19.5, 3.4, 5.8, 160.4, 20.6264, 18.0, "LOW", false);
        seed("Diphenylamine", 20.0, 3.3, 5.9, 145.9, 21.1116, 17.3, "LOW", false);
        seed("Diphenylmethane", 19.5, 1.0, 1.0, 168.2, 19.5512, 18.4, "LOW", false);
        seed("Dipropyl Amine", 15.3, 1.4, 4.1, 136.9, 15.9016, 16.8, "LOW", false);
        seed("Dipropyl Ketone", 15.8, 5.7, 4.9, 140.8, 17.4969, 17.0, "LOW", false);
        seed("Dipropylene Glycol Monomethyl Ether", 16.3, 4.9, 8.0, 195.7, 18.8069, 19.8, "MEDIUM", false);
        seed("Dithiabutane", 17.3, 7.8, 6.5, 88.6, 20.0594, 14.4, "LOW", false);
        seed("Ditridecyl Phthalate", 16.6, 5.4, 1.9, 558.3, 17.5593, 37.9, "LOW", false);
        seed("Divinyl Sulfid", 16.5, 4.6, 5.6, 93.6, 18.0214, 14.7, "LOW", false);
        seed("Dodecane", 16.0, 0.0, 0.0, 228.6, 16.0, 21.4, "LOW", false);
        seed("Dodecanol", 16.0, 4.0, 9.3, 224.5, 18.9338, 21.2, "MEDIUM", false);
        seed("Eicosane", 16.5, 0.0, 0.0, 359.8, 16.5, 28.0, "LOW", false);
        seed("Epsilon-Caprolactam", 19.4, 13.8, 3.9, 110.7, 24.1249, 15.5, "LOW", false);
        seed("Ethane Dithiol", 17.9, 7.2, 8.7, 83.9, 21.1646, 14.2, "MEDIUM", false);
        seed("Ethanesulfonylchloride", 17.7, 14.9, 6.8, 94.7, 24.1151, 14.7, "LOW", false);
        seed("Ethanethiol (Ethyl Mercaptan)", 15.7, 6.5, 7.1, 74.3, 18.416, 13.7, "MEDIUM", false);
        seed("Ethanol", 15.8, 8.8, 19.4, 58.5, 26.5224, 12.9, "LOW", false);
        seed("Ethoxyethyl Propionate", 16.2, 3.3, 8.8, 155.5, 18.7289, 17.8, "MEDIUM", false);
        seed("Ethyl Acetate", 15.8, 5.3, 7.2, 98.5, 18.1541, 14.9, "LOW", false);
        seed("Ethyl Acetylene", 15.1, 3.4, 5.0, 81.5, 16.2656, 14.1, "LOW", false);
        seed("Ethyl Acrylate", 15.5, 7.1, 5.5, 108.8, 17.914, 15.4, "LOW", false);
        seed("Ethyl Amine", 15.0, 5.6, 10.7, 65.6, 19.2575, 13.3, "MEDIUM", false);
        seed("Ethyl Amyl Ketone", 16.2, 4.5, 4.1, 156.0, 17.3061, 17.8, "LOW", false);
        seed("Ethyl Benzene", 17.8, 0.6, 1.4, 123.1, 17.865, 16.2, "LOW", true);
        seed("Ethyl Benzoate", 17.9, 6.2, 6.0, 144.3, 19.8708, 17.2, "LOW", false);
        seed("Ethyl Bromide", 16.5, 8.4, 2.3, 74.6, 18.6574, 13.7, "LOW", false);
        seed("Ethyl Butyl K etone", 16.2, 5.0, 4.1, 139.0, 17.4428, 16.9, "LOW", false);
        seed("Ethyl Butyrate", 15.5, 5.6, 5.0, 132.9, 17.2224, 16.6, "LOW", false);
        seed("Ethyl Caproate", 15.5, 3.2, 5.9, 149.6, 16.8908, 17.5, "LOW", false);
        seed("Ethyl Carbamate", 16.8, 10.1, 13.0, 91.2, 23.5213, 14.6, "MEDIUM", false);
        seed("Ethyl Carbylamine", 15.6, 15.2, 5.8, 74.4, 22.5397, 13.7, "LOW", false);
        seed("Ethyl Chloride", 15.7, 6.1, 2.9, 70.0, 17.0912, 13.5, "LOW", false);
        seed("Ethyl Chloroformate", 15.5, 10.0, 6.7, 95.6, 19.625, 14.8, "LOW", false);
        seed("Ethyl Cyanoacetate", 16.7, 7.9, 8.3, 107.1, 20.2531, 15.4, "MEDIUM", false);
        seed("Ethyl Ethynylether", 15.4, 7.9, 5.9, 87.6, 18.2861, 14.4, "LOW", false);
        seed("Ethyl Formate", 15.5, 8.4, 8.4, 80.2, 19.5287, 14.0, "MEDIUM", false);
        seed("Ethyl Hexyl Acetate", 15.8, 2.9, 5.1, 196.0, 16.8541, 19.8, "LOW", false);
        seed("Ethyl Hexyl Acrylate", 14.8, 4.7, 3.4, 208.2, 15.8962, 20.4, "LOW", false);
        seed("Ethyl Hypochlorite", 15.7, 8.6, 6.5, 79.5, 19.0447, 14.0, "LOW", false);
        seed("Ethyl Iodide", 17.3, 7.9, 7.2, 81.2, 20.3357, 14.1, "MEDIUM", false);
        seed("Ethyl Isocyanate", 15.4, 12.0, 2.5, 78.7, 19.6827, 13.9, "LOW", false);
        seed("Ethyl Isothiocyanate", 17.2, 14.7, 9.0, 87.1, 24.3502, 14.4, "MEDIUM", false);
        seed("Ethyl Methyl Sulfid", 17.1, 4.8, 2.5, 91.2, 17.936, 14.6, "LOW", false);
        seed("Ethyl Propionate", 15.5, 6.1, 4.9, 115.5, 17.3629, 15.8, "LOW", false);
        seed("Ethyl Thiocyanate", 15.4, 13.4, 5.4, 87.1, 21.1159, 14.4, "LOW", false);
        seed("Ethyl Vinylether", 14.9, 4.9, 5.6, 94.9, 16.6547, 14.7, "LOW", false);
        seed("Ethyl Vinylketone", 15.8, 11.3, 4.5, 99.3, 19.9394, 15.0, "LOW", false);
        seed("Ethylene", 15.0, 2.0, 3.8, 63.0, 15.6026, 13.2, "LOW", false);
        seed("Ethylene Glycol", 17.0, 11.0, 26.0, 55.8, 32.9545, 12.8, "LOW", false);
        seed("Ethylene Glycol Butyl Ether Acetate", 15.3, 4.5, 8.8, 171.2, 18.2148, 18.6, "MEDIUM", false);
        seed("Ethylene Glycol Diacetate", 16.2, 4.7, 9.8, 132.8, 19.5082, 16.6, "MEDIUM", false);
        seed("Ethylene Glycol Monoeth yl Ether", 15.9, 5.1, 9.3, 147.7, 19.1131, 17.4, "MEDIUM", false);
        seed("Ethylene Glycol Monometh yl Ether", 15.9, 5.5, 11.6, 121.6, 20.4358, 16.1, "MEDIUM", false);
        seed("Ethylene Methyl Sulfonate", 16.9, 9.3, 9.6, 97.9, 21.5467, 14.9, "MEDIUM", false);
        seed("Ethylene Oxide", 15.6, 10.0, 11.0, 49.9, 21.549, 12.5, "MEDIUM", false);
        seed("Ethylene Sulfid", 19.3, 9.0, 6.5, 59.5, 22.2652, 13.0, "LOW", false);
        seed("Ethylenediamine", 16.6, 8.8, 17.0, 67.3, 25.3377, 13.4, "HIGH", false);
        seed("Ethyleneimine", 18.6, 9.8, 7.7, 51.8, 22.3895, 12.6, "MEDIUM", false);
        seed("Ethylhexylamine", 15.7, 4.2, 6.1, 163.3, 17.3591, 18.2, "LOW", false);
        seed("Ethylidene Acetone", 16.2, 12.1, 4.5, 99.0, 20.7147, 14.9, "LOW", false);
        seed("Ethynyl Methyl Ether", 15.8, 8.1, 6.5, 70.1, 18.9077, 13.5, "LOW", false);
        seed("Fluorobenzene", 18.7, 6.1, 2.0, 94.7, 19.7712, 14.7, "LOW", false);
        seed("Fluoroethylene", 15.2, 7.0, 1.0, 57.5, 16.7642, 12.9, "LOW", false);
        seed("Fluoromethane", 13.4, 10.6, 9.5, 40.7, 19.5492, 12.0, "MEDIUM", false);
        seed("Formaldehyde", 12.8, 14.4, 15.4, 36.8, 24.665, 11.8, "HIGH", true);
        seed("Formamide", 17.2, 26.2, 19.0, 39.8, 36.6508, 12.0, "HIGH", false);
        seed("Formic Acid", 14.3, 11.9, 16.6, 37.8, 24.9331, 11.9, "MEDIUM", false);
        seed("Formyl Fluoride", 15.0, 10.1, 8.6, 56.5, 20.0242, 12.8, "MEDIUM", false);
        seed("Furan", 17.8, 1.8, 5.3, 72.5, 18.6593, 13.6, "LOW", false);
        seed("Furfural", 18.6, 14.9, 5.1, 83.2, 24.3717, 14.2, "LOW", false);
        seed("Furfuryl Alcohol", 17.4, 7.6, 15.1, 86.5, 24.2596, 14.3, "HIGH", false);
        seed("Furonitrile", 18.4, 15.0, 8.2, 87.5, 25.1157, 14.4, "MEDIUM", false);
        seed("Gamma-Butyrolactone", 19.0, 16.6, 7.4, 76.8, 26.293, 13.8, "MEDIUM", false);
        seed("Gamma-Thiobutyrolactone", 19.0, 6.9, 6.2, 86.6, 21.1436, 14.3, "LOW", false);
        seed("Glycerol", 17.4, 12.1, 29.3, 73.3, 36.1616, 13.7, "HIGH", false);
        seed("Glycerol Diacetate (Isomer Mix)", 16.4, 8.9, 14.2, 149.4, 23.448, 17.5, "MEDIUM", false);
        seed("Glycidaldehyde", 17.5, 13.4, 9.8, 63.2, 24.1216, 13.2, "MEDIUM", false);
        seed("Glycidol", 18.2, 9.0, 17.9, 66.5, 27.0675, 13.3, "HIGH", false);
        seed("Glyoxal (Ethandial)", 15.0, 17.0, 13.3, 50.9, 26.2848, 12.5, "MEDIUM", false);
        seed("Heptane", 15.3, 0.0, 0.0, 147.4, 15.3, 17.4, "LOW", false);
        seed("Heptanol", 16.0, 5.3, 11.7, 141.4, 20.5178, 17.1, "MEDIUM", false);
        seed("Heptene", 15.0, 1.1, 2.6, 141.9, 15.2634, 17.1, "LOW", false);
        seed("Hexadecane", 16.3, 0.0, 0.0, 294.1, 16.3, 24.7, "LOW", false);
        seed("Hexanal", 15.8, 8.5, 5.4, 120.2, 18.7363, 16.0, "LOW", false);
        seed("Hexanoic Acid", 15.0, 4.1, 9.4, 125.9, 18.1706, 16.3, "MEDIUM", false);
        seed("Hexanol", 15.9, 5.8, 12.5, 124.9, 21.0404, 16.2, "MEDIUM", false);
        seed("Hexene", 14.7, 1.1, 0.0, 126.1, 14.7411, 16.3, "LOW", false);
        seed("Hexyl Acetate", 15.8, 2.9, 5.9, 165.0, 17.1132, 18.2, "LOW", false);
        seed("Hexylene Glycol", 15.7, 8.4, 17.8, 123.0, 25.1772, 16.1, "HIGH", false);
        seed("Hexylene Glycol Diacetate", 15.3, 4.5, 7.2, 204.3, 17.498, 20.2, "MEDIUM", false);
        seed("Hydrazine", 14.2, 8.3, 8.9, 32.1, 18.7013, 11.6, "MEDIUM", true);
        seed("Hydrogen Cyanide", 12.3, 17.6, 9.0, 39.3, 23.282, 12.0, "MEDIUM", false);
        seed("Hydroxy Tetrahydrofuran", 18.9, 9.4, 16.3, 80.0, 26.6695, 14.0, "HIGH", false);
        seed("Hydroxyethyl Acrylate", 16.0, 13.2, 13.4, 114.9, 24.6941, 15.7, "MEDIUM", false);
        seed("Iodobenzene", 19.5, 6.0, 6.1, 114.4, 21.2946, 15.7, "LOW", false);
        seed("Iodoform", 20.2, 3.6, 10.6, 98.2, 23.0946, 14.9, "MEDIUM", false);
        seed("Isoamyl Acetate", 15.3, 3.1, 7.0, 148.8, 17.1085, 17.4, "LOW", false);
        seed("Isoamyl Propionate", 15.7, 5.2, 5.6, 165.7, 17.4611, 18.3, "LOW", false);
        seed("Isobutyl Acetate", 15.1, 3.7, 6.3, 133.5, 16.7747, 16.7, "LOW", false);
        seed("Isobutyl Acrylate", 15.5, 6.2, 5.0, 145.0, 17.4267, 17.2, "LOW", false);
        seed("Isobutyl Formate", 15.5, 6.5, 6.7, 117.4, 18.0939, 15.9, "LOW", false);
        seed("Isobutyl Isobutyrate", 15.1, 2.9, 5.9, 163.0, 16.4691, 18.1, "LOW", false);
        seed("Isobutylene", 14.5, 2.0, 1.5, 89.4, 14.7139, 14.5, "LOW", false);
        seed("Isobutyric Acid", 16.5, 5.4, 11.1, 93.4, 20.6063, 14.7, "MEDIUM", false);
        seed("Isocyanic Acid", 15.8, 10.5, 13.6, 37.7, 23.342, 11.9, "MEDIUM", false);
        seed("Isopropyl Acetate", 14.9, 4.5, 8.2, 117.1, 17.5926, 15.9, "MEDIUM", false);
        seed("Isopropyl Amine (2-Propan Amine)", 14.8, 4.4, 6.6, 86.8, 16.7917, 14.3, "LOW", false);
        seed("Isopropyl Benzene (Cumene)", 18.1, 1.2, 1.2, 139.1, 18.1794, 17.0, "LOW", true);
        seed("Isopropyl Palmitate", 14.3, 3.9, 3.7, 330.0, 15.2771, 26.5, "LOW", false);
        seed("Isoxazole", 18.8, 13.4, 11.2, 64.1, 25.6601, 13.2, "MEDIUM", false);
        seed("Ketene", 15.4, 7.3, 5.8, 53.0, 18.0025, 12.7, "LOW", false);
        seed("L-Menthyl Acetate", 16.8, 4.7, 4.9, 215.8, 18.1202, 20.8, "LOW", false);
        seed("Maleic Anhydride", 20.2, 18.1, 12.6, 66.3, 29.9067, 13.3, "MEDIUM", false);
        seed("Malononitrile", 17.7, 18.4, 6.7, 55.5, 26.3958, 12.8, "LOW", false);
        seed("Mesitylene", 18.0, 0.0, 0.6, 139.8, 18.01, 17.0, "LOW", false);
        seed("Methanol", 15.1, 12.3, 22.3, 40.7, 29.6073, 12.0, "MEDIUM", false);
        seed("Methoxy Butyl Acetate", 15.3, 4.1, 8.1, 153.9, 17.7907, 17.7, "MEDIUM", false);
        seed("Methyl Acetate", 15.5, 7.2, 7.6, 79.7, 18.7043, 14.0, "MEDIUM", false);
        seed("Methyl Acetylene", 15.1, 3.8, 9.2, 59.6, 18.0856, 13.0, "MEDIUM", false);
        seed("Methyl Acrylate", 15.3, 6.7, 9.4, 90.3, 19.1661, 14.5, "MEDIUM", false);
        seed("Methyl Allyl Alcohol", 16.0, 6.0, 15.5, 84.4, 23.0705, 14.2, "HIGH", false);
        seed("Methyl Amine", 13.0, 7.3, 17.3, 44.4, 22.8381, 12.2, "HIGH", false);
        seed("Methyl Amyl Acetate", 15.2, 3.1, 6.8, 167.4, 16.9378, 18.4, "LOW", false);
        seed("Methyl Benzoate", 18.9, 8.2, 4.7, 124.9, 21.1315, 16.2, "LOW", false);
        seed("Methyl Bromide", 17.0, 8.8, 2.6, 56.8, 19.3184, 12.8, "LOW", false);
        seed("Methyl Butyl K etone", 15.3, 6.1, 4.1, 123.6, 16.9738, 16.2, "LOW", false);
        seed("Methyl Chloride", 15.3, 6.1, 3.9, 55.4, 16.9266, 12.8, "LOW", false);
        seed("Methyl Chloroformate", 16.3, 9.5, 8.5, 77.3, 20.6928, 13.9, "MEDIUM", false);
        seed("Methyl Cyanoacetate", 16.8, 14.8, 9.1, 88.3, 24.168, 14.4, "MEDIUM", false);
        seed("Methyl Cyclohexane", 16.0, 0.0, 1.0, 128.3, 16.0312, 16.4, "LOW", false);
        seed("Methyl Ethyl Ether", 14.7, 4.9, 6.2, 84.1, 16.6895, 14.2, "LOW", false);
        seed("Methyl Ethyl Ketone", 16.0, 9.0, 5.1, 90.1, 19.0528, 14.5, "LOW", false);
        seed("Methyl Ethyl Ketoxime", 14.7, 4.9, 7.8, 94.8, 17.3476, 14.7, "MEDIUM", false);
        seed("Methyl Formate", 15.3, 8.4, 10.2, 62.2, 20.2161, 13.1, "MEDIUM", false);
        seed("Methyl Furoate", 17.4, 6.9, 9.7, 107.0, 21.0822, 15.4, "MEDIUM", false);
        seed("Methyl Hydrazine", 16.2, 8.7, 14.8, 52.7, 23.6044, 12.6, "MEDIUM", true);
        seed("Methyl Iodide", 17.5, 7.7, 5.3, 62.3, 19.8401, 13.1, "LOW", false);
        seed("Methyl Isocyanate", 15.6, 7.3, 2.5, 61.8, 17.404, 13.1, "LOW", false);
        seed("Methyl Isothiocyanate", 17.3, 16.2, 10.1, 68.4, 25.7632, 13.4, "MEDIUM", false);
        seed("Methyl Mercaptan", 16.6, 7.7, 8.6, 54.1, 20.2191, 12.7, "MEDIUM", false);
        seed("Methyl Nitrate", 15.8, 14.0, 4.8, 63.8, 21.649, 13.2, "LOW", false);
        seed("Methyl Phenyl Sulfid", 19.6, 4.8, 4.7, 117.4, 20.7193, 15.9, "LOW", false);
        seed("Methyl Phenyl Sulfone", 20.0, 16.9, 7.8, 124.6, 27.3212, 16.2, "MEDIUM", false);
        seed("Methyl Phosphonic Difluorid", 14.0, 14.0, 8.4, 73.9, 21.5072, 13.7, "MEDIUM", false);
        seed("Methyl Propionate", 15.5, 6.5, 7.7, 96.8, 18.4876, 14.8, "MEDIUM", false);
        seed("Methyl Silane", 15.5, 3.3, 0.0, 71.0, 15.8474, 13.6, "LOW", false);
        seed("Methyl Thiocyanate", 17.3, 15.0, 6.0, 68.5, 23.6704, 13.4, "LOW", false);
        seed("Methyl Vinyl Ether", 14.9, 5.3, 6.3, 75.2, 17.0232, 13.8, "LOW", false);
        seed("Methyl Vinyl Ketone", 15.6, 12.5, 5.0, 81.2, 20.6061, 14.1, "LOW", false);
        seed("Methyl Vinyl Sulfid", 16.4, 4.9, 6.0, 82.1, 18.1375, 14.1, "LOW", false);
        seed("Methyl Vinyl Sulfone", 16.8, 19.6, 4.8, 87.6, 26.2572, 14.4, "LOW", false);
        seed("Methyl n-Amyl K etone", 16.2, 5.7, 4.1, 139.8, 17.6562, 17.0, "LOW", false);
        seed("Methyl n-Propyl Ketone", 16.0, 7.6, 4.7, 106.7, 18.3262, 15.3, "LOW", false);
        seed("Methyl-3-Methoxy Butyl Acetate", 15.3, 3.8, 7.7, 168.6, 17.5448, 18.4, "MEDIUM", false);
        seed("Methyl-4-Toluenesulfonate", 19.6, 15.3, 3.8, 152.6, 25.1533, 17.6, "LOW", false);
        seed("Methylal (Dimethoxymethane)", 15.0, 1.8, 8.6, 169.4, 17.3839, 18.5, "MEDIUM", false);
        seed("Methylene Dichloride", 18.2, 6.3, 6.1, 63.9, 20.2025, 13.2, "LOW", false);
        seed("Methylene Diiodide", 17.8, 3.9, 5.5, 80.5, 19.0342, 14.0, "LOW", false);
        seed("Methylenedioxybenzene", 19.0, 6.7, 5.9, 114.8, 20.9929, 15.7, "LOW", false);
        seed("Morpholine", 18.8, 4.9, 9.2, 87.1, 21.4963, 14.4, "MEDIUM", false);
        seed("N,N,N,N-Tetramethylthiourea", 17.3, 6.0, 10.5, 132.2, 21.1078, 16.6, "MEDIUM", false);
        seed("N,N-Dibutyl Formamide", 15.5, 8.9, 6.2, 182.0, 18.9182, 19.1, "LOW", false);
        seed("N,N-Dichloroethyl Amine", 16.8, 7.6, 7.7, 98.3, 19.9822, 14.9, "MEDIUM", false);
        seed("N,N-Dichloromethyl Amine", 16.8, 7.6, 8.0, 90.8, 20.0998, 14.5, "MEDIUM", false);
        seed("N,N-Diethyl Acetamide", 16.4, 11.3, 7.5, 124.5, 21.2814, 16.2, "MEDIUM", false);
        seed("N,N-Diethyl Formamide", 16.4, 11.4, 9.2, 111.4, 21.99, 15.6, "MEDIUM", false);
        seed("N,N-Dimethyl Acetamide", 16.8, 11.5, 10.2, 92.5, 22.7713, 14.6, "MEDIUM", false);
        seed("N,N-Dimethyl Butyramide", 16.4, 10.6, 7.4, 127.8, 20.8825, 16.4, "MEDIUM", false);
        seed("N-Ethyl Formamide", 17.2, 10.0, 14.0, 76.5, 24.3278, 13.8, "MEDIUM", false);
        seed("N-Formyl Hexamethylene Imine", 18.5, 10.4, 7.6, 127.0, 22.5426, 16.4, "MEDIUM", false);
        seed("N-Formyl Piperidine", 18.7, 10.6, 7.8, 111.5, 22.8668, 15.6, "MEDIUM", false);
        seed("N-Methyl Acetamide", 16.9, 18.7, 13.9, 76.9, 28.7838, 13.8, "MEDIUM", false);
        seed("N-Methyl Formamide", 17.4, 18.8, 15.9, 59.1, 30.1498, 13.0, "HIGH", false);
        seed("N-Methylaniline", 19.5, 6.0, 11.5, 108.4, 23.4201, 15.4, "MEDIUM", false);
        seed("Naphthalene", 19.2, 2.0, 5.9, 111.5, 20.1854, 15.6, "LOW", false);
        seed("Naphthol", 19.7, 6.3, 12.3, 131.7, 24.0639, 16.6, "MEDIUM", false);
        seed("Nitrobenzene", 20.0, 8.6, 4.1, 102.7, 22.1533, 15.1, "LOW", false);
        seed("Nitroethane", 16.0, 15.5, 4.5, 71.5, 22.7266, 13.6, "LOW", false);
        seed("Nitroethylene", 16.3, 16.6, 5.0, 59.9, 23.796, 13.0, "LOW", false);
        seed("Nitromethane", 15.8, 18.8, 5.1, 54.3, 25.0817, 12.7, "HIGH", false);
        seed("Nitrosobenzene", 20.0, 12.7, 4.0, 89.3, 24.0269, 14.5, "LOW", false);
        seed("Nonane", 15.7, 0.0, 0.0, 179.7, 15.7, 19.0, "LOW", false);
        seed("Nonanediol", 15.7, 7.0, 15.1, 170.5, 22.8801, 18.5, "HIGH", false);
        seed("Nonanol", 16.0, 4.8, 10.6, 174.4, 19.7838, 18.7, "MEDIUM", false);
        seed("Nonene", 15.4, 1.0, 2.2, 170.5, 15.5885, 18.5, "LOW", false);
        seed("Octadecane", 16.4, 0.0, 0.0, 326.9, 16.4, 26.3, "LOW", false);
        seed("Octane", 15.5, 0.0, 0.0, 163.5, 15.5, 18.2, "LOW", false);
        seed("Octanoic Acid", 15.1, 3.3, 8.2, 159.0, 17.4969, 17.9, "MEDIUM", false);
        seed("Octanol", 16.0, 5.0, 11.9, 157.7, 20.5575, 17.9, "MEDIUM", false);
        seed("Octene", 15.3, 1.0, 2.4, 158.0, 15.5193, 17.9, "LOW", false);
        seed("Octyl Acetate", 15.8, 2.9, 5.1, 196.0, 16.8541, 19.8, "LOW", false);
        seed("Oxalic Acid", 17.0, 14.3, 22.0, 47.4, 31.2648, 12.4, "HIGH", false);
        seed("Oxalylchloride", 16.1, 3.8, 7.5, 85.8, 18.1631, 14.3, "MEDIUM", false);
        seed("Oxetane (Trimethylene Oxide)", 18.0, 9.1, 5.4, 65.0, 20.8799, 13.2, "LOW", false);
        seed("Paracetamol", 17.8, 10.5, 13.9, 151.2, 24.9058, 17.6, "MEDIUM", false);
        seed("Pentamethylene Sulfid", 18.5, 6.3, 8.9, 103.6, 21.4744, 15.2, "MEDIUM", false);
        seed("Pentanal (Valeraldehyde)", 15.7, 9.4, 5.8, 106.4, 19.1961, 15.3, "LOW", false);
        seed("Pentane", 14.5, 0.0, 0.0, 116.2, 14.5, 15.8, "LOW", false);
        seed("Pentanedione", 17.1, 9.0, 4.1, 103.1, 19.754, 15.2, "LOW", false);
        seed("Pentanoic Acid", 15.0, 4.1, 10.3, 109.2, 18.6521, 15.5, "MEDIUM", false);
        seed("Pentanol", 15.9, 5.9, 13.9, 108.6, 21.9278, 15.4, "MEDIUM", false);
        seed("Pentenal", 15.5, 8.1, 6.8, 98.7, 18.7643, 14.9, "LOW", false);
        seed("Pentene", 13.9, 1.4, 3.8, 110.4, 14.4779, 15.5, "LOW", false);
        seed("Phenanthrenequinone", 20.3, 17.1, 4.8, 148.2, 26.9729, 17.4, "LOW", false);
        seed("Phenetole (Ethyl Phenyl Ether)", 18.4, 4.5, 4.0, 127.3, 19.36, 16.4, "LOW", false);
        seed("Phenol", 18.0, 5.9, 14.9, 87.5, 24.1002, 14.4, "MEDIUM", false);
        seed("Phenyl Acetate", 19.8, 5.2, 6.4, 126.9, 21.4485, 16.3, "LOW", false);
        seed("Phenyl Acetonitrile", 19.5, 12.3, 3.8, 114.9, 23.3662, 15.7, "LOW", false);
        seed("Phenyl Acetylene", 18.8, 2.8, 4.0, 109.1, 19.4237, 15.5, "LOW", false);
        seed("Phenylhydrazine", 20.4, 6.5, 14.0, 98.5, 25.5814, 14.9, "MEDIUM", false);
        seed("Phosgene", 16.4, 5.3, 5.3, 71.7, 18.0316, 13.6, "LOW", false);
        seed("Phosphoric Acid", 14.7, 18.6, 26.8, 52.8, 35.7811, 12.6, "HIGH", false);
        seed("Phosphorous Oxychloride (Phosphoryl", 18.1, 9.3, 0.0, 93.2, 20.3494, 14.7, "LOW", false);
        seed("Phosphorus Trichloride", 18.4, 3.6, 0.0, 87.3, 18.7489, 14.4, "LOW", false);
        seed("Phthalic Anhydride", 20.6, 20.1, 10.1, 96.8, 30.5021, 14.8, "MEDIUM", false);
        seed("Picric Acid (2,4,6-Trinitrophenol)", 19.2, 7.0, 6.0, 130.0, 21.2988, 16.5, "LOW", false);
        seed("Piperazine", 18.1, 5.6, 8.0, 97.9, 20.5662, 14.9, "MEDIUM", false);
        seed("Piperidine", 17.6, 4.5, 8.9, 98.9, 20.2292, 14.9, "MEDIUM", false);
        seed("Propadiene (Allene)", 15.3, 3.0, 6.8, 60.1, 17.0097, 13.0, "LOW", false);
        seed("Propane", 13.4, 0.0, 0.0, 89.5, 13.4, 14.5, "LOW", false);
        seed("Propanediol (Trimethyleneglycol)", 16.8, 13.5, 23.2, 72.5, 31.6659, 13.6, "HIGH", false);
        seed("Propanethiol", 16.3, 6.8, 6.5, 94.1, 18.8197, 14.7, "LOW", false);
        seed("Propanol", 16.0, 6.8, 17.4, 75.2, 24.5967, 13.8, "HIGH", false);
        seed("Propargyl Acetate", 16.3, 5.2, 8.3, 98.8, 19.0163, 14.9, "MEDIUM", false);
        seed("Propargylaldehyde", 16.2, 11.9, 8.7, 60.0, 21.903, 13.0, "MEDIUM", false);
        seed("Propionaldehyde", 15.3, 11.1, 6.9, 73.4, 20.1224, 13.7, "LOW", false);
        seed("Propionaldehyde-2,3-Epoxy", 17.5, 13.4, 9.8, 63.2, 24.1216, 13.2, "MEDIUM", false);
        seed("Propionamide", 16.7, 9.8, 11.5, 78.9, 22.5207, 13.9, "MEDIUM", false);
        seed("Propionic Acid", 14.7, 5.3, 12.4, 75.0, 19.9484, 13.8, "MEDIUM", false);
        seed("Propionic Anhydride", 16.2, 10.0, 8.7, 129.4, 20.9316, 16.5, "MEDIUM", false);
        seed("Propionitrile", 15.3, 14.3, 5.5, 70.9, 21.6525, 13.5, "LOW", false);
        seed("Propionylchloride", 16.1, 10.3, 5.3, 86.9, 19.8341, 14.3, "LOW", false);
        seed("Propyl Amine", 16.9, 4.9, 8.6, 83.0, 19.5852, 14.2, "MEDIUM", false);
        seed("Propyl Formate", 15.5, 7.1, 8.6, 97.9, 19.095, 14.9, "MEDIUM", false);
        seed("Propylene", 15.1, 1.6, 1.5, 68.8, 15.2584, 13.4, "LOW", false);
        seed("Propylene Glycol", 16.8, 9.4, 23.3, 73.6, 30.224, 13.7, "HIGH", false);
        seed("Propylene Glycol Monoeth yl Ether", 15.6, 4.3, 9.0, 155.1, 18.5162, 17.8, "MEDIUM", false);
        seed("Propylene Glycol Monometh yl Ether", 15.6, 5.6, 9.8, 137.1, 19.2551, 16.9, "MEDIUM", false);
        seed("Propyn-1-ol", 16.1, 8.8, 19.1, 57.7, 26.4851, 12.9, "HIGH", false);
        seed("Propynonitrile", 15.5, 17.0, 6.3, 62.5, 23.8525, 13.1, "LOW", false);
        seed("Pyridazine", 20.2, 17.4, 11.7, 72.6, 29.1151, 13.6, "MEDIUM", false);
        seed("Pyridine", 19.0, 8.8, 5.9, 80.9, 21.7543, 14.0, "HIGH", false);
        seed("Pyrogallol (1,2,3-Trihydroxybenzene)", 20.7, 10.7, 21.1, 87.0, 31.4355, 14.4, "HIGH", false);
        seed("Pyrrolidine", 17.9, 6.5, 7.4, 83.5, 20.4309, 14.2, "MEDIUM", false);
        seed("Pyrrolidone", 19.4, 17.4, 11.3, 76.4, 28.4044, 13.8, "MEDIUM", false);
        seed("Quinoline", 19.8, 5.6, 5.7, 118.0, 21.3516, 15.9, "LOW", false);
        seed("Sebacic Acid", 17.1, 7.1, 11.7, 167.6, 21.9023, 18.4, "MEDIUM", false);
        seed("Sec-Butyl Acetate", 15.0, 3.7, 7.6, 133.6, 17.2177, 16.7, "MEDIUM", false);
        seed("Stearic Acid", 16.3, 3.3, 5.5, 326.0, 17.5166, 26.3, "LOW", false);
        seed("Styrene", 18.6, 1.0, 4.1, 115.6, 19.0728, 15.8, "LOW", false);
        seed("Succinaldehyde (Butanedial)", 16.8, 9.8, 10.5, 81.2, 22.1027, 14.1, "MEDIUM", false);
        seed("Succinic Anhydride", 18.6, 19.2, 16.6, 66.8, 31.4668, 13.3, "HIGH", false);
        seed("Succinonitrile", 17.9, 16.2, 7.9, 81.2, 25.402, 14.1, "MEDIUM", false);
        seed("Sulfolane", 20.3, 18.2, 10.9, 95.7, 29.3622, 14.8, "HIGH", false);
        seed("Sulfur Dicyanide", 18.1, 13.5, 0.0, 60.0, 22.5801, 13.0, "LOW", false);
        seed("Sulfur Dioxide", 15.8, 8.4, 10.0, 44.0, 20.4988, 12.2, "MEDIUM", false);
        seed("Sulfuryl Chloride", 17.6, 7.2, 0.0, 81.0, 19.0158, 14.1, "LOW", false);
        seed("Terephthalic Acid", 18.8, 6.1, 10.7, 166.0, 22.4753, 18.3, "MEDIUM", false);
        seed("Tetrachlorobenzene 1,2,4,5-T", 21.2, 10.7, 3.4, 116.2, 23.9894, 15.8, "LOW", false);
        seed("Tetradecene", 16.1, 0.5, 1.9, 253.4, 16.2194, 22.7, "LOW", false);
        seed("Tetrahydrofuran", 16.8, 5.7, 8.0, 81.7, 19.461, 14.1, "MEDIUM", false);
        seed("Tetrahydropyran", 16.4, 6.3, 6.0, 97.8, 18.5648, 14.9, "LOW", false);
        seed("Tetrahydrothiapyran", 18.5, 6.3, 8.9, 103.6, 21.4744, 15.2, "MEDIUM", false);
        seed("Tetramethylene Sulfid", 18.6, 6.7, 9.1, 88.3, 21.7637, 14.4, "MEDIUM", false);
        seed("Tetramethylene Sulfone (Sulfolane)", 20.3, 18.2, 10.9, 95.7, 29.3622, 14.8, "MEDIUM", false);
        seed("Tetramethylene Sulfoxide", 18.2, 11.0, 9.1, 90.0, 23.1311, 14.5, "MEDIUM", false);
        seed("Tetramethylurea", 16.7, 8.2, 11.0, 120.4, 21.6132, 16.0, "MEDIUM", false);
        seed("Tetranitromethane", 15.5, 9.9, 7.5, 119.7, 19.8623, 16.0, "MEDIUM", false);
        seed("Thiabutane", 16.2, 5.9, 5.3, 90.4, 18.0372, 14.5, "LOW", false);
        seed("Thiacyclopropane", 19.3, 9.1, 5.0, 58.0, 21.9157, 12.9, "LOW", false);
        seed("Thiazole", 20.5, 18.8, 10.8, 70.9, 29.8384, 13.5, "MEDIUM", false);
        seed("Thioacetamide", 17.5, 20.6, 20.2, 75.0, 33.7439, 13.8, "HIGH", false);
        seed("Thioacetic Acid", 17.0, 6.7, 8.9, 71.5, 20.3249, 13.6, "MEDIUM", false);
        seed("Thiocyanic Acid", 16.8, 8.9, 10.9, 51.7, 21.9148, 12.6, "MEDIUM", false);
        seed("Thioglycolic Acid (Mercapto Acetic", 16.0, 8.6, 14.8, 69.5, 23.4307, 13.5, "MEDIUM", false);
        seed("Thionyl Chloride", 16.9, 6.2, 5.9, 79.0, 18.9436, 13.9, "LOW", false);
        seed("Thiophene", 18.9, 2.4, 7.8, 79.0, 20.5866, 13.9, "MEDIUM", false);
        seed("Thiophenol", 20.0, 4.5, 10.3, 102.4, 22.9421, 15.1, "MEDIUM", false);
        seed("Thiourea", 20.0, 21.7, 14.8, 72.8, 33.0141, 13.6, "MEDIUM", false);
        seed("Toluene", 18.0, 1.4, 2.0, 106.8, 18.1648, 15.3, "MEDIUM", false);
        seed("Toluidine", 19.4, 5.8, 9.4, 107.8, 22.324, 15.4, "MEDIUM", false);
        seed("Trans-Decahydronaphthalene", 18.0, 0.0, 0.0, 156.9, 18.0, 17.8, "LOW", false);
        seed("Tri Butyl Phosphate", 16.3, 6.3, 4.3, 274.0, 17.9964, 23.7, "LOW", false);
        seed("Tri-n-Butyl Borate", 16.7, 1.8, 4.6, 269.7, 17.4152, 23.5, "LOW", false);
        seed("Trichloro-Methyl-Silane", 16.5, 6.6, 3.5, 117.4, 18.1124, 15.9, "LOW", false);
        seed("Trichloroacetic Acid", 18.3, 5.8, 11.4, 100.2, 22.3269, 15.0, "MEDIUM", false);
        seed("Trichloroacetonitrile", 16.4, 7.4, 6.1, 100.0, 18.9982, 15.0, "LOW", false);
        seed("Trichlorofluoromethane (Freon 11", 15.3, 2.0, 0.0, 92.8, 15.4302, 14.6, "LOW", false);
        seed("Tricresyl Phosphate", 19.0, 12.3, 4.5, 316.0, 23.0768, 25.8, "LOW", false);
        seed("Tridecyl Alcohol", 16.2, 3.1, 9.0, 242.0, 18.7896, 22.1, "MEDIUM", false);
        seed("Triethylamine", 17.8, 0.4, 1.0, 138.6, 17.8326, 16.9, "HIGH", false);
        seed("Triethylphosphate", 16.7, 11.4, 9.2, 171.0, 22.2146, 18.6, "MEDIUM", false);
        seed("Trifluoroacetic Aci", 15.6, 9.9, 11.6, 74.2, 21.8158, 13.7, "MEDIUM", false);
        seed("Trifluoromethane (Freon 23", 14.4, 8.9, 6.5, 46.1, 18.1334, 12.3, "LOW", false);
        seed("Triisononyl Trimellilate", 16.6, 5.7, 2.2, 602.9, 17.6887, 40.1, "LOW", false);
        seed("Triisooctyl Trimellitate", 16.6, 6.0, 2.5, 553.1, 17.8272, 37.7, "LOW", false);
        seed("Trimethyl Amine", 14.6, 3.4, 1.8, 90.3, 15.0983, 14.5, "LOW", false);
        seed("Trimethyl-1,3-Pentanediol", 15.1, 6.1, 9.8, 227.4, 19.0068, 21.4, "MEDIUM", false);
        seed("Trimethylenesulfid", 18.8, 7.8, 9.4, 72.8, 22.4196, 13.6, "MEDIUM", false);
        seed("Trimethylphosphate", 16.7, 15.9, 10.2, 115.8, 25.2139, 15.8, "MEDIUM", false);
        seed("Trinitomethane", 15.5, 10.3, 7.3, 94.6, 19.9907, 14.7, "MEDIUM", false);
        seed("Trioctylphosphate", 16.2, 5.9, 4.2, 469.8, 17.7451, 33.5, "LOW", false);
        seed("Triphenyl Phosphate", 20.1, 6.4, 6.8, 271.9, 22.1633, 23.6, "LOW", false);
        seed("Undecane", 16.0, 0.0, 0.0, 212.7, 16.0, 20.6, "LOW", false);
        seed("Valeronitrile", 15.3, 11.0, 4.8, 103.8, 19.4456, 15.2, "LOW", false);
        seed("Vinyl Acetate", 16.0, 7.2, 5.9, 92.6, 18.5108, 14.6, "LOW", false);
        seed("Vinyl Acetic Acid", 16.8, 5.2, 12.3, 85.3, 21.4609, 14.3, "MEDIUM", false);
        seed("Vinyl Acetylene", 15.1, 1.7, 12.0, 74.3, 19.3623, 13.7, "MEDIUM", false);
        seed("Vinyl Amine", 15.7, 7.2, 11.8, 51.8, 20.9182, 12.6, "MEDIUM", false);
        seed("Vinyl Bromide", 15.9, 6.3, 5.4, 71.6, 17.9349, 13.6, "LOW", false);
        seed("Vinyl Butyrate", 15.6, 3.9, 6.9, 126.5, 17.498, 16.3, "LOW", false);
        seed("Vinyl Chloride", 16.0, 6.5, 2.4, 68.7, 17.4359, 13.4, "LOW", false);
        seed("Vinyl Ether", 14.8, 4.2, 5.8, 90.7, 16.4414, 14.5, "LOW", false);
        seed("Vinyl Ethyl Ether", 14.5, 4.9, 6.0, 95.0, 16.4396, 14.8, "LOW", false);
        seed("Vinyl Ethyl Sulfid", 16.4, 5.8, 6.3, 101.3, 18.5011, 15.1, "LOW", false);
        seed("Vinyl Formate", 15.3, 6.5, 9.7, 74.7, 19.2466, 13.7, "MEDIUM", false);
        seed("Vinyl Iodide (Iodoethene)", 17.1, 5.5, 7.3, 75.6, 19.3894, 13.8, "MEDIUM", false);
        seed("Vinyl Propionate", 15.6, 8.0, 4.7, 110.1, 18.1508, 15.5, "LOW", false);
        seed("Vinyl Silane", 15.5, 2.6, 4.0, 89.4, 16.2176, 14.5, "LOW", false);
        seed("Vinyl Trifluoro Acetate", 13.9, 4.3, 7.6, 116.4, 16.4152, 15.8, "MEDIUM", false);
        seed("Vinyl Trimethyl Silane", 14.5, 1.0, 2.5, 145.3, 14.7479, 17.3, "LOW", false);
        seed("Xylene", 17.6, 1.0, 3.1, 123.3, 17.8989, 16.2, "MEDIUM", false);
        seed("alpha,alpha,alpha Trifluoro oluene", 17.5, 8.8, 0.0, 122.9, 19.588, 16.1, "LOW", false);
        seed("beta-Propiolactone", 19.7, 18.2, 10.3, 65.5, 28.7301, 13.3, "MEDIUM", false);
        seed("n-Butyl Acetate", 15.8, 3.7, 6.3, 132.5, 17.4075, 16.6, "LOW", false);
        seed("n-Butyl Acrylate", 15.6, 6.2, 4.9, 143.8, 17.4874, 17.2, "LOW", false);
        seed("n-Butyl Amine", 16.2, 4.5, 8.0, 99.0, 18.6196, 14.9, "MEDIUM", false);
        seed("n-Butyl Butyrate", 15.6, 2.9, 5.6, 166.7, 16.8265, 18.3, "LOW", false);
        seed("n-Butyl Cyclohexane", 16.2, 0.0, 0.6, 176.7, 16.2111, 18.8, "LOW", false);
        seed("n-Butyl Cyclopentane", 16.4, 0.0, 1.0, 162.0, 16.4305, 18.1, "LOW", false);
        seed("n-Butyl Propionate", 15.7, 5.5, 5.9, 149.7, 17.6508, 17.5, "LOW", false);
        seed("n-Butylbenzene", 17.4, 0.1, 1.1, 157.0, 17.435, 17.9, "LOW", false);
        seed("n-Butyramide", 16.9, 13.7, 12.3, 98.4, 24.9918, 14.9, "MEDIUM", false);
        seed("n-Heptyl Acetate", 15.8, 2.9, 5.5, 181.1, 16.9794, 19.1, "LOW", false);
        seed("n-Hexane", 14.9, 0.0, 0.0, 131.6, 14.9, 16.6, "LOW", false);
        seed("n-Pentyl Propionate", 15.8, 5.2, 5.7, 165.3, 17.5832, 18.3, "LOW", false);
        seed("n-Propyl Acetate", 15.3, 4.3, 7.6, 115.3, 17.6165, 15.8, "MEDIUM", false);
        seed("n-Tetradecane", 16.2, 0.0, 0.0, 261.3, 16.2, 23.1, "LOW", false);
        seed("o-Xylene", 17.8, 1.0, 3.1, 121.2, 18.0956, 16.1, "LOW", false);
        seed("tert-Butyl Acetate", 15.4, 6.2, 6.2, 134.1, 17.7212, 16.7, "LOW", false);

        solventRepository.findAll().stream()
                .filter(s -> s.getMolarVolume() == 0.0)
                .forEach(s -> { s.setMolarVolume(100.0); solventRepository.save(s); });

        long after = solventRepository.count();
        if (after > before) {
            System.out.println("[DataSeeder] Added " + (after - before) + " new solvents (total: " + after + ")");
        } else {
            System.out.println("[DataSeeder] Solvent catalog up to date: " + after + " solvents");
        }
    }
}