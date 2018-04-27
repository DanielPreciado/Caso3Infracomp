package uniandes.edu.infracomp.caso3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * 
 * @author Daniel Preciado
 *
 */
public class ProofApp {
	
	// -----------------------------------------------------------------
	// CONSTANTS
	// -----------------------------------------------------------------
	/**
	 * Indica el PUERTO por el cual se quiere establecer la conexion
	 */
	public static final int PUERTO = 9999;

	/**
	 * Indica la direccion sobre la cual se esta ejecutando el server
	 */
	public static final String SERVIDOR = "157.253.165.9";
	
	
	// -----------------------------------------------------------------
	// ATTRIBUTES
	// -----------------------------------------------------------------
    /**
     * representacion del cliente ubicado en las unidades de distribucion
     */
    private static Generator generadorCargaCliente;
    
   
	/**
     * 
     * @param args
	 * @throws Exception 
     */
    
    public static void main(String args[]) throws Exception
    {
    	String mensajeEnviado = "";
    	String mensajeRecibido = "";
    	System.out.println("comienza la ejecucion del cliente con carga");
    	
    	String respuesta;
    	
    	/**
    	 * Es el canal usado para comunicarse con el servidor
    	 */
         Socket canal;
    	
    	/**
    	 * El flujo que envía los datos al servidor a través del socket canal
    	 */
         PrintWriter out;

    	/**
    	 * Es el flujo de donde se leen los datos que llegan del servidor a través del socket canal
    	 */
        BufferedReader in;
		try
		{
			// Conectar al servidor
			int puerto = PUERTO;
			String server = SERVIDOR;
			
			//inicializar socket
			canal = new Socket( server, puerto );
			//inicializar pOut
			out = new PrintWriter( canal.getOutputStream( ), true );
			//inicializar pIn
			in = new BufferedReader( new InputStreamReader( canal.getInputStream( ) ) );
		}
		catch( UnknownHostException e )
		{
			e.printStackTrace( );
			throw new Exception( "No fue posible establecer una conexión al servidor. " + e.getMessage( ) );
		}
		catch( IOException e )
		{
			e.printStackTrace( );
			throw new Exception( "No fue posible establecer una conexión al servidor. " + e.getMessage( ) );
		}
    	
		Scanner sc = new Scanner(System.in);
    	System.out.println("desea ingresar datos de carga personalizados, responda si o no");
  
			respuesta = sc.nextLine();

    	if(respuesta.equals("si")) 
    	{
    		System.out.println("ingrese el numero de tareas que desea generar");
    		System.out.println("cada tarea ejecuta un nuevo cliente que se comunica con el server");
    		int pNumber = sc.nextInt();
    		
    		System.out.println("ingrese el tiempo de distancia en milisegundos para las tareas ");
    		System.out.println("recuerde que si ingresa el nunmero 0 se desplegaran todas las tareas a la vez");
    		int pGap = sc.nextInt();
    		
    		System.out.println("ingreso "+ pNumber +  " tareas" + " en un intervalo de: "+ pGap+ " milisegundos");
    		mensajeEnviado = "CLIENTES:"+pNumber;
    		out.println(mensajeEnviado);
    		mensajeRecibido = in.readLine();
    		if(mensajeRecibido.equals("OK")) 
    		{
    			generadorCargaCliente = new Generator(pNumber, pGap);
    		}
    		else
    		{
    			System.out.println("el servidor no recibio de manera correcta el numero de clientes");
    		}
    		
    	}
    	else
    	{
        	int pNumberOfTask = (int) (Math.random() * 1000 ) + 1;
        	System.out.println("el numero de tareas asignado es igual a " + pNumberOfTask);
        	int pGap = (int) (Math.random() * 100 );
        	System.out.println("el numero de tareas se esta ejecutando cada " + pGap + " (milisegundos)tiempo");
       		mensajeEnviado = "CLIENTES:"+pNumberOfTask;
    		out.println(mensajeEnviado);
    		mensajeRecibido = in.readLine();
    		if(mensajeRecibido.equals("OK")) 
    		{
    			generadorCargaCliente = new Generator(pNumberOfTask, pGap);
    		}
    		else
    		{
    			System.out.println("el servidor no recibio de manera correcta el numero de clientes");
    		}
    	}

    	canal.close();
   	} 
}
