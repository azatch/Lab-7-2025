import functions.*;
import functions.basic.Exp;
import functions.basic.Log;
import functions.basic.Cos;
import functions.basic.Sin;
import threads.*;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class Main {

    private static void testIntegrate() {
        Function exp = new Exp();
        double theoreticalValue = Math.exp(1) - 1;

        System.out.println("Теоретическое значение интеграла e^x на [0, 1]: " + theoreticalValue);
        System.out.println();

        for (double step = 0.1; step >= 0.0001; step /= 10) {
            double result = Functions.integrate(exp, 0, 1, step);
            double error = Math.abs(result - theoreticalValue);
            System.out.printf("Шаг: %.4f, Результат: %.10f, Ошибка: %.2e%n",
                    step, result, error);
        }
    }

    private static void nonThread() {
        Task task = new Task();
        task.setTasksCount(100);

        for (int i = 0; i < task.getTasksCount(); i++) {
            double base = 1 + Math.random() * 9;
            double leftX = Math.random() * 100;
            double rightX = 100 + Math.random() * 100;
            double step = Math.random();

            task.setFunction(new Log(base));
            task.setLeftX(leftX);
            task.setRightX(rightX);
            task.setStep(step);

            System.out.printf("Source %.2f %.2f %.6f%n", leftX, rightX, step);

            double result = Functions.integrate(
                    task.getFunction(),
                    task.getLeftX(),
                    task.getRightX(),
                    task.getStep()
            );

            System.out.printf("Result %.2f %.2f %.6f %.6f%n",
                    task.getLeftX(), task.getRightX(), task.getStep(), result);
        }
    }

    private static void simpleThreads() {
        Task task = new Task();
        task.setTasksCount(100);

        Thread generator = new Thread(new SimpleGenerator(task), "Generator");
        Thread integrator = new Thread(new SimpleIntegrator(task), "Integrator");

        generator.start();
        integrator.start();

        try {
            generator.join();
            integrator.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("Оба потока завершили работу");
    }

    private static void complicatedThreads() {
        Task task = new Task();
        task.setTasksCount(100);

        Semaphore semaphore = new Semaphore();

        Generator generator = new Generator(task, semaphore);
        Integrator integrator = new Integrator(task, semaphore);

        generator.setPriority(Thread.MIN_PRIORITY);
        integrator.setPriority(Thread.MAX_PRIORITY);

        generator.start();
        integrator.start();

        try {
            Thread.sleep(50);
            generator.interrupt();
            integrator.interrupt();

            generator.join();
            integrator.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("Оба потока завершили работу (прерваны)");
    }

    private static void testIterators() {
        double[] values = {0, 0.5, 1.0, 1.5, 2.0};
        TabulatedFunction arrayFunc = new ArrayTabulatedFunction(0, 4, values);
        System.out.println("ArrayTabulatedFunction с iterator:");
        for (FunctionPoint p : arrayFunc) {
            System.out.println(p);
        }

        TabulatedFunction listFunc = new LinkedListTabulatedFunction(0, 4, values);
        System.out.println("\nLinkedListTabulatedFunction с iterator:");
        for (FunctionPoint p : listFunc) {
            System.out.println(p);
        }

        System.out.println("\nПроверка UnsupportedOperationException при удалении:");
        try {
            Iterator<FunctionPoint> it = arrayFunc.iterator();
            if (it.hasNext()) {
                it.next();
                it.remove();
            }
        } catch (UnsupportedOperationException e) {
            System.out.println("Поймано: " + e.getMessage());
        }

        System.out.println("\nПроверка NoSuchElementException при исчерпании:");
        try {
            Iterator<FunctionPoint> it = arrayFunc.iterator();
            while (it.hasNext()) {
                it.next();
            }
            it.next();
        } catch (NoSuchElementException e) {
            System.out.println("Поймано: " + e.getMessage());
        }
    }

    private static void testFactories() {
        System.out.println("\nПроверка фабрик TabulatedFunctionFactory:");
        Function f = new Cos();
        TabulatedFunction tf;

        tf = TabulatedFunctions.tabulate(f, 0, Math.PI, 11);
        System.out.println("Текущая фабрика, класс: " + tf.getClass().getName());

        TabulatedFunctions.setTabulatedFunctionFactory(
                new LinkedListTabulatedFunction.LinkedListTabulatedFunctionFactory());
        tf = TabulatedFunctions.tabulate(f, 0, Math.PI, 11);
        System.out.println("После установки LinkedList фабрики, класс: " + tf.getClass().getName());

        TabulatedFunctions.setTabulatedFunctionFactory(
                new ArrayTabulatedFunction.ArrayTabulatedFunctionFactory());
        tf = TabulatedFunctions.tabulate(f, 0, Math.PI, 11);
        System.out.println("После возврата Array фабрики, класс: " + tf.getClass().getName());
    }

    private static void testReflection() {
        System.out.println("\nТестирование рефлексивного создания объектов:");

        TabulatedFunction f;

        f = TabulatedFunctions.createTabulatedFunction(
                ArrayTabulatedFunction.class, 0, 10, 3);
        System.out.println("Класс: " + f.getClass().getName());
        System.out.println(f);

        f = TabulatedFunctions.createTabulatedFunction(
                ArrayTabulatedFunction.class, 0, 10, new double[]{0, 5, 10});
        System.out.println("\nКласс: " + f.getClass().getName());
        System.out.println(f);

        f = TabulatedFunctions.createTabulatedFunction(
                LinkedListTabulatedFunction.class,
                new FunctionPoint[]{
                        new FunctionPoint(0, 0),
                        new FunctionPoint(10, 10)
                }
        );
        System.out.println("\nКласс: " + f.getClass().getName());
        System.out.println(f);

        f = TabulatedFunctions.tabulate(
                LinkedListTabulatedFunction.class, new functions.basic.Sin(), 0, Math.PI, 11);
        System.out.println("\nКласс: " + f.getClass().getName());
        System.out.println(f);
    }


    public static void main(String[] args) {
//        System.out.println("Задание 1 интегрирование e^x ");
//        testIntegrate();
//
//        System.out.println("\nЗадание 2 последовательная версия");
//        nonThread();
//
//        System.out.println("\nЗадание 3 многопоточная версия ");
//        simpleThreads();
//
//        System.out.println("\nЗадание 4 с семафором");
//        complicatedThreads();

        System.out.println("\nИтераторы");
        testIterators();

        System.out.println("\nФабрики табулированных функций");
        testFactories();

        System.out.println("\nРефлексия");
        testReflection();
    }
}
