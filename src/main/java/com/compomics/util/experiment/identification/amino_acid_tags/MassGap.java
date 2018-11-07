package com.compomics.util.experiment.identification.amino_acid_tags;

import com.compomics.util.experiment.personalization.ExperimentObject;
import com.compomics.util.parameters.identification.advanced.SequenceMatchingParameters;

/**
 * An undefined mass gap.
 *
 * @author Marc
 */
public class MassGap extends ExperimentObject implements TagComponent {

    /**
     * The value of the mass gap.
     */
    private double value;
    /**
     * The value as sequence.
     */
    private String sequence = null;

    /**
     * Constructor.
     *
     * @param value the value of the mass gap
     */
    public MassGap(double value) {
        this.value = value;
    }

    /**
     * Sets the mass of the gap.
     *
     * @param value the mass of the gap
     */
    public void setMass(double value) {
        writeDBMode();
        this.value = value;
        sequence = null;
    }

    @Override
    public String asSequence() {
        readDBMode();
        
        if (sequence == null) {
        
            String valueAsString = Double.toString(value);
            StringBuilder stringBuilder = new StringBuilder(valueAsString.length() + 2);
            stringBuilder.append('<').append(valueAsString).append('>');
            sequence = stringBuilder.toString();
            
        }
        
        return sequence;
    
    }

    @Override
    public double getMass() {
        readDBMode();
        return value;
    }

    @Override
    public boolean isSameAs(TagComponent anotherCompontent, SequenceMatchingParameters sequenceMatchingPreferences) {
        readDBMode();
        return (anotherCompontent instanceof MassGap) && anotherCompontent.getMass() == value;
    }

    @Override
    public boolean isSameSequenceAndModificationStatusAs(TagComponent anotherCompontent, SequenceMatchingParameters sequenceMatchingPreferences) {
        readDBMode();
        return isSameAs(anotherCompontent, sequenceMatchingPreferences);
    }
}
