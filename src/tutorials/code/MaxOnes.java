package tutorials.code;

import ec.*;
import ec.simple.*;
import ec.util.*;
import ec.vector.*;

public class MaxOnes extends Problem implements SimpleProblemForm
{
    @Override
    public void evaluate(EvolutionState evolutionState,
                         Individual ind,
                         int subpopulation,
                         int threadnum)
    {
        if (ind.evaluated) return;   //don't evaluate the individual if it's already evaluated

        if (!(ind instanceof BitVectorIndividual))
            evolutionState.output.fatal("Whoa!  It's not a BitVectorIndividual!!!",null);

        BitVectorIndividual ind2 = (BitVectorIndividual)ind;

        int sum = 0;
        for(int x = 0; x<ind2.genome.length; x++)
            sum += (ind2.genome[x] ? 1 : 0);

        if (!(ind2.fitness instanceof SimpleFitness))
            evolutionState.output.fatal("Whoa!  It's not a SimpleFitness!!!",null);

        ((SimpleFitness)ind2.fitness).setFitness(evolutionState,
                // ...the fitness...
                //((double)sum)/ind2.genome.length,
                sum,
                ///... is the individual ideal?  Indicate here...
                sum == ind2.genome.length);

        ind2.evaluated = true;
    }
}
