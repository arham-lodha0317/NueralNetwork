import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.DoubleUnaryOperator;


/**
 *
 * Some methods and functions for matrix operations
 * @author arhamlodha
 *
 * **/


public class Matrix {

    private ArrayList<ArrayList<Double>> matrix;
    private int width;
    private int height;

    /**
     *
     * Create a matrix with dimensions (Y * X) initializing all values to 0
     * @param x number of columns in the matrix
     * @param y number of rows in the matrix
     *
     * **/

    public Matrix(int x, int y){

        matrix = new ArrayList<>();

        this.width = x;
        this.height = y;

        for (int r = 0; r < y; r++) {

            matrix.add(new ArrayList<>());

            for (int i = 0; i < x; i++) {

                matrix.get(r).add(0.0);

            }

        }
    }

    /**
     *
     * Creates a vector matrix with dimensions (1 x X) initializing all values to 0
     * @param x number of columns in the vector
     *
     * **/

    public Matrix(int x){
        matrix = new ArrayList<>();
        this.width = x;
        this.height = 1;

        matrix.add(new ArrayList<>());
        for (int i = 0; i < x; i++) {
            matrix.get(i).add(0.0);
        }
    }

    /**
     *
     * Creates a copy of another matrix with the same number of rows and colums
     * @param matrix - Matrix to copy
     *
     * **/

    public Matrix(Matrix matrix){
        this.matrix = matrix.getMatrix();

        height = matrix.getHeight();
        width = matrix.getWidth();


    }

    /**
     *
     * Creates new matrix by copying the contents of a ArrayList<ArrayList<Double>>
     * @param matrix - ArrayList to copy to this.matrix
     *
     * **/

    public Matrix(ArrayList<ArrayList<Double>> matrix){
        this.matrix = (ArrayList<ArrayList<Double>>) matrix.clone();
        height = matrix.size();
        width = matrix.get(0).size();
    }



    public Matrix(double[] matrix){
        this.matrix = new ArrayList<>();
        this.matrix.add(new ArrayList<>());
        Arrays.stream(matrix).forEach(n -> this.matrix.get(0).add(n));
        height = 1;
        width = matrix.length;
    }

    public Matrix(Double[] doubles) {
        this.matrix = new ArrayList<>();
        this.matrix.add(new ArrayList<>());
        Arrays.stream(doubles).forEach(n -> this.matrix.get(0).add(n));
        height = 1;
        width = doubles.length;
    }

    public Matrix(int x, int y, double d){
        matrix = new ArrayList<>();

        this.width = x;
        this.height = y;

        for (int r = 0; r < y; r++) {

            matrix.add(new ArrayList<>());

            for (int i = 0; i < x; i++) {

                matrix.get(r).add(d);

            }

        }
    }

    /**
     *
     * Changes a square matrix to a identity matrix
     *
     * **/

    public void identity() throws Exception {
        if(height != width) throw new Exception("Identity Matrix cannot be generated");

        for (int r = 0; r < matrix.size(); r++) {

            for (int c = 0; c < matrix.get(r).size(); c++) {

                if(r == c) setValue(r, c, 1);
                else setValue(r, c, 0);
            }

        }
    }


    public void ones() {
        for (int r = 0; r < matrix.size(); r++) {

            for (int c = 0; c < matrix.get(r).size(); c++) {
                setValue(r, c, 1);
            }

        }
    }


    /**
     *
     * Randomizes the contents of the matrix to any random value from a given upper and lower bound
     * @param lower - lower bound for random numbers
     * @param upper - upper bound for random numbers
     *
     * **/

    public void randomize(double upper, double lower){
        Random random = new Random();

        for (int r = 0; r < height; r++) {

            for (int i = 0; i < width; i++) {

                matrix.get(r).set(i, random.nextDouble() * (upper - lower) + lower);

            }

        }
    }

    public void randomGaussian(int seed){
        Random random = new Random(seed);

        for (int r = 0; r < height; r++) {

            for (int i = 0; i < width; i++) {

                matrix.get(r).set(i, random.nextGaussian());

            }

        }
    }

    /**
     *
     * Calculates the dot product of a row of a matrix and the column of another matrix
     * @param row - row to do dot product
     * @param col - col to do dot product
     * @param m - other matrix
     *
     * @return double - dot product
     *
     * **/

    public double dotProduct(int row, int col, Matrix m){
        double sum = 0.0;

        for (int r = 0; r < m.getHeight(); r++) {
            sum += this.matrix.get(row).get(r) * m.getValue(r, col);
        }

        return sum;
    }

    /**
     *
     * Does Matrix multiplication and returns a new matrix
     *
     *
     * **/

    public Matrix multiply(Matrix m) throws Exception{

        if(this.width != m.height){
            throw new Exception("Incorrect Dimensions for the Matrix");
        }

        int number = m.width * this.height;
        Matrix product = new Matrix(m.width, height);

        ExecutorService executor = Executors.newFixedThreadPool(number);

        for (int r = 0; r < this.height; r++) {
            for (int c = 0; c < m.width; c++) {
                Runnable worker = new Thread(new multiplier(r, c, this, m, product));
                executor.execute(worker);
            }
        }

        executor.shutdown();
        return product;
    }

    public Matrix multiply(double d){

        Matrix newMatrix = new Matrix(this);

        for (int r = 0; r < newMatrix.height; r++) {
            for (int c = 0; c < newMatrix.width; c++) {
                newMatrix.setValue(r, c, matrix.get(r).get(c) * d);
            }
        }


        return newMatrix;
    }

    public void multiply_(double d){
        for (int r = 0; r < matrix.size(); r++) {
            for (int c = 0; c < matrix.get(r).size(); c++) {
                matrix.get(r).set(c, matrix.get(r).get(c) * d);
            }
        }
    }

    public Matrix add(Matrix m) throws Exception {
        if(m.getWidth() != width && m.getHeight() != height) throw new Exception("Matrix dimensions are incorrect");
        Matrix nM = new Matrix(this);

        for (int r = 0; r < matrix.size(); r++) {
            for (int c = 0; c < matrix.get(r).size(); c++) {

                nM.setValue(r, c, getValue(r, c) + m.getValue(r, c));

            }
        }

        return nM;
    }

    public void add_(Matrix m) throws Exception{
        if(m.getWidth() != width && m.getHeight() != height) throw new Exception("Matrix dimensions are incorrect");
        for (int r = 0; r < matrix.size(); r++) {
            for (int c = 0; c < matrix.get(r).size(); c++) {

                setValue(r, c, getValue(r, c) + m.getValue(r, c));

            }
        }
    }

    public Matrix subtract(Matrix m) throws Exception {
        if(m.getWidth() != width && m.getHeight() != height) throw new Exception("Matrix dimensions are incorrect");
        Matrix nM = new Matrix(this);

        for (int r = 0; r < matrix.size(); r++) {
            for (int c = 0; c < matrix.get(r).size(); c++) {

                nM.setValue(r, c, getValue(r, c) - m.getValue(r, c));

            }
        }

        return nM;
    }

    public void subtract_(Matrix m) throws Exception{
        if(m.getWidth() != width && m.getHeight() != height) throw new Exception("Matrix dimensions are incorrect");
        for (int r = 0; r < matrix.size(); r++) {
            for (int c = 0; c < matrix.get(r).size(); c++) {

                setValue(r, c, getValue(r, c) - m.getValue(r, c));

            }
        }
    }

    public void transpose_(){
        Matrix self = new Matrix(height, width);

        for (int r = 0; r < matrix.size(); r++) {
            for (int c = 0; c < matrix.get(r).size(); c++) {

                self.setValue(c, r, matrix.get(r).get(c));

            }
        }

        matrix = self.getMatrix();
    }

    public Matrix transpose(){
        Matrix self = new Matrix(height, width);

        for (int r = 0; r < matrix.size(); r++) {
            for (int c = 0; c < matrix.get(r).size(); c++) {

                self.setValue(c, r, matrix.get(r).get(c));

            }
        }

        return self;
    }

    public void map_(DoubleUnaryOperator fn){
        for (int r = 0; r < matrix.size(); r++) {
            for (int c = 0; c < matrix.get(r).size(); c++) {
                setValue(r, c, fn.applyAsDouble(getValue(r, c)));
            }
        }
    }

    public Matrix map(DoubleUnaryOperator fn){
        Matrix m = new Matrix(this);

        for (int r = 0; r < matrix.size(); r++) {
            for (int c = 0; c < matrix.get(r).size(); c++) {
                m.setValue(r, c, fn.applyAsDouble(m.getValue(r, c)));
            }
        }

        return m;
    }

    public Matrix relu(){

        Matrix changed = new Matrix(this);

        for (ArrayList<Double> list : changed.getMatrix()) {

            for (int c = 0; c < list.size(); c++) {

                if (list.get(c) < 0) {
                    list.set(c, 0.01 * list.get(c));
                }

            }

        }



        return new Matrix(changed);
    }

    public Matrix sigmoid(){

        Matrix changed = new Matrix(this);

        for (int r = 0; r < height; r++) {

            for (int c = 0; c < width; c++) {

                double value = (1 / (1 + Math.exp(-1 * getValue(r, c))));

                changed.setValue(r, c, value);

            }

        }



        return changed;

    }

    public Matrix sigmoidPrime(){
        Matrix changed = new Matrix(this);

        for (int r = 0; r < height; r++) {

            for (int c = 0; c < width; c++) {

                double value = (1/Math.pow(1 + Math.exp(-getValue(r,c)), 1)) * Math.exp(-getValue(r,c));

                changed.setValue(r, c, value);

            }

        }



        return changed;
    }

    public Matrix reluPrime(){
        Matrix changed = new Matrix(this);

        for (int r = 0; r < height; r++) {

            for (int c = 0; c < width; c++) {

                changed.setValue(r, c, getValue(r,c) > 0 ? 1 : 0.01);

            }

        }



        return changed;
    }

    public double crossEntropy(Matrix target){
        if(target.width != width && target.height != height) new Exception("Dimensions are incorrect");

        double sum = 0.0;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                sum += Math.log(getValue(i, j)) * target.getValue(i, j);
            }
        }

        return sum;
    }

    public double squaredError(Matrix target){
        if(target.width != width && target.height != height) new Exception("Dimensions are incorrect");

        double sum = 0.0;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                sum += Math.pow(this.getValue(i, j) - target.getValue(i, j), 2);
            }
        }

        return sum;
    }

    public void multiplyRow(int row, double scalar){
        for (int i = 0; i < matrix.get(row).size(); i++) {
            matrix.get(row).set(i, getValue(row, i) * scalar);
        }
    }

    public void setColumn(int column, double value){
        matrix.forEach(n -> n.set(column, value));
    }

    public double sumAll(){
        return matrix.stream().mapToDouble(arr -> arr.stream().mapToDouble(n -> n).sum()).sum();
    }

    public void addFirstToRow(double d){
        for (ArrayList<Double> doubles : matrix) {
            doubles.add(0, d);
        }

        width++;
    }

    public void addFirstToColumn(double d){
        matrix.add(0, new ArrayList<>());
        for (int i = 0; i < width; i++) {
            matrix.get(0).add(d);
        }

        height++;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public double getValue(int r, int c){
        return this.matrix.get(r).get(c);
    }

    public void setValue(int r, int c, double value){
        matrix.get(r).set(c, value);
    }

    public ArrayList<ArrayList<Double>> getMatrix() {
        return matrix;
    }

    public Double[] toArray(){
        return (Double[]) matrix.get(0).toArray();
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        matrix.forEach(n -> {
            n.forEach(x -> stringBuilder.append(x).append(" "));
        });

        return stringBuilder.toString().substring(0, stringBuilder.toString().length() - 1);
    }

    private static class multiplier extends Thread{

        int row;
        int col;
        Matrix m1;
        Matrix m2;
        Matrix m3;

        public multiplier(int row, int col, Matrix m1, Matrix m2, Matrix m3){
            this.row = row;
            this.col = col;
            this.m1 = m1;
            this.m2 = m2;
            this.m3 = m3;
        }

        @Override
        public void run() {
            double sum = 0.0;

            for (int r = 0; r < m1.getWidth(); r++) {
                sum += m1.getValue(row, r) * m2.getValue(r, col);
            }

            m3.setValue(row, col, sum);
        }
    }

    public static void main(String[] args) throws Exception {
        Scanner kb = new Scanner(new BufferedReader(new InputStreamReader(System.in)));

        int col = kb.nextInt();
        int rows = kb.nextInt();

        kb.nextLine();

        ArrayList<ArrayList<Double>> m1 = new ArrayList<>();
        for (int i = 0; i < rows; i++) {
            m1.add(new ArrayList<>());
            for (int j = 0; j < col; j++) {
                m1.get(i).add(kb.nextDouble());
            }

            kb.nextLine();
        }

        Matrix matrix = new Matrix(m1);



        System.out.println(matrix.map(d -> Math.pow(d, 2)));

        kb.close();
    }
}
