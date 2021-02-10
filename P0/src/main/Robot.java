package main;

import java.util.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.*;

//*EN input SE TIENE UNA CADENA CON LA ENTRADA DEL ARHIVO EN LA QUE CADA PALABRA O CARACTER ESTA
//SEPARADA POR UN UNICO ESPACIO 
//*EN input_List TIENES CADA PALABRA O CARACTER EN UNA POSICION DEL ARREGLO}
//CREE LOS ARREGLOS QUE CONTIENEN TODOS LOS COMANDOS Y LOS TIPOS POSIBLES DE PARAMETROS, AUNQUE
//FALTA EL TIPO n QUE SON NUMEROS Y VARIABLES PERO PSS ES SOLO REVISAR SI ES UN NUMERO O SI 
//ESTA EN EL HASH 
//POR AHORA REVISA 
//1. QUE COMIENCE CON PARENTESIS
//2. QUE HALLA EL MISMO NUMERO DE PARENTESIS DE ABRIR QUE DE CERRAR 

public class Robot {

	/**
	 * Estructura que guarda las variables declaradas
	 */
	public static Hashtable<String, Integer> variables = new Hashtable<String, Integer>();

	/**
	 * variable que guarda el contenido del documento
	 */
	public static String input = "";

	/**
	 * Numero de parentesis abiertos que no han cerrado
	 */
	public static int abiertos = 0;

	/**
	 * Lista de comandos
	 */
	public static ArrayList<String> comandos = new ArrayList<>();

	/**
	 * Lista de condicionales
	 */
	public static ArrayList<String> condicionales = new ArrayList<>();

	/**
	 * Lista de posibles argumentos D = left, right , back
	 */
	public static ArrayList<String> D = new ArrayList<>();

	/**
	 * Lista de posibles argumentos O = N, E , W , S
	 */
	public static ArrayList<String> O = new ArrayList<>();

	/**
	 * Main del programa , se realiza la lectura de la entrada por teclado y se
	 * llama al metodo que lo va a interpretar
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		/**
		 * inicializacion arreglos
		 */
		comandos.add("walk");
		comandos.add("rotate");
		comandos.add("look");
		comandos.add("drop");
		comandos.add("free");
		comandos.add("pick");
		comandos.add("grab");
		comandos.add("walkTo");
		comandos.add("NOP");
		comandos.add("block");
		comandos.add("if");
		comandos.add("define");

		condicionales.add("blocked");
		condicionales.add("facing");
		condicionales.add("can");
		condicionales.add("not");

		D.add("left");
		D.add("right");
		D.add("back");

		O.add("N");
		O.add("E");
		O.add("W");
		O.add("S");

		System.out.println("Empezamos el programa");

		File archivo = null;
		FileReader fr = null;
		BufferedReader br = null;

		try {
			// Apertura del fichero y creacion de BufferedReader para poder
			// hacer una lectura comoda (disponer del metodo readLine()).
			archivo = new File("../P0/src/robot-conf");
			fr = new FileReader(archivo);
			br = new BufferedReader(fr);

			// Lectura del fichero
			String linea = "";
			while ((linea = br.readLine()) != null) {
				input = input + linea;
			}
			String[] lista = formatString(input);
			boolean a = verificador(lista);
			System.out.println(a);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// En el finally cerramos el fichero, para asegurarnos
			// que se cierra tanto si todo va bien como si salta
			// una excepcion.
			try {
				if (null != fr) {
					fr.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}

	}
	/**
	 * Separe este metodo para que el verificador no quede tan largo
	 * @return retorna la lista de palabras del documento
	 */
	public static String[] formatString(String input) {
		// quitamos todos los espacios dobles, queda una linea en la que cada palabra
		// esta
		// separada por un unico espacio

		input = input.replace("(", " ( ");
		input = input.replace(")", " ) ");
		input = input.trim();
		input = input.replaceAll("\\s{2,}", " ");
		System.out.println(input);

		// guardamos la entrada en un arreglo
		String[] input_List = input.split(" ");
		return input_List;
	}

	public static boolean verificador(String[] input_List) {
		boolean valido = true;

		// inicialmente veriicamos que el numero de parentesis abiertos sea el mismo que
		// el
		// numero de parentesis cerrados

		if (!input_List[0].equals("(")) {
			return valido = false;
			// debe iniciar por parentesis
		}

		for (int i = 0; i < input_List.length; ++i) {
			String a = input_List[i];
			// System.out.println(a);
			if (a.equals("(")) {
				abiertos = abiertos + 1;
			}
			if (a.equals(")")) {
				abiertos = abiertos - 1;
				if (abiertos < 0) {
					valido = false;
				}
			}

		}
		if (abiertos != 0) {
			return valido = false;
			// no hace falta ni sobra ningun parentesis
		}
		
		// Guardamos las variables en el hashtable
		boolean vars = llenarHash(input_List);
		if(!vars) {
			// Si una variable esta mal declarada se retorna false.
			return false;
		}
		
		// Acá empesamos a verificar que este bien escrito el programa
		for(int i = 0; i < input_List.length; i++) {
			boolean esComando = comandos.contains(input_List[i]);
			if(esComando) {
				if(input_List[i].equals("walk")) {
					if(!input_List[i-1].equals("(")) {
						System.out.println("Error: falta '(' antes del 'walk'.");
						return false;
					}
					boolean esVar = variables.containsKey(input_List[i+1]);
					boolean esInt = isNumeric(input_List[i+1]);
					if(!esVar && !esInt) {
						System.out.println("Error: se esperaba una variable o un numero pero se encontro: " + input_List[i+1]);
						return false;
					}
					if(!input_List[i+2].equals(")")) {
						System.out.println("Error: falta el ')' del 'walk'.");
						return false;
					}
				}
			}
		}

		return valido;
	}

	/**
	 * Verifica si una cadena es un comando (nota: la idea es verificar si si es ,
	 * luego verificar que tipo de parametros necesita y verificar si los parametros
	 * estan bien)
	 * 
	 * @param a cadena a verificar
	 * @return true si es un comando false si no lo es
	 */
	public static boolean esComando(String a) {
		boolean resp = false;
		resp = comandos.contains(a);
		return resp;
	}

	/**
	 * Verifica si una cadena es parameto D: D = left, right , back
	 * 
	 * @param a cadena a verificar
	 * @return true si es un parametro de tipo D false si no lo es
	 */
	public static boolean esD(String a) {
		boolean resp = false;
		resp = D.contains(a);
		return resp;
	}

	/**
	 * Verifica si una cadena es parameto O = N, E , W , S
	 * 
	 * @param a cadena a verificar
	 * @return true si es un parametro de tipo O false si no lo es
	 */

	public static boolean esO(String a) {
		boolean resp = false;
		resp = O.contains(a);
		return resp;
	}
	
	/**
	 * Metodo para ver si un string es un numero
	 */
	public static boolean isNumeric(String strNum) {
	    if (strNum == null) {
	        return false;
	    }
	    try {
	        double d = Double.parseDouble(strNum);
	    } catch (NumberFormatException nfe) {
	        return false;
	    }
	    return true;
	}
	
	/**
	 * Metodo para llenar la tabla hash con las variables declaradas en el codigo.
	 * @return Retorna true si todo sale bien, false si hay algun error al declarar la varible.
	 */
	public static boolean llenarHash(String[] in) {
		boolean re = true;
		for(int i = 0; i < in.length; i++) {
			if (in[i].equals("define")) {
				if(!in[i+2].equals("(")) {
					if(isNumeric(in[i+2])) {
						int v = Integer.parseInt(in[i+2]);
						variables.put(in[i+1], v);
					}
					else {
						re = false;
					}
				}
			}
		}
		return re;
	}

}
