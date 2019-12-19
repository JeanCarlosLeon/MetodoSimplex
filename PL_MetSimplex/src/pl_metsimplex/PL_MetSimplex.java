/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl_metsimplex;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javax.swing.JOptionPane;

/**
 *
 * @author Usuario
 */
public class PL_MetSimplex {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        Scanner entradaEscaner = new Scanner(System.in);

        boolean seguir = true;
        int subindice = 1;
        double coef = 0;
        int si;
        int cantRestricciones;
        double valorCOmprobar;

        String presentacion;
        String funcion = "";

        List<Double> coeficientesFOS = new ArrayList<Double>();
        List<Integer> posicionesNegativos = new ArrayList<Integer>();

        System.out.println("Recepción de datos");
        System.out.println("Por favor introduzca la función objetivo:");
        while (seguir == true) {
            System.out.print("Valor para x" + subindice + " :");
            coef = entradaEscaner.nextInt();
            coeficientesFOS.add(coef);
            subindice++;
            System.out.print("Ingrese 1 para seguir añadiendo variables: ");
            si = entradaEscaner.nextInt();
            if (si != 1) {
                System.out.println("Ingrese equivalencia de la ecuación: ");
                coef = entradaEscaner.nextInt();
                coeficientesFOS.add(coef);
                seguir = false;
            }
        }

        //System.out.println("size()" + coeficientesFOS.size());
        //0subindice = 1;
        for (int i = 0; i < coeficientesFOS.size(); i++) {
            presentacion = String.valueOf(coeficientesFOS.get(i));
            if (i > 0 && coeficientesFOS.get(i) >= 0) {
                presentacion = "+" + presentacion;
            }

            if (i + 1 == coeficientesFOS.size()) {
                funcion = funcion + " = " + presentacion;
            } else {
                funcion = funcion + presentacion + "x" + (i + 1);
            }

        }
        //JOptionPane.showMessageDialog(null, funcion, "Función", JOptionPane.WARNING_MESSAGE);
        System.out.println("Ingrese el número de restricciones: ");
        cantRestricciones = entradaEscaner.nextInt();
        double matrizRestSinAjustar[][] = new double[cantRestricciones][coeficientesFOS.size()];
        int signoIgualdades[] = new int[cantRestricciones];

        for (int i = 0; i < cantRestricciones; i++) {
            System.out.println("------Valores para restriccion " + (i + 1) + ": ");
            for (int j = 0; j < coeficientesFOS.size() - 1; j++) {
                System.out.print("Ingrese valor para x" + (j + 1) + ": ");
                matrizRestSinAjustar[i][j] = entradaEscaner.nextInt();
                if (j + 1 == coeficientesFOS.size() - 1) {
                    System.out.print("Ingrese signo de la ecuación: <=(1), =(2), >=(3) ");
                    signoIgualdades[i] = entradaEscaner.nextInt();
                    System.out.print("Ingrese equivalencia de la ecuación: ");
                    matrizRestSinAjustar[i][j + 1] = entradaEscaner.nextInt();
                }
            }
        }

        int val = 0;
        System.out.println("Ingrese 1 para Maximizar o 2 para Minimizar");
        val = entradaEscaner.nextInt();

        if (val == 1) {
            for (int j = 0; j < coeficientesFOS.size(); j++) {
                coeficientesFOS.set(j, coeficientesFOS.get(j) * -1);
            }
        }

        System.out.println("--------Original-------");
        for (int i = 0; i < matrizRestSinAjustar.length; i++) {
            for (int j = 0; j < matrizRestSinAjustar[i].length; j++) {
                System.out.print(matrizRestSinAjustar[i][j] + "\t");
            }
            System.out.println();
        }

        //Conteo de las desigualdades para ajustes
        int numAjustes = 0;
        for (int j = 0; j < signoIgualdades.length; j++) {
            if (signoIgualdades[j] != 2) {
                numAjustes++;
            }
        }

        //Añadir variables para la estandarización de las restriciones
        double matrizRest[][] = new double[cantRestricciones][coeficientesFOS.size() + numAjustes];
        int contPosi = 0;
        for (int i = 0; i < matrizRest.length; i++) {
            //si no funciona es aqui
            for (int j = 0; j < matrizRest[i].length; j++) {
                if (j + 1 == matrizRest[i].length) {
                    matrizRest[i][j] = matrizRestSinAjustar[i][coeficientesFOS.size() - 1];
                } else {
                    if (j < coeficientesFOS.size() - 1) {
                        matrizRest[i][j] = matrizRestSinAjustar[i][j];
                    } else {
                        if (signoIgualdades[i] == 2) {
                            matrizRest[i][j] = 0;
                        } else {
                            if (signoIgualdades[i] == 1) {
                                if (j == coeficientesFOS.size() - 1 + contPosi) {
                                    signoIgualdades[i] = 2;
                                    matrizRest[i][j] = 1;
                                    contPosi++;
                                } else {
                                    matrizRest[i][j] = 0;
                                }
                            } else {
                                if (j == coeficientesFOS.size() - 1 + contPosi) {
                                    signoIgualdades[i] = 2;
                                    matrizRest[i][j] = -1;
                                    contPosi++;
                                } else {
                                    matrizRest[i][j] = 0;
                                }
                            }
                        }
                    }

                }
            }
        }
        //Añadir valores funcion objetivo

        List<Double> coeficientesFO = new ArrayList<Double>();
        double valorInd = 0;
        for (int j = 0; j < coeficientesFOS.size() + numAjustes; j++) {
            if (j == coeficientesFOS.size() - 1) {
                valorInd = coeficientesFOS.get(j);
            } else {
                if (j < coeficientesFOS.size() - 1) {
                    coeficientesFO.add(coeficientesFOS.get(j));
                } else {
                    coeficientesFO.add(0.0);
                }
            }
        }
        coeficientesFO.add(valorInd);

        System.out.println("--------Funcion Objetivo corregida-------");
        for (int j = 0; j < coeficientesFO.size(); j++) {
            System.out.print(coeficientesFO.get(j) + "\t");
        }
        System.out.println();
        System.out.println("Restricciones corregida");
        for (int i = 0; i < matrizRest.length; i++) {
            for (int j = 0; j < matrizRest[i].length; j++) {
                System.out.print(matrizRest[i][j] + "\t");
            }
            System.out.println();
        }

        seguir = true;
        int existeNegativo = 0;
        int posicion = 0;
        double menor = 0;
        double auxiliar;
        boolean ocupado = false;
        int columna = 0;
        int fila = 0;

        int bucle = 1;
        while (seguir == true) {
            posicionesNegativos.clear();
            //while (bucle < 2) {

            //obtener el valor minimo de la función objetivo siempre
            // y cuando sea menor a 0
            for (int j = 0; j < coeficientesFO.size(); j++) {
                valorCOmprobar = coeficientesFO.get(j);
                if (valorCOmprobar < 0) {
                    if (ocupado == false) {
                        menor = valorCOmprobar;
                        posicion = j;
                        ocupado = true;
                    } else {
                        if (valorCOmprobar <= menor) {
                            menor = valorCOmprobar;
                            posicion = j;
                        }
                    }
                    existeNegativo++;
                }
            }
            posicionesNegativos.add(posicion);

            //si tengo dos valores negativos guardo sus dos posiciones
            for (int j = 0; j < coeficientesFO.size(); j++) {
                if (coeficientesFO.get(j) == menor) {
                    posicionesNegativos.add(j);
                }
            }

            if (existeNegativo == 0) {
                seguir = false;
            } else {
                existeNegativo = 0;

                //Comprobar restricción mediante razon
                int contarPositivo = 0;
                for (int h = 0; h < posicionesNegativos.size(); h++) {
                    posicion = posicionesNegativos.get(h);

                    ocupado = false;
                    menor = 0;
                    for (int i = 0; i < matrizRest.length; i++) {
                        for (int k = posicion; k < coeficientesFO.size(); k++) {
                            //System.out.println("si ingresa" + k);
                            if (matrizRest[i][k] > 0) {
                                if (k == posicion) {
                                    contarPositivo++;
                                    //System.out.println(matrizRest[i][coeficientesFO.size() - 1] + "/" + matrizRest[i][k]);
                                    auxiliar = matrizRest[i][coeficientesFO.size() - 1] / matrizRest[i][k];
                                    if (ocupado == false) {
                                        menor = auxiliar;
                                        fila = i;
                                        columna = k;
                                        ocupado = true;
                                    } else {
                                        if (auxiliar < menor) {
                                            menor = auxiliar;
                                            fila = i;
                                            columna = k;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (contarPositivo > 0) {
                        h = posicionesNegativos.size();
                    }
                }

                if (contarPositivo == 0) {
                    seguir = false;
                    System.out.println("No se puede continuar por negatividad en restricciones.");
                }

                contarPositivo = 0;

                if (seguir == true) {
                    double valorMultiplicar = 0;
                    double auxM = 0;
                    //Intercambio de valores en las restricciones
                    for (int i = 0; i < matrizRest.length; i++) {
                        for (int k = 0; k < coeficientesFO.size(); k++) {
                            if (i != fila) {
                                if (k == 0) {
                                    valorMultiplicar = (matrizRest[i][columna] / matrizRest[fila][columna]) * (-1);
                                }
                                //System.out.println("valorMultiplicar: " + valorMultiplicar);
                                matrizRest[i][k] = (matrizRest[fila][k] * valorMultiplicar) + matrizRest[i][k];
                            } else {
                                if (k == 0) {
                                    auxM = matrizRest[fila][columna];
                                }
                                matrizRest[i][k] = matrizRest[i][k] / auxM;
                            }
                        }
                    }

                    double valorReemplazar = 0;

                    valorMultiplicar = 0;

                    //calculo de la nueva funcion objetivo
                    for (int j = 0; j < coeficientesFO.size(); j++) {
                        if (j == 0) {
                            valorMultiplicar = coeficientesFO.get(columna) * (-1);
                        }
                        valorReemplazar = coeficientesFO.get(j) + (valorMultiplicar * matrizRest[fila][j]);
                        coeficientesFO.set(j, valorReemplazar);
                    }
                    
                    System.out.println("");
                    System.out.println("");
                    System.out.println("--------Iteraccion-------");
                    for (int i = 0; i < matrizRest.length; i++) {
                        for (int j = 0; j < matrizRest[i].length; j++) {
                            System.out.print(matrizRest[i][j] + "\t");
                        }
                        System.out.println();
                    }

                    System.out.println("--------Funcion Objetivo-------");
                    for (int j = 0; j < coeficientesFO.size(); j++) {
                        System.out.print(coeficientesFO.get(j) + "\t");
                    }
                    System.out.println("");
                }
            }

        }

    }
}
