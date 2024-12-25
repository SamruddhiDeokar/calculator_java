import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Calculator extends JFrame {
    public Calculator() {
        setTitle("Calculator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 600);
        setLocationRelativeTo(null); // Center the frame on the screen
        CalculatorPanel calc = new CalculatorPanel();
        add(calc);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Calculator frame = new Calculator();
            frame.setVisible(true);
        });
    }
}

class CalculatorPanel extends JPanel implements ActionListener {
    JButton[] numberButtons = new JButton[10];
    JButton plus, minus, mul, div, dot, equal, clear, backspace, percent, sqrt, memoryClear, memoryRecall, memoryAdd, memorySubtract;
    static JTextField result = new JTextField("0", 20);
    static String lastCommand = null;
    double preRes = 0, secVal = 0, memory = 0;
    boolean startNewValue = true;

    public CalculatorPanel() {
        setLayout(new BorderLayout());
        result.setEditable(false);
        result.setFont(new Font("Arial", Font.BOLD, 24));
        result.setHorizontalAlignment(SwingConstants.RIGHT);
        result.setBackground(Color.WHITE);
        add(result, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(6, 4, 10, 10));

        // Add memory buttons
        addButton(buttonPanel, memoryClear = new JButton("MC"));
        addButton(buttonPanel, memoryRecall = new JButton("MR"));
        addButton(buttonPanel, memoryAdd = new JButton("M+"));
        addButton(buttonPanel, memorySubtract = new JButton("M-"));

        // Add special buttons
        addButton(buttonPanel, clear = new JButton("C"));
        addButton(buttonPanel, backspace = new JButton("←"));
        addButton(buttonPanel, percent = new JButton("%"));
        addButton(buttonPanel, sqrt = new JButton("√"));

        // Add number and operator buttons
        for (int i = 7; i <= 9; i++) addButton(buttonPanel, numberButtons[i] = new JButton(String.valueOf(i)));
        addButton(buttonPanel, div = new JButton("/"));

        for (int i = 4; i <= 6; i++) addButton(buttonPanel, numberButtons[i] = new JButton(String.valueOf(i)));
        addButton(buttonPanel, mul = new JButton("*"));

        for (int i = 1; i <= 3; i++) addButton(buttonPanel, numberButtons[i] = new JButton(String.valueOf(i)));
        addButton(buttonPanel, minus = new JButton("-"));

        addButton(buttonPanel, dot = new JButton("."));
        addButton(buttonPanel, numberButtons[0] = new JButton("0"));
        addButton(buttonPanel, equal = new JButton("="));
        addButton(buttonPanel, plus = new JButton("+"));

        add(buttonPanel, BorderLayout.CENTER);
    }

    private void addButton(JPanel panel, JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.addActionListener(this);
        panel.add(button);
    }

    private void assign(String no) {
        if (startNewValue || result.getText().equals("0")) {
            result.setText(no);
            startNewValue = false;
        } else {
            result.setText(result.getText() + no);
        }
    }

    private double calculateIntermediateResult(double a, double b, String command) {
        switch (command) {
            case "/": return b != 0 ? a / b : Double.NaN;
            case "*": return a * b;
            case "-": return a - b;
            case "+": return a + b;
            default: return b;
        }
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        Object source = ae.getSource();

        // Number button actions
        for (int i = 0; i < 10; i++) {
            if (source == numberButtons[i]) {
                assign(String.valueOf(i));
                return;
            }
        }

        // Dot button action
        if (source == dot && !result.getText().contains(".")) {
            result.setText(result.getText() + ".");
        }

        // Clear button action
        else if (source == clear) {
            result.setText("0");
            preRes = 0;
            secVal = 0;
            lastCommand = null;
            startNewValue = true;
        }

        // Backspace button action
        else if (source == backspace && result.getText().length() > 0) {
            String text = result.getText();
            result.setText(text.length() > 1 ? text.substring(0, text.length() - 1) : "0");
        }

        // Percent button action
        else if (source == percent) {
            double value = Double.parseDouble(result.getText());
            result.setText(String.valueOf(value / 100));
            startNewValue = true;
        }

        // Square root button action
        else if (source == sqrt) {
            double value = Double.parseDouble(result.getText());
            result.setText(String.valueOf(Math.sqrt(value)));
            startNewValue = true;
        }

        // Memory operations
        else if (source == memoryClear) {
            memory = 0;
        } else if (source == memoryRecall) {
            result.setText(String.valueOf(memory));
        } else if (source == memoryAdd) {
            memory += Double.parseDouble(result.getText());
        } else if (source == memorySubtract) {
            memory -= Double.parseDouble(result.getText());
        }

        // Arithmetic operations
        else if (source == plus || source == minus || source == mul || source == div) {
            if (lastCommand != null) {
                secVal = Double.parseDouble(result.getText());
                preRes = calculateIntermediateResult(preRes, secVal, lastCommand);
            } else {
                preRes = Double.parseDouble(result.getText());
            }
            lastCommand = ((JButton) source).getText();
            startNewValue = true;
        }

        // Equal button action
        else if (source == equal) {
            try {
                secVal = Double.parseDouble(result.getText());
                preRes = calculateIntermediateResult(preRes, secVal, lastCommand);
                result.setText(Double.toString(preRes));
                lastCommand = null;
                startNewValue = true;
            } catch (Exception e) {
                result.setText("Error");
                startNewValue = true;
            }
        }
    }
}
