package cn.rectcircle;


import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import cn.rectcircle.antlr4test.EvalVisitor;
import cn.rectcircle.antlr4test.parser.ExprLexer;
import cn.rectcircle.antlr4test.parser.ExprParser;

/**
 * Hello world!
 */
public final class App {
    private App() {
    }

    /**
     * Says hello to the world.
     * @param args The arguments of the program.
     */
    public static void main(String[] args) {
        String [] stats = {
                "1 + 2 + 3",
                "a = 6",
                "b = 2",
                "a",
                "b",
                "c = a+b",
                "a + b",
                "d",
                "c"
        };

        EvalVisitor visitor = new EvalVisitor();

        for (String stat : stats) {
            ExprLexer lexer = new ExprLexer(CharStreams.fromString(stat + "\n"));
            System.out.println(">>> " + stat);
            CommonTokenStream tokenStream = new CommonTokenStream(lexer);
            ExprParser parser = new ExprParser(tokenStream);
            visitor.visit(parser.prog());
            System.out.println();
        }

    }
}
