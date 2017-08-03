package com.compomics.util.experiment.biology;

import com.compomics.util.db.object.ObjectsDB;
import com.compomics.util.experiment.identification.protein_sequences.SequenceFactory;
import com.compomics.util.experiment.identification.matches.ModificationMatch;
import com.compomics.util.experiment.identification.matches.VariantMatch;
import com.compomics.util.experiment.personalization.ExperimentObject;
import com.compomics.util.experiment.identification.identification_parameters.PtmSettings;
import com.compomics.util.experiment.massspectrometry.utils.StandardMasses;
import com.compomics.util.preferences.DigestionPreferences;
import com.compomics.util.preferences.SequenceMatchingPreferences;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class models a peptide.
 *
 * @author Marc Vaudel
 * @author Dominik Kopczynski
 */
public class Peptide extends ExperimentObject {

    /**
     * The version UID for serialization/deserialization compatibility.
     */
    static final long serialVersionUID = 5632064601627536034L;
    /**
     * The peptide sequence.
     */
    private String sequence;
    /**
     * The peptide key.
     */
    private String key;
    /**
     * The peptide matching key.
     */
    private String matchingKey;
    /**
     * The peptide mass.
     */
    private double mass = -1;
    /**
     * The mapping of this peptide on proteins as a map, accession to position. Position on protein sequences is 0 based.
     */
    private HashMap<String, HashSet<Integer>> proteinMapping = null;
    /**
     * The modifications carried by the peptide.
     */
    private ArrayList<ModificationMatch> modificationMatches = null;
    /**
     * The variants observed when mapping this peptide to the database.
     */
    private ArrayList<VariantMatch> variantMatches = null;
    /**
     * Separator preceding confident localization of the confident localization
     * of a modification.
     */
    public final static String MODIFICATION_LOCALIZATION_SEPARATOR = "-ATAA-";
    /**
     * Separator used to separate modifications in peptide keys as string.
     */
    public final static String MODIFICATION_SEPARATOR = "_";

    /**
     * Constructor for the peptide.
     */
    public Peptide() {
    }

    /**
     * Constructor.
     *
     * @param aSequence the peptide sequence, assumed to be in upper case only
     * @param modificationMatches the PTM of this peptide
     * @param sanityCheck boolean indicating whether the input should be checked
     */
    public Peptide(String aSequence, ArrayList<ModificationMatch> modificationMatches, boolean sanityCheck) {
        this.sequence = aSequence;
        if (modificationMatches != null) {
            this.modificationMatches = new ArrayList<>(modificationMatches);
        }
        if (sanityCheck) {
            sanityCheck();
        }
    }

    /**
     * Constructor.
     *
     * @param aSequence the peptide sequence, assumed to be in upper case only
     * @param modificationMatches the PTM of this peptide
     * @param sanityCheck boolean indicating whether the input should be checked
     * @param mass the mass of the peptide
     */
    public Peptide(String aSequence, ArrayList<ModificationMatch> modificationMatches, boolean sanityCheck, double mass) {
        this.sequence = aSequence;
        if (modificationMatches != null) {
            this.modificationMatches = new ArrayList<>(modificationMatches);
        }
        if (sanityCheck) {
            sanityCheck();
        }
        setMass(mass);
    }

    /**
     * Constructor. No sanity check is performed on the input.
     *
     * @param aSequence the peptide sequence, assumed to be in upper case only
     * @param modifications the PTM of this peptide
     */
    public Peptide(String aSequence, ArrayList<ModificationMatch> modifications) {
        this(aSequence, modifications, false);
    }

    /**
     * Constructor for the peptide.
     *
     * @param aSequence the peptide sequence, assumed to be in upper case only
     * @param modifications the PTM of this peptide
     * @param variants the variants compared to the database
     * @param sanityCheck boolean indicating whether the input should be checked
     */
    public Peptide(String aSequence, ArrayList<ModificationMatch> modifications, ArrayList<VariantMatch> variants, boolean sanityCheck) {
        this.sequence = aSequence;
        this.modificationMatches = new ArrayList<>(modifications);
        this.variantMatches = new ArrayList<>(variants);
        if (sanityCheck) {
            sanityCheck();
        }
    }

    /**
     * Removes characters from the sequence and checks the modifications names
     * for forbidden characters.
     */
    private void sanityCheck() {
        ObjectsDB.increaseRWCounter();
        zooActivateWrite();
        ObjectsDB.decreaseRWCounter();
        sequence = sequence.replaceAll("[#*$%&]", "");
        if (modificationMatches != null) {
            HashSet<String> conflictingPtms = modificationMatches.parallelStream().map(modificationMatch -> modificationMatch.getTheoreticPtm())
                    .filter((modificationName) -> (modificationName.contains(MODIFICATION_SEPARATOR) || modificationName.contains(MODIFICATION_LOCALIZATION_SEPARATOR)))
                    .collect(Collectors.toCollection(HashSet::new));
            if (!conflictingPtms.isEmpty()) {
                String conflictingPtmsString = conflictingPtms.stream().collect(Collectors.joining(", "));
                throw new IllegalArgumentException("PTM names containing '" + MODIFICATION_SEPARATOR + "' or '" + MODIFICATION_LOCALIZATION_SEPARATOR + "' are not supported. Conflicting name(s): " + conflictingPtmsString);
            }
        }
    }

    /**
     * Sets the mass.
     *
     * @param mass the mass
     */
    public void setMass(double mass) {
        ObjectsDB.increaseRWCounter();
        zooActivateWrite();
        ObjectsDB.decreaseRWCounter();
        this.mass = mass;
    }

    /**
     * Sets the object key.
     *
     * @param key the object key
     */
    public void setKey(String key) {
        ObjectsDB.increaseRWCounter();
        zooActivateWrite();
        ObjectsDB.decreaseRWCounter();
        this.key = key;
    }

    /**
     * Returns the proteins where this peptide can be found.
     *
     * @return the proteins where this peptide can be found
     */
    public HashMap<String, HashSet<Integer>> getProteinMapping() {
        ObjectsDB.increaseRWCounter();
        zooActivateRead();
        ObjectsDB.decreaseRWCounter();
        return proteinMapping;
    }

    /**
     * Sets the sequence variant matches of this peptide.
     *
     * @param variantMatches the variant matches of this peptide
     */
    public void setVariantMatches(ArrayList<VariantMatch> variantMatches) {
        ObjectsDB.increaseRWCounter();
        zooActivateWrite();
        ObjectsDB.decreaseRWCounter();
        this.variantMatches = variantMatches;
    }

    /**
     * Returns the sequence variant matches of this peptide.
     *
     * @return the sequence variant matches of this peptide
     */
    public ArrayList<VariantMatch> getVariantMatches() {
        ObjectsDB.increaseRWCounter();
        zooActivateRead();
        ObjectsDB.decreaseRWCounter();
        return variantMatches;
    }

    /**
     * Getter for the mass.
     *
     * @return the peptide mass
     */
    public double getMass() {
        ObjectsDB.increaseRWCounter();
        zooActivateRead();
        ObjectsDB.decreaseRWCounter();
        if (mass == -1) {
            estimateTheoreticMass();
        }
        return mass;
    }

    /**
     * Getter for the modifications carried by this peptide.
     *
     * @return the modifications matches as found by the search engine
     */
    public ArrayList<ModificationMatch> getModificationMatches() {
        ObjectsDB.increaseRWCounter();
        zooActivateRead();
        ObjectsDB.decreaseRWCounter();
        return modificationMatches;
    }

    /**
     * Sets new modification matches for the peptide.
     *
     * @param modificationMatches the new modification matches
     *
     * @exception InterruptedException exception thrown if the thread is
     * interrupted.
     */
    public void setModificationMatches(ArrayList<ModificationMatch> modificationMatches) throws InterruptedException {
        ObjectsDB.increaseRWCounter();
        zooActivateWrite();
        ObjectsDB.decreaseRWCounter();
        this.modificationMatches = modificationMatches;
        setMass(-1);
        setKey(null);
        setMatchingKey(null);
    }

    /**
     * Clears the list of imported modification matches.
     *
     * @exception InterruptedException exception thrown whenever the thread is
     * interrupted.
     */
    public void clearModificationMatches() throws InterruptedException {
        ObjectsDB.increaseRWCounter();
        zooActivateWrite();
        ObjectsDB.decreaseRWCounter();
        modificationMatches.clear();
        setMass(-1);
        setKey(null);
        setMatchingKey(null);
    }

    /**
     * Adds a modification match.
     *
     * @param modificationMatch the modification match to add
     */
    public void addModificationMatch(ModificationMatch modificationMatch) {
        ObjectsDB.increaseRWCounter();
        zooActivateWrite();
        ObjectsDB.decreaseRWCounter();
        if (modificationMatches == null) {
            modificationMatches = new ArrayList<>(1);
        }
        modificationMatches.add(modificationMatch);
        setMass(-1);
        setKey(null);
        setMatchingKey(null);
    }

    /**
     * Clears the list of imported variant matches.
     */
    public void clearVariantMatches() {
        ObjectsDB.increaseRWCounter();
        zooActivateWrite();
        ObjectsDB.decreaseRWCounter();
        if (variantMatches != null) {
            variantMatches.clear();
        }
    }

    /**
     * Adds a variant match.
     *
     * @param variantMatch the variant match to add
     */
    public void addVariantMatch(VariantMatch variantMatch) {
        ObjectsDB.increaseRWCounter();
        zooActivateWrite();
        ObjectsDB.decreaseRWCounter();
        if (variantMatches == null) {
            variantMatches = new ArrayList<>(1);
        }
        variantMatches.add(variantMatch);
    }

    /**
     * Adds variant matches.
     *
     * @param variantMatch the variant match to add
     */
    public void addVariantMatches(Collection<VariantMatch> variantMatch) {
        ObjectsDB.increaseRWCounter();
        zooActivateWrite();
        ObjectsDB.decreaseRWCounter();
        if (variantMatches == null) {
            variantMatches = new ArrayList<>(variantMatch != null ? variantMatch.size() : 0);
        }
        if (variantMatch != null) {
            variantMatches.addAll(variantMatch);
        }
    }

    /**
     * Getter for the sequence.
     *
     * @return the peptide sequence
     */
    public String getSequence() {
        ObjectsDB.increaseRWCounter();
        zooActivateRead();
        ObjectsDB.decreaseRWCounter();
        return sequence;
    }

    /**
     * Setter for the sequence.
     * 
     * @param sequence the peptide sequence
     */
    public void setSequence(String sequence) {
        ObjectsDB.increaseRWCounter();
        zooActivateWrite();
        ObjectsDB.decreaseRWCounter();
        this.sequence = sequence;
    }

    /**
     * Returns the number of missed cleavages using the specified enzyme.
     *
     * @param enzyme the enzyme used
     * @return the amount of missed cleavages
     */
    public int getNMissedCleavages(Enzyme enzyme) {
        ObjectsDB.increaseRWCounter();
        zooActivateRead();
        ObjectsDB.decreaseRWCounter();
        return enzyme.getNmissedCleavages(sequence);
    }

    /**
     * Returns the number of missed cleavages using the digestion preferences.
     * Null if no cleavage set. If multiple enzymes were used, the minimum across the different enzymes.
     *
     * @param digestionPreferences the digestion preferences
     * 
     * @return the amount of missed cleavages
     */
    public Integer getNMissedCleavages(DigestionPreferences digestionPreferences) {
        ObjectsDB.increaseRWCounter();
        zooActivateRead();
        ObjectsDB.decreaseRWCounter();
        Integer peptideMinMissedCleavages = null;
        if (digestionPreferences.getCleavagePreference() == DigestionPreferences.CleavagePreference.enzyme) {
            for (Enzyme enzyme : digestionPreferences.getEnzymes()) {
                int tempMissedCleavages = getNMissedCleavages(enzyme);
                if (peptideMinMissedCleavages == null || tempMissedCleavages < peptideMinMissedCleavages) {
                    peptideMinMissedCleavages = tempMissedCleavages;
                }
            }
        }
        return peptideMinMissedCleavages;
    }

    /**
     * Returns the key accounting for sequence matching preferences
     *
     * @return the key accounting for sequence matching preferences
     */
    public String getMatchingKey() {
        ObjectsDB.increaseRWCounter();
        zooActivateRead();
        ObjectsDB.decreaseRWCounter();
        return matchingKey;
    }

    /**
     * Sets the key accounting for sequence matching preferences.
     *
     * @param matchingKey the key accounting for sequence matching preferences
     */
    public void setMatchingKey(String matchingKey) {
        ObjectsDB.increaseRWCounter();
        zooActivateWrite();
        ObjectsDB.decreaseRWCounter();
        this.matchingKey = matchingKey;
    }

    /**
     * Returns a unique key for the peptide when considering the given matching
     * preferences. When ambiguity the first amino acid according to
     * AminoAcid.getAminoAcidsList() will be selected. For example the matching
     * key of peptide PEPTLDE_mod1_mod2 is PEPTIDE_mod1_mod2
     *
     * @param sequenceMatchingPreferences the sequence matching preferences
     *
     * @return a key unique to the given matching type
     */
    public String getMatchingKey(SequenceMatchingPreferences sequenceMatchingPreferences) {
        ObjectsDB.increaseRWCounter();
        zooActivateRead();
        ObjectsDB.decreaseRWCounter();
        if (matchingKey == null) {
            String matchingSequence = AminoAcid.getMatchingSequence(sequence, sequenceMatchingPreferences);
            setMatchingKey(getKey(matchingSequence, modificationMatches));
        }
        return matchingKey;
    }

    /**
     * Resets the internal cache of the keys.
     */
    public void resetKeysCaches() {
        ObjectsDB.increaseRWCounter();
        zooActivateWrite();
        ObjectsDB.decreaseRWCounter();
        setMatchingKey(null);
        setKey(null);
    }

    /**
     * Returns the reference key of a peptide. index =
     * SEQUENCE_modMass1_modMass2 with modMass1 and modMass2 modification masses
     * ordered alphabetically.
     *
     * Note: the key is not unique for indistinguishable sequences, see
     * getMatchingKey(SequenceMatchingPreferences sequenceMatchingPreferences).
     * Modifications must be loaded in the PTM factory.
     *
     * @return the key of the peptide
     */
    public String getKey() {
        ObjectsDB.increaseRWCounter();
        zooActivateRead();
        ObjectsDB.decreaseRWCounter();
        if (key == null) {
            setKey(getKey(getSequence(), getModificationMatches()));
        }
        return key;
    }

    /**
     * Returns the reference key of a peptide. key = SEQUENCE_mod1_mod2 modMass1
     * and modMass2 modification masses ordered alphabetically.
     *
     * @param sequence the sequence of the peptide
     * @param modificationMatches list of modification matches
     *
     * @return the key of the peptide
     */
    public static String getKey(String sequence, ArrayList<ModificationMatch> modificationMatches) {
        if (modificationMatches == null) {
            return sequence;
        }
        int size = sequence.length();
        ArrayList<String> tempModifications = new ArrayList<>(modificationMatches.size());
        for (ModificationMatch mod : modificationMatches) {
            if (mod.getVariable()) {
                String ptmName = mod.getTheoreticPtm();
                if (ptmName != null) {
                    PTM ptm = PTMFactory.getInstance().getPTM(ptmName);
                    if (mod.getConfident() || mod.getInferred()) {
                        StringBuilder tempModKey = new StringBuilder();
                        tempModKey.append(ptm.getMassAsString()).append(MODIFICATION_LOCALIZATION_SEPARATOR).append(mod.getModificationSite());
                        tempModifications.add(tempModKey.toString());
                        size += tempModKey.length();
                    } else {
                        String massAsString = ptm.getMassAsString();
                        tempModifications.add(massAsString);
                        size += massAsString.length();
                    }
                } else {
                    tempModifications.add("unknown-modification");
                }
            }
        }
        StringBuilder result = new StringBuilder(size);
        result.append(sequence);
        Collections.sort(tempModifications);
        for (String mod : tempModifications) {
            result.append(MODIFICATION_SEPARATOR).append(mod);
        }
        return result.toString();
    }

    /**
     * Indicates whether a peptide carries modifications.
     *
     * @return a boolean indicating whether a peptide carries modifications
     */
    public boolean isModified() {
        ObjectsDB.increaseRWCounter();
        zooActivateRead();
        ObjectsDB.decreaseRWCounter();
        return modificationMatches != null && !modificationMatches.isEmpty();
    }

    /**
     * Returns the number of variable modifications found with the given mass.
     *
     * @param modificationMass the mass of the modification
     * @return the number of occurrences of this modification
     */
    public int getNVariableModifications(double modificationMass) {
        ObjectsDB.increaseRWCounter();
        zooActivateRead();
        ObjectsDB.decreaseRWCounter();
        return modificationMatches == null ? 0 : (int) modificationMatches.stream()
                .filter(modificationMatch -> modificationMatch.getVariable())
                .map(modificationMatch -> PTMFactory.getInstance().getPTM(modificationMatch.getTheoreticPtm()))
                .filter(ptm -> ptm.getMass() == modificationMass).count();
    }

    /**
     * Returns the number of modifications carried by this peptide.
     *
     * @return the number of modifications carried by this peptide
     */
    public int getNModifications() {
        ObjectsDB.increaseRWCounter();
        zooActivateRead();
        ObjectsDB.decreaseRWCounter();
        return modificationMatches != null ? modificationMatches.size() : 0;
    }

    /**
     * Returns a list of masses of the variable modifications found in the key
     * of a peptide.
     *
     * @param peptideKey the key of a peptide
     *
     * @return a list of names of the variable modifications found in the key
     */
    public static ArrayList<String> getModificationFamily(String peptideKey) {
        ArrayList<String> result = new ArrayList<>();
        String[] parsedKey = peptideKey.split(MODIFICATION_SEPARATOR);
        for (int i = 1; i < parsedKey.length; i++) {
            String[] parsedMod = parsedKey[i].split(MODIFICATION_LOCALIZATION_SEPARATOR);
            result.add(parsedMod[0]);
        }
        return result;
    }

    /**
     * Returns a list of proteins where this peptide can be found in the
     * N-terminus. The proteins must be accessible via the sequence factory. If
     * none found, an empty list is returned.
     *
     * @param sequenceMatchingPreferences the sequence matching preferences
     *
     * @return a list of proteins where this peptide can be found in the
     * N-terminus
     *
     * @throws IOException exception thrown whenever an error occurred while
     * reading the protein sequence
     * @throws IllegalArgumentException exception thrown whenever an error
     * occurred while reading the protein sequence
     * @throws InterruptedException exception thrown whenever an error occurred
     * while reading the protein sequence
     * @throws ClassNotFoundException if a ClassNotFoundException occurs
     * @throws SQLException if an SQLException occurs
     * @throws FileNotFoundException if a FileNotFoundException occurs
     */
    public ArrayList<String> isNterm(SequenceMatchingPreferences sequenceMatchingPreferences)
            throws IOException, IllegalArgumentException, InterruptedException, FileNotFoundException, ClassNotFoundException, SQLException {

        ObjectsDB.increaseRWCounter();
        zooActivateRead();
        ObjectsDB.decreaseRWCounter();
        SequenceFactory sequenceFactory = SequenceFactory.getInstance();
        ArrayList<String> result = new ArrayList<>();

        if (parentProteins == null) {
            getParentProteins(sequenceMatchingPreferences);
        }

        for (String accession : parentProteins) {
            Protein protein = sequenceFactory.getProtein(accession);
            if (protein.isNTerm(sequence, sequenceMatchingPreferences)) {
                result.add(accession);
            }
        }

        return result;
    }

    /**
     * Returns a list of proteins where this peptide can be found in the
     * C-terminus. The proteins must be accessible via the sequence factory. If
     * none found, an empty list is returned. Warning: if the parent proteins
     * are not set, they will be set using the default protein tree and the
     * given matching type and mass tolerance
     *
     * @param sequenceMatchingPreferences the sequence matching preferences
     *
     * @return a list of proteins where this peptide can be found in the
     * C-terminus
     *
     * @throws IOException exception thrown whenever an error occurred while
     * reading a protein sequence
     * @throws IllegalArgumentException exception thrown whenever an error
     * occurred while reading a protein sequence
     * @throws InterruptedException exception thrown whenever an error occurred
     * while reading a protein sequence
     * @throws ClassNotFoundException if a ClassNotFoundException occurs
     * @throws SQLException if an SQLException occurs
     * @throws FileNotFoundException if a FileNotFoundException occurs
     */
    public ArrayList<String> isCterm(SequenceMatchingPreferences sequenceMatchingPreferences)
            throws IOException, IllegalArgumentException, InterruptedException, FileNotFoundException, ClassNotFoundException, SQLException {

        ObjectsDB.increaseRWCounter();
        zooActivateRead();
        ObjectsDB.decreaseRWCounter();
        SequenceFactory sequenceFactory = SequenceFactory.getInstance();
        ArrayList<String> result = new ArrayList<>();

        for (String accession : parentProteins) {
            Protein protein = sequenceFactory.getProtein(accession);
            if (protein.isCTerm(sequence, sequenceMatchingPreferences)) {
                result.add(accession);
            }
        }

        return result;
    }

    /**
     * Indicates whether the given modification can be found on the peptide. For
     * instance, 'oxidation of M' cannot be found on sequence "PEPTIDE". For the
     * inspection of protein termini and peptide terminus the proteins sequences
     * must be accessible from the sequence factory.
     *
     * @param ptm the PTM of interest
     * @param sequenceMatchingPreferences the sequence matching preferences
     *
     * @return a boolean indicating whether the given modification can be found
     * on the peptide
     *
     * @throws IOException exception thrown whenever an error occurred while
     * reading a protein sequence
     * @throws IllegalArgumentException exception thrown whenever an error
     * occurred while reading a protein sequence
     * @throws InterruptedException exception thrown whenever an error occurred
     * while reading a protein sequence
     * @throws ClassNotFoundException if a ClassNotFoundException occurs
     * @throws SQLException if an SQLException occurs
     * @throws FileNotFoundException if a FileNotFoundException occurs
     */
    public boolean isModifiable(PTM ptm, SequenceMatchingPreferences sequenceMatchingPreferences)
            throws IOException, IllegalArgumentException, InterruptedException, FileNotFoundException, ClassNotFoundException, SQLException {

        ObjectsDB.increaseRWCounter();
        zooActivateRead();
        ObjectsDB.decreaseRWCounter();
        AminoAcidPattern pattern = ptm.getPattern();

        switch (ptm.getType()) {
            case PTM.MODAA:
                int patternLength = pattern.length();
                int target = pattern.getTarget();
                if (target >= 0 && patternLength - target <= 1) {
                    return pattern.matchesIn(sequence, sequenceMatchingPreferences);
                } else {
                    SequenceFactory sequenceFactory = SequenceFactory.getInstance();
                    for (String accession : parentProteins) {
                        Protein protein = sequenceFactory.getProtein(accession);
                        for (int index : protein.getPeptideStart(sequence, sequenceMatchingPreferences)) {
                            int beginIndex = index - target - 1;
                            int endIndex = index + sequence.length() - 2 + patternLength - target;
                            if (endIndex < protein.getLength()) {
                                String tempSequence = protein.getSequence().substring(beginIndex, endIndex);
                                if (pattern.matchesIn(tempSequence, sequenceMatchingPreferences)) {
                                    return true;
                                }
                            }
                        }
                    }
                    return false;
                }
            case PTM.MODCP:
                return true;
            case PTM.MODNP:
                return true;
            case PTM.MODC:
                return !isCterm(sequenceMatchingPreferences).isEmpty();
            case PTM.MODN:
                return !isNterm(sequenceMatchingPreferences).isEmpty();
            case PTM.MODCAA:
                if (isCterm(sequenceMatchingPreferences).isEmpty()) {
                    return false;
                }
            case PTM.MODCPAA:
                patternLength = pattern.length();
                target = pattern.getTarget();
                if (target == patternLength - 1 && sequence.length() >= patternLength) {
                    return pattern.isEnding(sequence, sequenceMatchingPreferences);
                } else {
                    SequenceFactory sequenceFactory = SequenceFactory.getInstance();
                    for (String accession : parentProteins) {
                        Protein protein = sequenceFactory.getProtein(accession);
                        for (int index : protein.getPeptideStart(sequence, sequenceMatchingPreferences)) {
                            int beginIndex = index - target - 1;
                            int endIndex = index + sequence.length() - 2 + patternLength - target;
                            if (endIndex < protein.getLength()) {
                                String tempSequence = protein.getSequence().substring(beginIndex, endIndex);
                                if (pattern.isEnding(tempSequence, sequenceMatchingPreferences)) {
                                    return true;
                                }
                            }
                        }
                    }
                    return false;
                }
            case PTM.MODNAA:
                if (isNterm(sequenceMatchingPreferences).isEmpty()) {
                    return false;
                }
            case PTM.MODNPAA:
                patternLength = pattern.length();
                target = pattern.getTarget();
                if (target == 0 && sequence.length() >= patternLength) {
                    return pattern.isStarting(sequence, sequenceMatchingPreferences);
                } else {
                    SequenceFactory sequenceFactory = SequenceFactory.getInstance();
                    for (String accession : parentProteins) {
                        Protein protein = sequenceFactory.getProtein(accession);
                        for (int index : protein.getPeptideStart(sequence, sequenceMatchingPreferences)) {
                            int beginIndex = index - target - 1;
                            int endIndex = index + sequence.length() - 2 + patternLength - target;
                            if (endIndex < protein.getLength()) {
                                String tempSequence = protein.getSequence().substring(beginIndex, endIndex);
                                if (pattern.isStarting(tempSequence, sequenceMatchingPreferences)) {
                                    return true;
                                }
                            }
                        }
                    }
                    return false;
                }
            default:
                return false;
        }
    }

    /**
     * Returns the potential modification sites as an ordered list of sites. 1
     * is the first amino acid. An empty list is returned if no possibility was
     * found. This method does not account for protein terminal modifications.
     *
     * @param ptmMass the mass of the potential PTM
     * @param sequenceMatchingPreferences the sequence matching preferences for
     * peptide to protein mapping
     * @param ptmSequenceMatchingPreferences the sequence matching preferences
     * for PTM to peptide mapping
     * @param modificationProfile the modification profile of the identification
     *
     * @return a list of potential modification sites
     *
     * @throws IOException exception thrown whenever an error occurred while
     * reading a protein sequence
     * @throws IllegalArgumentException exception thrown whenever an error
     * occurred while reading a protein sequence
     * @throws InterruptedException exception thrown whenever an error occurred
     * while reading a protein sequence
     * @throws ClassNotFoundException if a ClassNotFoundException occurs
     * @throws SQLException if an SQLException occurs
     * @throws FileNotFoundException if a FileNotFoundException occurs
     */
    public ArrayList<Integer> getPotentialModificationSites(Double ptmMass, SequenceMatchingPreferences sequenceMatchingPreferences, SequenceMatchingPreferences ptmSequenceMatchingPreferences,
            PtmSettings modificationProfile) throws IOException, IllegalArgumentException, InterruptedException, FileNotFoundException, ClassNotFoundException, SQLException {

        ObjectsDB.increaseRWCounter();
        zooActivateRead();
        ObjectsDB.decreaseRWCounter();
        ArrayList<Integer> sites = new ArrayList<>();

        for (String ptmName : modificationProfile.getAllNotFixedModifications()) {
            PTM ptm = PTMFactory.getInstance().getPTM(ptmName);
            if (ptm.getMass() == ptmMass) { //@TODO: use a mass tolerance
                for (int site : getPotentialModificationSites(ptm, sequenceMatchingPreferences, ptmSequenceMatchingPreferences)) {
                    if (!sites.contains(site)) {
                        sites.add(site);
                    }
                }
            }
        }

        return sites;
    }

    /**
     * Returns the potential modification sites as an ordered list of sites. 1
     * is the first amino acid. An empty list is returned if no possibility was
     * found.
     *
     * @param ptm the PTM considered
     * @param sequenceMatchingPreferences the sequence matching preferences for
     * peptide to protein mapping
     * @param ptmSequenceMatchingPreferences the sequence matching preferences
     * for PTM to peptide mapping
     *
     * @return a list of potential modification sites
     *
     * @throws IOException exception thrown whenever an error occurred while
     * interacting with a file while mapping potential modification sites
     * @throws InterruptedException exception thrown whenever a threading issue
     * occurred while mapping potential modification sites
     * @throws ClassNotFoundException exception thrown whenever an error
     * occurred while deserializing an object from the ProteinTree
     * @throws SQLException exception thrown whenever an error occurred while
     * interacting with the ProteinTree
     */
    public ArrayList<Integer> getPotentialModificationSites(PTM ptm, SequenceMatchingPreferences sequenceMatchingPreferences, SequenceMatchingPreferences ptmSequenceMatchingPreferences)
            throws IOException, InterruptedException, ClassNotFoundException, SQLException {

        ObjectsDB.increaseRWCounter();
        zooActivateRead();
        ObjectsDB.decreaseRWCounter();
        ArrayList<Integer> possibleSites = new ArrayList<>(1);

        switch (ptm.getType()) {
            case PTM.MODAA:
                AminoAcidPattern pattern = ptm.getPattern();
                int patternLength = pattern.length();
                int target = pattern.getTarget();
                if (target >= 0 && patternLength - target <= 1) {
                    return pattern.getIndexes(sequence, ptmSequenceMatchingPreferences);
                } else {
                    SequenceFactory sequenceFactory = SequenceFactory.getInstance();
                    for (String accession : parentProteins) {
                        Protein protein = sequenceFactory.getProtein(accession);
                        for (int index : protein.getPeptideStart(sequence, sequenceMatchingPreferences)) {
                            int beginIndex = index - target - 1;
                            int endIndex = index + sequence.length() - 2 + patternLength - target;
                            if (endIndex < protein.getLength()) {
                                String tempSequence = protein.getSequence().substring(beginIndex, endIndex);
                                if (pattern.matchesIn(tempSequence, ptmSequenceMatchingPreferences)) {
                                    for (int tempIndex : pattern.getIndexes(tempSequence, ptmSequenceMatchingPreferences)) {
                                        Integer sequenceIndex = tempIndex - target;
                                        if (!possibleSites.contains(sequenceIndex)) {
                                            possibleSites.add(tempIndex);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                return possibleSites;
            case PTM.MODC:
                if (isCterm(sequenceMatchingPreferences).isEmpty()) {
                    return possibleSites;
                }
            case PTM.MODCP:
                possibleSites.add(sequence.length());
                return possibleSites;
            case PTM.MODN:
                if (isNterm(sequenceMatchingPreferences).isEmpty()) {
                    return possibleSites;
                }
            case PTM.MODNP:
                possibleSites.add(1);
                return possibleSites;
            case PTM.MODCAA:
                if (isCterm(sequenceMatchingPreferences).isEmpty()) {
                    return possibleSites;
                }
            case PTM.MODCPAA:
                pattern = ptm.getPattern();
                patternLength = pattern.length();
                target = pattern.getTarget();
                if (target == patternLength - 1 && sequence.length() >= patternLength) {
                    if (pattern.isEnding(sequence, ptmSequenceMatchingPreferences)) {
                        possibleSites.add(sequence.length());
                    }
                    return possibleSites;
                } else {
                    SequenceFactory sequenceFactory = SequenceFactory.getInstance();
                    Protein protein;
                    for (String accession : parentProteins) {
                        protein = sequenceFactory.getProtein(accession);
                        for (int index : protein.getPeptideStart(sequence, sequenceMatchingPreferences)) {
                            int beginIndex = index - target - 1;
                            int endIndex = index + sequence.length() - 2 + patternLength - target;
                            if (endIndex < protein.getLength()) {
                                String tempSequence = protein.getSequence().substring(beginIndex, endIndex);
                                if (pattern.isEnding(tempSequence, ptmSequenceMatchingPreferences)) {
                                    possibleSites.add(sequence.length());
                                    return possibleSites;
                                }
                            }
                        }
                    }
                    return possibleSites;
                }
            case PTM.MODNAA:
                if (isNterm(sequenceMatchingPreferences).isEmpty()) {
                    return possibleSites;
                }
            case PTM.MODNPAA:
                pattern = ptm.getPattern();
                patternLength = pattern.length();
                target = pattern.getTarget();
                if (target == 0 && sequence.length() >= patternLength) {
                    if (pattern.isStarting(sequence, ptmSequenceMatchingPreferences)) {
                        possibleSites.add(1);
                    }
                } else {
                    SequenceFactory sequenceFactory = SequenceFactory.getInstance();
                    Protein protein;
                    for (String accession : parentProteins) {
                        protein = sequenceFactory.getProtein(accession);
                        for (int index : protein.getPeptideStart(sequence, sequenceMatchingPreferences)) {
                            int beginIndex = index - target - 1;
                            int endIndex = index + sequence.length() - 2 + patternLength - target;
                            if (endIndex < protein.getLength()) {
                                String tempSequence = protein.getSequence().substring(beginIndex, endIndex);
                                if (pattern.isStarting(tempSequence, ptmSequenceMatchingPreferences)) {
                                    possibleSites.add(1);
                                    return possibleSites;
                                }
                            }
                        }
                    }
                }
                return possibleSites;
            default:
                throw new UnsupportedOperationException("Modification site not implemented for modification of type " + ptm.getType() + ".");
        }
    }

    /**
     * Returns the potential modification sites as an ordered list of sites. No
     * amino acid combination is tested. 1 is the first amino acid. An empty
     * list is returned if no possibility was found. No peptide to protein
     * mapping is done. The index on the protein must be provided with 0 as
     * first amino acid.
     *
     * @param ptm the PTM considered
     * @param proteinSequence the protein sequence
     * @param indexOnProtein the index of the peptide on the protein
     *
     * @return a list of potential modification sites
     */
    public ArrayList<Integer> getPotentialModificationSitesNoCombination(PTM ptm, String proteinSequence, Integer indexOnProtein) {

        ObjectsDB.increaseRWCounter();
        zooActivateRead();
        ObjectsDB.decreaseRWCounter();
        ArrayList<Integer> possibleSites = new ArrayList<>(1);

        switch (ptm.getType()) {
            case PTM.MODAA:
                AminoAcidPattern aminoAcidPattern = ptm.getPattern();
                HashSet<Character> targetedAA = aminoAcidPattern.getAminoAcidsAtTargetSet();
                if (aminoAcidPattern.length() == 1) {
                    for (int i = 0; i < sequence.length(); i++) {
                        Character aa = sequence.charAt(i);
                        if (targetedAA.contains(aa)) {
                            possibleSites.add(i + 1);
                        }
                    }
                } else {
                    for (int i = 0; i < sequence.length(); i++) {
                        Character aa = sequence.charAt(i);
                        if (targetedAA.contains(aa) && aminoAcidPattern.matchesAt(proteinSequence, SequenceMatchingPreferences.defaultStringMatching, indexOnProtein + i)) {
                            possibleSites.add(i + 1);
                        }
                    }
                }
                return possibleSites;
            case PTM.MODC:
                int peptideLength = sequence.length();
                if (indexOnProtein + peptideLength == proteinSequence.length()) {
                    possibleSites.add(peptideLength);
                }
                return possibleSites;
            case PTM.MODCP:
                possibleSites.add(sequence.length());
                return possibleSites;
            case PTM.MODN:
                if (indexOnProtein == 0) {
                    possibleSites.add(1);
                }
                return possibleSites;
            case PTM.MODNP:
                possibleSites.add(1);
                return possibleSites;
            case PTM.MODCAA:
                aminoAcidPattern = ptm.getPattern();
                targetedAA = aminoAcidPattern.getAminoAcidsAtTargetSet();
                peptideLength = sequence.length();
                if (indexOnProtein + peptideLength == proteinSequence.length()) {
                    Character aa = sequence.charAt(peptideLength - 1);
                    if (aminoAcidPattern.length() == 1) {
                        if (targetedAA.contains(aa)) {
                            possibleSites.add(peptideLength);
                        }
                    } else {
                        if (targetedAA.contains(aa) && aminoAcidPattern.matchesAt(proteinSequence, SequenceMatchingPreferences.defaultStringMatching, indexOnProtein + peptideLength)) {
                            possibleSites.add(peptideLength);
                        }
                    }
                }
                return possibleSites;
            case PTM.MODCPAA:
                aminoAcidPattern = ptm.getPattern();
                targetedAA = aminoAcidPattern.getAminoAcidsAtTargetSet();
                peptideLength = sequence.length();
                Character aa = sequence.charAt(peptideLength - 1);
                if (aminoAcidPattern.length() == 1) {
                    if (targetedAA.contains(aa)) {
                        possibleSites.add(peptideLength);
                    }
                } else {
                    if (targetedAA.contains(aa) && aminoAcidPattern.matchesAt(proteinSequence, SequenceMatchingPreferences.defaultStringMatching, indexOnProtein + peptideLength)) {
                        possibleSites.add(peptideLength);
                    }
                }
                return possibleSites;
            case PTM.MODNAA:
                aminoAcidPattern = ptm.getPattern();
                targetedAA = aminoAcidPattern.getAminoAcidsAtTargetSet();
                if (indexOnProtein == 0) {
                    aa = sequence.charAt(0);
                    if (aminoAcidPattern.length() == 1) {
                        if (targetedAA.contains(aa)) {
                            possibleSites.add(1);
                        }
                    } else {
                        if (targetedAA.contains(aa) && aminoAcidPattern.matchesAt(proteinSequence, SequenceMatchingPreferences.defaultStringMatching, 0)) {
                            possibleSites.add(1);
                        }
                    }
                }
                return possibleSites;
            case PTM.MODNPAA:
                aminoAcidPattern = ptm.getPattern();
                targetedAA = aminoAcidPattern.getAminoAcidsAtTargetSet();
                aa = sequence.charAt(0);
                if (aminoAcidPattern.length() == 1) {
                    if (targetedAA.contains(aa)) {
                        possibleSites.add(1);
                    }
                } else {
                    if (targetedAA.contains(aa) && aminoAcidPattern.matchesAt(proteinSequence, SequenceMatchingPreferences.defaultStringMatching, 0)) {
                        possibleSites.add(1);
                    }
                }
                return possibleSites;
            default:
                throw new UnsupportedOperationException("Modification site not implemented for modification of type " + ptm.getType() + ".");
        }
    }

    /**
     * Indicates whether another peptide has the same sequence and modification
     * status without accounting for modification localization.
     *
     * @param anotherPeptide the other peptide to compare to this instance
     * @param sequenceMatchingPreferences the sequence matching preferences
     *
     * @return a boolean indicating whether the other peptide has the same
     * sequence and modification status.
     */
    public boolean isSameSequenceAndModificationStatus(Peptide anotherPeptide, SequenceMatchingPreferences sequenceMatchingPreferences) {
        ObjectsDB.increaseRWCounter();
        zooActivateRead();
        ObjectsDB.decreaseRWCounter();
        return isSameSequence(anotherPeptide, sequenceMatchingPreferences) && isSameModificationStatus(anotherPeptide);
    }

    /**
     * Returns a boolean indicating whether another peptide has the same
     * sequence as the given peptide
     *
     * @param anotherPeptide the other peptide to compare
     * @param sequenceMatchingPreferences the sequence matching preferences
     *
     * @return a boolean indicating whether the other peptide has the same
     * sequence
     */
    public boolean isSameSequence(Peptide anotherPeptide, SequenceMatchingPreferences sequenceMatchingPreferences) {
        ObjectsDB.increaseRWCounter();
        zooActivateRead();
        ObjectsDB.decreaseRWCounter();
        AminoAcidSequence pattern = new AminoAcidSequence(anotherPeptide.getSequence());
        return pattern.matches(getSequence(), sequenceMatchingPreferences);
    }

    /**
     * Indicates whether another peptide has the same variable modifications as
     * this peptide. The localization of the PTM is not accounted for.
     * Modifications are considered equal when of same mass. Modifications
     * should be loaded in the PTM factory.
     *
     * @param anotherPeptide the other peptide
     * @return a boolean indicating whether the other peptide has the same
     * variable modifications as the peptide of interest
     */
    public boolean isSameModificationStatus(Peptide anotherPeptide) {

        ObjectsDB.increaseRWCounter();
        zooActivateRead();
        ObjectsDB.decreaseRWCounter();
        if (!isModified() && !anotherPeptide.isModified()) {
            return true;
        }

        if (getNModifications() != anotherPeptide.getNModifications()) {
            return false;
        }

        PTMFactory ptmFactory = PTMFactory.getInstance();
        ArrayList<String> modifications1 = getModificationFamily(getKey());
        HashMap<Double, Integer> masses1 = new HashMap<>();
        for (String modName : modifications1) {
            PTM ptm = ptmFactory.getPTM(modName);
            double tempMass = ptm.getMass();
            Integer occurrence = masses1.get(tempMass);
            if (occurrence == null) {
                masses1.put(tempMass, 1);
            } else {
                masses1.put(tempMass, occurrence + 1);
            }
        }

        ArrayList<String> modifications2 = getModificationFamily(anotherPeptide.getKey());
        HashMap<Double, Integer> masses2 = new HashMap<>();
        for (String modName : modifications2) {
            PTM ptm = ptmFactory.getPTM(modName);
            double tempMass = ptm.getMass();
            Integer occurrence = masses2.get(tempMass);
            if (occurrence == null) {
                masses2.put(tempMass, 1);
            } else {
                masses2.put(tempMass, occurrence + 1);
            }
        }

        if (masses1.size() != masses2.size()) {
            return false;
        }
        for (Double tempMass : masses1.keySet()) {
            Integer occurrence1 = masses1.get(tempMass);
            Integer occurrence2 = masses2.get(tempMass);
            if (occurrence2 == null || occurrence2.intValue() != occurrence1) {
                return false;
            }
        }

        return true;
    }

    /**
     * Indicates whether another peptide has the same modifications at the same
     * localization as this peptide. This method comes as a complement of
     * isSameAs, here the localization of all PTMs is taken into account.
     * Modifications are considered equal when of same mass. Modifications
     * should be loaded in the PTM factory.
     *
     * @param anotherPeptide another peptide
     * @param ptms the PTMs
     * @return true if the other peptide has the same positions at the same
     * location as the considered peptide
     */
    public boolean sameModificationsAs(Peptide anotherPeptide, ArrayList<String> ptms) {

        ObjectsDB.increaseRWCounter();
        zooActivateRead();
        ObjectsDB.decreaseRWCounter();
        if (!isModified() && !anotherPeptide.isModified()) {
            return true;
        }

        if (getNModifications() != anotherPeptide.getNModifications()) {
            return false;
        }

        HashMap<Double, ArrayList<Integer>> ptmToPositionsMap1 = new HashMap<>();
        HashMap<Double, ArrayList<Integer>> ptmToPositionsMap2 = new HashMap<>();
        PTMFactory ptmFactory = PTMFactory.getInstance();
        for (ModificationMatch modificationMatch : modificationMatches) {
            String modName = modificationMatch.getTheoreticPtm();
            if (ptms.contains(modName)) {
                double tempMass = ptmFactory.getPTM(modName).getMass();
                ArrayList<Integer> sites = ptmToPositionsMap1.get(tempMass);
                if (sites == null) {
                    sites = new ArrayList<>();
                    ptmToPositionsMap1.put(tempMass, sites);
                }
                int position = modificationMatch.getModificationSite();
                sites.add(position);
            }
        }
        for (ModificationMatch modificationMatch : anotherPeptide.getModificationMatches()) {
            String modName = modificationMatch.getTheoreticPtm();
            if (ptms.contains(modName)) {
                double tempMass = ptmFactory.getPTM(modName).getMass();
                ArrayList<Integer> sites = ptmToPositionsMap2.get(tempMass);
                if (sites == null) {
                    sites = new ArrayList<>();
                    ptmToPositionsMap2.put(tempMass, sites);
                }
                int position = modificationMatch.getModificationSite();
                sites.add(position);
            }
        }
        for (Double tempMass : ptmToPositionsMap1.keySet()) {
            ArrayList<Integer> sites1 = ptmToPositionsMap1.get(tempMass);
            ArrayList<Integer> sites2 = ptmToPositionsMap2.get(tempMass);
            if (sites2 == null || sites1.size() != sites2.size()) {
                return false;
            }
            Collections.sort(sites1);
            Collections.sort(sites2);
            for (int i = 0; i < sites1.size(); i++) {
                if (sites1.get(i).intValue() != sites2.get(i)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Indicates whether another peptide has the same modifications at the same
     * localization as this peptide. This method comes as a complement of
     * isSameAs, here the localization of all PTMs is taken into account.
     * Modifications are considered equal when of same mass. Modifications
     * should be loaded in the PTM factory.
     *
     * @param anotherPeptide another peptide
     * @return true if the other peptide has the same positions at the same
     * location as the considered peptide
     */
    public boolean sameModificationsAs(Peptide anotherPeptide) {

        ObjectsDB.increaseRWCounter();
        zooActivateRead();
        ObjectsDB.decreaseRWCounter();
        if (!isModified() && !anotherPeptide.isModified()) {
            return true;
        }

        if (getNModifications() != anotherPeptide.getNModifications()) {
            return false;
        }

        ArrayList<String> ptms = new ArrayList<>();
        for (ModificationMatch modificationMatch : getModificationMatches()) {
            String modName = modificationMatch.getTheoreticPtm();
            if (!ptms.contains(modName)) {
                ptms.add(modName);
            }
        }
        for (ModificationMatch modificationMatch : anotherPeptide.getModificationMatches()) {
            String modName = modificationMatch.getTheoreticPtm();
            if (!ptms.contains(modName)) {
                ptms.add(modName);
            }
        }
        return sameModificationsAs(anotherPeptide, ptms);
    }

    /**
     * Returns the N-terminal of the peptide as a String. Returns "NH2" if the
     * terminal is not modified, otherwise returns the name of the modification.
     * /!\ this method will work only if the PTM found in the peptide are in the
     * PTMFactory.
     *
     * @return the N-terminal of the peptide as a String, e.g., "NH2"
     */
    public String getNTerminal() {

        ObjectsDB.increaseRWCounter();
        zooActivateRead();
        ObjectsDB.decreaseRWCounter();
        String nTerm = "NH2";

        PTMFactory ptmFactory = PTMFactory.getInstance();

        if (modificationMatches != null) {
            for (ModificationMatch modificationMatch : modificationMatches) {
                if (modificationMatch.getModificationSite() == 1) {
                    PTM ptm = ptmFactory.getPTM(modificationMatch.getTheoreticPtm());
                    if (ptm.getType() != PTM.MODAA && ptm.getType() != PTM.MODMAX) {
                        nTerm = ptm.getShortName();
                    }
                }
            }
        }

        nTerm = nTerm.replaceAll("-", " ");
        return nTerm;
    }

    /**
     * Returns the C-terminal of the peptide as a String. Returns "COOH" if the
     * terminal is not modified, otherwise returns the name of the modification.
     * /!\ This method will work only if the PTM found in the peptide are in the
     * PTMFactory.
     *
     * @return the C-terminal of the peptide as a String, e.g., "COOH"
     */
    public String getCTerminal() {

        ObjectsDB.increaseRWCounter();
        zooActivateRead();
        ObjectsDB.decreaseRWCounter();
        String cTerm = "COOH";
        PTMFactory ptmFactory = PTMFactory.getInstance();

        if (modificationMatches != null) {
            for (int i = 0; i < modificationMatches.size(); i++) {
                if (modificationMatches.get(i).getModificationSite() == sequence.length()) {
                    PTM ptm = ptmFactory.getPTM(modificationMatches.get(i).getTheoreticPtm());
                    if (ptm.getType() != PTM.MODAA && ptm.getType() != PTM.MODMAX) {
                        cTerm = ptm.getShortName();
                    }
                }
            }
        }

        cTerm = cTerm.replaceAll("-", " ");
        return cTerm;
    }

    /**
     * Returns the modified sequence as an tagged string with potential
     * modification sites color coded or with PTM tags, e.g, &lt;mox&gt;. /!\
     * this method will work only if the PTM found in the peptide are in the
     * PTMFactory. /!\ This method uses the modifications as set in the
     * modification matches of this peptide and displays all of them.
     *
     * @param modificationProfile the modification profile of the search
     * @param useHtmlColorCoding if true, color coded HTML is used, otherwise
     * PTM tags, e.g, &lt;mox&gt;, are used
     * @param includeHtmlStartEndTags if true, start and end HTML tags are added
     * @param useShortName if true the short names are used in the tags
     * @param excludeAllFixedPtms if true, all fixed PTMs are excluded
     * @return the modified sequence as a tagged string
     */
    public String getTaggedModifiedSequence(PtmSettings modificationProfile, boolean useHtmlColorCoding, boolean includeHtmlStartEndTags, boolean useShortName, boolean excludeAllFixedPtms) {
        HashMap<Integer, ArrayList<String>> confidentModificationSites = new HashMap<>();
        HashMap<Integer, ArrayList<String>> representativeModificationSites = new HashMap<>();
        HashMap<Integer, ArrayList<String>> secondaryModificationSites = new HashMap<>();
        HashMap<Integer, ArrayList<String>> fixedModificationSites = new HashMap<>();

        ObjectsDB.increaseRWCounter();
        zooActivateRead();
        ObjectsDB.decreaseRWCounter();
        if (modificationMatches != null) {
            for (ModificationMatch modMatch : modificationMatches) {
                String modName = modMatch.getTheoreticPtm();
                int modSite = modMatch.getModificationSite();
                if (modMatch.getVariable()) {
                    if (modMatch.getConfident()) {
                        if (!confidentModificationSites.containsKey(modSite)) {
                            confidentModificationSites.put(modSite, new ArrayList<>(1));
                        }
                        confidentModificationSites.get(modSite).add(modName);
                    } else {
                        if (!representativeModificationSites.containsKey(modSite)) {
                            representativeModificationSites.put(modSite, new ArrayList<>(1));
                        }
                        representativeModificationSites.get(modSite).add(modName);
                    }
                } else if (!excludeAllFixedPtms) {
                    if (!fixedModificationSites.containsKey(modSite)) {
                        fixedModificationSites.put(modSite, new ArrayList<>(1));
                    }
                    fixedModificationSites.get(modSite).add(modName);
                }
            }
        }
        return getTaggedModifiedSequence(modificationProfile, this, confidentModificationSites, representativeModificationSites, secondaryModificationSites,
                fixedModificationSites, useHtmlColorCoding, includeHtmlStartEndTags, useShortName);
    }

    /**
     * Returns the modified sequence as an tagged string with potential
     * modification sites color coded or with PTM tags, e.g, &lt;mox&gt;. /!\
     * this method will work only if the PTM found in the peptide are in the
     * PTMFactory. /!\ This method uses the modifications as set in the
     * modification matches of this peptide and displays all of them.
     *
     * @param modificationProfile the modification profile of the search
     * @param useHtmlColorCoding if true, color coded HTML is used, otherwise
     * PTM tags, e.g, &lt;mox&gt;, are used
     * @param includeHtmlStartEndTags if true, start and end HTML tags are added
     * @param useShortName if true the short names are used in the tags
     * @return the modified sequence as a tagged string
     */
    public String getTaggedModifiedSequence(PtmSettings modificationProfile, boolean useHtmlColorCoding, boolean includeHtmlStartEndTags, boolean useShortName) {
        ObjectsDB.increaseRWCounter();
        zooActivateRead();
        ObjectsDB.decreaseRWCounter();
        return getTaggedModifiedSequence(modificationProfile, useHtmlColorCoding, includeHtmlStartEndTags, useShortName, false);
    }

    /**
     * Returns the modified sequence as an tagged string with potential
     * modification sites color coded or with PTM tags, e.g, &lt;mox&gt;. /!\
     * This method will work only if the PTM found in the peptide are in the
     * PTMFactory.
     *
     * @param modificationProfile the modification profile of the search
     * @param includeHtmlStartEndTags if true, start and end HTML tags are added
     * @param peptide the peptide to annotate
     * @param confidentModificationSites the confidently localized variable
     * modification sites in a map: aa number &gt; list of modifications (1 is
     * the first AA) (can be null)
     * @param representativeAmbiguousModificationSites the representative site
     * of the ambiguously localized variable modifications in a map: aa number
     * &gt; list of modifications (1 is the first AA) (can be null)
     * @param secondaryAmbiguousModificationSites the secondary sites of the
     * ambiguously localized variable modifications in a map: aa number &gt;
     * list of modifications (1 is the first AA) (can be null)
     * @param fixedModificationSites the fixed modification sites in a map: aa
     * number &gt; list of modifications (1 is the first AA) (can be null)
     * @param useHtmlColorCoding if true, color coded HTML is used, otherwise
     * PTM tags, e.g, &lt;mox&gt;, are used
     * @param useShortName if true the short names are used in the tags
     * @return the tagged modified sequence as a string
     */
    public static String getTaggedModifiedSequence(PtmSettings modificationProfile, Peptide peptide,
            HashMap<Integer, ArrayList<String>> confidentModificationSites, HashMap<Integer, ArrayList<String>> representativeAmbiguousModificationSites,
            HashMap<Integer, ArrayList<String>> secondaryAmbiguousModificationSites, HashMap<Integer, ArrayList<String>> fixedModificationSites,
            boolean useHtmlColorCoding, boolean includeHtmlStartEndTags, boolean useShortName) {

        if (confidentModificationSites == null) {
            confidentModificationSites = new HashMap<>(0);
        }
        if (representativeAmbiguousModificationSites == null) {
            representativeAmbiguousModificationSites = new HashMap<>(0);
        }
        if (secondaryAmbiguousModificationSites == null) {
            secondaryAmbiguousModificationSites = new HashMap<>(0);
        }
        if (fixedModificationSites == null) {
            fixedModificationSites = new HashMap<>(0);
        }

        String modifiedSequence = "";

        if (useHtmlColorCoding && includeHtmlStartEndTags) {
            modifiedSequence += "<html>";
        }

        modifiedSequence += peptide.getNTerminal() + "-";

        modifiedSequence += AminoAcidSequence.getTaggedModifiedSequence(modificationProfile, peptide.sequence, confidentModificationSites,
                representativeAmbiguousModificationSites, secondaryAmbiguousModificationSites, fixedModificationSites, useHtmlColorCoding, useShortName);

        modifiedSequence += "-" + peptide.getCTerminal();

        if (useHtmlColorCoding && includeHtmlStartEndTags) {
            modifiedSequence += "</html>";
        }

        return modifiedSequence;
    }

    /**
     * Returns the peptide modifications as a string.
     *
     * @param peptide the peptide
     * @param variablePtms if true, only variable PTMs are shown, false return
     * only the fixed PTMs
     *
     * @return the peptide modifications as a string
     */
    public static String getPeptideModificationsAsString(Peptide peptide, boolean variablePtms) {

        StringBuilder result = new StringBuilder();

        HashMap<String, ArrayList<Integer>> modMap = new HashMap<>();
        if (peptide.isModified()) {
            for (ModificationMatch modificationMatch : peptide.getModificationMatches()) {
                if ((variablePtms && modificationMatch.getVariable()) || (!variablePtms && !modificationMatch.getVariable())) {
                    if (!modMap.containsKey(modificationMatch.getTheoreticPtm())) {
                        modMap.put(modificationMatch.getTheoreticPtm(), new ArrayList<>());
                    }
                    modMap.get(modificationMatch.getTheoreticPtm()).add(modificationMatch.getModificationSite());
                }
            }
        }

        boolean first = true, first2;
        ArrayList<String> mods = new ArrayList<>(modMap.keySet());

        Collections.sort(mods);
        for (String mod : mods) {
            if (first) {
                first = false;
            } else {
                result.append(", ");
            }
            first2 = true;
            result.append(mod);
            result.append(" (");
            for (int aa : modMap.get(mod)) {
                if (first2) {
                    first2 = false;
                } else {
                    result.append(", ");
                }
                result.append(aa);
            }
            result.append(")");
        }

        return result.toString();
    }

    /**
     * Returns the indexes of the residues in the peptide that contain at least
     * one variable modification.
     *
     * @return the indexes of the modified residues
     */
    public ArrayList<Integer> getModifiedIndexes() {
        return getModifiedIndexes(true);
    }

    /**
     * Returns the indexes of the residues in the peptide that contain at least
     * one modification.
     *
     * @param excludeFixed exclude fixed PTMs
     * @return the indexes of the modified residues
     */
    public ArrayList<Integer> getModifiedIndexes(boolean excludeFixed) {
        ObjectsDB.increaseRWCounter();
        zooActivateRead();
        ObjectsDB.decreaseRWCounter();

        if (modificationMatches == null) {
            return new ArrayList<>(0);
        }

        ArrayList<Integer> modifiedResidues = new ArrayList<>(modificationMatches.size());

        PTMFactory ptmFactory = PTMFactory.getInstance();
        for (int i = 0; i < sequence.length(); i++) {
            for (int j = 0; j < modificationMatches.size(); j++) {
                PTM ptm = ptmFactory.getPTM(modificationMatches.get(j).getTheoreticPtm());
                if (ptm.getType() == PTM.MODAA && (modificationMatches.get(j).getVariable() || !excludeFixed)) {
                    if (modificationMatches.get(j).getModificationSite() == (i + 1)) {
                        modifiedResidues.add(i + 1);
                    }
                }
            }
        }

        return modifiedResidues;
    }

    /**
     * Returns an indexed map of all fixed modifications amino acid, (1 is the
     * first) &gt; list of modification names.
     *
     * @return an indexed map of all fixed modifications amino acid
     */
    public HashMap<Integer, ArrayList<String>> getIndexedFixedModifications() {
        ObjectsDB.increaseRWCounter();
        zooActivateRead();
        ObjectsDB.decreaseRWCounter();

        if (modificationMatches == null) {
            return new HashMap<>(0);
        }

        HashMap<Integer, ArrayList<String>> result = new HashMap<>(modificationMatches.size());
        for (ModificationMatch modificationMatch : modificationMatches) {
            if (!modificationMatch.getVariable()) {
                int aa = modificationMatch.getModificationSite();
                if (!result.containsKey(aa)) {
                    result.put(aa, new ArrayList<>());
                }
                result.get(aa).add(modificationMatch.getTheoreticPtm());
            }
        }
        return result;
    }

    /**
     * Estimates the theoretic mass of the peptide. The previous version is
     * silently overwritten.
     */
    public synchronized void estimateTheoreticMass() {
        ObjectsDB.increaseRWCounter();
        zooActivateRead();
        ObjectsDB.decreaseRWCounter();

        if (mass == -1) {

            double tempMass = StandardMasses.h2o.mass;
            char[] sequenceAsCharArray = sequence.toCharArray();

            for (char aa : sequenceAsCharArray) {
                AminoAcid currentAA = AminoAcid.getAminoAcid(aa);
                tempMass += currentAA.getMonoisotopicMass();
            }

            if (modificationMatches != null) {
                PTMFactory ptmFactory = PTMFactory.getInstance();
                tempMass += modificationMatches.stream().mapToDouble(modificationMatch -> ptmFactory.getPTM(modificationMatch.getTheoreticPtm()).getMass()).sum();
            }

            setMass(tempMass);
        }
    }

    /**
     * Returns the sequence of this peptide as AminoAcidPattern.
     *
     * @return the sequence of this peptide as AminoAcidPattern
     */
    public AminoAcidPattern getSequenceAsPattern() {
        return getSequenceAsPattern(sequence);
    }

    /**
     * Returns the given sequence as AminoAcidPattern.
     *
     * @param sequence the sequence of interest
     * @return the sequence as AminoAcidPattern
     */
    public static AminoAcidPattern getSequenceAsPattern(String sequence) {
        return AminoAcidPattern.getAminoAcidPatternFromString(sequence);
    }

    /**
     * Returns the sequence of this peptide as AminoAcidSequence.
     *
     * @return the sequence of this peptide as AminoAcidSequence
     */
    public AminoAcidSequence getSequenceAsAminoAcidSequence() {
        ObjectsDB.increaseRWCounter();
        zooActivateRead();
        ObjectsDB.decreaseRWCounter();
        return getSequenceAsAminoAcidSequence(sequence);
    }

    /**
     * Returns the given sequence as AminoAcidSequence.
     *
     * @param sequence the sequence of interest
     *
     * @return the sequence as AminoAcidSequence
     */
    public static AminoAcidSequence getSequenceAsAminoAcidSequence(String sequence) {
        return new AminoAcidSequence(sequence);
    }

    /**
     * Indicates whether a peptide can be derived from a decoy protein.
     *
     * @param sequenceMatchingPreferences the sequence matching preferences
     *
     * @return whether a peptide can be derived from a decoy protein
     *
     * @throws IOException exception thrown whenever an error occurred while
     * reading a protein sequence
     * @throws InterruptedException exception thrown whenever an error occurred
     * while reading a protein sequence
     * @throws ClassNotFoundException if a ClassNotFoundException occurs
     * @throws SQLException if an SQLException occurs
     */
    public boolean isDecoy(SequenceMatchingPreferences sequenceMatchingPreferences) throws IOException, InterruptedException, SQLException, ClassNotFoundException {

        ObjectsDB.increaseRWCounter();
        zooActivateRead();
        ObjectsDB.decreaseRWCounter();
        return parentProteins.stream().anyMatch(accession -> SequenceFactory.getInstance().isDecoyAccession(accession));
    }

    /**
     * Returns a version of the peptide which does not contain the inspected
     * PTMs.
     *
     * @param peptide the original peptide
     * @param ptms list of inspected PTMs
     *
     * @return a not modified version of the peptide
     *
     * @throws IOException exception thrown whenever an error occurred while
     * reading a protein sequence
     * @throws InterruptedException exception thrown whenever an error occurred
     * while reading a protein sequence
     * @throws ClassNotFoundException if a ClassNotFoundException occurs
     * @throws SQLException if an SQLException occurs
     */
    public static Peptide getNoModPeptide(Peptide peptide, ArrayList<PTM> ptms) throws IOException, SQLException, ClassNotFoundException, InterruptedException {

        Peptide noModPeptide = new Peptide(peptide.getSequence(), new ArrayList<>());
        noModPeptide.setParentProteins(peptide.getParentProteinsNoRemapping());

        if (peptide.isModified()) {
            for (ModificationMatch modificationMatch : peptide.getModificationMatches()) {
                boolean found = false;
                for (PTM ptm : ptms) {
                    if (modificationMatch.getTheoreticPtm().equals(ptm.getName())) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    noModPeptide.addModificationMatch(modificationMatch);
                }
            }
        }

        return noModPeptide;
    }
}
