/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package packer;

import packer.evaluators.Evaluator;

/**
 *
 * @author Poly
 */
//subclass for each pattern
public class PackerPattern {

    private String expression;
    private Evaluator evaluator;
    private int length;

    public PackerPattern(String expression, Evaluator evaluator) {
        this.expression = expression;
        this.evaluator = evaluator;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public Evaluator getEvaluator() {
        return evaluator;
    }

    public void setEvaluator(Evaluator evaluator) {
        this.evaluator = evaluator;
    }

    @Override
    public String toString() {
        return "(" + expression + ")";
    }
}
