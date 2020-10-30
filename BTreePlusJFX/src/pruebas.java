public class pruebas {
    public static void main(String[] args) {

        BPlusTree prueba1 = new BPlusTree();
        prueba1.initialize(3);
        prueba1.insertElement(1);
        //prueba1.print();
        prueba1.insertElement(1);
        //prueba1.print();
        prueba1.insertElement(3);
        //  prueba1.deleteElement( 8);
        prueba1.print();
        prueba1.insertElement(7);
        prueba1.print();
        //prueba1.insert(1);duplicado revisar
        prueba1.insertElement(10);
        prueba1.print();
        prueba1.insertElement(0);
        prueba1.print();
        prueba1.insertElement(1);
        prueba1.print();
        prueba1.insertElement(2);
        prueba1.print();

        /*for (int i = 0; i < 200; i++) {
            System.out.println("Iteracion: "+(i+1));
            System.out.println("Insertando el elemento: " + i);
            prueba1.insertElement(i);
            prueba1.print();
        }*/
        //
        //

        prueba1.print();
        prueba1.insertElement(11);
        prueba1.print();

        System.out.println("Delete");
        prueba1.deleteElement(11);
        prueba1.print();
        prueba1.deleteElement(10);
        prueba1.print();
        prueba1.deleteElement(8);
        prueba1.print();
        prueba1.deleteElement(7);
        prueba1.print();
        prueba1.deleteElement(3);
        prueba1.print();
        prueba1.print();
        prueba1.insertElement(13);
        prueba1.print();
        //prueba1.print();
        prueba1.insertElement(15);
        prueba1.print();
        //prueba1.print();
        prueba1.insertElement(14);
        prueba1.print();
        //prueba1.print();
        prueba1.insertElement(12);
        prueba1.print();
        //prueba1.print();
        prueba1.insertElement(20);
        prueba1.print();
        prueba1.findElement(1);
        /*for (int i = 0; i < 200; i++) {
            prueba1.insertElement(i);
            prueba1.print();
        }*/
    }
}
