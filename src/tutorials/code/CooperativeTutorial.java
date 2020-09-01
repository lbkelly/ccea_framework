package tutorials.code;

import ec.EvolutionState;
import ec.Individual;
import ec.Population;
import ec.Problem;
import ec.coevolve.GroupedProblemForm;
import ec.simple.SimpleFitness;
import ec.vector.BitVectorIndividual;
import ec.vector.DoubleVectorIndividual;

import java.util.ArrayList;

public class CooperativeTutorial extends Problem implements GroupedProblemForm {
    @Override
    public void preprocessPopulation(EvolutionState state,
                                     Population pop,
                                     boolean[] prepareForAssessment,
                                     boolean countVictoriesOnly) {
        for (int i = 0; i < pop.subpops.size(); i++) {
            if (prepareForAssessment[i]) {
                for (int j = 0; j < pop.subpops.get(i).individuals.size(); j++) {
                    SimpleFitness fit = (SimpleFitness) (pop.subpops.get(i).individuals.get(j).fitness);
                    fit.trials = new ArrayList();
                }
            }
        }
    }

    @Override
    public int postprocessPopulation(EvolutionState state,
                                     Population pop,
                                     boolean[] assessFitness,
                                     boolean countVictoriesOnly) {
        for (int i = 0; i < pop.subpops.size(); i++)
            if (assessFitness[i]) for (int j = 0; j < pop.subpops.get(i).individuals.size(); j++)
                if (!pop.subpops.get(i).individuals.get(j).evaluated) {
                    SimpleFitness fit = (SimpleFitness) (pop.subpops.get(i).individuals.get(j).fitness);

                    // Let’s set the fitness to the maximum of the trials
                    int len = fit.trials.size();
                    double max = Double.NEGATIVE_INFINITY;
                    for (int l = 0; l < len; l++)
                        max = Math.max(max, ((Double) (fit.trials.get(l))).doubleValue());

                    fit.setFitness(state, max, false);
                    pop.subpops.get(i).individuals.get(j).evaluated = true;
                }
        return 0;
    }


    @Override
    public void evaluate(EvolutionState state,
                         Individual[] ind,
                         boolean[] updateFitness,
                         boolean countVictoriesOnly,
                         int[] subpops,
                         int threadnum) {
        // First compute the value of this trial.  For example, imagine if each Individual was a
        // BitVector, and the fitness function was simply their sum.
        int sum = 0;
        for (int i = 0; i < ind.length; i++) {
            BitVectorIndividual bitVectorind = (BitVectorIndividual) (ind[i]);
            for (int j = 0; j < bitVectorind.genome.length; j++) {
                sum += (bitVectorind.genome[j] ? 1 : 0);
            }
        }
        // next update each Individual to reflect this trial result
        for (int i = 0; i < ind.length; i++) {
            if (updateFitness[i])  // need to include this trial for this Individual
            {
                // We presume that the best trial found so far has been stored at
                // position 0 of the trials array.  This makes it O(1) to determine if
                // we’re the new "best trial".  Heeeere we go!
                ArrayList tr = ind[i].fitness.trials;
                if (tr.size() == 0)  // we’re the first trial to be performed so far
                {
                    tr.add(new Double(sum));  // just dump us in
                    ind[i].fitness.setContext(ind, i);  // set the context to us
                } else if (((Double) (tr.get(0))).doubleValue() < sum)  // we’re the new best
                {
                    // swap us to the 0th position
                    Double t = (Double) (tr.get(0));
                    tr.set(0, new Double(sum));    // I’m at 0
                    tr.add(t);     // move the old guy to the end of the line
                    ind[i].fitness.setContext(ind, i);  // set the context to us
                }
            }
            // don’t bother setting the fitness here
        }
    }
}

