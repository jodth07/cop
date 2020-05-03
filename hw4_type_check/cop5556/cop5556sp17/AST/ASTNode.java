package cop5556sp17.AST;

import cop5556sp17.Scanner.Token;

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


	public Type.TypeName getTypeName() {
		return typeName;
	}

	Type.TypeName typeName;

	public void setTypeName(Type.TypeName typeName){
		this.typeName = typeName;
	}


}

