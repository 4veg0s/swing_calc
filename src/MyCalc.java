import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Objects;

public class MyCalc {
    JFrame theFrame;
    JTextField historyField;    // поле для истории ввода
    JTextField countField;  // поле для ввода
    ArrayList<JButton> buttonList;
    LinkedList<Double> operands;
    StringBuilder operations;

    public static void main(String[] args) {
        new MyCalc().buildGUI();
    }

    public void buildGUI() {
        theFrame = new JFrame("Калькулятор");
        theFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel background = new JPanel(new BorderLayout());
        background.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        Box twoTextFields = new Box(BoxLayout.Y_AXIS);
        Box signAndTextFieldBox = new Box(BoxLayout.X_AXIS);

        signAndTextFieldBox.add(createButton("+/-", new MySignListener()));

        // Настройка поля ввода
        countField = new JTextField(20);
        countField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    String countFieldText = countField.getText();
                    countFieldText = countFieldText.substring(0, countFieldText.length() - 1);
                    countField.setText(countFieldText);
                }
                if (e.getButton() == MouseEvent.BUTTON3) {
                    operands.clear();
                    operations = new StringBuilder();
                    countField.setText(null);
                    historyField.setText(null);
                }
            }
        });
        countField.setFont(new Font("tahoma", Font.PLAIN, 20));
        countField.setToolTipText("<HTML>Нажмите ЛКМ, чтобы стереть символ<br>Нажмите ПКМ, чтобы очистить историю вычислений");
        countField.setEnabled(false);
        countField.setText("");
        countField.setHorizontalAlignment(JTextField.RIGHT);

        historyField = new JTextField(20);
        historyField.setFont(new Font("tahoma", Font.PLAIN, 20));
        historyField.setToolTipText("История операций");
        historyField.setEnabled(false);
        historyField.setText("");
        historyField.setHorizontalAlignment(JTextField.RIGHT);

        //Box digitButtonBox = new Box(BoxLayout.Y_AXIS);         // для расположения 9 цифр и в отдельной строчке ноль и десятичная точка
        GridLayout digitGrid = new GridLayout(4, 4);
        digitGrid.setHgap(2);
        digitGrid.setVgap(2);
        JPanel digitPanel = new JPanel(digitGrid);

        digitPanel.add(createButton("7", new MyDigitListener()));
        digitPanel.add(createButton("8", new MyDigitListener()));
        digitPanel.add(createButton("9", new MyDigitListener()));
        digitPanel.add(createButton("+", new MyOperationListener()));

        digitPanel.add(createButton("4", new MyDigitListener()));
        digitPanel.add(createButton("5", new MyDigitListener()));
        digitPanel.add(createButton("6", new MyDigitListener()));
        digitPanel.add(createButton("-", new MyOperationListener()));

        digitPanel.add(createButton("1", new MyDigitListener()));
        digitPanel.add(createButton("2", new MyDigitListener()));
        digitPanel.add(createButton("3", new MyDigitListener()));
        digitPanel.add(createButton("*", new MyOperationListener()));

        digitPanel.add(createButton("0", new MyDigitListener()));
        digitPanel.add(createButton(".", new MyDigitListener()));
        digitPanel.add(createButton("=", new MyOperationListener()));
        digitPanel.add(createButton("/", new MyOperationListener()));

        //signAndTextFieldPanel.add(countField);
        signAndTextFieldBox.add(countField);
        twoTextFields.add(historyField);
        twoTextFields.add(signAndTextFieldBox);
        background.add(BorderLayout.NORTH, twoTextFields);
        background.add(BorderLayout.CENTER, digitPanel);

        theFrame.getContentPane().add(background);
        theFrame.setBounds(500, 200, 250, 300);
        theFrame.setVisible(true);
        operands = new LinkedList<>();
        operations = new StringBuilder();
    }

    public class MyDigitListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            countField.setText(countField.getText() + e.getActionCommand());
        }
    }

    public class MyOperationListener implements ActionListener {
        private String previousOperand;
        @Override
        public void actionPerformed(ActionEvent e) {
            String newOperation = e.getActionCommand();
            if (!Objects.equals(countField.getText(), "")) {    // если не вводили число, то нечего и считать
                if (!Objects.equals(newOperation, "=")) {
                    operations.append(newOperation);
                    previousOperand = countField.getText();
                    operands.addLast(Double.parseDouble(previousOperand));
                    historyField.setText(historyField.getText()
                            + " " + ((Double.parseDouble(previousOperand) < 0) ? ("(" + previousOperand + ")") : previousOperand)
                            + " " + newOperation);
                    countField.setText(null);
                } else {
                    previousOperand = countField.getText();
                    operands.addLast(Double.parseDouble(previousOperand));
                    historyField.setText(historyField.getText()
                            + " " + ((Double.parseDouble(previousOperand) < 0) ? ("(" + previousOperand + ")") : previousOperand)
                            + " " + newOperation);
                    while (operations.length() > 0) {
                        Double n1 = operands.pollFirst();
                        Double n2 = operands.pollFirst();
                        operands.addFirst(simpleOperation(n1, n2, operations.charAt(0)));
                        operations.deleteCharAt(0);
                    }
                    countField.setText(Double.toString(operands.pollFirst()));
                    trimMeaningless();
                }
            }


        }

        private static Double simpleOperation(Double n1, Double n2, char operation) {
            if (operation == '+') {
                return n1 + n2;
            } else if (operation == '-') {
                return n1 - n2;
            } else if (operation == '*') {
                return n1 * n2;
            } else if (operation == '/') {
                return n1 / n2;
            }
            return null;
        }
    }

    public class MySignListener implements ActionListener {
        private Double countFieldData;

        @Override
        public void actionPerformed(ActionEvent e) {
            if (!countField.getText().isEmpty()) {
                countFieldData = Double.parseDouble(countField.getText());
                countField.setText(Double.toString(-countFieldData));
                trimMeaningless();
            }
        }
    }

    public static JButton createButton(String label, ActionListener listener) {
        JButton button = new JButton(label);
        button.addActionListener(listener);
        return button;
    }

    void trimMeaningless() {
        String countFieldText = countField.getText();
        String dotZero = countFieldText.substring(countFieldText.length() - 2, countFieldText.length());
        if (dotZero.equals(".0")) {
            countFieldText = countFieldText.substring(0, countFieldText.length() - 2);
        }

        if (Objects.equals(countFieldText, "-0")) {
            countFieldText = "0";
        }
        countField.setText(countFieldText);
    }
}
