/******************************************************************************
*  A Graduate Student		     Developed by Amad Ul Hassen, UCF
*******************************************************************************/
public class SequenceDecoder {
	

	public static int[][] decodeChromo(Chromo X){	
		int [][] feature_vector = new int[Preferences.NUMBER_OF_PERSONS][Preferences.MAX_SHIFTS];
		boolean [] trace_mem = new boolean[Preferences.MAX_SHIFTS];
		int     [] schedule  = new int[Preferences.MAX_SHIFTS];

		for (int g = 0 ; g < Parameters.numGenes ;  g++){
			schedule[g] = 0 ;
			
			// decoding binary value of each gene
			for (int b=0; b < Parameters.geneSize; b++){
				schedule[g] = 2 * schedule[g]; 
				if (X.chromo.charAt(Parameters.geneSize * g + b) == '1') 
					schedule[g] = schedule[g] + 1;
			}
			schedule[g] = schedule[g] % (g+1); 
		}
		
		for (int z=0; z< Preferences.MAX_SHIFTS ; z++){           
			trace_mem[z] = false;
		}

		int dec_val = 0;
		for (int index = (Preferences.MAX_SHIFTS-1); index >= 0; index--){          
			

			dec_val = 0;
			int j   = 0;
			do {            									  
				if(trace_mem[dec_val] == true)                   
					j--;

				j++;
				dec_val++;
			}
			while( j <= schedule[index]);
			trace_mem[dec_val-1] = true;
			schedule[index] = dec_val-1;
			//-------------------------------------------------------------------------------
		}
		
		
		for (int person = 0; person < Preferences.NUMBER_OF_PERSONS; person++)
			for (int shift = 0; shift < 5; shift++){
				feature_vector[person][schedule[(5*person + shift)]] = 1;
			}
		return feature_vector;
	}



}















