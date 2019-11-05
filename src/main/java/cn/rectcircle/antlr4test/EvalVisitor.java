package cn.rectcircle.antlr4test;

import java.util.HashMap;


import cn.rectcircle.antlr4test.parser.ExprBaseVisitor;
import cn.rectcircle.antlr4test.parser.ExprParser;
import cn.rectcircle.antlr4test.parser.ExprParser.AddSubContext;
import cn.rectcircle.antlr4test.parser.ExprParser.BlankContext;
import cn.rectcircle.antlr4test.parser.ExprParser.IdContext;
import cn.rectcircle.antlr4test.parser.ExprParser.PrintExprContext;
import cn.rectcircle.antlr4test.parser.ExprParser.ProgContext;

class EvalException extends RuntimeException {
    private static final long serialVersionUID = 1743556669279352945L;
    private final String message;

    public EvalException(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}

public class EvalVisitor extends ExprBaseVisitor<Integer> {

    private HashMap<String, Integer> vars;
    private int resultId = 0;
    private int line = 0;
    private boolean quickFailed = true;

    public EvalVisitor() {
        this(true);
    }

    public EvalVisitor(boolean quickFailed) {
        this.quickFailed = quickFailed;
        this.vars = new HashMap<String, Integer>();
        this.resultId = 0;
        this.line = 0;
    }

    @Override
    public Integer visitProg(ProgContext context) {
        try {
            return super.visitProg(context);
        } catch (EvalException e) {
            if (quickFailed) {
                System.out.println(e.getMessage());
            }
            return null;
        }
    }

    @Override
    public Integer visitPrintExpr(PrintExprContext ctx) {
        try {
            line++;
            String id = "res" + resultId;
            resultId++;
            Integer value = visit(ctx.expr());
            this.vars.put(id, value);
            System.out.println(id + ": " + value);
            return value;
        } catch (EvalException e) {
            if (quickFailed) {
                throw e;
            } else {
                System.out.println(e.getMessage());
                return null;
            }
        }

    }

    @Override
    public Integer visitAssign(ExprParser.AssignContext ctx) {
        try {
            line++;
            String id = ctx.ID().getText();
            Integer value = visit(ctx.expr());
            this.vars.put(id, value);
            System.out.println(id + ": " + value);
            line++;
            return value;
        } catch (EvalException e) {
            if (quickFailed) {
                throw e;
            } else {
                System.out.println(e.getMessage());
                return null;
            }
        }
    }

    @Override
    public Integer visitBlank(BlankContext ctx) {
        line++;
        return super.visitBlank(ctx);
    }

    @Override
    public Integer visitMulDiv(ExprParser.MulDivContext ctx) {
        Integer left = visit(ctx.expr(0));
        Integer right = visit(ctx.expr(1));

        if (ctx.op.getType() == ExprParser.MUL) {
            return left * right;
        } else {
            return left / right;
        }
    }

    @Override
    public Integer visitAddSub(AddSubContext ctx) {
        Integer left = visit(ctx.expr(0));
        Integer right = visit(ctx.expr(1));

        if (ctx.op.getType() == ExprParser.ADD) {
            return left + right;
        } else {
            return left - right;
        }
    }

    @Override
    public Integer visitInt(ExprParser.IntContext ctx) {
        try {
            return Integer.valueOf(ctx.INT().getText());
        } catch (NumberFormatException e) {
            throw new EvalException("line " + line + ": 数值解析失败 " + ctx.INT().getText());
        }
    }

    @Override
    public Integer visitId(IdContext ctx) {
        String id = ctx.getText();
        if (this.vars.containsKey(id)) {
            return this.vars.get(id);
        }
        throw new EvalException("line "+ line + ": 未定义的标识符 " + id);
    }
}
