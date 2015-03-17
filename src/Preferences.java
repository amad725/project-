import java.io.BufferedReader;
import java.io.FileReader;
/*********************************************************************************************************
 * Amad Ul Hassen--(11th March 2015) -- HW2-CAP5512
 * *******************************************************************************************************/

/*
 * This class stores preferences of each person in a 7 by 35 dimensional Matrix(2 dimensional static array)
 * Additionally, this class also calculates
 * */

public class Preferences {
	
	public static final int MAX_SHIFTS = 35;
	public static final int NUMBER_OF_PERSONS = 7;	
	public final static int w1 = 100;
	public final static int w2 = 50;
	public final static int w3 = 25;
	public final static int w4 = 1;
	public final static int w0 = -3400;

	// Following vector encodes preference of shifts number, index at 6 corresponds to First shift of day 2
	public static int[][] preference_vector = new int[NUMBER_OF_PERSONS][MAX_SHIFTS]; 
	
	
	/**********************************************************************************************************
	 Reads Preference file and stores personal preference for each shift in a static vector(preference_vector)
	 ********************************************************************************************************** */
	public Preferences(String prefFileName) throws java.io.IOException{
		
		System.out.println("Input Preference File Name was "+prefFileName );
		BufferedReader parmInput = new BufferedReader(new FileReader (prefFileName));
		String line = parmInput.readLine(); 
		
		
		for (int person = 0; person < NUMBER_OF_PERSONS; person++){

			for (int shift = 0; shift < 5; shift++){
				line = parmInput.readLine();               // read line for shift
				for (int day = 0; day < 7; day++){
					preference_vector[person][5*day + shift] = Integer.parseInt(line.substring(2*day,2*day + 1));
					
					
					switch(preference_vector[person][5*day + shift]){
						case 0: 
							preference_vector[person][5*day + shift] = w0;
						break;

						case 1:
							preference_vector[person][5*day + shift] = w1;
						break;

						case 2: 
							preference_vector[person][5*day + shift] = w2;
						break;

						case 3: 
							preference_vector[person][5*day + shift] = w3;
						break;

						case 4: 
							preference_vector[person][5*day + shift] = w4;
						break;
						
						default:
							System.out.println("Invalide Preference Given");
						break;
					}
				}
			}
			line = parmInput.readLine();                   // line has string containing persons name
		}
		
		
		for (int test = 0; test < 35; test++)
			System.out.print(" "+preference_vector[0][test]);
			
		parmInput.close();

	}
	
	
	

	
}
