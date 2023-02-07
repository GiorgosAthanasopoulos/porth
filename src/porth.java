import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;
import java.util.Stack;

public class porth {
    enum OP {
        PUSH, // push to the stack
        POP, // pop 1 element from the stack
        ADD, // pop 2 times and push the add of the 2 popped elements
        SUB, // pop 2 times and push the sub of the 2 popped elements
        MUL, // pop 2 times and push the mul of the 2 popped elements
        DIV, // pop 2 times and push the div of the 2 popped elements
        REM, // pop 2 times and push the rem of the 2 popped elements
        SQUARE, // pop 1 time and push popped element squared
        DUMP, // pop 1 time and print popped element
        READ, // push to the stack (an integer from std input)
        COMP, // pop 2 times and compare the 2 popped elements. if they re equal push 1 to the stack otherwise push 0
        JCOMP, // pop 2 times and compare the 2 popped elements. if they re equal jump given amount of instructions otherwise don't
        GT, // pop 2 times and compare the 2 popped elements. if the first 1 is greater than the second one then push 1 otherwise push 0
        LT, // pop 2 times and compare the 2 popped elements. if the first 1 is lesser than the second one then push 1 otherwise push 0
        ASSERT, // pop 1 time and assert that the popped element is equal to the first parameter.
        SQRT, // pop 1 time and push the square root of the popped element to the stack
        FLIP, // pop 1 time and push the opposite of the popped element to the stack
        BACK, // move back give amount of instructions. Mainly for loops
    }

    static ArrayList<Map<OP, Integer>> parse(String filename) throws FileNotFoundException {
        ArrayList<Map<OP, Integer>> program = new ArrayList<>();

        Scanner sc = new Scanner(new File(filename));
        String line;
        String[] lineSplit;
        int lineCounter = 1;

        while (sc.hasNextLine()) {
            line = sc.nextLine().toLowerCase();

            // comment/empty line
            if (line.startsWith("#") || line.strip().trim().equals(" ") || line.strip().trim().equals("")) {
                lineCounter++;
                continue;
            }

            lineSplit = line.split(" ");

            switch (lineSplit[0]) {
                case "push" -> program.add(Map.of(OP.PUSH, Integer.parseInt(lineSplit[1])));
                case "read" -> program.add(Map.of(OP.READ, 0));
                case "square" -> program.add(Map.of(OP.SQUARE, 0));
                case "comp" -> program.add(Map.of(OP.COMP, 0));
                case "jcomp" -> program.add(Map.of(OP.JCOMP, Integer.parseInt(lineSplit[1])));
                case "gt" -> program.add(Map.of(OP.GT, 0));
                case "lt" -> program.add(Map.of(OP.LT, 0));
                case "add" -> program.add(Map.of(OP.ADD, 0));
                case "sub" -> program.add(Map.of(OP.SUB, 0));
                case "mul" -> program.add(Map.of(OP.MUL, 0));
                case "div" -> program.add(Map.of(OP.DIV, 0));
                case "rem" -> program.add(Map.of(OP.REM, 0));
                case "dump" -> program.add(Map.of(OP.DUMP, 0));
                case "assert" -> program.add(Map.of(OP.ASSERT, Integer.parseInt(lineSplit[1])));
                case "flip" -> program.add(Map.of(OP.FLIP, 0));
                case "sqrt" -> program.add(Map.of(OP.SQRT, 0));
                case "pop" -> program.add(Map.of(OP.POP, 0));
                case "back" -> program.add(Map.of(OP.BACK, Integer.parseInt(lineSplit[1])));
                default -> {
                    System.out.println("ERROR: Invalid operation@" + filename + "#" + lineCounter);
                    System.exit(1);
                }
            }

            lineCounter++;
        }

        sc.close();
        return program;
    }

    static void run(ArrayList<Map<OP, Integer>> program, String filename) {
        Stack<Integer> stack = new Stack<>();
        OP op;
        int a = -1, b = -1, lineCounter = 0;
        Scanner sc = new Scanner(System.in);
        Map<OP, Integer> operation;

        for (int i = 0; i < program.size(); i++) {
            operation = program.get(i);
            op = (porth.OP) operation.keySet().toArray()[0];

            if (op != OP.DUMP && op != OP.PUSH && op != OP.READ && op != OP.SQUARE && op != OP.ASSERT && op != OP.SQRT && op != OP.FLIP && op != OP.POP && op != OP.BACK) {
                a = stack.pop();
                b = stack.pop();
            }
            if (op == OP.SQUARE || op == OP.DUMP || op == OP.ASSERT || op == OP.SQRT || op == OP.FLIP) a = stack.pop();

            if (op == OP.PUSH) stack.push(operation.get(op));
            if (op == OP.READ) {
                stack.push(sc.nextInt());
            } else if (op == OP.SQUARE) {
                stack.push((int) Math.pow(a, 2));
            } else if (op == OP.COMP) {
                stack.push(a == b ? 1 : 0);
            } else if (op == OP.JCOMP) {
                stack.push(a == b ? 1 : 0);
                if (a == b) {
                    i += operation.get(op);
                }
            } else if (op == OP.GT) {
                stack.push(b > a ? 1 : 0);
            } else if (op == OP.LT) {
                stack.push(b < a ? 1 : 0);
            } else if (op == OP.ASSERT) {
                stack.push(a == operation.get(op) ? 1 : 0);
                if (a != operation.get(op)) {
                    throw new AssertionError(filename + "#" + (lineCounter + 1));
                }
            } else if (op == OP.SQRT) {
                stack.push((int) Math.sqrt(a));
            } else if (op == OP.FLIP) {
                stack.push(-a);
            } else if (op == OP.POP) {
                stack.pop();
            } else if (op == OP.BACK) {
                i -= operation.get(op) + 1;
            } else if (op == OP.ADD) stack.push(b + a);
            else if (op == OP.SUB) {
                stack.push(b - a);
            } else if (op == OP.MUL) stack.push(b * a);
            else if (op == OP.DIV) {
                stack.push(b / a);
            } else if (op == OP.REM) {
                stack.push(b % a);
            } else if (op == OP.DUMP) System.out.println(a);

            lineCounter++;
        }

        sc.close();
    }

    static void compile(ArrayList<Map<OP, Integer>> program, String filename) throws IOException {
        File out = new File(filename.split("\\.")[0] + ".asm");
        OP op;

        try (FileWriter fw = new FileWriter(out)) {
            fw.write("\t.text\n\t.globl main\nmain:");

            for (Map<OP, Integer> operation : program) {
                op = (porth.OP) operation.keySet().toArray()[0];

                if (op == OP.PUSH) {
                    fw.write("\n\tsub $sp,$sp,4\n\tli $t0," + operation.get(op) + "\n\tsw $t0,($sp)");
                } else if (op == OP.READ) {
                    fw.write("\n\tli $v0,5\n\tsyscall\n\tsub $sp,$sp,4\n\tsw $v0,($sp)");
                } else if (op == OP.SQUARE) {
                    fw.write("\n\tlw $t0,($sp)\n\tadd $sp,$sp,4\n\tmul $t1,$t0,$t0\n\tsub $sp,$sp,4\n\tsw $t1,($sp)");
                } else if (op == OP.COMP) {
                    fw.write("\n\tlw $t0,($sp)\n\tadd $sp,$sp,4\n\tlw $t1,($sp)\n\tadd $sp,$sp,4\n\tseq $t2,$t0,$t1\n\tsub $sp,$sp,4\n\tsw $t2,($sp)");
                } else if (op == OP.ADD) {
                    fw.write("\n\tlw $t0,($sp)\n\tadd $sp,$sp,4\n\tlw $t1,($sp)\n\tadd $sp,$sp,4\n\tadd $t2,$t0,$t1\n\tsub $sp,$sp,4\n\tsw $t2,($sp)");
                } else if (op == OP.SUB) {
                    fw.write("\n\tlw $t0,($sp)\n\tadd $sp,$sp,4\n\tlw $t1,($sp)\n\tadd $sp,$sp,4\n\tsub $t2,$t0,$t1\n\tsub $sp,$sp,4\n\tsw $t2,($sp)");
                } else if (op == OP.MUL) {
                    fw.write("\n\tlw $t0,($sp)\n\tadd $sp,$sp,4\n\tlw $t1,($sp)\n\tadd $sp,$sp,4\n\tmul $t2,$t0,$t1\n\tsub $sp,$sp,4\n\tsw $t2,($sp)");
                } else if (op == OP.DIV) {
                    fw.write("\n\tlw $t0,($sp)\n\tadd $sp,$sp,4\n\tlw $t1,($sp)\n\tadd $sp,$sp,4\n\tdiv $t2,$t0,$t1\n\tsub $sp,$sp,4\n\tsw $t2,($sp)");
                } else if (op == OP.REM) {
                    fw.write("\n\tlw $t0,($sp)\n\tadd $sp,$sp,4\n\tlw $t1,($sp)\n\tadd $sp,$sp,4\n\trem $t2,$t0,$t1\n\tsub $sp,$sp,4\n\tsw $t2,($sp)");
                } else if (op == OP.DUMP) {
                    fw.write("\n\tlw $t0,($sp)\n\tadd $sp,$sp,4\n\tmove $a0,$t0\n\tli $v0,1\n\tsyscall");
                } else if (op == OP.JCOMP) {
                    fw.write(""); //TODO
                } else if (op == OP.GT) {
                    fw.write(""); //TODO
                } else if (op == OP.LT) {
                    fw.write(""); //TODO
                } else if (op == OP.ASSERT) {
                    fw.write(""); //TODO
                } else if (op == OP.SQRT) {
                    fw.write(""); //TODO
                } else if (op == OP.FLIP) {
                    fw.write(""); //TODO
                }
            }

            fw.write("\nexit:\n\tli $v0,10\n\tsyscall");
        }
    }

    static void usage() {
        System.out.println("USAGE: porth <MODE> [FILE]");
        System.out.println("MODE: run, compile");
        System.out.println("FILE: porth source code file name");
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            usage();
            System.out.println("ERROR: Missing argument: " + (args.length < 1 ? "MODE" : "FILE"));
            System.exit(1);
        }

        String mode = args[0], filename = args[1];
        ArrayList<Map<OP, Integer>> program = parse(filename);

        if (mode.equals("run")) {
            run(program, filename);
        } else if (mode.equals("compile")) {
            compile(program, filename);
        } else {
            System.out.println("ERROR: Unknown MODE: " + mode);
            System.exit(1);
        }
    }
}
