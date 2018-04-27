package uniandes.edu.infracomp.caso3;

import uniandes.gload.core.LoadGenerator;
import uniandes.gload.core.Task;

public class Generator {
	
	//____________________________________________________________
	//Attributes 
	//____________________________________________________________
	
	/**
	 * 
	 */
	private LoadGenerator generator;
	
	/**
	 * 
	 */
	private int numberOfTask;
	
	/**¨
	 * 
	 */
	private int gapBetweenTask;
	
	//____________________________________________________________
	//Constructor 
	//____________________________________________________________
	
	/**
	 * 
	 */
	public Generator (int pNumberOfTask, int pGap) 
	{
		Task work = crateTask();
		numberOfTask = pNumberOfTask;
		gapBetweenTask = pGap;
		generator = new LoadGenerator("Client to server load test", numberOfTask, work, gapBetweenTask);
		System.out.println("construi el generador0");
		generator.generate();
		
		
	}
	
	//____________________________________________________________
	//methods 
	//____________________________________________________________
	
	/**
	 * 
	 */
	private Task crateTask() 
	{
		ClienteUnidadDistribucion temp = new ClienteUnidadDistribucion();
//		temp.execute();
		return temp;
	}
}
