package cop5556sp17.AST;

import cop5556sp17.Scanner.Token;
import org.objectweb.asm.Label;

public abstract class ASTNode {

	final public Token firstToken;

	protected ASTNode(Token firstToken){
		this.firstToken=firstToken;
	}

	public Token getFirstToken() {
		return firstToken;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((firstToken == null) ? 0 : firstToken.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof ASTNode)) {
			return false;
		}
		ASTNode other = (ASTNode) obj;
		if (firstToken == null) {
			if (other.firstToken != null) {
				return false;
			}
		} else if (!firstToken.equals(other.firstToken)) {
			return false;
		}
		return true;
	}

	public abstract Object visit(ASTVisitor v, Object arg) throws Exception;

	private Type.TypeName typeName;

	public Type.TypeName getTypeName() {
		return typeName;
	}

	public void setTypeName(Type.TypeName typeName){
		this.typeName = typeName;
	}

	public Dec dec; //TODO CHANGE TO PRIVATE

	public void setDec(Dec dec) {
		this.dec = dec;
	}

	public Dec getDec() {
		return dec;
	}

//	private Label startLabel;
//
//	private Label endLabel;
//
//	public Label getStartLabel() {
//		return startLabel;
//	}
//
//	public void setStartLabel() {
//		Label startLabel = new Label();
//		this.startLabel = startLabel;
//	}
//
//	public Label getEndLabel() {
//		return endLabel;
//	}
//
//	public void setEndLabel() {
//		Label endLabel = new Label();
//		this.endLabel = endLabel;
//	}

//	int slot;
//
//	public int getSlot() {
//		return slot;
//	}
//
//	public void setSlot(int slot) {
//		this.slot = slot;
//	}
}

