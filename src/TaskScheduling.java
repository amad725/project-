/******************************************************************************
*  A Graduate Student		     Developed by Amad Ul Hassen, UCF
*******************************************************************************/

import java.io.*;


public class TaskScheduling extends FitnessFunction{

/*******************************************************************************
*                            INSTANCE VARIABLES                                *
*******************************************************************************/


/*******************************************************************************
*                            STATIC VARIABLES                                  *
*******************************************************************************/


/*******************************************************************************
*                              CONSTRUCTORS                                    *
*******************************************************************************/

	public TaskScheduling(){
		name = "Task Scheduling Problem";
	}

/*******************************************************************************
*                                MEMBER METHODS                                *
*******************************************************************************/

//  COMPUTE A CHROMOSOME'S RAW FITNESS *************************************
	public void doRawFitness(Chromo X){		
		int [] individualFitness = individualFitness( SequenceDecoder.decodeChromo(X));
		X.rawFitness = 0;
		for (int person = 0; person < Preferences.NUMBER_OF_PERSONS; person++)
			X.rawFitness = X.rawFitness + individualFitness[person];
	}

//  PRINT OUT AN INDIVIDUAL GENE TO THE SUMMARY FILE *********************************
	public void doPrintGenes(Chromo X, FileWriter output) throws java.io.IOException{

		for (int i=0; i<Parameters.numGenes; i++){
			Hwrite.right(X.getGeneAlpha(i),11,output);
		}
		output.write("   RawFitness");
		output.write("\n        ");
		for (int i=0; i<Parameters.numGenes; i++){
			Hwrite.right(X.getPosIntGeneValue(i),11,output);
		}
		Hwrite.right((int) X.rawFitness,13,output);
		output.write("\n\n");
		return;
	}
	

/*******************************************************************************************************
*                             STATIC METHODS                                   *
*******************************************************************************************************/

	/**************************************************************************************************
	 Calculates/Prints Number of shifts assigned as illegal, first, second, third and fourth preference
	 *********************************************************************************************** */
	public static void scheduleAnalysis(Chromo X){
		// Initializations
		int [][] assignedShifts = SequenceDecoder.decodeChromo(X);
		int shift_freq[] = new int[5];
		for (int s = 0; s < 5; s++){
			shift_freq[s] = 0;
		}
		
		//frequency calculations
		for(int person = 0; person < 7; person++){
			for(int shift = 0; shift < Preferences.MAX_SHIFTS; shift++)
				if(assignedShifts[person][shift] == 1){
					switch(Preferences.preference_vector[person][shift]){
						case Preferences.w0: 
							shift_freq[0]++;
						break;

						case Preferences.w1:
							shift_freq[1]++;
						break;

						case Preferences.w2: 
							shift_freq[2]++;
						break;

						case Preferences.w3: 
							shift_freq[3]++;
						break;

						case Preferences.w4: 
							shift_freq[4]++;
						break;
						
						default:
							System.out.println("Invalide Preference Given");
						break;
					} // switch
				} // if				
					
		}// For
		
		
		
		// Print analysis
		System.out.println(""); //Printing Analysis
		for (int s = 0; s < 5; s++){
			System.out.print(" - " + shift_freq[s]);
		}
	}

	/**************************************************************************************************
	 Calculates individual fitness by taking dot product of binary_schedule and personal preferences
	 *********************************************************************************************** */
	public static int[] individualFitness(int [][] schedule){
		int [] result = new int[Preferences.NUMBER_OF_PERSONS];               // Each element corresponds to Fitness Contribution of each person	
		for (int p = 0; p < Preferences.NUMBER_OF_PERSONS; p++){
			result[p] = 0;
			for (int shift = 0; shift < Preferences.MAX_SHIFTS; shift++){
				result[p] = result[p] + Preferences.preference_vector[p][shift] * schedule[p][shift];
				
			}
		}
		return result;
	}


	// Returns true if optimal solution to OneMax problem is found	
	public static boolean optimalityTest(Chromo X){
			return true;
			
		}


}   // End of OneMax.java ******************************************************

