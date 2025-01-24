public class ThreeAddressCode {
    private String operator;
    private String operand1;
    private String operand2;
    private String result;

    public ThreeAddressCode(String operator, String operand1, String operand2, String result) {
        this.operator = operator;
        this.operand1 = operand1;
        this.operand2 = operand2;
        this.result = result;
    }

    @Override
    public String toString() {
        return result + " = " + operand1 + " " + operator + " " + operand2;
    }
}