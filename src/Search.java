/******************************************************************************
*  A Teaching GA					  Developed by Hal Stringer & Annie Wu, UCF
*  Version 2, January 18, 2004
*******************************************************************************/

import java.io.*;
import java.util.*;
import java.text.*;

public class Search {

/*******************************************************************************
*                           INSTANCE VARIABLES                                 *
*******************************************************************************/

/*******************************************************************************
*                           STATIC VARIABLES                                   *
*******************************************************************************/

	public static FitnessFunction problem;

	public static Chromo[] member;
	public static Chromo[] child;

	public static Chromo bestOfGenChromo;
	public static int bestOfGenR;
	public static int bestOfGenG;
	public static Chromo bestOfRunChromo;
	public static int bestOfRunR;
	public static int bestOfRunG;
	public static Chromo bestOverAllChromo;
	public static int bestOverAllR;
	public static int bestOverAllG;
	
	//*************************************HomeWork1*******************************************
	private static double globalFitnessStats[][][];  // Q1-a (3xRUNSxGENS)          
	
	public static int globalBestOfRunFitness[];      // Q1-b (1x RUNS)	

	public static int maxFitnessChromoGen[];         // Q1-c (1x RUNS) 

	// Average Average Fitness and Average Best Fitness
	private static double avgBestFitness[][];        // avgBest, std_Best   
	private static double avgAvgFitness[][];         // avgAvg, std_avg          
        
	
	private final static int AVG  = 0;
	private final static int BEST = 1;
	private final static int STDV = 2;	
	
	//*****************************************************************************************

	public static double sumRawFitness;
	public static double sumRawFitness2;	// sum of squares of fitness
	public static double sumSclFitness;
	public static double sumProFitness;
	public static double defaultBest;
	public static double defaultWorst;

	public static double averageRawFitness;
	public static double stdevRawFitness;

	public static int G;
	public static int R;
	public static Random r = new Random();
	private static double randnum;

	private static int memberIndex[];
	private static double memberFitness[];
	private static int TmemberIndex;
	private static double TmemberFitness;

	private static double fitnessStats[][];     // 0=Avg, 1=Best, 2= STDV 

/*******************************************************************************
*                              CONSTRUCTORS                                    *
*******************************************************************************/


/*******************************************************************************
*                             MEMBER METHODS                                   *
*******************************************************************************/


/*******************************************************************************
*                             STATIC METHODS                                   *
*******************************************************************************/

	
	public static void main(String[] args) throws java.io.IOException{

		Calendar dateAndTime = Calendar.getInstance(); 
		Date startTime = dateAndTime.getTime();

	//  Read Parameter File
		System.out.println("\nParameter File Name is: " + args[0] + "\n");
		Parameters parmValues = new Parameters(args[0]);

		//  Read Preference File
		System.out.println("\nPreference File Name is: " + args[1] + "\n");
		Preferences prefValues = new Preferences(args[1]);
		

		
		
		
	//  Write Parameters To Summary Output File
		String summaryFileName = Parameters.expID + "_summary.txt";
		FileWriter summaryOutput = new FileWriter(summaryFileName);
		parmValues.outputParameters(summaryOutput);

	//	Set up Fitness Statistics matrix
		fitnessStats = new double[2][Parameters.generations];
		for (int i=0; i<Parameters.generations; i++){
			fitnessStats[AVG ][i] = 0;                
			fitnessStats[BEST][i] = 0;				   
		}
		
		
	
		// --------------------------------Global Fitness Statistics For HW1------------------------------
		
		globalBestOfRunFitness = new int[Parameters.numRuns];
		globalFitnessStats  = new double[3][Parameters.numRuns][Parameters.generations];
		maxFitnessChromoGen = new int[Parameters.numRuns];
		for (int run = 0; run < Parameters.numRuns; run++ ){
			globalBestOfRunFitness[run] = 0;
			maxFitnessChromoGen[run]    = -1000;     // Unrealistic values                
			for (int gen = 0; gen < Parameters.generations ; gen++){
				globalFitnessStats[AVG ][run][gen]  = 0;         // Avg_fitness of population at a particular run and generation
				globalFitnessStats[STDV][run][gen]  = 0;         // STDV of fitness from pop. AVG 
				globalFitnessStats[BEST][run][gen]  = 0;         // Best fitness encountered in pop.
				
			}
		}
		
		int test_array[] = new int[25];
		for (int i = 0; i < test_array.length; i++){
			test_array[i] = i * i * 1;
	    }
		//HW1_plots pGraph = new HW1_plots("Testing Testing Tesing", "TestX", "TestY", "New Line ", test_array );
		//pGraph.plotGraph();
		// ----------------------------------------------------------------------------------------------

	//	Problem Specific Setup - For new new fitness function problems, create
	//	the appropriate class file (extending FitnessFunction.java) and add
	//	an else_if block below to instantiate the problem.
 
		if (Parameters.problemType.equals("NM")){
				problem = new NumberMatch();
		}
		else if (Parameters.problemType.equals("OM")){
				problem = new OneMax();
		}
		else if (Parameters.problemType.equals("TS")){
			problem = new TaskScheduling();
		}
		else System.out.println("Invalid Problem Type");

		System.out.println(problem.name);

	//	Initialize RNG, array sizes and other objects
		r.setSeed(Parameters.seed);
		memberIndex = new int[Parameters.popSize];
		memberFitness = new double[Parameters.popSize];
		member = new Chromo[Parameters.popSize];
		child  = new Chromo[Parameters.popSize];
		bestOfGenChromo = new Chromo();
		bestOfRunChromo = new Chromo();
		bestOverAllChromo = new Chromo();

		if (Parameters.minORmax.equals("max")){
			defaultBest = 0;
			defaultWorst = 999999999999999999999.0;
		}
		else{
			defaultBest = 999999999999999999999.0;
			defaultWorst = 0;
		}

		bestOverAllChromo.rawFitness = defaultBest;

		//************************************************************************************//
		//                         Start of program for multiple runs
		//************************************************************************************//
		for (R = 1; R <= Parameters.numRuns; R++){

			bestOfRunChromo.rawFitness = defaultBest;
			System.out.println();

			//	Initialize First Generation
			for (int i=0; i<Parameters.popSize; i++){
				member[i] = new Chromo();
				child[i] = new Chromo();
			}

			//	Begin Each Run
			for (G=0; G<Parameters.generations; G++){

				sumProFitness = 0;
				sumSclFitness = 0;
				sumRawFitness = 0;
				sumRawFitness2 = 0;
				bestOfGenChromo.rawFitness = defaultBest;

				//	Fitness of Each Member
				for (int i=0; i<Parameters.popSize; i++){

					/////////////////////////////////////////
					member[i].rawFitness = 0;
					member[i].sclFitness = 0;
					member[i].proFitness = 0;
					// Why does he has to re-initialize these variables in every generation??
					///////////////////////////////////////////
					
					
					problem.doRawFitness(member[i]);

					sumRawFitness = sumRawFitness + member[i].rawFitness;
					sumRawFitness2 = sumRawFitness2 +
						member[i].rawFitness * member[i].rawFitness;

					if (Parameters.minORmax.equals("max")){
						if (member[i].rawFitness > bestOfGenChromo.rawFitness){
							Chromo.copyB2A(bestOfGenChromo, member[i]);
							bestOfGenR = R;
							bestOfGenG = G;
						}
						if (member[i].rawFitness > bestOfRunChromo.rawFitness){
							Chromo.copyB2A(bestOfRunChromo, member[i]);
							bestOfRunR = R;
							bestOfRunG = G;
						}
						if (member[i].rawFitness > bestOverAllChromo.rawFitness){
							Chromo.copyB2A(bestOverAllChromo, member[i]);
							bestOverAllR = R;
							bestOverAllG = G;
						}
					}
					else {
						if (member[i].rawFitness < bestOfGenChromo.rawFitness){
							Chromo.copyB2A(bestOfGenChromo, member[i]);
							bestOfGenR = R;
							bestOfGenG = G;
						}
						if (member[i].rawFitness < bestOfRunChromo.rawFitness){
							Chromo.copyB2A(bestOfRunChromo, member[i]);
							bestOfRunR = R;
							bestOfRunG = G;
						}
						if (member[i].rawFitness < bestOverAllChromo.rawFitness){
							Chromo.copyB2A(bestOverAllChromo, member[i]);
							bestOverAllR = R;
							bestOverAllG = G;
						}
					}
				}   // End of Fitness Evaluation Loop

				// Accumulate fitness statistics
				
				// AVERAGE fitness of each generation is being accumulated at corresponding index, irrespective of runs
				fitnessStats[AVG ][G] += sumRawFitness / Parameters.popSize;
				
				// BEST fitness of each generation is being accumulated at corresponding index, irrespective of runs
				fitnessStats[BEST][G] += bestOfGenChromo.rawFitness;
	
				
				averageRawFitness = sumRawFitness / Parameters.popSize;
				stdevRawFitness = Math.sqrt(
							Math.abs(sumRawFitness2 - 
							sumRawFitness*sumRawFitness/Parameters.popSize)
							/
							(Parameters.popSize-1)
							);
				
				//*******************************************************************************
				globalFitnessStats[AVG ][R-1][G] = averageRawFitness;
				globalFitnessStats[STDV][R-1][G] = stdevRawFitness;
				globalFitnessStats[BEST][R-1][G] = bestOfGenChromo.rawFitness;
				
				// Recording the birth Generation of first optimal Chromo for each run 
				//OneMax.optimalityTest(bestOfGenChromo)  -- bestOfGenChromo.rawFitness == 200
				if( (OneMax.optimalityTest(bestOfGenChromo)) && (maxFitnessChromoGen[R-1] < 0)  )
					maxFitnessChromoGen[R-1] = G; 
				//*******************************************************************************
				
				// Output generation statistics to screen
				//System.out.println(R + "\t" + G +  "\t" + (int)bestOfGenChromo.rawFitness + "\t" + averageRawFitness + "\t" + stdevRawFitness);
				//==System.out.println(R + "\t" + G +  "\t" + (int)globalFitnessStats[BEST ][R-1][G] + "\t" + globalFitnessStats[AVG ][R-1][G] + "\t" + globalFitnessStats[STDV][R-1][G]);
				
				// Output generation statistics to summary file
				summaryOutput.write(" R ");
				Hwrite.right(R, 3, summaryOutput);
				summaryOutput.write(" G ");
				Hwrite.right(G, 3, summaryOutput);
				Hwrite.right((int)bestOfGenChromo.rawFitness, 7, summaryOutput);
				Hwrite.right(averageRawFitness, 11, 3, summaryOutput);
				Hwrite.right(stdevRawFitness, 11, 3, summaryOutput);
				summaryOutput.write("\n");


		// *********************************************************************
		// **************** SCALE FITNESS OF EACH MEMBER AND SUM ***************
		// *********************************************************************

				switch(Parameters.scaleType){

				case 0:     // No change to raw fitness
					for (int i=0; i<Parameters.popSize; i++){
						member[i].sclFitness = member[i].rawFitness + .000001;
						sumSclFitness += member[i].sclFitness;
					}
					break;

				case 1:     // Fitness not scaled.  Only inverted.
					for (int i=0; i<Parameters.popSize; i++){
						member[i].sclFitness = 1/(member[i].rawFitness + .000001);
						sumSclFitness += member[i].sclFitness;
					}
					break;

				case 2:     // Fitness scaled by Rank (Maximizing fitness)

					//  Copy genetic data to temp array
					for (int i=0; i<Parameters.popSize; i++){
						memberIndex[i] = i;
						memberFitness[i] = member[i].rawFitness;
					}
					//  Bubble Sort the array by floating point number
					for (int i=Parameters.popSize-1; i>0; i--){
						for (int j=0; j<i; j++){
							if (memberFitness[j] > memberFitness[j+1]){
								TmemberIndex = memberIndex[j];
								TmemberFitness = memberFitness[j];
								memberIndex[j] = memberIndex[j+1];
								memberFitness[j] = memberFitness[j+1];
								memberIndex[j+1] = TmemberIndex;
								memberFitness[j+1] = TmemberFitness;
							}
						}
					}
					//  Copy ordered array to scale fitness fields
					for (int i=0; i<Parameters.popSize; i++){
						member[memberIndex[i]].sclFitness = i;
						sumSclFitness += member[memberIndex[i]].sclFitness;
					}

					break;

				case 3:     // Fitness scaled by Rank (minimizing fitness)

					//  Copy genetic data to temp array
					for (int i=0; i<Parameters.popSize; i++){
						memberIndex[i] = i;
						memberFitness[i] = member[i].rawFitness;
					}
					//  Bubble Sort the array by floating point number
					for (int i=1; i<Parameters.popSize; i++){
						for (int j=(Parameters.popSize - 1); j>=i; j--){
							if (memberFitness[j-i] < memberFitness[j]){
								TmemberIndex = memberIndex[j-1];
								TmemberFitness = memberFitness[j-1];
								memberIndex[j-1] = memberIndex[j];
								memberFitness[j-1] = memberFitness[j];
								memberIndex[j] = TmemberIndex;
								memberFitness[j] = TmemberFitness;
							}
						}
					}
					//  Copy array order to scale fitness fields
					for (int i=0; i<Parameters.popSize; i++){
						member[memberIndex[i]].sclFitness = i;
						sumSclFitness += member[memberIndex[i]].sclFitness;
					}

					break;

				default:
					System.out.println("ERROR - No scaling method selected");
				}


		// *********************************************************************
		// ****** PROPORTIONALIZE SCALED FITNESS FOR EACH MEMBER AND SUM *******
		// *********************************************************************

				for (int i=0; i<Parameters.popSize; i++){
					member[i].proFitness = member[i].sclFitness/sumSclFitness;
					sumProFitness = sumProFitness + member[i].proFitness;
				}

		// *********************************************************************
		// ************ CROSSOVER AND CREATE NEXT GENERATION *******************
		// *********************************************************************

				int parent1 = -1;
				int parent2 = -1;

				//  Assumes always two offspring per mating
				for (int i=0; i<Parameters.popSize; i=i+2){

					//	Select Two Parents
					parent1 = Chromo.selectParent();
					parent2 = parent1;
					while (parent2 == parent1){
						parent2 = Chromo.selectParent();
					}

					//	Crossover Two Parents to Create Two Children
					randnum = r.nextDouble();
					if (randnum < Parameters.xoverRate){
						Chromo.mateParents(parent1, parent2, member[parent1], member[parent2], child[i], child[i+1]);
					}
					else {
						Chromo.mateParents(parent1, member[parent1], child[i]);
						Chromo.mateParents(parent2, member[parent2], child[i+1]);
					}
				} // End Crossover

				//	Mutate Children
				for (int i=0; i<Parameters.popSize; i++){
					child[i].doMutation();
				}

				//	Swap Children with Last Generation
				for (int i=0; i<Parameters.popSize; i++){
					Chromo.copyB2A(member[i], child[i]);
				}

			} //  End of Generation Loop

			Hwrite.left(bestOfRunR, 4, summaryOutput);
			Hwrite.right(bestOfRunG, 4, summaryOutput);

			problem.doPrintGenes(bestOfRunChromo, summaryOutput);
			globalBestOfRunFitness[R-1] = (int)bestOfRunChromo.rawFitness;
			System.out.println("Best of Run Fitness " + globalBestOfRunFitness[R-1]);
			System.out.println(R + "\t" + "B" + "\t"+ (int)bestOfRunChromo.rawFitness);
			TaskScheduling.scheduleAnalysis(bestOfRunChromo);

		} //End of a Run

		Hwrite.left("B", 8, summaryOutput);

		problem.doPrintGenes(bestOverAllChromo, summaryOutput);

		//	Output Fitness Statistics matrix
		// (Average performance at same generation index for 50 runs)
		summaryOutput.write("Gen                 AvgFit              BestFit \n");
		for (int i=0; i<Parameters.generations; i++){
			Hwrite.left(i, 15, summaryOutput);
			
			// (Average Average Fitness) and (Average Best Fitness) over 100 Generations
			// Can be used for Q-1bb
			Hwrite.left(fitnessStats[AVG ][i]/Parameters.numRuns, 20, 2, summaryOutput);
			Hwrite.left(fitnessStats[BEST][i]/Parameters.numRuns, 20, 2, summaryOutput);
			//summaryOutput.write("Test As Well\n");
			summaryOutput.write("\n");
			
		}
		
		String specs = "Population = " + Parameters.popSize + ", Mutation Rate = "+ Parameters.mutationRate + ", CrossOver Rate = " + Parameters.xoverRate + ", Selection = " + Parameters.selectType + ", Optimal Individual Found = "+ optimalIndvStats();
		
		//calAvgBestFitness();
		//HW1_plots Q1aa = new HW1_plots("Average of Best Fitness", "Generations", "Avg of Best Fitness", specs,  avgBestFitness[0]) ;
		//Q1aa.plotGraph();

		//HW1_plots Q1ab = new HW1_plots("ST-DEV of Average of Best Fitness", "Generations", "Stdv of Avg of Best Fitness", specs, avgBestFitness[1]) ;
		//Q1ab.plotGraph();

		//calAvgAvgFitness();
		//HW1_plots Q1ba = new HW1_plots("Average of Average Fitness", "Generations", "Avg of Avg Fitness",specs,  avgAvgFitness[0]) ;
		//Q1ba.plotGraph();
		
		//HW1_plots Q1bb = new HW1_plots("ST-DEV of Average of Average Fitness", "Generations", "Std of Avg of Avg Fitness", specs, avgAvgFitness[1]) ;
		//Q1bb.plotGraph();
		

		
		avgOfBestOfRunFtiness();
		optimalIndvStats();
		System.out.println(specs);
		
		
		
		summaryOutput.write("\n");
		summaryOutput.close();

		System.out.println();
		System.out.println("Start:  " + startTime);
		dateAndTime = Calendar.getInstance(); 
		Date endTime = dateAndTime.getTime();
		System.out.println("End  :  " + endTime);
		
		

	} // End of Main Class
	
	private static void calAvgAvgFitness(){
		double avgAvg;		
		avgAvgFitness = new double[2][Parameters.generations];
		for (int g = 0; g < Parameters.generations; g++){
			avgAvgFitness[AVG][g] = 0 ;
			avgAvgFitness[1][g]   = 0 ;
			avgAvg = fitnessStats[AVG ][g]/Parameters.numRuns;
			for (int r = 0; r < Parameters.numRuns; r++){
				avgAvgFitness[0][g] += globalFitnessStats[AVG][r][g]; 
				avgAvgFitness[1][g] += (avgAvg - globalFitnessStats[AVG][r][g])*(avgAvg - globalFitnessStats[AVG][r][g]);
			}
			avgAvgFitness[0][g] /= Parameters.numRuns;
			avgAvgFitness[1][g] /= (Parameters.numRuns-1);
			avgAvgFitness[1][g] = Math.sqrt(avgAvgFitness[1][g]);

		}
	}
	
	private static void calAvgBestFitness(){
		double avgBest;
		avgBestFitness = new double[2][Parameters.generations];		
		for (int g = 0; g < Parameters.generations; g++){
			avgBestFitness[0][g] = 0 ;
			avgBestFitness[1][g] = 0 ;
			avgBest = fitnessStats[BEST][g]/Parameters.numRuns;
			for (int r = 0; r < Parameters.numRuns; r++){
				avgBestFitness[0][g] += globalFitnessStats[BEST][r][g]; 
				avgBestFitness[1][g] += (avgBest - globalFitnessStats[BEST][r][g])*(avgBest - globalFitnessStats[BEST][r][g]);

			}
			avgBestFitness[0][g] /= Parameters.numRuns;
			avgBestFitness[1][g] /= (Parameters.numRuns-1);
			avgBestFitness[1][g] = Math.sqrt(avgBestFitness[1][g]);
		}	
	}
	
	private static void avgOfBestOfRunFtiness(){
		double avg  = 0;
		double stdv = 0;

		for (int r= 0; r < Parameters.numRuns; r++){
			avg += globalBestOfRunFitness[r];
		}
		avg /= Parameters.numRuns;
		
		for (int r= 0; r < Parameters.numRuns; r++){
			stdv += (avg - globalBestOfRunFitness[r])*(avg - globalBestOfRunFitness[r]);
		}
		stdv /= (Parameters.numRuns-1);
		stdv = Math.sqrt(stdv);

		System.out.println("Average of Best of RUN fitness " + avg );
		System.out.println("Standard Deviation of Best of RUN fitness " + stdv );
		System.out.println("95 Percent Confidence interval is from " + (avg - stdv)+ " to " + (avg + stdv)  );

		
	}

	private static boolean optimalIndvStats(){
		boolean optimality = false;
		double avgGen = 0, stdv = 0, optimalRuns = 0;
		for (int r = 0; r < Parameters.numRuns; r++){
			if(maxFitnessChromoGen[r] > 0){
				System.out.println("Optimal Individual Found in Run = " + (r+1) + " Generation " + maxFitnessChromoGen[r]);
				optimality = true;
				optimalRuns++;
				avgGen += maxFitnessChromoGen[r];
			}
		}
		if( optimality ){
			
			// Average
			avgGen /= optimalRuns;

			// Standard Deviation
			for (int r = 0; r < Parameters.numRuns; r++){
				if(maxFitnessChromoGen[r] > 0){
					stdv += (maxFitnessChromoGen[r] - avgGen) * (maxFitnessChromoGen[r] -avgGen);
				}
			}
			stdv /= optimalRuns;
			stdv = Math.sqrt(stdv);
			
			//Printing Results
			System.out.println("\nOptimal Individual Found in " + optimalRuns + " out of " + Parameters.numRuns + " runs" );
			System.out.println("\nAverage Generation for Optimal Individual = " + avgGen);
			System.out.println("\nStandard Deviation of Generation Optimal Individual  " + stdv);
			System.out.println("\n95 Percent Confidence interval of Optimal Generation Number is from  " + (avgGen - stdv) + " to " + (stdv+ avgGen));		
		}
		else
			System.out.println("No Optimal Individual Found");
		return optimality;
	}

}   // End of Search.Java ******************************************************

