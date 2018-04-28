package uniandes.edu.infracomp.caso3;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.Security;
import java.security.SignatureException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.KeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;
import java.util.logging.Logger;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.security.auth.x500.X500Principal;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.asn1.eac.PublicKeyDataObject;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.ExtendedKeyUsage;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x509.X509Extensions;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.util.PublicKeyFactory;
import org.bouncycastle.x509.X509V3CertificateGenerator;

import uniandes.gload.core.Task;


/**
 * 
 * @author Daniel Preciado
 *
 */

public class ClienteUnidadDistribucion extends Task {

	// -----------------------------------------------------------------
	// Constantes
	// -----------------------------------------------------------------

	/**
	 * Indica el PUERTO por el cual se quiere establecer la conexion
	 */
	public static final int PUERTO = 5000;

	/**
	 * Indica la direccion sobre la cual se esta ejecutando el server
	 */
	public static final String SERVIDOR = "157.253.225.147";

	/**
	 * tamanio de llave utilizado
	 */
	public static final int KEY_SIZE = 1024;



	// -----------------------------------------------------------------
	// Comandos
	// -----------------------------------------------------------------

	/**
	 * Indica un comando del protocolo de comunicacion
	 */
	public static final String HOLA = "HOLA";

	/**
	 * Indica un comando del protocolo de comunicacion
	 */
	public static final String INICIO = "INICIO";

	/**
	 * Indica un comando del protocolo de comunicacion
	 */
	public static final String LISTA_ALGORITMOS = "ALGORITMOS:AES:RSA:HMACSHA256";

	/**
	 * Indica un comando del protocolo de comunicacion
	 */
	public static final String ESTADO_OK = "ESTADO:OK";

	/**
	 * Indica un comando del protocolo de comunicacion
	 */
	public static final String OK = "OK";

	/**
	 * Indica un comando del protocolo de comunicacion
	 */
	public static final String ESTADO_ERROR = "ESTADO:ERROR";

	/**
	 * Indica un comando del protocolo de comunicacion
	 */
	public static final String CERTIFICADO_CLIENTE = "CERTCLNT";

	/**
	 * Indica un comando del protocolo de comunicacion
	 */
	public static final String CERTIFICADO_SERVIDOR = "CERTSRV";

	/**
	 * Indica un comando del protocolo de comunicacion
	 */
	public static final String ALGORITMO = "RSA";

	/**
	 * Indica un comando del protocolo de comunicacion
	 */
	public static final String ACT1 =  "ACT1";

	/**
	 * Indica un comando del protocolo de comunicacion
	 */
	public static final String ACT2 =  "ACT2";




	// -----------------------------------------------------------------
	// Atributos
	// -----------------------------------------------------------------

	/**
	 * La última dirección de servidor al que se conectó
	 */
	private String servidor;

	/**
	 * ultimo mensaje recibido
	 */
	private String mensajeRecibido;

	/**
	 * ultimo mensaje enviado
	 */
	private String mensajeEnviado;

	/**
	 * El último puerto usado para conectarse
	 */
	private int puerto;

	/**
	 * Es el canal usado para comunicarse con el servidor
	 */
	private Socket canal;

	/**
	 * El flujo que envía los datos al servidor a través del socket canal
	 */
	private PrintWriter out;

	/**
	 * Es el flujo de donde se leen los datos que llegan del servidor a través del socket canal
	 */
	private BufferedReader in;

	/**
	 * par de llaves donde se almecenan las llaves del cliente
	 */
	private KeyPair keyPair;

	/**
	 * llave simetrica
	 */
	private SecretKey key;



	// -----------------------------------------------------------------
	// Constructores
	// -----------------------------------------------------------------

	/**
	 * Inicializa el cliente
	 */
	public ClienteUnidadDistribucion( )
	{
		servidor = SERVIDOR;
		puerto = PUERTO;

	}

	// -----------------------------------------------------------------
	// Métodos
	// -----------------------------------------------------------------

	/**
	 * Retorna la dirección del servidor
	 * @return servidor
	 */
	public String darDireccionServidor( )
	{
		return servidor;
	}

	/**
	 * Retorna el ultimo mensaje enviado por del servidor
	 * @return ultimoMensajeRecibido
	 */
	public String darUltimoMensajeRecibido( )
	{
		return mensajeRecibido;
	}

	/**
	 * Retorna el ultimo mensaje enviado por del cliente
	 * @return ultimoMensajeEnviado
	 */
	public String darUltimoMensajeEnviado( )
	{
		return mensajeEnviado;
	}

	/**
	 * Retorna el puerto usado para conectarse al servidor
	 * @return puerto
	 */
	public int darPuertoServidor( )
	{
		return puerto;
	}

	/**
	 * Establece una conexión con el servidor del juego y envía los datos del jugador para poder empezar un juego<br>
	 * Este método termina cuando se consigue un oponente y se establece la conexión entre los dos jugadores.
	 * @param nom El nombre del jugador local - nombre != null
	 * @param dirServ La dirección usada para encontrar el servidor - direccionServidor != null
	 * @param puertoServ El puerto usado para realizar la conexión - puertoServidor > 0
	 * @throws BatallaNavalException Se lanza esta excepción si hay problemas estableciendo la comunicación
	 */
	public void conectar( ) throws Exception
	{
		try
		{
			// Conectar al servidor
			canal = new Socket( servidor, puerto );
			out = new PrintWriter( canal.getOutputStream( ), true );
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
	}

	/***
	 * 
	 * @throws IOException
	 * @throws CertificateException
	 */
	public void comunicarse() throws IOException, CertificateException 
	{
		mensajeEnviado = HOLA;
		out.println(mensajeEnviado);
		mensajeRecibido = in.readLine();
		if(mensajeRecibido.equals(INICIO))
		{
//			System.out.println("recibi respuesta del servidor");
//			System.out.println(mensajeRecibido);

			mensajeEnviado = LISTA_ALGORITMOS;
			out.println(mensajeEnviado);
			mensajeRecibido = in.readLine();
//			System.out.println(mensajeRecibido);
			if(mensajeRecibido.equals(ESTADO_OK)) 
			{
				mensajeEnviado = CERTIFICADO_CLIENTE;
				out.println(mensajeEnviado);
				X509Certificate certificado = certificado();
				byte[] myByte = certificado.getEncoded();
				canal.getOutputStream().write(myByte);
				canal.getOutputStream().flush();

				mensajeRecibido = in.readLine();
//				System.out.println(mensajeRecibido +"comenzando recepcion de certficado del server");
				if(mensajeRecibido.equals(ESTADO_OK)) 
				{
					mensajeRecibido = in.readLine();
//					System.out.println(mensajeRecibido + " CertificadoServer");
					if(mensajeRecibido.equals(CERTIFICADO_SERVIDOR)) 
					{
//						System.out.println("certificado del server recepcion flujo "+ mensajeRecibido);
						CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
						X509Certificate cert = (X509Certificate)certFactory.generateCertificate(canal.getInputStream());
//						System.out.println(cert.getPublicKey());
						try 
						{
							cert.verify(cert.getPublicKey());
						} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchProviderException
								| SignatureException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						mensajeEnviado = ESTADO_OK;
						out.println(mensajeEnviado);

						mensajeRecibido = in.readLine();
						if(mensajeRecibido.equals(INICIO)) 
						{
//							System.out.println("recibi el mensaje de inicio con el codigo separado por :");
//							String cod = mensajeRecibido.split(":")[1];
//							byte[] cipheredText = transformarDecodificar(cod);
//							descifrar(cipheredText);
//							cod = "41 24.2028, 2 10.4418";
//							cipheredText = cifrar(cod);
//							System.out.println(cipheredText+ "public key");
							long initialTime = System.currentTimeMillis();
							out.println(ACT1);
							
							
							out.println(ACT2);

							mensajeRecibido = in.readLine();
							long  finalTime = System.currentTimeMillis();
//							System.out.println(mensajeRecibido);
							long totalTime = finalTime - initialTime;
							System.out.println( totalTime );



						}
					}
					else
					{
//						System.out.println("no recibi la respuesta correcta del servidor"+ "flujo de bytes de envio de cert");
						System.out.println(ESTADO_ERROR);
					}

				}	    		
				else 
				{
//					System.out.println("no recibi la respuesta correcta del servidor"+ "PROTOCOLO de envio de cert");
					System.out.println(ESTADO_ERROR);
				}

			}
			else 
			{
//				System.out.println("no recibi la respuesta correcta del servidor");
				System.out.println(ESTADO_ERROR);
			}
		}
		else
		{
			System.out.println("no recibi respuesta del servidor");
		}

	}

	/**
	 * 
	 * @param pATransformar
	 * @return
	 */
	public static byte[] transformarDecodificar(String pATransformar)
	{
		byte[] ret = new byte[pATransformar.length() / 2];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = ((byte)Integer.parseInt(pATransformar.substring(i * 2, (i + 1) * 2), 16));
		}
		return ret;
	}

	/**
	 * 
	 * @param b
	 * @return
	 */
	public static String transformarCodificar(byte[] b)
	{
		String ret = "";
		for (int i = 0; i < b.length; i++)
		{
			String g = Integer.toHexString((char)b[i] & 0xFF);
			ret = ret + (g.length() == 1 ? "0" : "") + g;
		}
		return ret;
	}

	/**
	 * 
	 * @return
	 */
	public byte[] cifrar(String aCifrar) 
	{
		try {
			Cipher cipher = Cipher.getInstance("AES");
			byte [] clearText = aCifrar.getBytes();
//			System.out.println("clave original: " + aCifrar);
			cipher.init(Cipher.ENCRYPT_MODE, key);
			long startTime = System.nanoTime();
			byte [] cipheredText = cipher.doFinal(clearText);
			long endTime = System.nanoTime();
//			System.out.println("clave cifrada: " + cipheredText);
//			System.out.println("Tiempo asimetrico: " + (endTime - startTime));
			return cipheredText;
		}
		catch (Exception e) {
			System.out.println("Excepcion: " + e.getMessage());
			return null;
		}
	}

	/**
	 * 
	 * @return
	 */
	public byte[] cifrar2(String aCifrar, PublicKey key) 
	{
		try {
			Cipher cipher = Cipher.getInstance(ALGORITMO);
			byte [] clearText = aCifrar.getBytes();
//			System.out.println("clave original: " + aCifrar);
			cipher.init(Cipher.ENCRYPT_MODE, key);
			long startTime = System.nanoTime();
			byte [] cipheredText = cipher.doFinal(clearText);
			long endTime = System.nanoTime();
//			System.out.println("clave cifrada: " + cipheredText);
//			System.out.println("Tiempo asimetrico: " + (endTime - startTime));
			return cipheredText;
		}
		catch (Exception e) {
			System.out.println("Excepcion: " + e.getMessage());
			return null;
		}
	}

	/**
	 * 
	 * @param cipheredText
	 */
	public String descifrar(byte[] cipheredText) {
		String s3 = "";
		try
		{
			Cipher cipher = Cipher.getInstance(ALGORITMO);
			cipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
			byte [] clearText = cipher.doFinal(cipheredText);
			s3 = new String(clearText);
			key =  new SecretKeySpec(clearText, ALGORITMO);
//			System.out.println("-__- clave original: " + s3);

		}
		catch (Exception e) 
		{
			System.out.println("Excepcion: " + e.getMessage());
		}
		return s3;
	}

	/**
	 * 
	 * @return
	 */
	public byte[] calcularHash(String pDato)
	{
		try 
		{
			byte[] text = pDato.getBytes();
			String s1 = new String(text);
//			System.out.println("dato original: " + s1);
			byte [] digest = getKeyedDigest2(text);
			String s2 = new String(digest);
//			System.out.println("digest: "+ s2);
			return digest;
		}
		catch (Exception e) 
		{
			System.out.println("Excepcion: " + e.getMessage());
			return null;
		}
	}

	/**
	 * 
	 * @param buffer
	 * @return
	 */
	private byte[] getKeyedDigest(byte[] buffer) {
		try {
			MessageDigest md5 = MessageDigest.getInstance("sha1");
			md5.update(buffer);
			return md5.digest();
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * 
	 * @param buffer
	 * @return
	 */
	private byte[] getKeyedDigest2(byte[] buffer)
	{
		String hash = null;
		try {

		     Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
		     SecretKeySpec secret_key = new SecretKeySpec(key.getEncoded(), "HmacSHA256");
		     sha256_HMAC.init(secret_key);

		     hash = new String(
		    		 (sha256_HMAC.doFinal(buffer)));
//		     System.out.println(hash);
		    }
		    catch (Exception e){
		     System.out.println("Error");
		    }
		return hash.getBytes();
	}
	

	/**
	 * 
	 * @return
	 */
	public X509Certificate certificado() 
	{
		KeyPairGenerator generator = null;
		X509Certificate certificado = null;
		try 
		{
			generator = KeyPairGenerator.getInstance("RSA");
			generator.initialize(KEY_SIZE);
		} 
		catch (NoSuchAlgorithmException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		keyPair = generator.generateKeyPair();
		try 
		{
			certificado = generateV3Certificate(keyPair);
		}
		catch (Exception e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return certificado;
	}

	/**
	 * 
	 * @param pair
	 * @return
	 * @throws Exception
	 */
	public static X509Certificate generateV3Certificate(KeyPair pair) throws Exception 
	{
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

		X509V3CertificateGenerator certGen = new X509V3CertificateGenerator();

		certGen.setSerialNumber(BigInteger.valueOf(System.currentTimeMillis()));
		certGen.setIssuerDN(new X500Principal("CN=Test Certificate"));
		certGen.setNotBefore(new Date(System.currentTimeMillis() - 10000));
		certGen.setNotAfter(new Date(System.currentTimeMillis() + 10000));
		certGen.setSubjectDN(new X500Principal("CN=Test Certificate"));
		certGen.setPublicKey(pair.getPublic());
		certGen.setSignatureAlgorithm("SHA256WithRSAEncryption");

		certGen.addExtension(X509Extensions.BasicConstraints, true, new BasicConstraints(false));
		certGen.addExtension(X509Extensions.KeyUsage, true, new KeyUsage(KeyUsage.digitalSignature
				| KeyUsage.keyEncipherment));
		certGen.addExtension(X509Extensions.ExtendedKeyUsage, true, new ExtendedKeyUsage(
				KeyPurposeId.id_kp_serverAuth));

		certGen.addExtension(X509Extensions.SubjectAlternativeName, false, new GeneralNames(new GeneralName(GeneralName.rfc822Name, "test@test.test")));

		return certGen.generateX509Certificate(pair.getPrivate(), "BC");

	}


	/**
	 * Realiza las tareas necesarias para terminar el encuentro<br>
	 * Se averigua el nombre del ganador, la conexión con el servidor se cierra y el estado del juego pasa a SIN_CONECTAR<br>
	 * <b>pre:</b>juegoTerminado = true
	 * @throws BatallaNavalException Se lanza esta excepción si hay problemas en la comunicación
	 */
	public void terminarConexion( ) throws Exception
	{
		try
		{
			String mensajeFin = in.readLine( );

			// Cerrar la conexión al servidor
			out.close( );
			in.close( );
			canal.close( );

			out = null;
			in = null;
			canal = null;
		}
		catch( IOException e )
		{
			throw new Exception( "Se presentaron problemas con la conexión al servidor. " + e.getMessage( ) );
		}
	}



	public static byte[] hmacDigest(String msg, String keyString, String algo) {
		String digest = null;
		byte[] bytes = null;
		try {
			SecretKeySpec key = new SecretKeySpec((keyString).getBytes("UTF-8"), algo);
			Mac mac = Mac.getInstance(algo);
			mac.init(key);
			bytes = mac.doFinal(msg.getBytes("ASCII"));
			StringBuffer hash = new StringBuffer();
			digest = hash.toString();
		} catch (UnsupportedEncodingException e) {
		} catch (InvalidKeyException e) {
		} catch (NoSuchAlgorithmException e) {
		}
		return bytes;
	}

	
	//______________________________________________________________
	//METHODS AUX
	//______________________________________________________________
	
	/**
	 * 
	 */
	@Override
	public void fail() 
	{
		// TODO Auto-generated method stub
		System.out.println("la tarea ha fallado :" + Task.MENSAJE_FAIL);		
	}
	

	/**
	 * 
	 */
	@Override
	public void success() 
	{
		// TODO Auto-generated method stub
		System.out.println("la tarea ha sido resuelta :" + Task.OK_MESSAGE);	
		
	}

	/**
	 * 
	 */
	@Override
	public void execute() 
	{
		// TODO Auto-generated method stub
//    	System.out.println("comienza la ejecucion del cliente  en el execute");
    	ClienteUnidadDistribucion cliente = new ClienteUnidadDistribucion();
    	try 
    	{
			cliente.conectar();
			cliente.comunicarse();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
