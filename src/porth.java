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
        PUSH, ADD, SUB, MUL, DIV, REM, DUMP
    }

    static ArrayList<Map<OP, Integer>> parse(String filename) throws FileNotFoundException {
        ArrayList<Map<OP, Integer>> program = new ArrayList<>();

        Scanner sc = new Scanner(new File(filename));
        String line;
        String[] lineSplit;
        int lineCounter = 1;

        while (sc.hasNextLine()) {
            line = sc.nextLine();

            // comment/empty line
            if (line.startsWith("#") || line.strip().trim().equals(" ") || line.strip().trim().equals("")) continue;

            lineSplit = line.split(" ");

            switch (lineSplit[0]) {
                case "push" -> program.add(Map.of(OP.PUSH, Integer.parseInt(lineSplit[1])));
                case "add" -> program.add(Map.of(OP.ADD, 0));
                case "sub" -> program.add(Map.of(OP.SUB, 0));
                case "mul" -> program.add(Map.of(OP.MUL, 0));
                case "div" -> program.add(Map.of(OP.DIV, 0));
                case "rem" -> program.add(Map.of(OP.REM, 0));
                case "dump" -> program.add(Map.of(OP.DUMP, 0));
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

    static void run(ArrayList<Map<OP, Integer>> program) {
        Stack<Integer> stack = new Stack<>();
        OP op;
        int a = -1, b = -1;

        for (Map<OP, Integer> operation : program) {
            op = (porth.OP) operation.keySet().toArray()[0];

            if (op != OP.DUMP && op != OP.PUSH) {
                a = stack.pop();
                b = stack.pop();
            }

            if (op == OP.PUSH) stack.push(operation.get(op));
            else if (op == OP.ADD) stack.push(b + a);
            else if (op == OP.SUB) {
                stack.push(b - a);
            } else if (op == OP.MUL) stack.push(b * a);
            else if (op == OP.DIV) {
                stack.push(b / a);
            } else if (op == OP.REM) {
                stack.push(b % a);
            } else if (op == OP.DUMP) System.out.println(stack.pop());
        }
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
                }
            }

            fw.write("\nexit:\n\tli $v0,10\n\tsyscall\n");
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
            run(program);
        } else if (mode.equals("compile")) {
            compile(program, filename);
        } else {
            System.out.println("ERROR: Unknown MODE: " + mode);
            System.exit(1);
        }
    }
}
