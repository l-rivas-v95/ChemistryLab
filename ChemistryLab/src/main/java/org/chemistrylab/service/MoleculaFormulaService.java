package org.chemistrylab.service;

import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.Map;

@Service
public class MoleculaFormulaService {

    private static final Map<String, String> FORMULAS_POR_NOMBRE = Map.ofEntries(
            Map.entry("water", "H2O"),
            Map.entry("ammonia", "NH3"),
            Map.entry("hydrogen peroxide", "H2O2"),
            Map.entry("hydrogen sulfide", "H2S"),
            Map.entry("carbon dioxide", "CO2"),
            Map.entry("carbon monoxide", "CO"),
            Map.entry("sulfur dioxide", "SO2"),
            Map.entry("sulfur trioxide", "SO3"),
            Map.entry("nitric oxide", "NO"),
            Map.entry("nitrogen dioxide", "NO2"),
            Map.entry("dinitrogen monoxide", "N2O"),
            Map.entry("hydrochloric acid", "HCl"),
            Map.entry("sulfuric acid", "H2SO4"),
            Map.entry("nitric acid", "HNO3"),
            Map.entry("phosphoric acid", "H3PO4"),
            Map.entry("carbonic acid", "H2CO3"),
            Map.entry("boric acid", "H3BO3"),
            Map.entry("hydrofluoric acid", "HF"),
            Map.entry("hydrobromic acid", "HBr"),
            Map.entry("hydroiodic acid", "HI"),
            Map.entry("perchloric acid", "HClO4"),
            Map.entry("sodium hydroxide", "NaOH"),
            Map.entry("potassium hydroxide", "KOH"),
            Map.entry("calcium hydroxide", "Ca(OH)2"),
            Map.entry("magnesium hydroxide", "Mg(OH)2"),
            Map.entry("aluminum hydroxide", "Al(OH)3"),
            Map.entry("iron(iii) hydroxide", "Fe(OH)3"),
            Map.entry("copper(ii) hydroxide", "Cu(OH)2"),
            Map.entry("zinc hydroxide", "Zn(OH)2"),
            Map.entry("barium hydroxide", "Ba(OH)2"),
            Map.entry("ammonium hydroxide", "NH4OH"),
            Map.entry("sodium chloride", "NaCl"),
            Map.entry("potassium chloride", "KCl"),
            Map.entry("calcium chloride", "CaCl2"),
            Map.entry("magnesium chloride", "MgCl2"),
            Map.entry("aluminum chloride", "AlCl3"),
            Map.entry("iron(iii) chloride", "FeCl3"),
            Map.entry("copper(ii) chloride", "CuCl2"),
            Map.entry("zinc chloride", "ZnCl2"),
            Map.entry("silver chloride", "AgCl"),
            Map.entry("ammonium chloride", "NH4Cl"),
            Map.entry("sodium carbonate", "Na2CO3"),
            Map.entry("calcium carbonate", "CaCO3"),
            Map.entry("magnesium carbonate", "MgCO3"),
            Map.entry("potassium carbonate", "K2CO3"),
            Map.entry("ammonium carbonate", "(NH4)2CO3"),
            Map.entry("sodium bicarbonate", "NaHCO3"),
            Map.entry("potassium bicarbonate", "KHCO3"),
            Map.entry("sodium sulfate", "Na2SO4"),
            Map.entry("potassium sulfate", "K2SO4"),
            Map.entry("calcium sulfate", "CaSO4"),
            Map.entry("magnesium sulfate", "MgSO4"),
            Map.entry("copper(ii) sulfate", "CuSO4"),
            Map.entry("zinc sulfate", "ZnSO4"),
            Map.entry("iron(ii) sulfate", "FeSO4"),
            Map.entry("aluminum sulfate", "Al2(SO4)3"),
            Map.entry("ammonium sulfate", "(NH4)2SO4"),
            Map.entry("sodium sulfite", "Na2SO3"),
            Map.entry("sodium thiosulfate", "Na2S2O3"),
            Map.entry("sodium sulfide", "Na2S"),
            Map.entry("sodium nitrate", "NaNO3"),
            Map.entry("potassium nitrate", "KNO3"),
            Map.entry("calcium nitrate", "Ca(NO3)2"),
            Map.entry("ammonium nitrate", "NH4NO3"),
            Map.entry("silver nitrate", "AgNO3"),
            Map.entry("copper(ii) nitrate", "Cu(NO3)2"),
            Map.entry("iron(iii) nitrate", "Fe(NO3)3"),
            Map.entry("sodium nitrite", "NaNO2"),
            Map.entry("potassium nitrite", "KNO2"),
            Map.entry("calcium nitrite", "Ca(NO2)2"),
            Map.entry("sodium phosphate", "Na3PO4"),
            Map.entry("potassium phosphate", "K3PO4"),
            Map.entry("calcium phosphate", "Ca3(PO4)2"),
            Map.entry("ammonium phosphate", "(NH4)3PO4"),
            Map.entry("sodium hydrogen phosphate", "Na2HPO4"),
            Map.entry("sodium dihydrogen phosphate", "NaH2PO4"),
            Map.entry("calcium hydrogen phosphate", "CaHPO4"),
            Map.entry("magnesium phosphate", "Mg3(PO4)2"),
            Map.entry("aluminum phosphate", "AlPO4"),
            Map.entry("iron(iii) phosphate", "FePO4"),
            Map.entry("sodium hypochlorite", "NaClO"),
            Map.entry("sodium chlorate", "NaClO3"),
            Map.entry("potassium chlorate", "KClO3"),
            Map.entry("sodium perchlorate", "NaClO4"),
            Map.entry("potassium permanganate", "KMnO4"),
            Map.entry("sodium dichromate", "Na2Cr2O7"),
            Map.entry("potassium dichromate", "K2Cr2O7"),
            Map.entry("sodium cyanide", "NaCN"),
            Map.entry("potassium cyanide", "KCN")
    );

    private static final Map<String, String> FORMULAS_NORMALIZADAS = Map.ofEntries(
            Map.entry("ClH", "HCl"),
            Map.entry("FH", "HF"),
            Map.entry("BrH", "HBr"),
            Map.entry("H3N", "NH3"),
            Map.entry("H2O4S", "H2SO4"),
            Map.entry("H3O4P", "H3PO4"),
            Map.entry("CH2O3", "H2CO3"),
            Map.entry("BH3O3", "H3BO3"),
            Map.entry("ClHO4", "HClO4"),
            Map.entry("HNaO", "NaOH"),
            Map.entry("HKO", "KOH"),
            Map.entry("CaH2O2", "Ca(OH)2"),
            Map.entry("H2MgO2", "Mg(OH)2"),
            Map.entry("AlH3O3", "Al(OH)3"),
            Map.entry("FeH3O3", "Fe(OH)3"),
            Map.entry("CuH2O2", "Cu(OH)2"),
            Map.entry("H2O2Zn", "Zn(OH)2"),
            Map.entry("BaH2O2", "Ba(OH)2"),
            Map.entry("H5NO", "NH4OH"),
            Map.entry("ClNa", "NaCl"),
            Map.entry("ClK", "KCl"),
            Map.entry("Cl2Mg", "MgCl2"),
            Map.entry("Cl3Fe", "FeCl3"),
            Map.entry("Cl2Cu", "CuCl2"),
            Map.entry("Cl2Zn", "ZnCl2"),
            Map.entry("ClH4N", "NH4Cl"),
            Map.entry("CNa2O3", "Na2CO3"),
            Map.entry("CCaO3", "CaCO3"),
            Map.entry("CMgO3", "MgCO3"),
            Map.entry("C2K2O6", "K2CO3"),
            Map.entry("CHNaO3", "NaHCO3"),
            Map.entry("CHKO3", "KHCO3"),
            Map.entry("Na2O4S", "Na2SO4"),
            Map.entry("K2O4S", "K2SO4"),
            Map.entry("CaO4S", "CaSO4"),
            Map.entry("MgO4S", "MgSO4"),
            Map.entry("CuO4S", "CuSO4"),
            Map.entry("ZnO4S", "ZnSO4"),
            Map.entry("FeO4S", "FeSO4"),
            Map.entry("Al2O12S3", "Al2(SO4)3"),
            Map.entry("H8N2O4S", "(NH4)2SO4"),
            Map.entry("Na2O3S", "Na2SO3"),
            Map.entry("Na2O3S2", "Na2S2O3"),
            Map.entry("Na2S", "Na2S"),
            Map.entry("NNaO3", "NaNO3"),
            Map.entry("CaN2O6", "Ca(NO3)2"),
            Map.entry("H4N2O3", "NH4NO3"),
            Map.entry("CuN2O6", "Cu(NO3)2"),
            Map.entry("FeN3O9", "Fe(NO3)3"),
            Map.entry("NNaO2", "NaNO2"),
            Map.entry("CaN2O4", "Ca(NO2)2"),
            Map.entry("Na3O4P", "Na3PO4"),
            Map.entry("H2KO4P", "K3PO4"),
            Map.entry("Ca3O8P2", "Ca3(PO4)2"),
            Map.entry("H12N3O4P", "(NH4)3PO4"),
            Map.entry("HNa2O4P", "Na2HPO4"),
            Map.entry("H2NaO4P", "NaH2PO4"),
            Map.entry("CaHO4P", "CaHPO4"),
            Map.entry("Mg3O8P2", "Mg3(PO4)2"),
            Map.entry("AlO4P", "AlPO4"),
            Map.entry("FeO4P", "FePO4"),
            Map.entry("ClNaO", "NaClO"),
            Map.entry("ClNaO3", "NaClO3"),
            Map.entry("ClKO3", "KClO3"),
            Map.entry("ClNaO4", "NaClO4"),
            Map.entry("Cr2Na2O7", "Na2Cr2O7"),
            Map.entry("Cr2K2O7", "K2Cr2O7"),
            Map.entry("CNNa", "NaCN"),
            Map.entry("CKN", "KCN")
    );

    public String obtenerFormulaVisible(String nombre, String formula) {
        String nombreNormalizado = normalizarTexto(nombre);
        String formulaLimpia = limpiarFormula(formula);

        return FORMULAS_POR_NOMBRE.getOrDefault(
                nombreNormalizado,
                FORMULAS_NORMALIZADAS.getOrDefault(formulaLimpia, formulaLimpia)
        );
    }

    private String limpiarFormula(String value) {
        if (value == null || value.isBlank()) {
            return "Sin fórmula";
        }

        return value
                .replaceAll("[+-]\\d*$", "")
                .replaceAll("\\d*[+-]$", "")
                .replaceAll("\\s", "");
    }

    private String normalizarTexto(String value) {
        if (value == null) {
            return "";
        }

        String lower = value.toLowerCase().trim();
        return Normalizer.normalize(lower, Normalizer.Form.NFD)
                .replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
    }
}
