package com.compomics.util.experiment.biology;

import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.util.ArrayList;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/**
 * This factory will provide the implemented enzymes.
 *
 * @author Marc Vaudel
 */
public class EnzymeFactory {

    /**
     * The imported enzymes.
     */
    private HashMap<String, Enzyme> enzymes = null;
    /**
     * The instance of the factory.
     */
    private static EnzymeFactory instance = null;

    /**
     * The factory constructor.
     */
    private EnzymeFactory() {
    }

    /**
     * Static method to get an instance of the factory.
     *
     * @return the factory instance
     */
    public static EnzymeFactory getInstance() {
        if (instance == null) {
            instance = new EnzymeFactory();
        }
        return instance;
    }

    /**
     * Get the imported enzymes.
     *
     * @return The enzymes as ArrayList
     */
    public ArrayList<Enzyme> getEnzymes() {
        return new ArrayList<Enzyme>(enzymes.values());
    }

    /**
     * Returns the enzyme corresponding to the given name. Null if not found.
     *
     * @param enzymeName the name of the desired enzyme
     * @return the corresponding enzyme
     */
    public Enzyme getEnzyme(String enzymeName) {
        return enzymes.get(enzymeName);
    }

    /**
     * Adds an enzyme in the factory.
     *
     * @param enzyme the new enzyme to add
     */
    public void addEnzyme(Enzyme enzyme) {
        enzymes.put(enzyme.getName(), enzyme);
    }

    /**
     * Indicates whether an enzyme is loaded in the factory.
     *
     * @param enzyme the name of the enzyme
     * @return a boolean indicating whether an enzyme is loaded in the factory
     */
    public boolean enzymeLoaded(String enzyme) {
        return enzymes.containsKey(enzyme);
    }

    /**
     * Import enzymes.
     *
     * @param enzymeFile xml file containing the enzymes
     * @throws XmlPullParserException when the parser failed
     * @throws IOException when reading the corresponding file failed
     */
    public void importEnzymes(File enzymeFile) throws XmlPullParserException, IOException {

        // Create the pull parser.
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance(System.getProperty(XmlPullParserFactory.PROPERTY_NAME), null);
        factory.setNamespaceAware(true);
        XmlPullParser parser = factory.newPullParser();
        // Create a reader for the input file.
        BufferedReader br = new BufferedReader(new FileReader(enzymeFile));
        // Set the XML Pull Parser to read from this reader.
        parser.setInput(br);
        // Start the parsing.
        int type = parser.next();

        enzymes = new HashMap<String, Enzyme>();
        // Go through the whole document.
        while (type != XmlPullParser.END_DOCUMENT) {
            // If we find a 'MSModSpec' start tag,
            // we should parse the mod.
            if (type == XmlPullParser.START_TAG && parser.getName().equals("enzyme")) {
                parseEnzyme(parser);
            }
            type = parser.next();
        }
        br.close();
    }

    /**
     * Parse one enzyme.
     *
     * @param aParser xml parser
     * @throws XmlPullParserException when the parser failed
     * @throws IOException when reading the corresponding file failed
     */
    private void parseEnzyme(XmlPullParser aParser) throws XmlPullParserException, IOException {

        // Start tag.
        aParser.nextTag();
        // Validate correctness.
        if (!aParser.getName().equals("id")) {
            throw new XmlPullParserException("Found tag '" + aParser.getName() + "' where 'id' was expected on line " + aParser.getLineNumber() + ".");
        }

        // name
        aParser.next();
        String idString = aParser.getText();
        int id;
        try {
            id = Integer.parseInt(idString.trim());
        } catch (NumberFormatException nfe) {
            throw new XmlPullParserException("Found non-parseable text '" + idString + "' for the value of the 'id' tag on line " + aParser.getLineNumber() + ".");
        }
        int type = aParser.next();
        while (!(type == XmlPullParser.START_TAG && aParser.getName().equals("name"))) {
            type = aParser.next();
        }

        // aminoAcidBefore
        aParser.next();
        String name = aParser.getText().trim();
        type = aParser.next();
        while (!(type == XmlPullParser.START_TAG && aParser.getName().equals("aminoAcidBefore"))) {
            type = aParser.next();
        }
        aParser.next();
        String aaBefore = aParser.getText().trim();

        // restrictionBefore
        type = aParser.next();
        while (!(type == XmlPullParser.START_TAG && aParser.getName().equals("restrictionBefore"))) {
            type = aParser.next();
        }
        aParser.next();
        String restrictionBefore = aParser.getText().trim();

        // aminoAcidAfter
        type = aParser.next();
        while (!(type == XmlPullParser.START_TAG && aParser.getName().equals("aminoAcidAfter"))) {
            type = aParser.next();
        }
        aParser.next();
        String aaAfter = aParser.getText().trim();

        // restrictionAfter
        type = aParser.next();
        while (!(type == XmlPullParser.START_TAG && aParser.getName().equals("restrictionAfter"))) {
            type = aParser.next();
        }
        aParser.next();
        String restrictionAfter = aParser.getText().trim();

        // semiSpecific
        type = aParser.next();
        while (!(type == XmlPullParser.START_TAG && aParser.getName().equals("semiSpecific"))) {
            type = aParser.next();
        }
        aParser.next();
        String semiSpecificAsText = aParser.getText().trim();
        boolean semiSpecific = semiSpecificAsText.equalsIgnoreCase("yes");

        // create the enzyme
        enzymes.put(name, new Enzyme(id, name, aaBefore, restrictionBefore, aaAfter, restrictionAfter, semiSpecific));
    }

    /**
     * Tries to map the enzyme name given in the PRIDE file a utilities/OMSSA
     * enzyme.
     *
     * @param prideEnzymeName the PRIDE enzyme name
     * @return the Enzyme object, or null if not mapping is found
     */
    public Enzyme getUtilitiesEnzyme(String prideEnzymeName) {

        Enzyme tempEnzyme = null;
        prideEnzymeName = prideEnzymeName.trim().toLowerCase();

        if (prideEnzymeName.equalsIgnoreCase("trypsin")) {
            tempEnzyme = EnzymeFactory.getInstance().getEnzyme("Trypsin");
        } else if (prideEnzymeName.equalsIgnoreCase("chymotrypsin")) {
            tempEnzyme = EnzymeFactory.getInstance().getEnzyme("Chymotrypsin (FYWL)");
        } else if (prideEnzymeName.equalsIgnoreCase("arg-c")
                || prideEnzymeName.equalsIgnoreCase("argc")
                || prideEnzymeName.equalsIgnoreCase("arg c")) {
            tempEnzyme = EnzymeFactory.getInstance().getEnzyme("Arg-C");
        } else if (prideEnzymeName.equalsIgnoreCase("cnbr")) {
            tempEnzyme = EnzymeFactory.getInstance().getEnzyme("CNBr");
        } else if (prideEnzymeName.equalsIgnoreCase("formic acid")) {
            tempEnzyme = EnzymeFactory.getInstance().getEnzyme("Formic Acid");
        } else if (prideEnzymeName.equalsIgnoreCase("lys-c")
                || prideEnzymeName.equalsIgnoreCase("lysc")
                || prideEnzymeName.equalsIgnoreCase("lys c")) {
            tempEnzyme = EnzymeFactory.getInstance().getEnzyme("Lys-C");
        } else if (prideEnzymeName.equalsIgnoreCase("lys-c/p")
                || prideEnzymeName.equalsIgnoreCase("lysc/p")
                || prideEnzymeName.equalsIgnoreCase("lys c/p")) { // @TODO: other ways to annotate this?
            tempEnzyme = EnzymeFactory.getInstance().getEnzyme("Lys-C, no P rule");
        } else if (prideEnzymeName.equalsIgnoreCase("pepsin a")
                || prideEnzymeName.equalsIgnoreCase("pepsin")) {
            tempEnzyme = EnzymeFactory.getInstance().getEnzyme("Pepsin A");
        } else if (prideEnzymeName.equalsIgnoreCase("trypsin + cnbr")) {
            tempEnzyme = EnzymeFactory.getInstance().getEnzyme("Trypsin + CNBr");
        } else if (prideEnzymeName.equalsIgnoreCase("trypsin + chymotrypsin")) {
            tempEnzyme = EnzymeFactory.getInstance().getEnzyme("Trypsin + Chymotrypsin ((FYWLKR))");
        } else if (prideEnzymeName.equalsIgnoreCase("trypsin, no p rule")) {
            tempEnzyme = EnzymeFactory.getInstance().getEnzyme("Trypsin, no P rule"); // @TODO: other ways to annotate this?
        } else if (prideEnzymeName.equalsIgnoreCase("whole protein")) { // @TODO: other ways to annotate this?
            tempEnzyme = EnzymeFactory.getInstance().getEnzyme("Whole Protein");
        } else if (prideEnzymeName.equalsIgnoreCase("asp-n")
                || prideEnzymeName.equalsIgnoreCase("aspn")
                || prideEnzymeName.equalsIgnoreCase("asp n")) {
            tempEnzyme = EnzymeFactory.getInstance().getEnzyme("Asp-N");
        } else if (prideEnzymeName.equalsIgnoreCase("glu-c")
                || prideEnzymeName.equalsIgnoreCase("gluc")
                || prideEnzymeName.equalsIgnoreCase("glu c")) {
            tempEnzyme = EnzymeFactory.getInstance().getEnzyme("Glu-C");
        } else if (prideEnzymeName.equalsIgnoreCase("asp-n + glu-c")) { // @TODO: other ways to annotate this?
            tempEnzyme = EnzymeFactory.getInstance().getEnzyme("Asp-N + Glu-C");
        } else if (prideEnzymeName.equalsIgnoreCase("top-down")) { // @TODO: other ways to annotate this?
            tempEnzyme = EnzymeFactory.getInstance().getEnzyme("Top-Down");
        } else if (prideEnzymeName.equalsIgnoreCase("semi-tryptic")) { // @TODO: other ways to annotate this?
            tempEnzyme = EnzymeFactory.getInstance().getEnzyme("Semi-Tryptic");
        } else if (prideEnzymeName.equalsIgnoreCase("no enzyme")) { // @TODO: other ways to annotate this?
            tempEnzyme = EnzymeFactory.getInstance().getEnzyme("No Enzyme");
        } else if (prideEnzymeName.equalsIgnoreCase("chymotrypsin, no p rule")) {
            tempEnzyme = EnzymeFactory.getInstance().getEnzyme("Chymotrypsin, no P rule (FYWL)");
        } else if (prideEnzymeName.equalsIgnoreCase("asp-n de")
                || prideEnzymeName.equalsIgnoreCase("aspn de")
                || prideEnzymeName.equalsIgnoreCase("asp n de")) { // @TODO: other ways to annotate this?
            tempEnzyme = EnzymeFactory.getInstance().getEnzyme("Asp-N (DE)");
        } else if (prideEnzymeName.equalsIgnoreCase("glu-c de")
                || prideEnzymeName.equalsIgnoreCase("gluc de")
                || prideEnzymeName.equalsIgnoreCase("glu c de")) { // @TODO: other ways to annotate this?
            tempEnzyme = EnzymeFactory.getInstance().getEnzyme("Glu-C (DE)");
        } else if (prideEnzymeName.equalsIgnoreCase("lys-n k")
                || prideEnzymeName.equalsIgnoreCase("lys-n")) { // @TODO: other ways to annotate this?
            tempEnzyme = EnzymeFactory.getInstance().getEnzyme("Lys-N (K)");
        } else if (prideEnzymeName.equalsIgnoreCase("thermolysin")) { // @TODO: other ways to annotate this?
            tempEnzyme = EnzymeFactory.getInstance().getEnzyme("Thermolysin, no P rule");
        } else if (prideEnzymeName.equalsIgnoreCase("semi-chymotrypsin")) { // @TODO: other ways to annotate this?
            tempEnzyme = EnzymeFactory.getInstance().getEnzyme("Semi-Chymotrypsin (FYWL)");
        } else if (prideEnzymeName.equalsIgnoreCase("semi glu-c")
                || prideEnzymeName.equalsIgnoreCase("semi gluc")
                || prideEnzymeName.equalsIgnoreCase("semi glu c")) { // @TODO: other ways to annotate this?
            tempEnzyme = EnzymeFactory.getInstance().getEnzyme("Semi-Glu-C");
        } else {
            // unknown/unmapped enyzyme, nothing to do...
        }

        return tempEnzyme;
    }
}
