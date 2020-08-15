package fr.pulsedev.jarvis.calculator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This file is a part of jarvis, located on fr.pulsedev.jarvis.calculator
 * Copyright (c) Niout - All rights reserved
 *
 * @author Niout
 * Created the 14/08/2020 at 18:04.
 */
public class Expression {

    private final String expression;

    public Expression(String expression){
        this.expression = expression;
    }

    public String calculate()
            throws NumberFormatException{
        List<String> result = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        for(String charAt : expression.split("")){
            if(charAt.equals("+")){
                result.add(builder.toString());
                result.add("+");
                builder = new StringBuilder();
            }else if(charAt.equals("-")){
                result.add(builder.toString());
                result.add("-");
                builder = new StringBuilder();
            }else if(charAt.equals("/")){
                result.add(builder.toString());
                result.add("/");
                builder = new StringBuilder();
            }else if(charAt.equals("*")){
                result.add(builder.toString());
                result.add("*");
                builder = new StringBuilder();
            }else{
                builder.append(charAt);
            }
        }
        result.add(builder.toString());

        for(int i = 0; i< result.size(); i++){
            String toPlace;
            switch (result.get(i)) {
                case "+":
                    toPlace = String.valueOf(addition(Double.parseDouble(result.get(i - 1)), Double.parseDouble(result.get(i + 1))));
                    result.remove(0);
                    result.add(0, toPlace);
                    result.remove(i);
                    result.remove(i);
                    i -= 1;
                    break;
                case "-":
                    toPlace = String.valueOf(soustraction(Double.parseDouble(result.get(i - 1)), Double.parseDouble(result.get(i + 1))));
                    result.remove(0);
                    result.add(0, toPlace);
                    result.remove(i);
                    result.remove(i);
                    i -= 1;
                    break;
                case "/":
                    toPlace = String.valueOf(division(Double.parseDouble(result.get(i - 1)), Double.parseDouble(result.get(i + 1))));
                    result.remove(0);
                    result.add(0, toPlace);
                    result.remove(i);
                    result.remove(i);
                    i -= 1;
                    break;
                case "*":
                    toPlace = String.valueOf(multiplication(Double.parseDouble(result.get(i - 1)), Double.parseDouble(result.get(i + 1))));
                    result.remove(0);
                    result.add(0, toPlace);
                    result.remove(i);
                    result.remove(i);
                    i -= 1;
                    break;
            }

        }
        return result.get(0);
    }

    private double addition(double un, double deux){
        return un+deux;
    }
    private double multiplication(double un, double deux){
        return un*deux;
    }

    private double division(double un, double deux){
        return un/deux;
    }

    private double soustraction(double un, double deux){
        return un-deux;
    }

    public String getExpression() {
        return expression;
    }
}
