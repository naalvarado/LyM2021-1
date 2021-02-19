package main;

import java.util.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.*;

// EN input SE TIENE UNA CADENA CON LA ENTRADA DEL ARHIVO EN LA QUE CADA PALABRA O CARACTER ESTA
// SEPARADA POR UN UNICO ESPACIO 
// EN input_List TIENES CADA PALABRA O CARACTER EN UNA POSICION DEL ARREGLO

public class Robot {

	/**
	 * Estructura que guarda las variables declaradas
	 */
	public static Hashtable<String, Integer> variables = new Hashtable<String, Integer>();

	/**
	 * Estructura que guarda las funciones declaradas, usando el nombre de la
	 * funcion como llave y la cantidad de parametros como value
	 */
	public static Hashtable<String, Integer> funciones = new Hashtable<String, Integer>();

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
	 * Lista de posibles argumentos D = left, right , back
	 */
	public static ArrayList<String> D = new ArrayList<>();

	/**
	 * Lista de posibles argumentos O = N, E , W , S
	 */
	public static ArrayList<String> O = new ArrayList<>();

	/**
	 * Main del programa , se realiza la lectura de la entrada y se llama al metodo
	 * que lo va a interpretar
	 */
	public static void main(String[] args) throws IOException {

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
		comandos.add("blocked?");
		comandos.add("facing?");
		comandos.add("can");
		comandos.add("not");

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
	 * Metodo para poner todo el codigo del robot en un arreglo de Strings
	 * 
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
		try {

			if (!input_List[0].equals("(")) {
				System.out.println("Error: deberia iniciar por parentesis");
				return valido = false;
			}

			// Guardamos las variables y las funciones en el hashtable
			boolean vars = llenarHash(input_List);
			if (!vars) {
				// Si una variable esta mal declarada se retorna false.
				return false;
			}

			// inicialmente veriicamos que el numero de parentesis abiertos sea el mismo que
			// el
			// numero de parentesis cerrados
			for (int i = 0; i < input_List.length; ++i) {
				String a = input_List[i];
				if (a.equals("(")) {
					abiertos = abiertos + 1;
					if (isNumeric(input_List[i + 1]) || (!(esFuncion(input_List[i + 1]) || esComando(input_List[i + 1])
							|| input_List[i + 1].equals(")")))) {
						if (variables.containsKey(input_List[i + 1])) {
							if (!input_List[i - 2].equals("define")) {
								System.out.println(
										"Error : no puede haber una variable inmediatamente despues de un parentesis : "
												+ input_List[i + 1]);
								return false;
							}
						} else {
							System.out.println("despues del parentesis debe haber una funcion o un comando no : "
									+ input_List[i + 1]);
							return false;
						}
					}
				}
				if (a.equals(")")) {
					abiertos = abiertos - 1;
					if (abiertos < 0) {
						valido = false;
					}
				}

			}
			if (abiertos != 0) {
				System.out.println("Error: hace falta un parentesis");
				return valido = false;
			}

			// Acá empesamos a verificar que este bien escrito el programa
			for (int i = 0; i < input_List.length; i++) {
				boolean esComando = comandos.contains(input_List[i]);
				if (esComando) {
					if (!input_List[i - 1].equals("(")) {
						System.out.println("Error: falta '(' antes del 'comando'." + input_List[i]);
						return false;
					}
					// Se revisa que todos los metodos que tengan un solo parametro por entrada
					// tengan el ')'
					if (!(input_List[i].equals("walkTo") | input_List[i].equals("NOP") | input_List[i].equals("if")
							| input_List[i].equals("block") | input_List[i].equals("define")
							| input_List[i].equals("blocked?") | input_List[i].equals("not"))) {
						if (!input_List[i + 2].equals(")")) {
							System.out.println("Error: falta el ')' del 'COMANDO'." + input_List[i]);
							return false;
						}
					}

					if (input_List[i].equals("walk") | input_List[i].equals("drop") | input_List[i].equals("free")
							| input_List[i].equals("pick") | input_List[i].equals("grab")) {

						boolean esVar = variables.containsKey(input_List[i + 1]);
						boolean esInt = isNumeric(input_List[i + 1]);
						if (!esVar && !esInt) {
							System.out.println("Error: se esperaba una variable o un numero pero se encontro: "
									+ input_List[i + 1]);
							return false;
						}

					}
					if (input_List[i].equals("rotate")) {
						if ((!esD(input_List[i + 1]))) {
							System.out.println("Error: deberia ser un valor 'left right o back' pero se introdujo."
									+ input_List[i + 1]);
							return false;
						}
					}
					if (input_List[i].equals("look") | input_List[i].equals("facing?")) {
						if (!checkFacingLook(input_List, i)) {
							return false;
						}
					}

					if ((input_List[i].equals("walkTo"))) {
						boolean esVar = variables.containsKey(input_List[i + 1]);
						boolean esInt = isNumeric(input_List[i + 1]);
						if (!esVar && !esInt) {
							System.out.println("Error: se esperaba una variable o un numero pero se encontro: "
									+ input_List[i + 1]);
							return false;
						}

						if ((!esO(input_List[i + 2]))) {
							System.out.println("Error: deberia ser un valor O = N, E , W , S pero se introdujo."
									+ input_List[i + 1]);
							return false;
						}
						if (!input_List[i + 3].equals(")")) {
							System.out.println("Error: falta el ) del walk to" + input_List[i + 1]);
							return false;
						}
					}

					if ((input_List[i].equals("NOP"))) {
						if (!(input_List[i + 1]).equals(")")) {
							System.out.println("Error: deberia ser ')' pero se introdujo." + input_List[i + 1]);
							return false;
						}
					}

					if ((input_List[i].equals("blocked?"))) {
						if (!checkBloqued(input_List, i)) {
							return false;
						}
					}

					if ((input_List[i].equals("NOP"))) {
						if (!(input_List[i + 1]).equals(")")) {
							System.out.println("Error: deberia ser ')' pero se introdujo." + input_List[i + 1]);
							return false;
						}
					}
					if ((input_List[i].equals("can"))) {
						if (!checkCan(input_List, i)) {
							return false;
						}
					}

					if ((input_List[i].equals("not"))) {
						if (!checkNot(input_List, i)) {
							return false;
						}
					}

					// Es un if?
					if (input_List[i].equals("if")) {
						// Si es un if miramos que tennga un condicional
						if (input_List[i + 1].equals("(")) {
							if (input_List[i + 2].equals("blocked?") | input_List[i + 2].equals("facing?")
									| input_List[i + 2].equals("can") | input_List[i + 2].equals("not")) {
								// Si tiene un condicional miramos que este bien
								if (!checkAllConds(input_List, i + 2)) {
									return false;
								}
								// miramos que tenga dos comandos para el caso en que el condicional es
								// 'blocked?'
								if (input_List[i + 2].equals("blocked?")) {
									if (!checkNoComanIf(input_List, i + 4)) {
										System.out.println("Error: Le faltan o le sobran comendos al if");
										return false;
									}
								}
								// miramos que tenga dos comandos para el caso en que el condicional es
								// 'facing?' o 'can'
								else if (input_List[i + 2].equals("facing?") || input_List[i + 2].equals("can")) {
									if (!checkNoComanIf(input_List, i + 5)) {
										System.out.println("Error: Le faltan o le sobran comendos al if");
										return false;
									}
								}
								// miramos que tenga dos comandos para el caso en que el condicional es 'not'
								else if (input_List[i + 2].equals("not")) {
									// not puede tener un 'blocked?' o 'facing?' o 'can' adentro
									if (input_List[i + 4].equals("blocked?")) {
										if (!checkNoComanIf(input_List, i + 7)) {
											System.out.println("Error: Le faltan o le sobran comendos al if");
											return false;
										}
									} else {
										if (!checkNoComanIf(input_List, i + 8)) {
											System.out.println("Error: Le faltan o le sobran comendos al if");
											return false;
										}
									}
								}
							} else {
								System.out.println(
										"Error: falta el condicional del if, se esperaba un condicional pero se encontro: "
												+ input_List[i + 2]);
								return false;
							}
						} else {
							System.out.println("Error: el if no puede ser vacio");
							return false;
						}
					}

				}
				
				//aqui debe verificar el numero de parametros
				if (esFuncion(input_List[i]) && !input_List[i - 1].equals("define")) {

					int j = funciones.get(input_List[i]);
					for (int k = j; k > 0; k--) {
						boolean esVar = (variables.containsKey(input_List[i + k]));
						if (esVar) {
							if (variables.get(input_List[i + k]) < 0) {
								System.out.println("Error: no puedes llamar una variable no declarada");
								return false;
							}
						}
						boolean esInt = isNumeric(input_List[i + k]);
						if (!esVar && !esInt) {
							System.out.println("Error: se esperaba una variable o un numero pero se encontro: "
									+ input_List[i + 1] + " no se cumplen los parametros de la funcion");
							return false;
						}

					}

					if (!input_List[i + j + 1].equals(")")) {
						System.out.println("Error: hay menos o mas parametros de los solicitados");
						return false;
					}

				}
				if (!esFuncion(input_List[i]) && !esComando(input_List[i]) && !input_List[i].equals("(")
						&& !input_List[i].equals(")")
						&& !(esD(input_List[i]) | esO(input_List[i]) | isNumeric(input_List[i]))
						&& !variables.containsKey(input_List[i])) {
					System.out.println("Error: comando desconocido: " + input_List[i]);
					return false;

				}

				if (input_List[i].equals("(") && input_List[i + 2].equals(")")) {
					if (isNumeric(input_List[i + 1]) || variables.containsKey(input_List[i + 1])) {
						System.out.println("no puede haber algo de la forma ( x )");
						return false;
					}
				}

			}
		} catch (Exception e) {
			valido = false;
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
	 * 
	 * @return Retorna true si todo sale bien, false si hay algun error al declarar
	 *         la varible.
	 */
	public static boolean llenarHash(String[] in) {
		boolean re = true;
		for (int i = 0; i < in.length; i++) {
			if (in[i].equals("define")) {

				// si es definicion de una variable
				if (isNumeric(in[i + 2])) {
					int v = Integer.parseInt(in[i + 2]);
					variables.put(in[i + 1], v);
					if (!in[i + 3].equals(")")) {
						System.out.println("Error: no se esta cerrando la definicion de variable");
						return false;
					}
				}
				// si es definicion de una funcion
				else if (in[i + 2].equals("(")) {
					int j = 3;
					while (!in[i + j].equals(")")) {
						variables.put(in[i + j], -1);

						if (in[i + j].equals("(")) {
							System.out.println("Error: no se cerro la definicion correctamente");
							return false;
						}

						j = j + 1;
					}
					funciones.put(in[i + 1], j - 3);

				} else {
					re = false;
				}

			}
		}
		return re;
	}

	public static boolean checkBloqued(String[] list, int pos) {
		if (!(list[pos + 1]).equals(")")) {
			System.out.println("Error: deberia ser ')' pero se introdujo." + list[pos + 1]);
			return false;
		} else {
			return true;
		}
	}

	public static boolean checkFacingLook(String[] list, int pos) {
		if ((!esO(list[pos + 1]))) {
			System.out.println("Error: deberia ser un valor O = N, E , W , S pero se introdujo." + list[pos + 1]);
			return false;
		} else {
			return true;
		}
	}

	public static boolean checkCan(String[] list, int pos) {
		if (!(list[pos + 1]).equals("grab") | list[pos + 1].equals("drop") | list[pos + 1].equals("free")
				| list[pos + 1].equals("pick")) {
			System.out.println(
					"Error: deberia ser alguno de los siguientes 'drop','free','grab','pick' pero se introdujo."
							+ list[pos + 1]);
			return false;
		} else {
			return true;
		}
	}

	public static boolean checkNot(String[] list, int pos) {
		if (!(list[pos + 1]).equals("(")) {
			System.out.println("Error: deberia ser '(' pero se introdujo." + list[pos + 1]);
			return false;
		}
		if (!(list[pos + 2]).equals("blocked?") | list[pos + 2].equals("facing?") | list[pos + 2].equals("can")) {
			System.out
					.println("Error: deberia ser alguno de los siguientes 'blocked?','facing?','can' pero se introdujo."
							+ list[pos + 1]);
			return false;
		}
		if (list[pos + 2].equals("bloqued?")) {
			if (!checkBloqued(list, pos + 2)) {
				return false;
			}
		}
		if (list[pos + 2].equals("facing?")) {
			if (!checkFacingLook(list, pos + 2)) {
				return false;
			}
		}
		if (list[pos + 2].equals("can")) {
			if (!checkCan(list, pos + 2)) {
				return false;
			}
		}
		return true;
	}

	public static boolean esFuncion(String a) {
		boolean resp = false;
		if (funciones.containsKey(a)) {
			resp = true;
		}

		return resp;
	}

	public static boolean checkAllConds(String[] list, int pos) {
		boolean re = true;
		if (list[pos].equals("blocked?")) {
			re = checkBloqued(list, pos);
		} else if (list[pos].equals("facing?")) {
			re = checkFacingLook(list, pos);
		} else if (list[pos].equals("can")) {
			re = checkCan(list, pos);
		} else {
			re = checkNot(list, pos);
		}
		return re;
	}

	/**
	 * Metodo para verificar que el if tiene solo dos comandos (ni mas ni menos)
	 * 
	 * @param pos La posicion del paréntecis del primer comando del if
	 */
	public static boolean checkNoComanIf(String[] list, int pos) {
		boolean re = true;
		int contPar = 0;
		int contComm = 0;
		for (int i = pos; i < list.length; i++) {
			if (list[i].equals("(")) {
				contPar++;
			}
			if (list[i].equals(")")) {
				contPar--;
			}
			if (contPar == 0) {
				contComm++;
			}
			if (contComm == 2) {
				if (list[i + 1].equals(")")) {
					re = true;
					break;
				} else {
					re = false;
					break;
				}
			}
		}
		return re;
	}

}
