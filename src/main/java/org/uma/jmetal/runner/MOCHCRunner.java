//  MOCHCRunner.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//       Juan J. Durillo <durillo@lcc.uma.es>
//
//  Copyright (c) 2011 Antonio J. Nebro, Juan J. Durillo
//
//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
// 
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

package org.uma.jmetal.runner;

import org.uma.jmetal.core.Algorithm;
import org.uma.jmetal.core.Operator;
import org.uma.jmetal.core.Problem;
import org.uma.jmetal.core.SolutionSet;
import org.uma.jmetal.metaheuristic.mochc.MOCHC;
import org.uma.jmetal.operator.crossover.CrossoverFactory;
import org.uma.jmetal.operator.mutation.MutationFactory;
import org.uma.jmetal.operator.selection.SelectionFactory;
import org.uma.jmetal.problem.ZDT.ZDT5;
import org.uma.jmetal.util.Configuration;
import org.uma.jmetal.util.JMetalException;

import java.io.IOException;
import java.util.HashMap;

/**
 * This class executes the algorithm described in:
 * A.J. Nebro, E. Alba, G. Molina, F. Chicano, F. Luna, J.J. Durillo
 * "Optimal antenna placement using a new multi-objective chc algorithm".
 * GECCO '07: Proceedings of the 9th annual conference on Genetic and
 * evolutionary computation. London, England. July 2007.
 */
public class MOCHCRunner {

  public static void main(String[] args) throws IOException, JMetalException, ClassNotFoundException {
    Problem problem = new ZDT5("Binary");

    Algorithm algorithm = null;
    algorithm = new MOCHC();
    algorithm.setProblem(problem);

    algorithm.setInputParameter("initialConvergenceCount", 0.25);
    algorithm.setInputParameter("preservedPopulation", 0.05);
    algorithm.setInputParameter("convergenceValue", 3);
    algorithm.setInputParameter("populationSize", 100);
    algorithm.setInputParameter("maxEvaluations", 25000);

    Operator crossoverOperator;
    Operator mutationOperator;
    Operator parentsSelection;
    Operator newGenerationSelection;

    // Crossover operator
    HashMap<String, Object> crossoverParameters = new HashMap<String, Object>();
    crossoverParameters.put("probability", 1.0);
    crossoverOperator = CrossoverFactory.getCrossoverOperator("HUXCrossover", crossoverParameters);

    HashMap<String, Object> selectionParameters = null; // FIXME: why we are passing null?
    parentsSelection =
      SelectionFactory.getSelectionOperator("RandomSelection", selectionParameters);

    HashMap<String, Object> newSelectionParameters = new HashMap<String, Object>();
    newSelectionParameters.put("problem", problem);
    newGenerationSelection =
      SelectionFactory.getSelectionOperator("RankingAndCrowdingSelection", newSelectionParameters);

    // Mutation operator
    HashMap<String, Object> mutationParameters = new HashMap<String, Object>();
    mutationParameters.put("probability", 0.35);
    mutationOperator = MutationFactory.getMutationOperator("BitFlipMutation", mutationParameters);

    algorithm.addOperator("crossover", crossoverOperator);
    algorithm.addOperator("cataclysmicMutation", mutationOperator);
    algorithm.addOperator("parentSelection", parentsSelection);
    algorithm.addOperator("newGenerationSelection", newGenerationSelection);

    // Execute the Algorithm
    long initTime = System.currentTimeMillis();
    SolutionSet population = algorithm.execute();
    long estimatedTime = System.currentTimeMillis() - initTime;
    Configuration.logger_.info("Total execution time: " + estimatedTime);

    // Print results
    population.printVariablesToFile("VAR");
    population.printObjectivesToFile("FUN");
  }
}