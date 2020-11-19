package test;

import tree.BPlusTree;

public class pruebas {
    public static void main(String[] args) {

        BPlusTree prueba1 = new BPlusTree();
        prueba1.initialize(3);
        prueba1.insertElement(1);
        prueba1.print();
        prueba1.insertElement(1);
        prueba1.print();
        prueba1.insertElement(3);
        prueba1.print();
        prueba1.insertElement(7);
        prueba1.print();
        prueba1.insertElement(10);
        prueba1.print();
        prueba1.insertElement(0);
        prueba1.print();
        prueba1.insertElement(1);
        prueba1.print();
        prueba1.insertElement(2);
        prueba1.print();
        prueba1.insertElement(11);
        prueba1.print();
        prueba1.insertElement(25);
        prueba1.print();
        prueba1.search(0, 25);
        for (int i = 0; i < 200; i++) {
            System.out.println("Iteracion: " + (i + 1));
            System.out.println("Insertando el elemento: " + i);
            prueba1.insertElement(i);
            prueba1.print();
        }

    }
}
