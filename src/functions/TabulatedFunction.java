package functions;

import java.io.Serializable;

public interface TabulatedFunction extends Iterable<FunctionPoint> {
    double getLeftDomainBorder();

    double getRightDomainBorder();

    double getFunctionValue(double x);

    int getPointsCount();

    FunctionPoint getPoint(int index);

    void setPoint(int index, FunctionPoint point) throws InappropriateFunctionPointException;

    double getPointX(int index);

    void setPointX(int index, double x) throws InappropriateFunctionPointException;

    double getPointY(int index);

    void setPointY(int index, double y);

    void deletePoint(int index);

    void addPoint(FunctionPoint point) throws InappropriateFunctionPointException;

    @Override
    String toString();

    @Override
    boolean equals(Object o);

    @Override
    int hashCode();

    Object clone();
}
