package server;

import java.awt.FontFormatException;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Servidor {
	private static final int TIME_OUT = 10000;
	public static final int N_THREADS = 1;
	private static ServerSocket elSocket;
	private static Servidor elServidor;
	private FileWriter fichero;
	private PrintWriter pw = null;
	public static int numClientes;

	public Servidor() {
		try
        {
            fichero = new FileWriter("C:/Nueva carpeta/prueba.txt");
            pw = new PrintWriter(fichero);
            pw.println("Hola");

        } catch (Exception e) {
            e.printStackTrace();
        }
	}

	private ExecutorService executor = Executors.newFixedThreadPool(N_THREADS);

	public static void main(String[] args) throws IOException {
		elServidor = new Servidor();
		elServidor.runServidor();
	}

	private void runServidor() {
		int num = 0;
		try {
			System.out.print("Puerto: ");
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			int puerto = Integer.parseInt(br.readLine());
			elSocket = new ServerSocket(puerto);
			System.out.println("Servidor escuchando en puerto: " + puerto);
			
			try {
				
				Socket ss = elSocket.accept();
				PrintWriter writer = new PrintWriter(ss.getOutputStream(), true);
	            InputStream is = ss.getInputStream();
	            InputStreamReader isr = new InputStreamReader(is);
	            BufferedReader reader = new BufferedReader(isr);
	            String linea = read(reader);
	            
	            if(!linea.split(":")[0].equals("CLIENTES")) {
	            	 write(writer, "Error en el formato. Cerrando conexion");
	            	 throw new FontFormatException(linea);
	            }
	            write(writer,"OK");
	            numClientes = Integer.parseInt(linea.split(":")[1]);
	            
			}catch(Exception e){
				
			}
			for (;;) {
				Socket sThread = null;

				sThread = elSocket.accept();
				sThread.setSoTimeout(TIME_OUT);
				//System.out.println("Thread " + num + " recibe a un cliente.");
				executor.submit(new Worker(num, sThread,pw));
				num++;
			}
		} catch (Exception e) {
			//e.printStackTrace();
		}
	}
	
	private String read(BufferedReader reader) throws IOException {
        String linea = reader.readLine();
        
        return linea;
    }

    private void write(PrintWriter writer, String msg) {
        writer.println(msg);
    }
}
