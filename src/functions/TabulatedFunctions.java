package functions;

import java.io.*;

public class TabulatedFunctions {

    private TabulatedFunctions() {
    }

    private static TabulatedFunctionFactory factory =
            new ArrayTabulatedFunction.ArrayTabulatedFunctionFactory();

    public static void setTabulatedFunctionFactory(TabulatedFunctionFactory newFactory) {
        if (newFactory == null) {
            throw new IllegalArgumentException("Фабрика не может быть null");
        }
        factory = newFactory;
    }

    public static TabulatedFunction createTabulatedFunction(double leftX,
                                                            double rightX,
                                                            int pointsCount) {
        return factory.createTabulatedFunction(leftX, rightX, pointsCount);
    }

    public static TabulatedFunction createTabulatedFunction(double leftX,
                                                            double rightX,
                                                            double[] values) {
        return factory.createTabulatedFunction(leftX, rightX, values);
    }

    public static TabulatedFunction createTabulatedFunction(FunctionPoint[] points) {
        return factory.createTabulatedFunction(points);
    }

    public static TabulatedFunction tabulate(Function function,
                                             double leftX,
                                             double rightX,
                                             int pointsCount) {
        if (leftX < function.getLeftDomainBorder()
                || rightX > function.getRightDomainBorder()
                || leftX >= rightX
                || pointsCount < 2) {
            throw new IllegalArgumentException("Некорректные границы табулирования или количество точек");
        }

        double[] values = new double[pointsCount];
        double step = (rightX - leftX) / (pointsCount - 1);
        double x = leftX;

        for (int i = 0; i < pointsCount; i++) {
            values[i] = function.getFunctionValue(x);
            x += step;
        }

        return createTabulatedFunction(leftX, rightX, values);
    }

    public static void outputTabulatedFunction(TabulatedFunction function,
                                               OutputStream out) throws IOException {
        DataOutputStream dataOut = new DataOutputStream(out);

        int n = function.getPointsCount();
        dataOut.writeInt(n);
        for (int i = 0; i < n; i++) {
            dataOut.writeDouble(function.getPointX(i));
            dataOut.writeDouble(function.getPointY(i));
        }

        dataOut.flush();
    }

    public static TabulatedFunction inputTabulatedFunction(InputStream in) throws IOException {
        DataInputStream dataIn = new DataInputStream(in);

        int n = dataIn.readInt();
        FunctionPoint[] points = new FunctionPoint[n];

        for (int i = 0; i < n; i++) {
            double x = dataIn.readDouble();
            double y = dataIn.readDouble();
            points[i] = new FunctionPoint(x, y);
        }

        return createTabulatedFunction(points);
    }

    public static void writeTabulatedFunction(TabulatedFunction function,
                                              Writer out) throws IOException {
        BufferedWriter writer = new BufferedWriter(out);

        int n = function.getPointsCount();
        writer.write(Integer.toString(n));
        writer.newLine();

        for (int i = 0; i < n; i++) {
            double x = function.getPointX(i);
            double y = function.getPointY(i);
            writer.write(x + " " + y);
            writer.newLine();
        }

        writer.flush();
    }

    public static TabulatedFunction readTabulatedFunction(Reader in) throws IOException {
        StreamTokenizer tokenizer = new StreamTokenizer(in);

        tokenizer.nextToken();
        int n = (int) tokenizer.nval;

        FunctionPoint[] points = new FunctionPoint[n];

        for (int i = 0; i < n; i++) {
            tokenizer.nextToken();
            double x = tokenizer.nval;

            tokenizer.nextToken();
            double y = tokenizer.nval;

            points[i] = new FunctionPoint(x, y);
        }

        return createTabulatedFunction(points);
    }

    // ===== Методы с использованием рефлексии =====

    public static TabulatedFunction createTabulatedFunction(Class<? extends TabulatedFunction> clazz,
                                                            double leftX,
                                                            double rightX,
                                                            int pointsCount) {
        try {
            var constructor = clazz.getConstructor(double.class, double.class, int.class);
            return constructor.newInstance(leftX, rightX, pointsCount);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                 java.lang.reflect.InvocationTargetException e) {
            throw new IllegalArgumentException("Не удалось создать объект класса " + clazz.getName(), e);
        }
    }

    public static TabulatedFunction createTabulatedFunction(Class<? extends TabulatedFunction> clazz,
                                                            double leftX,
                                                            double rightX,
                                                            double[] values) {
        try {
            var constructor = clazz.getConstructor(double.class, double.class, double[].class);
            return constructor.newInstance(leftX, rightX, values);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                 java.lang.reflect.InvocationTargetException e) {
            throw new IllegalArgumentException("Не удалось создать объект класса " + clazz.getName(), e);
        }
    }

    public static TabulatedFunction createTabulatedFunction(Class<? extends TabulatedFunction> clazz,
                                                            FunctionPoint[] points) {
        try {
            var constructor = clazz.getConstructor(FunctionPoint[].class);
            return constructor.newInstance((Object) points);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                 java.lang.reflect.InvocationTargetException e) {
            throw new IllegalArgumentException("Не удалось создать объект класса " + clazz.getName(), e);
        }
    }

    public static TabulatedFunction tabulate(Class<? extends TabulatedFunction> clazz,
                                             Function function,
                                             double leftX,
                                             double rightX,
                                             int pointsCount) {
        if (leftX < function.getLeftDomainBorder()
                || rightX > function.getRightDomainBorder()
                || leftX >= rightX
                || pointsCount < 2) {
            throw new IllegalArgumentException("Некорректные границы табулирования или количество точек");
        }

        double[] values = new double[pointsCount];
        double step = (rightX - leftX) / (pointsCount - 1);
        double x = leftX;

        for (int i = 0; i < pointsCount; i++) {
            values[i] = function.getFunctionValue(x);
            x += step;
        }

        return createTabulatedFunction(clazz, leftX, rightX, values);
    }
}
