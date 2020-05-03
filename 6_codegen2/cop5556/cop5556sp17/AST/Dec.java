package cop5556sp17.AST;

import cop5556sp17.Scanner.Token;
import org.objectweb.asm.Label;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Dec extends ASTNode {

	final Token ident;

	public Dec(Token firstToken, Token ident) {
		super(firstToken);
		this.ident = ident;
	}

	public Token getType() {
		return firstToken;
	}

	public Token getIdent() {
		return ident;
	}

	@Override
	public String toString() {
		return "Dec [ident=" + ident + ", firstToken=" + firstToken + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((ident == null) ? 0 : ident.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (!(obj instanceof Dec)) {
			return false;
		}
		Dec other = (Dec) obj;
		if (ident == null) {
			if (other.ident != null) {
				return false;
			}
		} else if (!ident.equals(other.ident)) {
			return false;
		}
		return true;
	}

	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return v.visitDec(this,arg);
	}


	Map<Integer, String> slots = new HashMap<>();

	int slot;

	public int getSlot() {
		return slot;
	}

	public void setSlot(int slot) {
		this.slot = slot;
	}

	private Label startLabel;

	private Label endLabel;

	public Label getStartLabel() {
		return startLabel;
	}

	public void setStartLabel(Label startLabel) {
		this.startLabel = startLabel;
	}

	public Label getEndLabel() {
		return endLabel;
	}

	public void setEndLabel(Label endLabel) {
		this.endLabel = endLabel;
	}



	// dec hashMap





	// hashmap to save slot numbers and dec
}
