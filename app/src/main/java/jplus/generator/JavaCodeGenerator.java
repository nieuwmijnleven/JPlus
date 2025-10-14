package jplus.generator;

import com.google.googlejavaformat.java.Formatter;
import com.google.googlejavaformat.java.FormatterException;
import jplus.base.JPlus20Parser;
import jplus.base.JPlus20ParserBaseVisitor;

public class JavaCodeGenerator extends JPlus20ParserBaseVisitor<String> {

    @Override
    protected String aggregateResult(String aggregate, String nextResult) {
        return nextResult == null ? aggregate : nextResult;
    }

    @Override
    public String visitCompilationUnit(JPlus20Parser.CompilationUnitContext ctx) {
        String generated = super.visitCompilationUnit(ctx);
        try {
            return new Formatter().formatSource(generated);
        } catch(FormatterException e) {
            return generated;
        }
    }

    @Override
    public String visitPackageDeclaration(JPlus20Parser.PackageDeclarationContext ctx) {
        StringBuilder sb = new StringBuilder();
        sb.append("package ");
        var it = ctx.identifier().listIterator();
        while (it.hasNext()) {
            String identifier = it.next().getText();
            sb.append(identifier).append(".");
        }
        sb.setLength(sb.length()-1);
        sb.append(";");
        sb.append("\n");

        return sb.toString();
    }

    @Override
    public String visitOrdinaryCompilationUnit(JPlus20Parser.OrdinaryCompilationUnitContext ctx) {
        StringBuilder sb = new StringBuilder();
        sb.append(visit(ctx.packageDeclaration()));

        ctx.topLevelClassOrInterfaceDeclaration().forEach(
                declaration -> sb.append(visit(declaration)).append("\n"));
        return sb.toString();
    }

    @Override
    public String visitTopLevelClassOrInterfaceDeclaration(JPlus20Parser.TopLevelClassOrInterfaceDeclarationContext ctx) {
        StringBuilder sb = new StringBuilder();

        if (ctx.classDeclaration() != null) {
            var normalClassDeclaration = ctx.classDeclaration().normalClassDeclaration();
            normalClassDeclaration.classModifier().forEach(classModifier -> sb.append(classModifier.getText()).append(" "));
            sb.append("class").append(" ");
            sb.append(normalClassDeclaration.typeIdentifier().getText());
            sb.append(visit(ctx.classDeclaration()));
        } else if (ctx.interfaceDeclaration() != null) {
            ;
        } else {
            ;
        }

        return sb.toString();
    }

    @Override
    public String visitClassBody(JPlus20Parser.ClassBodyContext ctx) {
        StringBuilder sb = new StringBuilder();
        sb.append("{").append("\n");

        ctx.classBodyDeclaration().forEach(
                classBodyDeclaration -> sb.append(visit(classBodyDeclaration))
        );

        sb.append("}");
        return sb.toString();
    }

    @Override
    public String visitFieldDeclaration(JPlus20Parser.FieldDeclarationContext ctx) {
        StringBuilder sb = new StringBuilder();
        var it = ctx.fieldModifier().listIterator();
        while (it.hasNext()) {
            sb.append(it.next().getText()).append(" ");
        }
        sb.append(visit(ctx.unannType())).append(" ");
        sb.append(visit(ctx.variableDeclaratorList()));
        sb.append("\n");
        return sb.toString();
    }

    @Override
    public String visitUnannType(JPlus20Parser.UnannTypeContext ctx) {
        StringBuilder sb = new StringBuilder();
        var unannUnannReferenceType = ctx.unannReferenceType();
        if (unannUnannReferenceType != null) {
            sb.append(unannUnannReferenceType.getText());
        }

        var unannPrimitiveType = ctx.unannPrimitiveType();
        if (unannPrimitiveType != null) {
            sb.append(unannPrimitiveType.getText());
        }

        return sb.toString();
    }

    @Override
    public String visitVariableDeclaratorList(JPlus20Parser.VariableDeclaratorListContext ctx) {
        StringBuilder sb = new StringBuilder();
        ctx.variableDeclarator().forEach(
              variableDeclarator -> {
                  sb.append(visit(variableDeclarator)).append(", ");
              }
        );
        sb.setLength(sb.length()-2);
        sb.append(";");
        return sb.toString();
    }

    @Override
    public String visitVariableDeclarator(JPlus20Parser.VariableDeclaratorContext ctx) {
        StringBuilder sb = new StringBuilder();
        sb.append(ctx.variableDeclaratorId().getText()).append(" ").append("=").append(" ");
        sb.append(visit(ctx.variableInitializer()));
        return sb.toString();
    }

    @Override
    public String visitIdentifier(JPlus20Parser.IdentifierContext ctx) {
        return ctx.getText();
    }

    @Override
    public String visitLiteral(JPlus20Parser.LiteralContext ctx) {
        StringBuilder sb = new StringBuilder();
        if (ctx.IntegerLiteral() != null) {
            sb.append(ctx.IntegerLiteral().getText());
        } else if (ctx.FloatingPointLiteral() != null) {
            sb.append(ctx.FloatingPointLiteral().getText());
        } else if (ctx.BooleanLiteral() != null) {
            sb.append(ctx.BooleanLiteral().getText());
        } else if (ctx.CharacterLiteral() != null) {
            sb.append(ctx.CharacterLiteral().getText());
        } else if (ctx.StringLiteral() != null) {
            sb.append(ctx.StringLiteral().getText());
        } else if (ctx.TextBlock() != null) {
            sb.append(ctx.TextBlock().getText());
        } else if (ctx.NullLiteral() != null) {
            sb.append(ctx.NullLiteral().getText());
        }
        return sb.toString();
    }

    @Override
    public String visitUnqualifiedClassInstanceCreationExpression(JPlus20Parser.UnqualifiedClassInstanceCreationExpressionContext ctx) {
        StringBuilder sb = new StringBuilder();
            sb.append(ctx.NEW().getText()).append(" ");
            if (ctx.typeArguments() != null) {
                sb.append(ctx.typeArguments().getText());
            }
            sb.append(ctx.classOrInterfaceTypeToInstantiate().getText());
            sb.append(visit(ctx.argumentList()));
        return sb.toString();
    }

    @Override
    public String visitArgumentList(JPlus20Parser.ArgumentListContext ctx) {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        ctx.expression().forEach(
                expression -> sb.append(visit(expression)).append(", ")
        );
        sb.setLength(sb.length()-2);
        sb.append(")");
        return sb.toString();
    }

    @Override
    public String visitConstructorDeclaration(JPlus20Parser.ConstructorDeclarationContext ctx) {
        StringBuilder sb = new StringBuilder();
        ctx.constructorModifier().forEach(modifier -> sb.append(modifier.getText()).append(" "));

        var constructorDeclarator = ctx.constructorDeclarator();
        sb.append(constructorDeclarator.simpleTypeName().getText());
        sb.append(visit(constructorDeclarator.formalParameterList()));
        sb.append(visit(ctx.constructorBody()));
        sb.append("\n");
        return sb.toString();
    }

    @Override
    public String visitFormalParameterList(JPlus20Parser.FormalParameterListContext ctx) {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        ctx.formalParameter().forEach(
                parameter -> sb.append(visit(parameter.unannType())).append(" ").append(parameter.variableDeclaratorId().getText()).append(", ")
        );
        sb.setLength(sb.length()-2);
        sb.append(")");
        return sb.toString();
    }

    @Override
    public String visitConstructorBody(JPlus20Parser.ConstructorBodyContext ctx) {
        StringBuilder sb = new StringBuilder();
        sb.append("{").append("\n");
        sb.append(visit(ctx.blockStatements()));
        sb.append("}");
        return sb.toString();
    }

    @Override
    public String visitBlockStatements(JPlus20Parser.BlockStatementsContext ctx) {
        StringBuilder sb = new StringBuilder();
        ctx.blockStatement().forEach(
                blockStatement ->
                    sb.append(visit(blockStatement)).append("\n")
        );
        return sb.toString();
    }

    @Override
    public String visitBlockStatement(JPlus20Parser.BlockStatementContext ctx) {
        StringBuilder sb = new StringBuilder();
        //ctx.localClassOrInterfaceDeclaration();
        var localVariableDeclarationStatement = ctx.localVariableDeclarationStatement();
        if (localVariableDeclarationStatement != null) {
            sb.append(visit(localVariableDeclarationStatement));
        }

        var statement = ctx.statement();
        if (statement != null) {
            sb.append(visit(statement));
        }

        return sb.toString();
    }

    @Override
    public String visitStatementExpression(JPlus20Parser.StatementExpressionContext ctx) {
        StringBuilder sb = new StringBuilder();
        var assignment = ctx.assignment();
        if (assignment != null) {
            sb.append(visit(assignment.leftHandSide().fieldAccess())).append("=").append(assignment.expression().getText());
        } else if (ctx.methodInvocation() != null) {
            sb.append(visit(ctx.methodInvocation()));
        } else if (ctx.preIncrementExpression() != null) {
            sb.append("++").append(ctx.preIncrementExpression().unaryExpression().getText());
        }
        sb.append(";");
        return sb.toString();
    }

    @Override
    public String visitFieldAccess(JPlus20Parser.FieldAccessContext ctx) {
        StringBuilder sb = new StringBuilder();
        sb.append(ctx.primary().getText()).append(".").append(ctx.identifier().getText());
        return sb.toString();
    }

    @Override
    public String visitLocalVariableDeclaration(JPlus20Parser.LocalVariableDeclarationContext ctx) {
        StringBuilder sb = new StringBuilder();
        ctx.variableModifier().forEach(
                modifier -> sb.append(modifier.getText()).append(" ")
        );

        var unannType = ctx.localVariableType().unannType();
        String localVariableType = unannType != null ? visit(unannType) : "var";
        sb.append(localVariableType).append(" ");
        sb.append(visit(ctx.variableDeclaratorList()));
        return sb.toString();
    }

    @Override
    public String visitMethodDeclaration(JPlus20Parser.MethodDeclarationContext ctx) {
        StringBuilder sb = new StringBuilder();
        ctx.methodModifier().forEach(
                modifier -> sb.append(modifier.getText()).append(" ")
        );
        sb.append(visit(ctx.methodHeader()));
        sb.append(visit(ctx.methodBody().block()));
        sb.append("\n");
        return sb.toString();
    }

    @Override
    public String visitMethodHeader(JPlus20Parser.MethodHeaderContext ctx) {
        StringBuilder sb = new StringBuilder();
        sb.append(ctx.result().getText()).append(" ");
        sb.append(visit(ctx.methodDeclarator()));
        return sb.toString();
    }

    @Override
    public String visitMethodDeclarator(JPlus20Parser.MethodDeclaratorContext ctx) {
        StringBuilder sb = new StringBuilder();
        sb.append(ctx.identifier().getText());

        var formalParameterList = ctx.formalParameterList();
        if (formalParameterList != null) {
            sb.append(visit(ctx.formalParameterList()));
        } else {
            sb.append("()");
        }
        return sb.toString();
    }

    @Override
    public String visitForStatement(JPlus20Parser.ForStatementContext ctx) {
        StringBuilder sb = new StringBuilder();
        if (ctx.basicForStatement() != null) {
            var basicForStatement = ctx.basicForStatement();
            sb.append("for").append("(");
            if (basicForStatement.forInit() != null) {
                sb.append(visit(basicForStatement.forInit()));
            }
            if (basicForStatement.expression() != null) {
                sb.append(basicForStatement.expression().getText());
            }
            sb.append(";");
            if (basicForStatement.forUpdate() != null) {
                sb.append(basicForStatement.forUpdate().getText());
            }
            sb.append(")");
            sb.append(visit(basicForStatement.statement()));
        } else if (ctx.enhancedForStatement() != null) {

        }
        return sb.toString();
    }

    @Override
    public String visitBlock(JPlus20Parser.BlockContext ctx) {
        StringBuilder sb = new StringBuilder();
        sb.append("{").append("\n");
        sb.append(visit(ctx.blockStatements()));
        sb.append("}");
        return sb.toString();
    }

    @Override
    public String visitIfThenElseStatement(JPlus20Parser.IfThenElseStatementContext ctx) {
        StringBuilder sb = new StringBuilder();
        sb.append(ctx.IF().getText()).append("(").append(ctx.expression().getText()).append(")");
        sb.append(ctx.statementNoShortIf().getText());
        if (ctx.ELSE() != null) {
            sb.append(ctx.ELSE().getText());
            sb.append(ctx.statement().getText());
        }
        return sb.toString();
    }

    @Override
    public String visitMethodInvocation(JPlus20Parser.MethodInvocationContext ctx) {
        StringBuilder sb = new StringBuilder();
        if (ctx.typeName() != null) {
            sb.append(ctx.typeName().getText()).append(".").append(ctx.identifier().getText());
            if (ctx.argumentList() != null) {
                sb.append(visit(ctx.argumentList()));
            } else {
                sb.append("()");
            }
        }

        return sb.toString();
    }

    @Override
    public String visitPreIncrementExpression(JPlus20Parser.PreIncrementExpressionContext ctx) {
        StringBuilder sb = new StringBuilder();
        sb.append(ctx.unaryExpression().getText());
        return sb.toString();
    }

    @Override
    public String visitPrimaryNoNewArray(JPlus20Parser.PrimaryNoNewArrayContext ctx) {
        StringBuilder sb = new StringBuilder();
        if (ctx.typeName() != null) {
            String variableName = visit(ctx.typeName());
            sb.append(variableName);
            sb.append(".");
            sb.append(ctx.identifier().getText());
            if (ctx.argumentList() != null) {
                sb.append(visit(ctx.argumentList()));
            } else {
                sb.append("()");
            }

            if (ctx.NULLSAFE() != null) {
                StringBuilder nullsafeBuilder = new StringBuilder();
                nullsafeBuilder.append("(");
                nullsafeBuilder.append("(").append(variableName).append("!=").append("null").append(")").append("?");
                nullsafeBuilder.append(sb.toString()).append(":").append("null");
                nullsafeBuilder.append(")");
                sb = nullsafeBuilder;
            }
        } else {
            sb.append(super.visitPrimaryNoNewArray(ctx));
        }
        return sb.toString();
    }

    @Override
    public String visitTypeName(JPlus20Parser.TypeNameContext ctx) {
        StringBuilder sb = new StringBuilder();
        sb.append(ctx.packageName().identifier().getText());
        if (ctx.typeIdentifier() != null) {
            sb.append(".");
            sb.append(ctx.typeIdentifier().getText());
        }
        return sb.toString();
    }

    @Override
    public String visitNullCoalescingExpression(JPlus20Parser.NullCoalescingExpressionContext ctx) {
        StringBuilder sb = new StringBuilder();
//        String conditionalOrExpression = ctx.conditionalOrExpression().getText();
        String conditionalOrExpression = visit(ctx.conditionalOrExpression());
        String expression = ctx.expression().getText();
        sb.append("(");
        sb.append("(").append("(").append(conditionalOrExpression).append(")").append("!=").append("null").append(")").append("?").append("(").append(conditionalOrExpression).append(")").append(":").append(expression);
        sb.append(")");
        return sb.toString();
    }
}
