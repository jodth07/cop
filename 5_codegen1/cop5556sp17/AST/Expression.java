package cop5556sp17.AST;

import cop5556sp17.Scanner.Token;

import java.util.Objects;

public abstract class Expression extends ASTNode {

	Object value;

	protected Expression(Token firstToken) {
		super(firstToken);
	}

	@Override
	abstract public Object visit(ASTVisitor v, Object arg) throws Exception;

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}
}
