package com.example.kalkulator;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView view;
    private StringBuffer equation;
    private StringBuffer historyMessage = new StringBuffer();
    List<String> operatorsList = new ArrayList<>();
    private boolean numberFlag;
    final static int REQUEST_CODE=1;
    final static String DELETE_FLAG = "DELETED?";
    public static String HISTORY_MESSAGE = "Historia";
    private Button dotButton,addButton,minusButton,divisionButton,multiButton,equalsButton,zeroButton;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        equation = new StringBuffer();
        view = (TextView)findViewById(R.id.textView);
        view.setText(equation);
        //inicjalizacja przyciskow ktore moga zostac wylaczone
        dotButton = (Button)findViewById(R.id.bKropka);
        addButton = (Button)findViewById(R.id.bPlus);
        minusButton = (Button)findViewById(R.id.bMinus);
        divisionButton = (Button)findViewById(R.id.bPodziel);
        multiButton = (Button)findViewById(R.id.bPomnoz);
        equalsButton = (Button)findViewById(R.id.bWynik);
        zeroButton = (Button)findViewById(R.id.bZero);
        numberFlag=false;
        turnOffOnStartActivity();
        addOperators(operatorsList);

    }

    public void sendMessage(View view)
    {
        Intent intent = new Intent(this,DisplayMessageActivity.class);
        String message = historyMessage.toString();
        intent.putExtra(HISTORY_MESSAGE,message);
        startActivityForResult(intent,REQUEST_CODE);
    }

    public void refresh()
    {
        view.setText(equation);
    }

    public void turnOffOnStartActivity() {
        equation.delete(0, equation.length());
        enableButton(dotButton,false);
        enableButton(equalsButton,false);
        turnOffOperators(false);
        enableButton(minusButton,true);
        numberFlag=false;

    }
    public void turnOffOperators(boolean arg) {
        enableButton(addButton,arg);
        enableButton(minusButton,arg);
        enableButton(divisionButton,arg);
        enableButton(multiButton,arg);
    }
    public void addOperators(List<String> operatorsList){
        operatorsList.add("+");
        operatorsList.add("-");
        operatorsList.add("*");
        operatorsList.add("/");

    }

    public void dotClick(View view){
        Button button = (Button)view;
        equation.append(button.getText());
        refresh();
        enableButton(dotButton,true);
        turnOffOperators(true);
        enableButton(zeroButton,true);
        enableButton(equalsButton,true);
        Log.i("Clicked: ",button.getText().toString());
    }


    public void clickNumber(View view) {
        if(!numberFlag) {
            turnOffOperators(true);
        }
        else {
            turnOffOperators(false);
            enableButton(equalsButton,true);
        }
        Button b = (Button) view;
        equation.append(b.getText()) ;
        refresh();
        dotValidation(b);
        Log.i("Wcisnięto: ",b.getText().toString());
    }

    public void clickOperator(View v){
        Button b = (Button)v;
        if(addButton.isEnabled()){
            turnOffOperators(false);
            enableButton(minusButton,true);
            enableButton(dotButton,false);
            numberFlag=true;
            enableButton(zeroButton,true);
        }
        else if(minusButton.isEnabled()){
            turnOffOperators(false);
        }
        equation.append(b.getText());
        refresh();
        Log.i("Wcisnieto przycisk: ",b.getText().toString());
    }
    public void clickEquals(View v)
    {
        String equationText = equation.toString();
        StringBuffer firstNumber = new StringBuffer();
        StringBuffer secondNumber = new StringBuffer();
        Character operator = ' ';
        setFirstNumber(equation,firstNumber);
        operator = setOperator(operator);
        setSecondNumber(equation,secondNumber);
        BigDecimal strFirstNumber = new BigDecimal(firstNumber.toString());
        BigDecimal strSecondNumber =  new BigDecimal(secondNumber.toString());
        if(!checkZeroDivision(operator,strSecondNumber)){
            Log.i("Error!","Bad operation "+equationText);
            saveToHistory(equationText,"error");
        }
        else{
            strFirstNumber = checkFormats(strFirstNumber);
            strSecondNumber = checkFormats(strSecondNumber);
            BigDecimal equal = operate(strFirstNumber,strSecondNumber,operator);
            saveToHistory(equationText,equal+"");
        }
        turnOffOnStartActivity();

    }

    private BigDecimal operate(BigDecimal firstNumber,BigDecimal secondNumber,Character operator)
    {
        switch(operator){
            case '+':
                return firstNumber.add(secondNumber);
            case '-':
                return firstNumber.subtract(secondNumber);
            case '*':
                return  firstNumber.multiply(secondNumber);
            case '/':
                return firstNumber.divide(secondNumber);
        }
        return new BigDecimal("0.0");
    }

    private BigDecimal checkFormats(BigDecimal numer){
        if(BigDecimal.ZERO.compareTo(numer)==0){
            numer = new BigDecimal("0");
        }
        return numer;
    }

    private void saveToHistory(String equationText, String result){
        equation.append(equationText + " = " + result);
        refresh();
        historyMessage.append(equationText.toString()+ " = "+ result+"\n");
    }

    private boolean checkZeroDivision(Character operator, BigDecimal secondNumber){
        if(operator.equals('/')){
            if(BigDecimal.ZERO.compareTo(secondNumber)==0){
                return false;
            }
        }
        return true;
    }

    private Character setOperator(Character operator)
    {
        operator = equation.charAt(0);
        equation.deleteCharAt(0);
        return operator;
    }

    public void setFirstNumber(StringBuffer _string,StringBuffer _firstNumber){
        _firstNumber.append(_string.charAt(0));
        _string.deleteCharAt(0);
        while(!operatorsList.contains(_string.charAt(0)+"")){
            _firstNumber.append(_string.charAt(0));
            _string.deleteCharAt(0);
        }
    }

    public void setSecondNumber(StringBuffer _string, StringBuffer _secondNumber){
        _secondNumber.append(_string.toString());
        _string.delete(0,_string.length());
    }
    public void disableZero(Button button)
    {
        if(button.getText().equals('0')){
            if(equation.length()==1){
                enableButton(zeroButton,false);
            }
            else if(operatorsList.contains(equation.charAt(equation.length()-2)+""))
            {
                enableButton(zeroButton,false);
            }
        }
    }

    public void dotValidation(Button button){
        String currentCharacter="";
        int deleteEquationLength = equation.length()-1;
        while(deleteEquationLength>=0 && !operatorsList.contains(currentCharacter)){
            if(currentCharacter.equals(".")){
                enableButton(dotButton,false);
                break;
            }
            else{
                enableButton(dotButton,true);
            }
            currentCharacter = equation.charAt(deleteEquationLength)+"";
            deleteEquationLength--;
        }
        disableZero(button);

    }

    public void enableButton(Button button, boolean flag)
    {
        button.setEnabled(flag);
    }

    public void clear()
    {
        equation.delete(0, equation.length());
    }

    public void clickClear(View view)
    {
        clear();
        refresh();
        turnOffOnStartActivity();
        enableButton(zeroButton,true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK)
        {
                String message = data.getStringExtra(DELETE_FLAG);
                if (message.equals("YES")) {
                    historyMessage.delete(0, historyMessage.length());
                }
            }
            super.onActivityResult(requestCode, resultCode, data);
    }
}
